package ru.lavrent.lab7.common.network.requests;

import ru.lavrent.lab7.common.models.DryLabWork;
import ru.lavrent.lab7.common.utils.Commands;

public class AddRequest extends Request {
  public final DryLabWork dryLabWork;

  public AddRequest(DryLabWork dryLabWork) {
    super(Commands.ADD);
    this.dryLabWork = dryLabWork;
  }
}