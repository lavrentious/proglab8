package ru.lavrent.lab8.client.utils;

import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.layout.Region;

public class GlobalExceptionHandler implements Thread.UncaughtExceptionHandler {

  @Override
  public void uncaughtException(Thread t, Throwable e) {
    e.printStackTrace();
    Platform.runLater(() -> {
      Throwable cause = e.getCause();
      while (cause != null && cause.getCause() != null) {
        System.out.println(cause);
        cause = cause.getCause();
      }
      Alert alert = new Alert(AlertType.ERROR,
          cause != null ? cause.getMessage() : L10nService.getInstance().getString("UnknownError"), ButtonType.OK);
      alert.setHeaderText(
          cause != null ? cause.getClass().getName() : L10nService.getInstance().getString("UnknownError"));
      alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
      alert.showAndWait();
    });
  }
}
