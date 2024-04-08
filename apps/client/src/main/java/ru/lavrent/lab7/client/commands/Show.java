package ru.lavrent.lab7.client.commands;

import ru.lavrent.lab7.client.utils.TCPClient;
import ru.lavrent.lab7.common.models.LabWork;
import ru.lavrent.lab7.common.network.requests.BlankRequest;
import ru.lavrent.lab7.common.network.responses.ShowResponse;
import ru.lavrent.lab7.common.utils.Commands;

import java.io.IOException;

public class Show extends Command {
  private TCPClient tcpClient;

  public Show(TCPClient tcpClient) {
    super(Commands.SHOW, "list the elements of the collection");
    this.tcpClient = tcpClient;
  }

  public void execute(String[] args) throws IOException {
    ShowResponse res = (ShowResponse) tcpClient.send(new BlankRequest(Commands.SHOW));
    if (res.list.size() == 0) {
      System.out.println("collection is empty");
      return;
    }
    int i = 1;
    System.out.println("current collection:");
    for (LabWork labWork : res.list) {
      System.out.println("%s. %s".formatted(i++, labWork.toString()));
    }
  }
}
