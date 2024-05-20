package ru.lavrent.lab8.common.network.responses;

import ru.lavrent.lab8.common.utils.Commands;
import ru.lavrent.lab8.common.utils.PublicUser;

public class AuthResponse extends Response {
  public final PublicUser user;

  public AuthResponse(PublicUser user) {
    super(Commands.AUTH);
    this.user = user;
  }
}
