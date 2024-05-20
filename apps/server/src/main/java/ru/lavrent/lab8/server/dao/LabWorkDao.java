package ru.lavrent.lab8.server.dao;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import ru.lavrent.lab8.common.models.Difficulty;
import ru.lavrent.lab8.common.models.LabWork;

import java.io.Serializable;
import java.time.ZonedDateTime;

@Entity
@Table(name = "labworks")
public class LabWorkDao implements Serializable {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @NotNull
  @NotBlank
  private String name;

  @NotNull
  @OneToOne(cascade = CascadeType.ALL)
  @JoinColumn(name = "coordinates_id", referencedColumnName = "id")
  private CoordinatesDao coordinates;

  @NotNull
  private ZonedDateTime creationDate;

  @NotNull
  private Long minimalPoint;

  @NotNull
  private Difficulty difficulty;

  @NotNull
  @OneToOne(cascade = CascadeType.ALL)
  @JoinColumn(name = "discipline_id", referencedColumnName = "id")
  private DisciplineDao discipline;

  @NotNull
  @ManyToOne(cascade = CascadeType.ALL)
  @JoinColumn(name = "author_id", referencedColumnName = "id")
  private UserDao author;

  protected LabWorkDao() {
  }

  public LabWorkDao(LabWork labWork) {
    this.name = labWork.getName();
    this.coordinates = new CoordinatesDao(labWork.getCoordinates());
    this.creationDate = labWork.getCreationDate();
    this.minimalPoint = labWork.getMinimalPoint();
    this.difficulty = labWork.getDifficulty();
    this.discipline = new DisciplineDao(labWork.getDiscipline());
  }

  public Long getId() {
    return id;
  }

  @PrePersist
  protected void onCreate() {
    creationDate = ZonedDateTime.now();
  }

  public void setAuthor(UserDao author) {
    this.author = author;
  }

  public LabWork toLabWork() {
    return new LabWork(id, name, coordinates.toCoordinates(), creationDate, minimalPoint, difficulty,
        discipline.todDiscipline(), author.getId());
  }

  public void setCoordinates(CoordinatesDao coordinates) {
    this.coordinates = coordinates;
  }

  public void setCreationDate(ZonedDateTime creationDate) {
    this.creationDate = creationDate;
  }

  public void setDifficulty(Difficulty difficulty) {
    this.difficulty = difficulty;
  }

  public void setDiscipline(DisciplineDao discipline) {
    this.discipline = discipline;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public void setMinimalPoint(Long minimalPoint) {
    this.minimalPoint = minimalPoint;
  }

  public void setName(String name) {
    this.name = name;
  }
}
