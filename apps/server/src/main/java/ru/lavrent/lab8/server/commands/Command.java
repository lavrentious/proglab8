package ru.lavrent.lab8.server.commands;

import ru.lavrent.lab8.common.utils.AutoToString;
import ru.lavrent.lab8.server.interfaces.IServerCommand;

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
