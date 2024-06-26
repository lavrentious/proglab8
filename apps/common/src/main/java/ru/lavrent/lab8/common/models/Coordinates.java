package ru.lavrent.lab8.common.models;

import ru.lavrent.lab8.common.exceptions.ValidationException;
import ru.lavrent.lab8.common.utils.Entity;

import java.util.Objects;

public class Coordinates extends Entity {
  private Long x; // Поле не может быть null
  private Integer y; // Значение поля должно быть больше -498, Поле не может быть null

  public Coordinates(Long x, Integer y) throws ValidationException {
    this.x = x;
    this.y = y;
    validate();
  }

  public long getX() {
    return x;
  }

  public int getY() {
    return y;
  }

  public static void validateX(Long x) throws ValidationException {
    if (x == null) {
      throw new ValidationException("x must not be null");
    }
  }

  public static void validateY(Integer y) throws ValidationException {
    if (y == null) {
      throw new ValidationException("y must not be null");
    }
    if (y <= -498) {
      throw new ValidationException("y must be > -498");
    }
  }

  @Override
  public void validate() throws ValidationException {
    validateX(x);
    validateY(y);
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null || getClass() != obj.getClass()) {
      return false;
    }
    Coordinates that = (Coordinates) obj;
    return x.equals(that.x) && y.equals(that.y);
  }

  @Override
  public int hashCode() {
    return Objects.hash(x, y);
  }
}