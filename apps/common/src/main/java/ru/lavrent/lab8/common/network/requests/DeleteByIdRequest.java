package ru.lavrent.lab8.common.network.requests;

import ru.lavrent.lab8.common.utils.Commands;

public class DeleteByIdRequest extends Request {
  public final long id;

  public DeleteByIdRequest(long id) {
    super(Commands.DELETE_BY_ID);
    this.id = id;
  }
}