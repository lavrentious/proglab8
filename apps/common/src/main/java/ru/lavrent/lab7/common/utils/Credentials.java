package ru.lavrent.lab7.common.utils;

import ru.lavrent.lab7.common.exceptions.ValidationException;

public class Credentials extends Entity {
  public String username;
  public String password;

  public Credentials(String username, String password) {
    this.username = username;
    this.password = password;
  }

  @Override
  public void validate() throws ValidationException {
  }
}
