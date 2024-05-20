package ru.lavrent.lab8.client.commands;

import ru.lavrent.lab8.client.utils.TCPClient;
import ru.lavrent.lab8.common.network.requests.BlankRequest;
import ru.lavrent.lab8.common.utils.Commands;

import java.io.IOException;

public class Clear extends Command {
  private TCPClient tcpClient;

  public Clear(TCPClient tcpClient) {
    super(Commands.CLEAR, "clear collection");
    this.tcpClient = tcpClient;
  }

  public void execute(String[] args) throws IOException {
    tcpClient.send(new BlankRequest(Commands.CLEAR));
    System.out.println("collection cleared");
  }
}
