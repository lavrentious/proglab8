package ru.lavrent.lab8.server.commands;

import ru.lavrent.lab8.common.network.requests.Request;
import ru.lavrent.lab8.common.network.responses.InfoResponse;
import ru.lavrent.lab8.common.utils.Commands;
import ru.lavrent.lab8.server.managers.CollectionManager;

import java.io.IOException;

public class Info extends Command {
  private CollectionManager collectionManager;

  public Info(CollectionManager collectionManager) {
    super(Commands.INFO);
    this.collectionManager = collectionManager;
  }

  public InfoResponse execute(Request req) throws IOException {
    return new InfoResponse(collectionManager.getType(), collectionManager.getCreatedAt(),
        collectionManager.getUpdatedAt());
  }
}
