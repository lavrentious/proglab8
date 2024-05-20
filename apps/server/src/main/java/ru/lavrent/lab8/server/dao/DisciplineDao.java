package ru.lavrent.lab8.server.dao;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import ru.lavrent.lab8.common.models.Discipline;

import java.io.Serializable;

@Entity
@Table(name = "disciplines")
public class DisciplineDao implements Serializable {
  public DisciplineDao(Discipline discipline) {
    this.name = discipline.getName();
    this.lectureHours = discipline.getLectureHours();
    this.practiceHours = discipline.getPracticeHours();
    this.labsCount = discipline.getLabsCount();
  }

  protected DisciplineDao() {
  }

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @NotBlank
  @NotNull
  private String name; // Поле не может быть null, Строка не может быть пустой

  @Column
  private long lectureHours;

  @NotNull
  private Long practiceHours; // Поле может быть null

  @NotNull
  private Integer labsCount; // Поле может быть null

  public Discipline todDiscipline() {
    return new Discipline(name, lectureHours, practiceHours, labsCount);
  }
}
