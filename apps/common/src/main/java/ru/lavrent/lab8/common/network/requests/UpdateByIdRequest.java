package ru.lavrent.lab8.common.network.requests;

import ru.lavrent.lab8.common.models.DryLabWork;
import ru.lavrent.lab8.common.utils.Commands;

public class UpdateByIdRequest extends Request {
  public final DryLabWork dryLabWork;
  public final long id;

  public UpdateByIdRequest(long id, DryLabWork dryLabWork) {
    super(Commands.UPDATE_BY_ID);
    this.dryLabWork = dryLabWork;
    this.id = id;
  }
}