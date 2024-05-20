package ru.lavrent.lab8.server.interfaces;

import ru.lavrent.lab8.common.network.requests.Request;
import ru.lavrent.lab8.common.network.responses.Response;

import java.io.IOException;

public interface IServerCommand {
  public Response execute(Request request) throws IOException;
}
