package ru.lavrent.lab8.common.interfaces;

import ru.lavrent.lab8.common.exceptions.InvalidConfigException;

public interface IConfig {
  public void onLoad();

  public void validate() throws InvalidConfigException;
}
