package ru.lavrent.lab8.common.interfaces;

import ru.lavrent.lab8.common.exceptions.ValidationException;

@FunctionalInterface
public interface ValidatorFn<T> {
  void validate(T t) throws ValidationException;
}