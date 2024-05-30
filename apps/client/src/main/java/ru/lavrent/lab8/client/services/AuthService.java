package ru.lavrent.lab8.client.services;

import ru.lavrent.lab8.client.utils.GlobalStorage;
import ru.lavrent.lab8.client.utils.TCPClient;
import ru.lavrent.lab8.common.exceptions.AuthException;
import ru.lavrent.lab8.common.models.User;
import ru.lavrent.lab8.common.network.requests.AuthRequest;
import ru.lavrent.lab8.common.network.requests.RegisterRequest;
import ru.lavrent.lab8.common.network.responses.AuthResponse;
import ru.lavrent.lab8.common.utils.Credentials;
import ru.lavrent.lab8.common.utils.PublicUser;

import java.io.IOException;

public class AuthService {
  private static AuthService instance;

  public static synchronized AuthService getInstance() {
    if (instance == null) {
      instance = new AuthService();
    }
    return instance;
  }

  public void setCredentials(Credentials credentials) {
    GlobalStorage.getInstance().setCredentials(credentials);
  }

  public Credentials getCredentials() {
    return GlobalStorage.getInstance().getCredentials();
  }

  public void logout() {
    GlobalStorage.getInstance().setUser(null);
    GlobalStorage.getInstance().setCredentials(null);
  }

  public PublicUser auth(Credentials credentials) {
    System.out.println("authorizing");
    TCPClient tcpClient = GlobalStorage.getInstance().getTCPClient();
    try {
      var res = (AuthResponse) tcpClient.send(new AuthRequest(credentials));
      GlobalStorage.getInstance().setUser(res.user);
      GlobalStorage.getInstance().setCredentials(credentials);
      return res.user;
    } catch (IOException e) {
      GlobalStorage.getInstance().setUser(null);
      GlobalStorage.getInstance().setCredentials(null);
      throw new AuthException(e);
    }
  }

  public PublicUser register(Credentials credentials) {
    TCPClient tcpClient = GlobalStorage.getInstance().getTCPClient();
    try {
      var res = (AuthResponse) tcpClient
          .send(new RegisterRequest(new User(credentials.username, credentials.password)));
      return res.user;
    } catch (IOException e) {
      throw new AuthException(e);
    }
  }
}
