package ru.lavrent.lab7.common.network.requests;

import ru.lavrent.lab7.common.models.User;
import ru.lavrent.lab7.common.utils.Commands;

public class RegisterRequest extends Request {
  public final User user;

  public RegisterRequest(User user) {
    super(Commands.REGISTER);
    this.user = user;
  }
}