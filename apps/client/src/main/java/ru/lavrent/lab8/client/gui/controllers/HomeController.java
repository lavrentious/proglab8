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
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;
import ru.lavrent.lab8.client.Main;
import ru.lavrent.lab8.client.services.AuthService;
import ru.lavrent.lab8.client.services.LabWorkService;
import ru.lavrent.lab8.client.utils.GlobalStorage;
import ru.lavrent.lab8.client.utils.L10nService;
import ru.lavrent.lab8.common.models.LabWork;

import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
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
          LabWorkService.getInstance().fetch(() -> HomeController.this.visualize(false));
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
  private Map<Long, Color> authorColors = new HashMap<>();

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
  private Button filtersButton;

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
  private Pane visualizePane;

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
  private TableColumn<LabWork, String> createdAtColumn;

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
    this.visualizeTab.setOnSelectionChanged(event -> {
      if (this.visualizeTab.isSelected()) {
        visualize(true);
      }
    });

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

    // GlobalStorage.getInstance().getObservableLabWorks().addListener((ListChangeListener<LabWork>)
    // change -> {
    // Platform.runLater(() -> {
    // this.visualize(false);
    // });
    // });
    idCol.setCellValueFactory(e -> new SimpleLongProperty(e.getValue().getId()).asObject());
    nameCol.setCellValueFactory(e -> new SimpleStringProperty(e.getValue().getName()));
    authorIdCol.setCellValueFactory(e -> new SimpleLongProperty(e.getValue().getAuthorId()).asObject());
    coordinatesXCol.setCellValueFactory(e -> new SimpleLongProperty(e.getValue().getCoordinates().getX()).asObject());
    coordinatesYCol
        .setCellValueFactory(e -> new SimpleIntegerProperty(e.getValue().getCoordinates().getY()).asObject());
    difficultyCol.setCellValueFactory(e -> new SimpleStringProperty(e.getValue().getDifficulty().name()));
    createdAtColumn.setCellValueFactory(
        e -> new SimpleStringProperty(
            L10nService.getInstance().getDate(Date.from(e.getValue().getCreationDate().toInstant()))));
    disciplineLabsCountCol
        .setCellValueFactory(e -> new SimpleIntegerProperty(e.getValue().getDiscipline().getLabsCount()).asObject());
    disciplinePracticeHoursCol
        .setCellValueFactory(e -> new SimpleLongProperty(e.getValue().getDiscipline().getPracticeHours()).asObject());
    disciplineLectureHoursCol
        .setCellValueFactory(e -> new SimpleLongProperty(e.getValue().getDiscipline().getLectureHours()).asObject());
    disciplineNameCol.setCellValueFactory(e -> new SimpleStringProperty(e.getValue().getDiscipline().getName()));
    minimalPointCol.setCellValueFactory(e -> new SimpleLongProperty(e.getValue().getMinimalPoint()).asObject());

    // this.dataTable.setItems(GlobalStorage.getInstance().getObservableLabWorks());
    this.dataTable.setItems(GlobalStorage.getInstance().getFilteredList());

    // initialize labwork fetch daemon
    this.labWorkFetcherService = new LabWorkFetcherService();
    labWorkFetcherService.setPeriod(Duration.seconds(3));
    labWorkFetcherService.start();
  }

  private void visualize(boolean forceAnimate) {
    if (!this.visualizeTab.isSelected()) {
      return;
    }
    System.out.println("visualizing");
    this.visualizePane.getChildren().clear();

    for (LabWork labWork : GlobalStorage.getInstance().getObservableLabWorks()) {
      if (!authorColors.containsKey(labWork.getAuthorId())) {
        authorColors.put(labWork.getAuthorId(), Color.color(Math.random(), Math.random(), Math.random()));
      }
      Color color = authorColors.get(labWork.getAuthorId());

      // set circle radius
      double radius = Math.log(labWork.getDiscipline().getLabsCount() * 3) * 20;
      radius = Math.max(15, Math.min(radius, 100));

      // set circle position
      long x = Math.abs(labWork.getCoordinates().getX());
      x = Math.max(50, Math.min(x, 800));
      long y = Math.abs(labWork.getCoordinates().getY());
      y = Math.max(50, Math.min(y, 350));

      // draw circle
      Circle circle = new Circle(radius, color);
      circle.setCenterX(x);
      circle.setCenterY(y);
      this.visualizePane.getChildren().add(circle);

      // id label
      Text id = new Text("id=" + String.valueOf(labWork.getId()));
      id.setFont(Font.font("Segoe UI", radius / 2));
      id.setX(circle.getCenterX() - id.getLayoutBounds().getWidth() / 2);
      id.setY(circle.getCenterY() + id.getLayoutBounds().getHeight() / 4);
      this.visualizePane.getChildren().add(id);

      // init events
      circle.setOnMouseClicked(mouseEvent -> {
        if (mouseEvent.getClickCount() == 2 && labWork.getAuthorId() == GlobalStorage.getInstance().getUser().getId()) {
          LabworkDialog.launch(labWork);
        }
      });

      Arrays.asList(circle, id).forEach((node) -> node.setOnMouseEntered(mouseEvent -> {
        circle.setFill(color.brighter());
      }));

      Arrays.asList(circle, id).forEach(node -> node.setOnMouseExited(mouseEvent -> {
        circle.setFill(color);
      }));
    }

    System.out.println("visualizing complete");
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
  public void openFilterMenu() {
    FiltersDialog.launch();
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
    this.editButton.setText(L10nService.getInstance().getString("EditButton"));
    this.logoutButton.setText(L10nService.getInstance().getString("LogoutButton"));
    this.refetchButton.setText(L10nService.getInstance().getString("RefetchButton"));
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

    Platform.runLater(() -> {
      this.createdAtColumn.setCellValueFactory(
          e -> new SimpleStringProperty(
              L10nService.getInstance().getDate(Date.from(e.getValue().getCreationDate().toInstant()))));
      this.refetchLabworks();
    });
  }
}
