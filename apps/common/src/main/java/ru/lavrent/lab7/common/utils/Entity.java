package ru.lavrent.lab7.common.utils;

import ru.lavrent.lab7.common.exceptions.ValidationException;
import ru.lavrent.lab7.common.interfaces.Validatable;

import java.io.Serializable;

public abstract class Entity extends AutoToString implements Validatable, Serializable {
  public boolean boolValidate() {
    try {
      validate();
      return true;
    } catch (ValidationException e) {
      return false;
    }
  }
}
