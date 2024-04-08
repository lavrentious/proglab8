package ru.lavrent.lab7.client.commands;

import ru.lavrent.lab7.client.utils.TCPClient;
import ru.lavrent.lab7.common.utils.Commands;

import java.io.IOException;

public class Logout extends Command {
  private TCPClient tcpClient;

  public Logout(TCPClient tcpClient) {
    super(Commands.LOGOUT, "log out");
    this.tcpClient = tcpClient;
  }

  public void execute(String[] args) throws IOException {
    System.out.println("logged out");
    tcpClient.deauth();
  }
}
