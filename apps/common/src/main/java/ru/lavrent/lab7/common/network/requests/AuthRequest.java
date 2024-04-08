package ru.lavrent.lab7.common.network.requests;

import ru.lavrent.lab7.common.utils.Commands;
import ru.lavrent.lab7.common.utils.Credentials;

public class AuthRequest extends Request {
  public final Credentials credentials;

  public AuthRequest(Credentials credentials) {
    super(Commands.AUTH);
    this.credentials = credentials;
  }
}