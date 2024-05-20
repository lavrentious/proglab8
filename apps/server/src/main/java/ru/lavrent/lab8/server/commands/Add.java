package ru.lavrent.lab8.server.commands;

import ru.lavrent.lab8.common.models.LabWork;
import ru.lavrent.lab8.common.network.requests.AddRequest;
import ru.lavrent.lab8.common.network.requests.Request;
import ru.lavrent.lab8.common.network.responses.AddResponse;
import ru.lavrent.lab8.common.utils.Commands;
import ru.lavrent.lab8.server.managers.CollectionManager;

import java.io.IOException;

public class Add extends Command {
  private CollectionManager collectionManager;

  public Add(CollectionManager collectionManager) {
    super(Commands.ADD);
    this.collectionManager = collectionManager;
  }

  public AddResponse execute(Request req) throws IOException {
    AddRequest request = (AddRequest) req;
    LabWork labWork = collectionManager.add(request.dryLabWork, req.getUser());
    var res = new AddResponse(labWork);
    return res;
  }
}
