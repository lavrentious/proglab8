package ru.lavrent.lab8.common.network.requests;

import ru.lavrent.lab8.common.utils.AutoToString;
import ru.lavrent.lab8.common.utils.PublicUser;

import java.io.Serializable;
import java.util.Objects;

public abstract class Request extends AutoToString implements Serializable {
  final String name;
  private transient PublicUser user;

  public void setUser(PublicUser user) {
    this.user = user;
  }

  public PublicUser getUser() {
    return user;
  }

  public Request(String name) {
    this.name = name;
  }

  public String getName() {
    return name;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o)
      return true;
    if (o == null || getClass() != o.getClass())
      return false;
    Request response = (Request) o;
    return Objects.equals(name, response.name);
  }

  @Override
  public int hashCode() {
    return Objects.hash(name);
  }
}