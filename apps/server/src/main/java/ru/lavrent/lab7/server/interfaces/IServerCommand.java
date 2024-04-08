package ru.lavrent.lab7.server.interfaces;

import ru.lavrent.lab7.common.network.requests.Request;
import ru.lavrent.lab7.common.network.responses.Response;

import java.io.IOException;

public interface IServerCommand {
  public Response execute(Request request) throws IOException;
}
