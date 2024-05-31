package ru.lavrent.lab8.client.gui.controllers;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import ru.lavrent.lab8.client.Main;
import ru.lavrent.lab8.client.gui.utils.FilterStorage;
import ru.lavrent.lab8.client.utils.GlobalStorage;
import ru.lavrent.lab8.client.utils.L10nService;
import ru.lavrent.lab8.common.models.LabWork;

import java.time.ZonedDateTime;
import java.util.Optional;

public class FiltersDialog {

  @FXML
  private Spinner<Integer> authorId;

  @FXML
  private CheckBox authorIdEnabled;

  @FXML
  private Label authorIdLabel;

  @FXML
  private DatePicker createdAtBegin;

  @FXML
  private CheckBox createdAtEnabled;

  @FXML
  private DatePicker createdAtEnd;

  @FXML
  private Label createdAtEndLabel;

  @FXML
  private Label createdAtLabel;

  @FXML
  private Label createdAtStartLabel;

  @FXML
  private CheckBox nameEnabled;

  @FXML
  private TextField nameField;

  @FXML
  private Label nameLabel;

  @FXML
  private Button okButton;

  @FXML
  private Button resetButton;

  @FXML
  void onOk(ActionEvent event) {
    System.out.println("applying filters");
    var filters = FilterStorage.getInstance();
    filters.setName(nameEnabled.isSelected() ? nameField.getText() : null);
    filters.setAuthorId(authorIdEnabled.isSelected() ? (long) authorId.getValue() : null);
    filters.setCreatedAtBegin(createdAtEnabled.isSelected() ? createdAtBegin.getValue() : null);
    filters.setCreatedAtEnd(createdAtEnabled.isSelected() ? createdAtEnd.getValue() : null);
    filters.apply();
    this.closeDialog();
  }

  @FXML
  void onReset(ActionEvent event) {
    var filters = FilterStorage.getInstance();
    filters.setName(null);
    filters.setAuthorId(null);
    filters.setCreatedAtBegin(null);
    filters.setCreatedAtEnd(null);
    filters.apply();
    this.closeDialog();
  }

  static void launch() {
    FXMLLoader loader = new FXMLLoader(SettingsDialog.class.getResource("/filters.fxml"));
    Parent root = Main.loadFxml(loader);

    Stage dialogStage = new Stage();
    dialogStage.setScene(new Scene(root));
    L10nService l10nService = L10nService.getInstance();
    dialogStage.setTitle(l10nService.getString("Filters"));
    l10nService.getObservableLocale()
        .addListener((observable, oldValue, newValue) -> dialogStage.setTitle(l10nService.getString("Filters")));
    dialogStage.showAndWait();
  }

  private void closeDialog() {
    Stage stage = (Stage) this.resetButton.getScene().getWindow();
    stage.close();
  }

  @FXML
  void initialize() {
    this.applyLocale();
    L10nService.getInstance().getObservableLocale().addListener((observable, oldValue, newValue) -> {
      this.applyLocale();
    });

    // init checkboxes
    authorIdEnabled.selectedProperty().addListener(new ChangeListener<Boolean>() {
      @Override
      public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
        authorId.setDisable(!newValue);
      }
    });

    nameEnabled.selectedProperty().addListener(new ChangeListener<Boolean>() {
      @Override
      public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
        nameField.setDisable(!newValue);
      }
    });

    createdAtEnabled.selectedProperty().addListener(new ChangeListener<Boolean>() {
      @Override
      public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
        createdAtBegin.setDisable(!newValue);
        createdAtEnd.setDisable(!newValue);
      }
    });

    // init spinner
    this.authorId.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, Integer.MAX_VALUE, 1));

    // init date pickers
    Optional<ZonedDateTime> maxDate = GlobalStorage.getInstance().getObservableLabWorks().stream()
        .map(LabWork::getCreationDate)
        .max(ZonedDateTime::compareTo);
    if (maxDate.isPresent()) {
      this.createdAtEnd.setValue(maxDate.get().toLocalDate().plusDays(1));
    }

    Optional<ZonedDateTime> minDate = GlobalStorage.getInstance().getObservableLabWorks().stream()
        .map(LabWork::getCreationDate)
        .min(ZonedDateTime::compareTo);
    if (minDate.isPresent()) {
      this.createdAtBegin.setValue(minDate.get().toLocalDate().minusDays(1));
    }

    // init values
    var filters = FilterStorage.getInstance();
    if (filters.getName() != null) {
      this.nameField.setText(filters.getName());
    }
    if (filters.getAuthorId() != null) {
      this.authorId.getValueFactory().setValue(Math.toIntExact(filters.getAuthorId()));
    }
    if (filters.getCreatedAtBegin() != null) {
      this.createdAtBegin
          .setValue(filters.getCreatedAtBegin());
    }
    if (filters.getCreatedAtEnd() != null) {
      this.createdAtEnd.setValue(filters.getCreatedAtEnd());
    }
    this.nameEnabled.setSelected(filters.getName() != null);
    this.authorIdEnabled.setSelected(filters.getAuthorId() != null);
    this.createdAtEnabled.setSelected(filters.getCreatedAtBegin() != null || filters.getCreatedAtEnd() != null);
  }

  private void applyLocale() {
    L10nService l10n = L10nService.getInstance();
    this.authorIdLabel.setText(l10n.getString("AuthorIdLabel"));
    this.createdAtLabel.setText(l10n.getString("CreatedAtLabel"));
    this.createdAtStartLabel.setText(l10n.getString("CreatedAtStartLabel"));
    this.createdAtEndLabel.setText(l10n.getString("CreatedAtEndLabel"));
    this.nameLabel.setText(l10n.getString("NameLabel"));
    this.okButton.setText(l10n.getString("OK"));
    this.resetButton.setText(l10n.getString("Reset"));
    this.authorId.setPromptText(l10n.getString("AuthorIdPrompt"));
    this.nameField.setPromptText(l10n.getString("NamePrompt"));
  }
}
