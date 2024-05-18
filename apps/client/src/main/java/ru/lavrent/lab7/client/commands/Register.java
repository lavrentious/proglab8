package ru.lavrent.lab7.client.commands;

import ru.lavrent.lab7.client.models.forms.RegisterForm;
import ru.lavrent.lab7.client.utils.GlobalStorage;
import ru.lavrent.lab7.client.utils.Reader;
import ru.lavrent.lab7.client.utils.TCPClient;
import ru.lavrent.lab7.common.exceptions.ValidationException;
import ru.lavrent.lab7.common.models.User;
import ru.lavrent.lab7.common.network.requests.RegisterRequest;
import ru.lavrent.lab7.common.network.responses.AuthResponse;
import ru.lavrent.lab7.common.utils.Commands;
import ru.lavrent.lab7.common.utils.Credentials;

import java.io.IOException;

public class Register extends Command {
  private TCPClient tcpClient;
  private Reader reader;

  public Register(Reader reader, TCPClient tcpClient) {
    super(Commands.REGISTER, "register a new user");
    this.tcpClient = tcpClient;
    this.reader = reader;
  }

  public void execute(String[] args) throws IOException {
    User user = reader.runForm(new RegisterForm());
    try {
      AuthResponse res = (AuthResponse) tcpClient.send(new RegisterRequest(user));
      System.out.println("registered successfully");
      GlobalStorage.getInstance().setUser(res.user);
      tcpClient.setCredentials(new Credentials(user.getUsername(), user.getPassword()));
    } catch (ValidationException e) {
      System.out.println("user validation failed");
      tcpClient.deauth();
    }
  }
}
