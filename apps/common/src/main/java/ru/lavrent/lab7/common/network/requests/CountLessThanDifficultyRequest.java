package ru.lavrent.lab7.common.network.requests;

import ru.lavrent.lab7.common.models.Difficulty;
import ru.lavrent.lab7.common.utils.Commands;

public class CountLessThanDifficultyRequest extends Request {
  public final Difficulty difficulty;

  public CountLessThanDifficultyRequest(Difficulty difficulty) {
    super(Commands.COUNT_LESS_THAN_DIFFICULTY);
    this.difficulty = difficulty;
  }
}