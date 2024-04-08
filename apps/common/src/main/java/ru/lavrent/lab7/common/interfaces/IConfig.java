package ru.lavrent.lab7.common.interfaces;

import ru.lavrent.lab7.common.exceptions.InvalidConfigException;

public interface IConfig {
  public void onLoad();

  public void validate() throws InvalidConfigException;
}
