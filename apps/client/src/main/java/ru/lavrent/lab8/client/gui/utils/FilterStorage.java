package ru.lavrent.lab8.client.gui.utils;

import com.google.common.base.Predicate;
import ru.lavrent.lab8.client.utils.GlobalStorage;
import ru.lavrent.lab8.common.models.LabWork;

import javax.annotation.Nonnull;

import java.time.LocalDate;

public class FilterStorage {
  private static FilterStorage instance;

  private String name;
  private Long authorId;
  private LocalDate createdAtBegin;
  private LocalDate createdAtEnd;

  static public synchronized FilterStorage getInstance() {
    if (instance == null) {
      instance = new FilterStorage();
    }
    return instance;
  }

  public void apply() {
    GlobalStorage.getInstance().setFiltersPredicate(new Predicate<LabWork>() {
      public boolean apply(@Nonnull LabWork t) {
        if (name != null && !(t.getName().toLowerCase().contains(name.toLowerCase()))) {
          return false;
        }
        if (authorId != null && !(t.getAuthorId() == authorId)) {
          return false;
        }
        if (createdAtBegin != null && !(t.getCreationDate().toLocalDate().isAfter(createdAtBegin))) {
          return false;
        }
        if (createdAtEnd != null && !(t.getCreationDate().toLocalDate().isBefore(createdAtEnd))) {
          return false;
        }
        return true;
      }
    });
  }

  public void setAuthorId(Long authorId) {
    this.authorId = authorId;
  }

  public void setCreatedAtBegin(LocalDate createdAtBegin) {
    this.createdAtBegin = createdAtBegin;
  }

  public void setCreatedAtEnd(LocalDate createdAtEnd) {
    this.createdAtEnd = createdAtEnd;
  }

  public static void setInstance(FilterStorage instance) {
    FilterStorage.instance = instance;
  }

  public void setName(String name) {
    this.name = name;
  }

  public Long getAuthorId() {
    return authorId;
  }

  public LocalDate getCreatedAtBegin() {
    return createdAtBegin;
  }

  public LocalDate getCreatedAtEnd() {
    return createdAtEnd;
  }

  public String getName() {
    return name;
  }
}
