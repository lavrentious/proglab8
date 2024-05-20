package ru.lavrent.lab8.server.commands;

import ru.lavrent.lab8.common.exceptions.InsufficientPermissionsException;
import ru.lavrent.lab8.common.exceptions.NotFoundException;
import ru.lavrent.lab8.common.models.LabWork;
import ru.lavrent.lab8.common.network.requests.Request;
import ru.lavrent.lab8.common.network.requests.UpdateByIdRequest;
import ru.lavrent.lab8.common.network.responses.AddResponse;
import ru.lavrent.lab8.common.utils.Commands;
import ru.lavrent.lab8.server.managers.CollectionManager;

import java.io.IOException;

public class UpdateById extends Command {
  private CollectionManager collectionManager;

  public UpdateById(CollectionManager collectionManager) {
    super(Commands.UPDATE_BY_ID);
    this.collectionManager = collectionManager;
  }

  public AddResponse execute(Request req) throws IOException {
    UpdateByIdRequest request = (UpdateByIdRequest) req;
    LabWork labWork = collectionManager.getById(request.id);
    if (labWork == null) {
      throw new NotFoundException("labwork id=%d not found".formatted(request.id));
    }
    if (labWork.getAuthorId() != request.getUser().getId()) {
      throw new InsufficientPermissionsException(
          "user id=%d is not author of labwork id=%d".formatted(request.getUser().getId(), labWork.getId()));
    }
    LabWork updatedLabWork = collectionManager.updateById(request.id, request.dryLabWork);
    var res = new AddResponse(updatedLabWork);
    return res;
  }
}
