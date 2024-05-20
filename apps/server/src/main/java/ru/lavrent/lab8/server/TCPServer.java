package ru.lavrent.lab8.server;

import org.apache.commons.lang3.SerializationUtils;
import ru.lavrent.lab8.common.network.requests.AuthRequest;
import ru.lavrent.lab8.common.network.requests.RegisterRequest;
import ru.lavrent.lab8.common.network.requests.Request;
import ru.lavrent.lab8.common.network.responses.AuthResponse;
import ru.lavrent.lab8.common.network.responses.ErrorResponse;
import ru.lavrent.lab8.common.network.responses.Response;
import ru.lavrent.lab8.common.utils.Block;
import ru.lavrent.lab8.common.utils.Credentials;
import ru.lavrent.lab8.common.utils.PublicUser;
import ru.lavrent.lab8.server.exceptions.BadRequest;
import ru.lavrent.lab8.server.managers.AuthManager;
import ru.lavrent.lab8.server.managers.RequestManager;
import ru.lavrent.lab8.server.managers.RuntimeManager;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TCPServer {
  class ClientInfo {
    private Credentials credentials;
    private ClientHandler clientHandler;
    private SocketChannel client;
    private boolean responseReady;
    private boolean readableHandled;
    private boolean writableHandled;
    private Set<Thread> threads;

    public ClientInfo(Block<Response> block, SocketChannel client, Credentials credentials) {
      this.credentials = credentials;
      this.clientHandler = new ClientHandler(this);
      this.client = client;
      this.responseReady = false;
      this.readableHandled = false;
      this.writableHandled = false;
      this.threads = new HashSet<>();
    }

    public void addThread(Thread thread) {
      this.threads.add(thread);
    }

    public void removeThread(Thread thread) {
      this.threads.remove(thread);
    }

    public void stopAllThreads() {
      for (Thread thread : threads) {
        thread.interrupt();
      }
    }

    public ClientHandler getClientHandler() {
      return clientHandler;
    }

    public Credentials getCredentials() {
      return credentials;
    }

    public boolean getReadableHandled() {
      return readableHandled;
    }

    public boolean getWritableHandled() {
      return writableHandled;
    }

    public boolean getResponseReady() {
      return responseReady;
    }

    public SocketChannel getClient() {
      return client;
    }

    public void setClientHandler(ClientHandler clientHandler) {
      this.clientHandler = clientHandler;
    }

    public void setCredentials(Credentials credentials) {
      this.credentials = credentials;
    }

    public void setResponseReady(boolean responseReady) {
      this.responseReady = responseReady;
    }

    public void setReadableHandled(boolean readableHandled) {
      this.readableHandled = readableHandled;
    }

    public void setWritableHandled(boolean writableHandled) {
      this.writableHandled = writableHandled;
    }

    public void setClient(SocketChannel client) {
      this.client = client;
    }
  }

  class ClientHandler {
    private ClientInfo clientInfo;
    private Block<Request> requestBlock;
    private Block<Response> responseBlock;

    public ClientHandler(ClientInfo clientInfo) {
      this.clientInfo = clientInfo;
      this.requestBlock = new Block<>();
      this.responseBlock = new Block<>();
    }

    void readRequest() {
      SocketChannel client = clientInfo.getClient();
      try {
        RuntimeManager.logger
            .fine("%s is readable (%s)".formatted(client.toString(), Thread.currentThread().toString()));
        int requestSize = TCPServer.this.readInt(client);
        RuntimeManager.logger.fine("incoming %d bytes".formatted(requestSize));
        if (requestSize == -1) {
          disconnect(client);
          return;
        }
        byte[] requestBytes = TCPServer.this.readRequest(client, requestSize);
        Request request = SerializationUtils.deserialize(requestBytes);
        RuntimeManager.logger.info(
            "received %s request from %s (%d bytes)".formatted(request.getName(),
                client.getRemoteAddress(),
                requestSize));
        this.requestBlock.put(request);
      } catch (IOException e) {
        disconnect(client);
      }
    }

    void generateResponse() {
      Request request = this.requestBlock.get(); // wait for the request
      try {
        Response response = TCPServer.this.generateResponse(request, clientInfo);
        this.responseBlock.put(response);
        RuntimeManager.logger.fine("response ready " + response.toString());
        this.clientInfo.setResponseReady(true);
      } catch (IOException e) {
        disconnect(clientInfo.getClient());
      }
    }

    void sendResponse() {
      RuntimeManager.logger.fine("waiting for response block");
      Response response = this.responseBlock.get();
      RuntimeManager.logger.fine("got response block");
      try {
        TCPServer.this.sendResponse(clientInfo.getClient(), response);
        RuntimeManager.logger.fine("sent response, not ready " + response.toString());
        this.clientInfo.setResponseReady(false);
      } catch (IOException e) {
        disconnect(clientInfo.getClient());
      }
    }
  }

  private final RequestManager requestManager;
  private final AuthManager authManager;
  private final Selector selector;
  private Map<SocketChannel, ClientInfo> channelDataMap;
  private final ByteBuffer intBuffer;
  private ExecutorService responseSendThreadPool;

  public TCPServer(int port, RequestManager requestManager, AuthManager authManager) throws IOException {
    this.requestManager = requestManager;
    this.authManager = authManager;
    this.channelDataMap = new HashMap<>();
    this.intBuffer = ByteBuffer.allocate(Integer.BYTES);
    ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
    serverSocketChannel.bind(new InetSocketAddress(port));
    serverSocketChannel.configureBlocking(false);
    selector = Selector.open();
    serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
    this.responseSendThreadPool = Executors.newFixedThreadPool(5);
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
    if (client == null) {
      return;
    }
    try {
      ClientInfo clientInfo = this.channelDataMap.get(client);
      if (clientInfo != null) {
        clientInfo.stopAllThreads();
      }
      this.channelDataMap.remove(client);
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
    this.channelDataMap.put(client, new ClientInfo(new Block<>(), client, null));
    RuntimeManager.logger.info("%s connected".formatted(client.getRemoteAddress()));
  }

  private void handleWritable(SelectionKey key) throws IOException {
    SocketChannel client = (SocketChannel) key.channel();
    ClientInfo clientInfo = this.channelDataMap.get(client);
    if (clientInfo == null || !clientInfo.getResponseReady() || clientInfo.getWritableHandled()) {
      return;
    }
    RuntimeManager.logger.fine("creating new thread for %s for writable".formatted(client.toString()));
    this.responseSendThreadPool.submit(
        () -> {
          clientInfo.getClientHandler().sendResponse();
          clientInfo.setReadableHandled(false);
          clientInfo.setWritableHandled(false);
        });
    clientInfo.setWritableHandled(true);
  }

  private void handleReadable(SelectionKey key) {
    SocketChannel client = (SocketChannel) key.channel();
    ClientInfo clientInfo = this.channelDataMap.get(client);
    if (clientInfo == null || clientInfo.getReadableHandled()) {
      return;
    }
    RuntimeManager.logger.fine("creating new thread for %s for readable (read)".formatted(client.toString()));
    Thread readRequestThread = new Thread(() -> {
      clientInfo.getClientHandler().readRequest();
      clientInfo.removeThread(Thread.currentThread());
    });
    clientInfo.addThread(readRequestThread);
    readRequestThread.start();
    RuntimeManager.logger
        .fine("creating new thread for %s for readable (generate response)".formatted(client.toString()));
    Thread generateResponsThread = new Thread(() -> {
      clientInfo.getClientHandler().generateResponse();
      clientInfo.removeThread(Thread.currentThread());
    });
    clientInfo.addThread(generateResponsThread);
    generateResponsThread.start();
    clientInfo.setReadableHandled(true);
  }

  private Response generateResponse(Request request, ClientInfo clientInfo)
      throws IOException {
    if (request instanceof AuthRequest || request instanceof RegisterRequest) {
      Credentials credentials;
      PublicUser authedUser;
      if (request instanceof AuthRequest) {
        credentials = ((AuthRequest) request).credentials;
        authedUser = authManager.auth(credentials);
      } else {
        credentials = new Credentials(((RegisterRequest) request).user.getUsername(),
            ((RegisterRequest) request).user.getPassword());
        authedUser = authManager.register(((RegisterRequest) request).user);
      }
      if (authedUser != null) {
        clientInfo.setCredentials(credentials);
        return new AuthResponse(authedUser);
      } else {
        clientInfo.setCredentials(null);
        return new ErrorResponse("auth failed");
      }
    } else if (clientInfo.getCredentials() == null) {
      return new ErrorResponse("unauthorized");
    }
    try {
      if (clientInfo.getCredentials() != null) {
        request.setUser(authManager.getUserByUsername(clientInfo.getCredentials().username).toPublicUser());
        clientInfo.setCredentials(null);
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
