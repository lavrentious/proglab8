package ru.lavrent.lab8.common.models;

import ru.lavrent.lab8.common.exceptions.ValidationException;

import java.time.ZonedDateTime;

public class LabWork extends DryLabWork implements Comparable<LabWork> {
  private Long id; // Поле не может быть null, Значение поля должно быть больше 0, Значение этого
  // поля должно быть уникальным, Значение этого поля должно генерироваться
  // автоматически
  private ZonedDateTime creationDate; // Поле не может быть null, Значение этого поля должно генерироваться
  // автоматически
  private long authorId;

  public LabWork(long id, String name, Coordinates coordinates, java.time.ZonedDateTime creationDate, Long minimalPoint,
      Difficulty difficulty, Discipline discipline, long authorId) throws ValidationException {
    super(name, coordinates, minimalPoint, difficulty, discipline);
    this.id = id;
    this.authorId = authorId;
    this.creationDate = creationDate;
  }

  public LabWork(DryLabWork labWork, long id, ZonedDateTime creationDate, long authorId) {
    super(labWork.name, labWork.coordinates, labWork.minimalPoint, labWork.difficulty,
        labWork.discipline);
    this.id = id;
    this.creationDate = creationDate;
    this.authorId = authorId;
    validateId(id);
    validateCreationDate(creationDate);
  }

  public long getId() {
    return id;
  }

  public void setId(long id) {
    this.id = id;
  }

  public ZonedDateTime getCreationDate() {
    return creationDate;
  }

  @Override
  public void validate() throws ValidationException {
    super.validate();
  }

  public static void validateId(Long id) throws ValidationException {
    if (id == null) {
      throw new ValidationException("id must be not null");
    }
    if (id <= 0) {
      throw new ValidationException("id must be greater than 0");
    }
  }

  public static void validateCreationDate(ZonedDateTime creationDate) throws ValidationException {
    if (creationDate == null) {
      throw new ValidationException("creationDate must be not null");
    }
  }

  public long getAuthorId() {
    return authorId;
  }

  @Override
  public int compareTo(LabWork o) {
    return Long.compare(getId(), o.getId()); // FIXME: what do i compare by??
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null || getClass() != obj.getClass()) {
      return false;
    }
    LabWork that = (LabWork) obj;
    return id.equals(that.getId()) && authorId == that.getAuthorId() && creationDate.equals(that.getCreationDate())
        && difficulty.equals(that.getDifficulty()) && coordinates.equals(that.getCoordinates())
        && discipline.equals(that.getDiscipline()) && minimalPoint.equals(that.getMinimalPoint())
        && minimalPoint.equals(that.getMinimalPoint());
  }

}
