package ru.lavrent.lab7.server.dao;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import ru.lavrent.lab7.common.models.Coordinates;

import java.io.Serializable;

@Entity
@Table(name = "coordinates")
public class CoordinatesDao implements Serializable {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @NotNull
  private long x; // Поле не может быть null

  @NotNull
  @Min(value = -498)
  private int y; // Значение поля должно быть больше -498, Поле не может быть null

  @OneToOne(mappedBy = "coordinates")
  private LabWorkDao labWork;

  public CoordinatesDao(Coordinates coordinates) {
    this.x = coordinates.getX();
    this.y = coordinates.getY();
  }

  protected CoordinatesDao() {
  }

  public Coordinates toCoordinates() {
    return new Coordinates(x, y);
  }
}
