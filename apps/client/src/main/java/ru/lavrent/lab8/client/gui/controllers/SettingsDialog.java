package ru.lavrent.lab8.client.gui.controllers;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import ru.lavrent.lab8.client.Main;
import ru.lavrent.lab8.client.utils.L10nService;

import java.util.ResourceBundle;

public class SettingsDialog extends Dialog<Void> {
  private String newLocale;

  @FXML
  private Label localeLabel;

  @FXML
  private Button cancelButton;

  @FXML
  private ComboBox<String> localeComboBox;

  @FXML
  private Button saveButton;

  private void closeDialog() {
    Stage stage = (Stage) this.cancelButton.getScene().getWindow();
    stage.close();
  }

  @FXML
  void closeSettings(ActionEvent event) {
    this.closeDialog();
  }

  @FXML
  void saveSettings(ActionEvent event) {
    if (this.newLocale != null) {
      Platform.runLater(() -> {
        L10nService.getInstance().setLocale(this.newLocale);
      });
    }
    this.closeDialog();
  }

  @FXML
  void initialize() {
    var locales = L10nService.getInstance().getAvailableLocales().keySet();
    this.localeComboBox.setItems(FXCollections.observableArrayList(locales));
    this.localeComboBox.setValue(L10nService.getInstance().getLocaleName());
    this.localeComboBox.getSelectionModel().selectedItemProperty()
        .addListener((observableValue, oldValue, newValue) -> {
          System.out.println("locale selected " + newValue);
          this.newLocale = newValue;
        });

    applyLocale(L10nService.getInstance().getBundle());
    L10nService.getInstance().getObservableLocale().addListener((observable, oldValue, newValue) -> {
      this.applyLocale(L10nService.getInstance().getBundle());
    });
  }

  private void applyLocale(ResourceBundle bundle) {
    this.localeLabel.setText(bundle.getString("LocaleLabel"));
    this.saveButton.setText(bundle.getString("Save"));
    this.cancelButton.setText(bundle.getString("Cancel"));
  }

  static void launch() {
    FXMLLoader loader = new FXMLLoader(SettingsDialog.class.getResource("/settings.fxml"));
    Parent root = Main.loadFxml(loader);
    // SettingsDialog dialogController = loader.getController();
    Stage dialogStage = new Stage();
    dialogStage.setScene(new Scene(root));
    dialogStage.setTitle(L10nService.getInstance().getString("Settings"));
    dialogStage.showAndWait();
  }
}
