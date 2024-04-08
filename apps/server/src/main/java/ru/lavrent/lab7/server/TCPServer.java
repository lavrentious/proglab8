package ru.lavrent.lab7.server;

import org.apache.commons.lang3.SerializationUtils;
import ru.lavrent.lab7.common.network.requests.AuthRequest;
import ru.lavrent.lab7.common.network.requests.RegisterRequest;
import ru.lavrent.lab7.common.network.requests.Request;
import ru.lavrent.lab7.common.network.responses.ErrorResponse;
import ru.lavrent.lab7.common.network.responses.OkResponse;
import ru.lavrent.lab7.common.network.responses.Response;
import ru.lavrent.lab7.common.utils.Credentials;
import ru.lavrent.lab7.server.exceptions.BadRequest;
import ru.lavrent.lab7.server.managers.AuthManager;
import ru.lavrent.lab7.server.managers.RequestManager;
import ru.lavrent.lab7.server.managers.RuntimeManager;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class TCPServer {
  private final RequestManager requestManager;
  private final AuthManager authManager;
  private final Selector selector;
  private Map<SocketChannel, Response> channelDataMap;
  private Map<SocketChannel, Credentials> authorizedClients;
  private final ByteBuffer intBuffer;

  public TCPServer(int port, RequestManager requestManager, AuthManager authManager) throws IOException {
    this.requestManager = requestManager;
    this.authManager = authManager;
    this.channelDataMap = new HashMap<>();
    this.authorizedClients = new HashMap<>();
    this.intBuffer = ByteBuffer.allocate(Integer.BYTES);
    ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
    serverSocketChannel.bind(new InetSocketAddress(port));
    serverSocketChannel.configureBlocking(false);
    selector = Selector.open();
    serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
  }

  public void listen() throws IOException {
    while (true) {
      selector.select();
      Set<SelectionKey> selectedKeys = selector.selectedKeys();
      Iterator<SelectionKey> keyIterator = selectedKeys.iterator();

      while (keyIterator.hasNext()) {
        SelectionKey key = keyIterator.next();

        if (key.isAcceptable()) {
          handleConnect(key);
        } else if (key.isReadable()) {
          handleReadable(key);
        } else if (key.isWritable()) {
          handleWritable(key);
        }
        keyIterator.remove();
      }
    }
  }

  private void disconnect(SocketChannel client) {
    try {
      RuntimeManager.logger.info("%s disconnected".formatted(client.getRemoteAddress()));
      client.close();
    } catch (IOException e) {
    }
  }

  private void handleConnect(SelectionKey key) throws IOException {
    ServerSocketChannel serverSocketChannel = (ServerSocketChannel) key.channel();
    SocketChannel client = serverSocketChannel.accept();
    client.configureBlocking(false);
    client.register(selector, SelectionKey.OP_READ + SelectionKey.OP_WRITE);
    RuntimeManager.logger.info("%s connected".formatted(client.getRemoteAddress()));
  }

  private void handleWritable(SelectionKey key) throws IOException {
    SocketChannel client = (SocketChannel) key.channel();
    Response response = channelDataMap.get(client);
    if (response == null) {
      return;
    }
    RuntimeManager.logger.fine("writing %s to %s".formatted(response.getName(), client.toString()));
    sendResponse(client, response);
    channelDataMap.remove(client);
  }

  private void handleReadable(SelectionKey key) throws IOException {
    SocketChannel client = (SocketChannel) key.channel();
    RuntimeManager.logger.fine("%s is readable".formatted(client.toString()));
    int requestSize = this.readInt(client);
    RuntimeManager.logger.fine("incoming %d bytes".formatted(requestSize));
    if (requestSize == -1) {
      disconnect(client);
      return;
    }
    byte[] requestBytes = this.readRequest(client, requestSize);
    Request request = SerializationUtils.deserialize(requestBytes);
    RuntimeManager.logger.info(
        "received %s request from %s (%d bytes)".formatted(request.getName(), client.getRemoteAddress(), requestSize));

    try {
      this.channelDataMap.put(client, generateResponse(request, client));
    } catch (IOException e) {
      disconnect(client);
    }
  }

  private Response generateResponse(Request request, SocketChannel client) throws IOException {
    boolean authorized = authorizedClients.containsKey(client);
    if (request instanceof AuthRequest) {
      Credentials credentials = ((AuthRequest) request).credentials;
      boolean authResult = authManager.auth(credentials);
      if (authResult) {
        authorizedClients.put(client, credentials);
        return new OkResponse();
      } else {
        authorizedClients.remove(client);
        return new ErrorResponse("auth failed");
      }
    } else if (!(request instanceof RegisterRequest) && !authorized) {
      return new ErrorResponse("unauthorized");
    }
    try {
      Credentials credentials = authorizedClients.get(client);
      if (credentials != null) {
        request.setUser(authManager.getUserByUsername(credentials.username).toUser());
        authorizedClients.remove(client);
      }
      return requestManager.handleRequest(request);
    } catch (BadRequest e) {
      return new ErrorResponse(e.getMessage());
    }
  }

  private void writeInt(SocketChannel client, int x) throws IOException {
    intBuffer.clear();
    intBuffer.putInt(x);
    intBuffer.flip();
    client.write(intBuffer);
  }

  private void sendResponse(SocketChannel client, Response response) throws IOException {
    byte[] responseBytes = SerializationUtils.serialize(response);
    this.writeInt(client, responseBytes.length);
    client.write(ByteBuffer.wrap(responseBytes));
  }

  private int readInt(SocketChannel client) throws IOException {
    intBuffer.clear();
    int size = client.read(intBuffer);
    RuntimeManager.logger.fine("header (should be 4 bytes): %d bytes".formatted(size));
    if (size == -1) {
      return -1;
    }
    return intBuffer.flip().getInt();
  }

  private byte[] readRequest(SocketChannel client, int responseSize) throws IOException {
    ByteBuffer buffer = ByteBuffer.allocate(responseSize);
    int curRead = 0;
    while (curRead < responseSize) {
      curRead = client.read(buffer);
      if (curRead == 0) {
        continue;
      }
      if (curRead == -1) {
        break;
      }
      RuntimeManager.logger.fine("read %d bytes (%d remaining)".formatted(curRead, responseSize));
      responseSize -= curRead;
    }
    return buffer.array();
  }
}
