package ru.lavrent.lab7.common.interfaces;

import ru.lavrent.lab7.common.exceptions.ValidationException;

public interface Validatable {
  void validate() throws ValidationException;
}
