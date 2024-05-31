package ru.lavrent.lab8.client.gui.controllers;

import javafx.animation.FadeTransition;
import javafx.animation.RotateTransition;
import javafx.animation.ScaleTransition;
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
import javafx.scene.Node;
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
import java.util.HashSet;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;

public class HomeController {
  private LabWorkFetcherService labWorkFetcherService;

  class LabWorkFetcherService extends ScheduledService<Void> {
    private volatile boolean firstRender = true;

    @Override
    protected Task<Void> createTask() {
      return new Task<Void>() {
        @Override
        protected Void call() throws Exception {
          isLoading.set(true);
          LabWorkService.getInstance()
              .fetch(firstRender ? (Set<Long> ids) -> HomeController.this.visualize(null)
                  : HomeController.this::visualize);
          LabWorkFetcherService.this.firstRender = false;
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

    @Override
    public void restart() {
      super.restart();
      this.firstRender = true;
    }
  }

  private SimpleBooleanProperty isLoading = new SimpleBooleanProperty(false);
  private SimpleObjectProperty<LabWork> selectedLabWork = new SimpleObjectProperty<>();
  private Map<Long, Color> authorColors = new HashMap<>();
  private Map<Long, Set<Node>> circles = new HashMap<>();

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
        visualize(null);
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

  /**
   * render visualization
   * 
   * @param rerenderIds a set of labwork ids that need to be rerendered (null -
   *                    rerender all; empty set - rerender none)
   */
  private void visualize(Set<Long> rerenderIds) {
    if (rerenderIds == null) {
      this.visualizePane.getChildren().clear();
      rerenderIds = new HashSet<>();
      for (LabWork labWork : GlobalStorage.getInstance().getObservableLabWorks()) {
        rerenderIds.add(labWork.getId());
      }
    }
    System.out.println("rerendering ids " + String.join(", ", rerenderIds.stream().map(String::valueOf).toList()));
    if (!this.visualizeTab.isSelected() || rerenderIds.size() == 0) {
      return;
    }
    System.out.println("visualizing");

    // remove circles for deleted labworks
    for (long removedId : rerenderIds.stream().filter(id -> GlobalStorage.getInstance().getLabWorkById(id) == null)
        .toList()) {
      for (Node node : this.circles.get(removedId)) {
        this.visualizePane.getChildren().remove(node);
      }
    }

    for (LabWork labWork : GlobalStorage.getInstance().getObservableLabWorks()) {
      if (!rerenderIds.contains(labWork.getId())) {
        continue;
      }
      System.out.println("rerendering id=" + labWork.getId());
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
      this.circles.put(labWork.getId(), new HashSet<Node>(Arrays.asList(circle)));

      // id label
      Text idLabelNode = new Text("id=" + String.valueOf(labWork.getId()));
      idLabelNode.setFont(Font.font("Segoe UI", radius / 2));
      idLabelNode.setX(circle.getCenterX() - idLabelNode.getLayoutBounds().getWidth() / 2);
      idLabelNode.setY(circle.getCenterY() + idLabelNode.getLayoutBounds().getHeight() / 4);
      this.visualizePane.getChildren().add(idLabelNode);
      this.circles.get(labWork.getId()).add(idLabelNode);

      // init events
      circle.setOnMouseClicked(mouseEvent -> {
        if (mouseEvent.getClickCount() == 2 && labWork.getAuthorId() == GlobalStorage.getInstance().getUser().getId()) {
          LabworkDialog.launch(labWork);
        }
      });

      Arrays.asList(circle, idLabelNode).forEach((node) -> node.setOnMouseEntered(mouseEvent -> {
        circle.setFill(color.brighter());
      }));

      Arrays.asList(circle, idLabelNode).forEach(node -> node.setOnMouseExited(mouseEvent -> {
        circle.setFill(color);
      }));

      // animation
      Duration animationDuration = Duration.millis(600);

      // animate scale
      ScaleTransition scaleTransition = new ScaleTransition(animationDuration, circle);
      scaleTransition.setFromX(0.2);
      scaleTransition.setFromY(0.2);
      scaleTransition.setToX(1);
      scaleTransition.setToY(1);
      scaleTransition.setCycleCount(1);

      // animate fade
      FadeTransition idFadeTransition = new FadeTransition(animationDuration, idLabelNode);
      idFadeTransition.setFromValue(0.1);
      idFadeTransition.setToValue(1.0);
      idFadeTransition.setCycleCount(1);

      // animate text rotation
      var idRotateTransition = new RotateTransition(animationDuration, idLabelNode);
      idRotateTransition.setFromAngle(0);
      idRotateTransition.setToAngle(720);
      idRotateTransition.setCycleCount(1);

      scaleTransition.play();
      idFadeTransition.play();
      idRotateTransition.play();
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
