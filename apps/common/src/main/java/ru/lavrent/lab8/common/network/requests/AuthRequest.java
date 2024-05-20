package ru.lavrent.lab8.common.network.requests;

import ru.lavrent.lab8.common.utils.Commands;
import ru.lavrent.lab8.common.utils.Credentials;

public class AuthRequest extends Request {
  public final Credentials credentials;

  public AuthRequest(Credentials credentials) {
    super(Commands.AUTH);
    this.credentials = credentials;
  }
}