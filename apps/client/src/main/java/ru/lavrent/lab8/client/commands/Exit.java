package ru.lavrent.lab8.client.commands;

import ru.lavrent.lab8.client.models.forms.Form;
import ru.lavrent.lab8.client.utils.Reader;
import ru.lavrent.lab8.common.utils.Commands;

import java.util.Objects;

public class Exit extends Command {
  private Reader reader;

  public Exit(Reader reader) {
    super(Commands.EXIT, "quit the application");
    this.reader = Objects.requireNonNull(reader);

  }

  public void execute(String[] args) {
    boolean wantsToQuit = Form.getYN("quit? (y/N) ", reader.getScanner(), false);
    if (!wantsToQuit)
      return;
    reader.halt();
  }
}
