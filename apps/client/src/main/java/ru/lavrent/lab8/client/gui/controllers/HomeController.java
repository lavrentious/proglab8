package ru.lavrent.lab8.client.gui.controllers;

import javafx.application.Platform;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.concurrent.ScheduledService;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.Tab;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import javafx.util.Duration;
import ru.lavrent.lab8.client.Main;
import ru.lavrent.lab8.client.services.AuthService;
import ru.lavrent.lab8.client.services.LabWorkService;
import ru.lavrent.lab8.client.utils.GlobalStorage;
import ru.lavrent.lab8.client.utils.L10nService;
import ru.lavrent.lab8.common.models.LabWork;

import java.util.ResourceBundle;

public class HomeController {
  private LabWorkFetcherService labWorkFetcherService;

  class LabWorkFetcherService extends ScheduledService<Void> {
    @Override
    protected Task<Void> createTask() {
      return new Task<Void>() {
        @Override
        protected Void call() throws Exception {
          isLoading.set(true);
          LabWorkService.getInstance().fetch();
          return null;
        }
      };
    }

    @Override
    protected void succeeded() {
      super.succeeded();
      isLoading.set(false);
    }

    @Override
    protected void failed() {
      super.failed();
      isLoading.set(false);
      Throwable exception = getException();
      if (exception != null) {
        System.out.println("unknown error: " + exception);
      }
    }
  }

  private SimpleBooleanProperty isLoading = new SimpleBooleanProperty(false);
  private SimpleObjectProperty<LabWork> selectedLabWork = new SimpleObjectProperty<>();

  @FXML
  private ProgressIndicator loadingIndicator;

  @FXML
  private Button createButton;

  @FXML
  private Button deleteButton;

  @FXML
  private Button editButton;

  @FXML
  private Button refetchButton;

  @FXML
  private Button settingsButton;

  @FXML
  private Button logoutButton;

  @FXML
  private Label loggedUsername;

  @FXML
  private Tab tableTab;

  @FXML
  private Tab visualizeTab;

  @FXML
  private TableView<LabWork> dataTable;

  @FXML
  private TableColumn<LabWork, Long> idCol;

  @FXML
  private TableColumn<LabWork, Long> authorIdCol;

  @FXML
  private TableColumn<LabWork, Long> coordinatesXCol;

  @FXML
  private TableColumn<LabWork, Integer> coordinatesYCol;

  @FXML
  private TableColumn<LabWork, String> difficultyCol;

  @FXML
  private TableColumn<LabWork, Integer> disciplineLabsCountCol;

  @FXML
  private TableColumn<LabWork, Long> disciplineLectureHoursCol;

  @FXML
  private TableColumn<LabWork, String> disciplineNameCol;

  @FXML
  private TableColumn<LabWork, Long> disciplinePracticeHoursCol;

  @FXML
  private TableColumn<LabWork, Long> minimalPointCol;

  @FXML
  private TableColumn<LabWork, String> nameCol;

  private void handleRowClick(MouseEvent event, TableRow<LabWork> row) {
    if (!row.isEmpty() && event.getButton() == MouseButton.PRIMARY) {
      LabWork clickedRow = row.getItem();
      if (event.getClickCount() == 1) {
        this.selectedLabWork.set(clickedRow);
      } else if (event.getClickCount() == 2
          && this.selectedLabWork.get().getAuthorId() == GlobalStorage.getInstance().getUser().getId()) {
        LabworkDialog.launch(this.selectedLabWork.get());
      }
    }
  }

  @FXML
  public void initialize() {
    var loggedUser = GlobalStorage.getInstance().getUser();

    // initialize locale
    this.applyLocale(L10nService.getInstance().getBundle());
    L10nService.getInstance().getObservableLocale().addListener((observable, oldValue, newValue) -> {
      this.applyLocale(L10nService.getInstance().getBundle());
    });

    this.loggedUsername.setText(GlobalStorage.getInstance().getUser().getUsername());

    // initialize handlers
    isLoading.addListener((observable, oldValue, newValue) -> {
      this.loadingIndicator.setVisible(newValue);
    });
    this.selectedLabWork.addListener((observable, oldValue, newValue) -> {
      this.deleteButton
          .setDisable(newValue == null || newValue.getAuthorId() != GlobalStorage.getInstance().getUser().getId());
      this.editButton
          .setDisable(newValue == null || newValue.getAuthorId() != GlobalStorage.getInstance().getUser().getId());
    });

    // initialize table
    this.dataTable.setRowFactory(tv -> {
      TableRow<LabWork> row = new TableRow<>() {
        @Override
        protected void updateItem(LabWork item, boolean empty) {
          super.updateItem(item, empty);
          if (item == null || empty) {
            setStyle("");
          } else {
            if (item.getAuthorId() == loggedUser.getId()) {
              setStyle("-fx-background-color: #AAFFAA;");
            } else {
              setStyle("");
            }
          }
        }
      };
      row.setOnMouseClicked(event -> handleRowClick(event, row));
      return row;
    });

    idCol.setCellValueFactory(e -> new SimpleLongProperty(e.getValue().getId()).asObject());
    nameCol.setCellValueFactory(e -> new SimpleStringProperty(e.getValue().getName()));
    authorIdCol.setCellValueFactory(e -> new SimpleLongProperty(e.getValue().getAuthorId()).asObject());
    coordinatesXCol.setCellValueFactory(e -> new SimpleLongProperty(e.getValue().getCoordinates().getX()).asObject());
    coordinatesYCol
        .setCellValueFactory(e -> new SimpleIntegerProperty(e.getValue().getCoordinates().getY()).asObject());
    difficultyCol.setCellValueFactory(e -> new SimpleStringProperty(e.getValue().getDifficulty().name()));
    disciplineLabsCountCol
        .setCellValueFactory(e -> new SimpleIntegerProperty(e.getValue().getDiscipline().getLabsCount()).asObject());
    disciplinePracticeHoursCol
        .setCellValueFactory(e -> new SimpleLongProperty(e.getValue().getDiscipline().getPracticeHours()).asObject());
    disciplineLectureHoursCol
        .setCellValueFactory(e -> new SimpleLongProperty(e.getValue().getDiscipline().getLectureHours()).asObject());
    disciplineNameCol.setCellValueFactory(e -> new SimpleStringProperty(e.getValue().getDiscipline().getName()));
    minimalPointCol.setCellValueFactory(e -> new SimpleLongProperty(e.getValue().getMinimalPoint()).asObject());

    this.dataTable.setItems(GlobalStorage.getInstance().getObservableLabWorks());

    // initialize labwork fetch daemon
    this.labWorkFetcherService = new LabWorkFetcherService();
    labWorkFetcherService.setPeriod(Duration.seconds(3));
    labWorkFetcherService.start();
  }

  @FXML
  public void refetchLabworks() {
    System.out.println("refetching");
    Platform.runLater(() -> {
      GlobalStorage.getInstance().clearLabWorks();
      this.labWorkFetcherService.restart();
    });
  }

  @FXML
  public void deleteLabwork() {
    LabWorkService.getInstance().deleteById(this.selectedLabWork.get().getId());
    this.selectedLabWork.set(null);
  }

  @FXML
  public void openSettings() {
    FXMLLoader loader = new FXMLLoader(getClass().getResource("/settings.fxml"));
    Parent root = Main.loadFxml(loader);
    // SettingsDialog dialogController = loader.getController();
    Stage dialogStage = new Stage();
    dialogStage.setScene(new Scene(root));
    dialogStage.showAndWait();
  }

  @FXML
  void logout() {
    AuthService.getInstance().logout();
    LoginController.launch();
    this.labWorkFetcherService.cancel();
  }

  @FXML
  void runCreateDialog() {
    LabworkDialog.launch(null);
  }

  @FXML
  void runEditDialog() {
    LabworkDialog.launch(this.selectedLabWork.get());
  }

  public static void launch() {
    Stage mainStage = GlobalStorage.getInstance().getMainStage();
    FXMLLoader loader = new FXMLLoader(HomeController.class.getResource("/home.fxml"));
    Parent root = Main.loadFxml(loader);

    mainStage.setScene(new Scene(root));
    mainStage.setTitle(L10nService.getInstance().getString("Home"));
    mainStage.setResizable(true);
    mainStage.show();
  }

  private void applyLocale(ResourceBundle bundle) {
    this.createButton.setText(L10nService.getInstance().getString("CreateButton"));
    this.deleteButton.setText(L10nService.getInstance().getString("DeleteButton"));
    this.settingsButton.setText(L10nService.getInstance().getString("Settings"));
    this.tableTab.setText(L10nService.getInstance().getString("TableTab"));
    this.visualizeTab.setText(L10nService.getInstance().getString("VisualizeTab"));
    this.idCol.setText(L10nService.getInstance().getString("IdCol"));
    this.authorIdCol.setText(L10nService.getInstance().getString("AuthorIdCol"));
    this.coordinatesXCol.setText(L10nService.getInstance().getString("CoordinatesXCol"));
    this.coordinatesYCol.setText(L10nService.getInstance().getString("CoordinatesYCol"));
    this.difficultyCol.setText(L10nService.getInstance().getString("DifficultyCol"));
    this.disciplineLabsCountCol.setText(L10nService.getInstance().getString("DisciplineLabsCountCol"));
    this.disciplineLectureHoursCol.setText(L10nService.getInstance().getString("DisciplineLectureHoursCol"));
    this.disciplineNameCol.setText(L10nService.getInstance().getString("DisciplineNameCol"));
    this.disciplinePracticeHoursCol.setText(L10nService.getInstance().getString("DisciplinePracticeHoursCol"));
    this.minimalPointCol.setText(L10nService.getInstance().getString("MinimalPointCol"));
    this.nameCol.setText(L10nService.getInstance().getString("NameCol"));
  }
}
