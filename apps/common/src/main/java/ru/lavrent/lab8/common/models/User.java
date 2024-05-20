package ru.lavrent.lab8.common.models;

import ru.lavrent.lab8.common.utils.Entity;

public class User extends Entity {
  private String username;
  private String password;

  public User(String username, String password) {
    this.username = username;
    this.password = password;
  }

  public String getUsername() {
    return username;
  }

  public String getPassword() {
    return password;
  }

  public void validate() {
  }
}
