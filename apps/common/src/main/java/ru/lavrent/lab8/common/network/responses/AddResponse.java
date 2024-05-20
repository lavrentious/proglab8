package ru.lavrent.lab8.common.network.responses;

import ru.lavrent.lab8.common.models.LabWork;
import ru.lavrent.lab8.common.utils.Commands;

public class AddResponse extends Response {
  public final LabWork labWork;

  public AddResponse(LabWork labWork) {
    super(Commands.ADD);
    this.labWork = labWork;
  }
}
