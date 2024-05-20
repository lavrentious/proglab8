package ru.lavrent.lab8.client.models.forms;

import ru.lavrent.lab8.common.exceptions.ValidationException;
import ru.lavrent.lab8.common.models.Coordinates;
import ru.lavrent.lab8.common.models.Difficulty;
import ru.lavrent.lab8.common.models.Discipline;
import ru.lavrent.lab8.common.models.DryLabWork;
import ru.lavrent.lab8.common.models.LabWork;

import java.util.Scanner;

public class LabWorkForm extends Form<DryLabWork> {
  public DryLabWork run(Scanner scanner) throws ValidationException {
    System.out.println("create labWork");
    String name = getString("labwork name: ", scanner, LabWork::validateName);

    Coordinates coordinates;
    try {
      coordinates = (new CoordinatesForm()).run(scanner);
    } catch (ValidationException e) {
      System.err.println("coordinates' validation failed");
      throw new ValidationException("incorrect coordinates");
    }

    long minimalPoint = getLong("minimal point: ", scanner, LabWork::validateMinimalPoint);
    Difficulty difficulty = getEnumValue("difficulty: ", Difficulty.class, scanner);

    Discipline discipline = (new DisciplineForm()).run(scanner);

    DryLabWork labWork = new DryLabWork(name,
        coordinates, minimalPoint, difficulty, discipline);
    return labWork;
  }
}
