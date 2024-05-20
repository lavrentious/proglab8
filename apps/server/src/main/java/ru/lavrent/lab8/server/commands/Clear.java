package ru.lavrent.lab8.server.commands;

import ru.lavrent.lab8.common.network.requests.Request;
import ru.lavrent.lab8.common.network.responses.OkResponse;
import ru.lavrent.lab8.common.utils.Commands;
import ru.lavrent.lab8.server.managers.CollectionManager;

import java.io.IOException;

public class Clear extends Command {
  private CollectionManager collectionManager;

  public Clear(CollectionManager collectionManager) {
    super(Commands.CLEAR);
    this.collectionManager = collectionManager;
  }

  public OkResponse execute(Request req) throws IOException {
    collectionManager.clear(req.getUser());
    return new OkResponse();
  }
}
