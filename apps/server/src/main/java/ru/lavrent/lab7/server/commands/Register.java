package ru.lavrent.lab7.server.commands;

import ru.lavrent.lab7.common.network.requests.RegisterRequest;
import ru.lavrent.lab7.common.network.requests.Request;
import ru.lavrent.lab7.common.network.responses.OkResponse;
import ru.lavrent.lab7.common.utils.Commands;
import ru.lavrent.lab7.server.managers.AuthManager;

import java.io.IOException;

public class Register extends Command {
  private AuthManager authManager;

  public Register(AuthManager authManager) {
    super(Commands.REGISTER);
    this.authManager = authManager;
  }

  public OkResponse execute(Request req) throws IOException {
    RegisterRequest request = (RegisterRequest) req;
    authManager.register(request.user);
    return new OkResponse();
  }
}
