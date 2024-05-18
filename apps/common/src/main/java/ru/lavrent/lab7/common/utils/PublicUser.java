package ru.lavrent.lab7.common.utils;

import ru.lavrent.lab7.common.exceptions.ValidationException;

public class PublicUser extends Entity {
  private long id;
  private String username;

  public PublicUser(long id, String username) {
    this.id = id;
    this.username = username;
  }

  public long getId() {
    return id;
  }

  public String getUsername() {
    return username;
  }

  @Override
  public void validate() throws ValidationException {
  }

}
