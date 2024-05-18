package ru.lavrent.lab7.client.utils;

import ru.lavrent.lab7.common.utils.Credentials;
import ru.lavrent.lab7.common.utils.PublicUser;

public class GlobalStorage {
  static private GlobalStorage instance;
  private Credentials credentials;
  private PublicUser user;

  static public GlobalStorage getInstance() {
    if (instance == null) {
      instance = new GlobalStorage();
    }
    return instance;
  }

  public Credentials getCredentials() {
    return credentials;
  }

  public void setCredentials(Credentials credentials) {
    this.credentials = credentials;
  }

  public PublicUser getUser() {
    return user;
  }

  public void setUser(PublicUser user) {
    System.out.println("setting user " + user);
    this.user = user;
  }
}
