package ru.lavrent.lab8.common.network.requests;

import ru.lavrent.lab8.common.models.User;
import ru.lavrent.lab8.common.utils.Commands;

public class RegisterRequest extends Request {
  public final User user;

  public RegisterRequest(User user) {
    super(Commands.REGISTER);
    this.user = user;
  }
}