package ru.lavrent.lab7.client.commands;

import ru.lavrent.lab7.client.models.forms.LoginForm;
import ru.lavrent.lab7.client.utils.GlobalStorage;
import ru.lavrent.lab7.client.utils.Reader;
import ru.lavrent.lab7.client.utils.TCPClient;
import ru.lavrent.lab7.common.exceptions.AuthException;
import ru.lavrent.lab7.common.network.requests.AuthRequest;
import ru.lavrent.lab7.common.network.responses.AuthResponse;
import ru.lavrent.lab7.common.utils.Commands;
import ru.lavrent.lab7.common.utils.Credentials;

import java.io.IOException;

public class Login extends Command {
  private TCPClient tcpClient;
  private Reader reader;

  public Login(Reader reader, TCPClient tcpClient) {
    super(Commands.AUTH, "log in");
    this.tcpClient = tcpClient;
    this.reader = reader;
  }

  public void execute(String[] args) throws IOException {
    try {
      Credentials credentials = reader.runForm(new LoginForm());
      tcpClient.setCredentials(credentials);
      AuthResponse res = (AuthResponse) tcpClient.send(new AuthRequest(credentials));
      System.out.println("login successful");
      GlobalStorage.getInstance().setUser(res.user);
    } catch (AuthException e) {
      System.out.println("invalid credentials");
      tcpClient.deauth();
    }
  }
}
