package ru.lavrent.lab7.common.interfaces;

import ru.lavrent.lab7.common.exceptions.ValidationException;

@FunctionalInterface
public interface ValidatorFn<T> {
  void validate(T t) throws ValidationException;
}