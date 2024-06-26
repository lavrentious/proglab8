package ru.lavrent.lab8.server.commands;

import ru.lavrent.lab8.common.network.requests.Request;
import ru.lavrent.lab8.common.network.responses.ShowResponse;
import ru.lavrent.lab8.common.utils.Commands;
import ru.lavrent.lab8.server.managers.CollectionManager;

import java.io.IOException;

public class Show extends Command {
  private CollectionManager collectionManager;

  public Show(CollectionManager collectionManager) {
    super(Commands.SHOW);
    this.collectionManager = collectionManager;
  }

  public ShowResponse execute(Request req) throws IOException {
    return new ShowResponse(collectionManager.getList());
  }
}
