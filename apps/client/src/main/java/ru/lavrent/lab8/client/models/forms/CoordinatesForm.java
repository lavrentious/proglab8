package ru.lavrent.lab8.client.models.forms;

import ru.lavrent.lab8.common.exceptions.ValidationException;
import ru.lavrent.lab8.common.models.Coordinates;

import java.util.Scanner;

public class CoordinatesForm extends Form<Coordinates> {
  public Coordinates run(Scanner scanner) throws ValidationException {
    System.out.println("create coordinates");
    Long x = getLong("x: ", scanner, Coordinates::validateX);
    Integer y = getInt("y: ", scanner, Coordinates::validateY);
    return new Coordinates(x, y);
  }
}