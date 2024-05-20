package ru.lavrent.lab8.client.commands;

import ru.lavrent.lab8.client.models.forms.LabWorkForm;
import ru.lavrent.lab8.client.utils.Reader;
import ru.lavrent.lab8.client.utils.TCPClient;
import ru.lavrent.lab8.common.exceptions.ValidationException;
import ru.lavrent.lab8.common.models.DryLabWork;
import ru.lavrent.lab8.common.network.requests.AddRequest;
import ru.lavrent.lab8.common.network.responses.AddResponse;
import ru.lavrent.lab8.common.utils.Commands;

import java.io.IOException;

public class Add extends Command {
  private Reader reader;
  private TCPClient tcpClient;

  public Add(Reader reader, TCPClient tcpClient) {
    super(Commands.ADD, "add a new element to collection");
    this.reader = reader;
    this.tcpClient = tcpClient;
  }

  public void execute(String[] args) throws IOException {
    try {
      DryLabWork labWork = reader.runForm(new LabWorkForm());
      System.out.println("formed lw: " + labWork.toString());
      AddResponse res = (AddResponse) tcpClient.send(new AddRequest(labWork));
      System.out.println("labwork created: " + res.labWork.getId());
    } catch (ValidationException e) {
      System.err.println("labwork validation failed: " + e.toString());
    }
  }
}
