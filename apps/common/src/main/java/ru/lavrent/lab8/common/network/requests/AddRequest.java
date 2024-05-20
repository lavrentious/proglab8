package ru.lavrent.lab8.common.network.requests;

import ru.lavrent.lab8.common.models.DryLabWork;
import ru.lavrent.lab8.common.utils.Commands;

public class AddRequest extends Request {
  public final DryLabWork dryLabWork;

  public AddRequest(DryLabWork dryLabWork) {
    super(Commands.ADD);
    this.dryLabWork = dryLabWork;
  }
}