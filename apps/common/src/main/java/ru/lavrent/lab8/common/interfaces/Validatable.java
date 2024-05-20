package ru.lavrent.lab8.common.interfaces;

import ru.lavrent.lab8.common.exceptions.ValidationException;

public interface Validatable {
  void validate() throws ValidationException;
}
