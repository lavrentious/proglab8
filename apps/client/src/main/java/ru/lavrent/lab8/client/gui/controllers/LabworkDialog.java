package ru.lavrent.lab8.client.gui.controllers;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.TextField;
import javafx.scene.layout.Region;
import javafx.stage.Stage;
import ru.lavrent.lab8.client.Main;
import ru.lavrent.lab8.client.models.forms.Form;
import ru.lavrent.lab8.client.services.LabWorkService;
import ru.lavrent.lab8.client.utils.L10nService;
import ru.lavrent.lab8.common.models.Coordinates;
import ru.lavrent.lab8.common.models.Difficulty;
import ru.lavrent.lab8.common.models.Discipline;
import ru.lavrent.lab8.common.models.DryLabWork;
import ru.lavrent.lab8.common.models.LabWork;

import java.util.Arrays;
import java.util.List;

public class LabworkDialog {
  private LabWork existingLabWork;

  @FXML
  private Button cancelButton;

  @FXML
  private ComboBox<String> difficultyComboBox;

  @FXML
  private Label difficultylabel;

  @FXML
  private TextField disciplineNameField;

  @FXML
  private Label disciplineNameLabel;

  @FXML
  private Spinner<Integer> labsCountField;

  @FXML
  private Label labsCountLabel;

  @FXML
  private TextField labworkNameField;

  @FXML
  private Label labworkNameLabel;

  @FXML
  private Spinner<Integer> lectureHoursField;

  @FXML
  private Label lectureHoursLabel;

  @FXML
  private Spinner<Integer> minimalPointField;

  @FXML
  private Label minimalPointLabel;

  @FXML
  private Spinner<Integer> practiceHoursField;

  @FXML
  private Label practiceHoursLabel;

  @FXML
  private Button submitButton;

  @FXML
  private Spinner<Integer> xField;

  @FXML
  private Label xLabel;

  @FXML
  private Spinner<Integer> yField;

  @FXML
  private Label yLabel;

  private Difficulty difficulty;

  @FXML
  void initialize() {
    System.out.println("initializing labwork dialog");
    List<String> difficultyKeys = Arrays.asList(Difficulty.values()).stream().map(e -> e.toString()).toList();
    this.difficultyComboBox.setItems(FXCollections.observableArrayList(difficultyKeys));
    this.difficultyComboBox.getSelectionModel().selectedItemProperty()
        .addListener((observableValue, oldValue, newValue) -> {
          this.difficulty = Form.getEnumValueByString(Difficulty.class, newValue);
        });

    this.minimalPointField.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 100, 1));
    this.xField
        .setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(Integer.MIN_VALUE, Integer.MAX_VALUE, 1));
    this.yField.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(-497, Integer.MAX_VALUE, 1));
    this.labsCountField.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, Integer.MAX_VALUE, 1));
    this.lectureHoursField.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, Integer.MAX_VALUE, 1));
    this.practiceHoursField
        .setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, Integer.MAX_VALUE, 1));

    this.populateFields();
  }

  @FXML
  void closeDialog() {
    Stage stage = (Stage) this.cancelButton.getScene().getWindow();
    stage.close();
  }

  private DryLabWork getLabWork() {
    String name = this.labworkNameField.getText();
    Long x = Long.valueOf(this.xField.getValue());
    Integer y = this.yField.getValue();
    Long minimalPoint = Long.valueOf(this.minimalPointField.getValue());
    String disciplineName = this.disciplineNameField.getText();
    Long lectureHours = Long.valueOf(this.lectureHoursField.getValue());
    Long practiceHours = Long.valueOf(this.practiceHoursField.getValue());
    Integer labsCount = this.labsCountField.getValue();

    Coordinates coordinates = new Coordinates(x, y);
    Discipline discipline = new Discipline(disciplineName, lectureHours, practiceHours, labsCount);
    return new DryLabWork(name, coordinates, minimalPoint, difficulty, discipline);
  }

  @FXML
  void createOrUpdate() {
    var labWork = this.getLabWork();
    Task<Void> authTask = new Task<Void>() {
      @Override
      protected Void call() throws Exception {
        LabworkDialog.this.cancelButton.setDisable(true);
        LabworkDialog.this.submitButton.setDisable(true);
        labWork.validate();
        if (LabworkDialog.this.existingLabWork != null) {
          LabWorkService.getInstance().updateLabWork(LabworkDialog.this.existingLabWork.getId(),
              LabworkDialog.this.getLabWork());
          Platform.runLater(() -> {
            Alert alert = new Alert(AlertType.INFORMATION, L10nService.getInstance().getString("LabworkUpdated"),
                ButtonType.OK);
            alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
            alert.showAndWait();
          });
        } else {
          LabWorkService.getInstance().create(labWork);
          Platform.runLater(() -> {
            Alert alert = new Alert(AlertType.INFORMATION, L10nService.getInstance().getString("LabworkCreated"),
                ButtonType.OK);
            alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
            alert.showAndWait();
          });
        }
        return null;
      }
    };

    authTask.setOnSucceeded((WorkerStateEvent event) -> {
      LabworkDialog.this.cancelButton.setDisable(false);
      LabworkDialog.this.submitButton.setDisable(false);
    });

    authTask.setOnFailed((WorkerStateEvent event) -> {
      LabworkDialog.this.cancelButton.setDisable(false);
      LabworkDialog.this.submitButton.setDisable(false);
    });

    Thread thread = new Thread(authTask);
    thread.setDaemon(true);
    thread.start();
    this.closeDialog();
  }

  static LabworkDialog launch(LabWork existingLabWork) {
    System.out.println("loading labwork dialog");
    FXMLLoader loader = new FXMLLoader(SettingsDialog.class.getResource("/create.fxml"));
    Parent root = Main.loadFxml(loader);

    LabworkDialog controller = loader.getController();
    controller.setExistingLabWork(existingLabWork);
    System.out.println(controller.getExistingLabWork());
    System.out.println(controller.toString());

    Stage dialogStage = new Stage();
    dialogStage.setScene(new Scene(root));
    dialogStage.setTitle(L10nService.getInstance().getString("CreateLabWork"));
    dialogStage.show();

    return controller;
  }

  public void setExistingLabWork(LabWork existingLabWork) {
    this.existingLabWork = existingLabWork;
    this.populateFields();
  }

  private void populateFields() {
    if (this.existingLabWork != null) {
      this.difficulty = existingLabWork.getDifficulty();
      this.difficultyComboBox.setValue(existingLabWork.getDifficulty().toString());
      this.minimalPointField.getValueFactory().setValue((int) existingLabWork.getMinimalPoint());
      this.xField.getValueFactory().setValue((int) existingLabWork.getCoordinates().getX());
      this.yField.getValueFactory().setValue((int) existingLabWork.getCoordinates().getY());
      this.labsCountField.getValueFactory().setValue((int) existingLabWork.getDiscipline().getLabsCount());
      this.lectureHoursField.getValueFactory().setValue((int) existingLabWork.getDiscipline().getLectureHours());
      this.practiceHoursField.getValueFactory()
          .setValue(Math.toIntExact(existingLabWork.getDiscipline().getPracticeHours()));
      this.labworkNameField.setText(existingLabWork.getName());
      this.disciplineNameField.setText(existingLabWork.getDiscipline().getName());
    } else {
      this.difficulty = Difficulty.VERY_EASY;
      this.difficultyComboBox.setValue(this.difficulty.toString());
      this.minimalPointField.getValueFactory().setValue(1);
      this.xField.getValueFactory().setValue(1);
      this.yField.getValueFactory().setValue(1);
      this.labsCountField.getValueFactory().setValue(1);
      this.lectureHoursField.getValueFactory().setValue(1);
      this.practiceHoursField.getValueFactory()
          .setValue(1);
      this.labworkNameField.setText("");
      this.disciplineNameField.setText("");
    }
  }

  public LabWork getExistingLabWork() {
    return existingLabWork;
  }
}
