package ru.lavrent.lab7.server.commands;

import ru.lavrent.lab7.common.utils.AutoToString;
import ru.lavrent.lab7.server.interfaces.IServerCommand;

public abstract class Command extends AutoToString implements IServerCommand {
  private String name;

  /**
   * initialize the command
   * 
   * @param name the command's alias
   */
  public Command(String name) {
    this.name = name;
  }

  public String getName() {
    return name;
  }
}
