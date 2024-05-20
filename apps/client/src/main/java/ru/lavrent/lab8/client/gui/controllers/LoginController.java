package ru.lavrent.lab8.client.gui.controllers;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import ru.lavrent.lab8.client.Main;
import ru.lavrent.lab8.client.services.AuthService;
import ru.lavrent.lab8.client.utils.GlobalStorage;
import ru.lavrent.lab8.client.utils.L10nService;
import ru.lavrent.lab8.common.exceptions.AuthException;
import ru.lavrent.lab8.common.utils.Credentials;

import java.util.ResourceBundle;

public class LoginController {
  private SimpleBooleanProperty isLoading = new SimpleBooleanProperty(false);

  @FXML
  private Button loginButton;

  @FXML
  private Button settingsButton;

  @FXML
  private PasswordField passwordField;

  @FXML
  private Label passwordLabel;

  @FXML
  private Button registerButton;

  @FXML
  private TextField usernameField;

  @FXML
  private Label usernameLabel;

  @FXML
  public void onLogin() {
    Task<Void> authTask = new Task<Void>() {
      @Override
      protected Void call() throws Exception {
        isLoading.set(true);
        Credentials credentials = new Credentials(usernameField.getText(), passwordField.getText());
        AuthService.getInstance().auth(credentials);
        return null;
      }
    };

    authTask.setOnSucceeded((WorkerStateEvent event) -> {
      isLoading.set(false);
      HomeController.launch();
    });

    authTask.setOnFailed((WorkerStateEvent event) -> {
      isLoading.set(false);
      Throwable exception = event.getSource().getException();
      if (exception instanceof AuthException) {
        this.showAuthenticationError(exception.getMessage());
        System.out.println("auth failed: " + exception.getMessage());
      } else {
        System.out.println("unknown error " + exception);
      }
    });

    Thread thread = new Thread(authTask);
    thread.setDaemon(true);
    thread.start();
  }

  private void showAuthenticationError(String message) {
    var l10n = L10nService.getInstance();
    Alert alert = new Alert(AlertType.ERROR);
    alert.setTitle(l10n.getString("Error"));
    alert.setHeaderText(l10n.getString("AuthError"));
    alert.setContentText(message);
    alert.showAndWait();
  }

  @FXML
  public void onRegister() {
    System.out.println("registering");
    System.out.println(usernameField.getText());
    System.out.println(passwordField.getText());
    HomeController.launch();
  }

  @FXML
  public void initialize() {
    System.out.println("initializing login");
    isLoading.addListener((observable, oldValue, newValue) -> {
      if (newValue) {
        this.loginButton.setDisable(true);
        this.registerButton.setDisable(true);
      } else {
        this.loginButton.setDisable(false);
        this.registerButton.setDisable(false);
      }
    });

    this.applyLocale(L10nService.getInstance().getBundle());
    L10nService.getInstance().getObservableLocale().addListener((observable,
        oldValue, newValue) -> {
      this.applyLocale(L10nService.getInstance().getBundle());
    });
  }

  @FXML
  public void openSettings(ActionEvent event) {
    SettingsDialog.launch();
  }

  public static void launch() {
    Stage mainStage = GlobalStorage.getInstance().getMainStage();
    FXMLLoader loginLoader = new FXMLLoader(LoginController.class.getResource("/login.fxml"));
    Parent authRoot = Main.loadFxml(loginLoader);

    mainStage.setScene(new Scene(authRoot));
    mainStage.setTitle(L10nService.getInstance().getString("Login"));
    mainStage.setResizable(true);
    mainStage.show();
  }

  private void applyLocale(ResourceBundle bundle) {
    this.loginButton.setText(L10nService.getInstance().getString("LoginButton"));
    this.registerButton.setText(L10nService.getInstance().getString("RegisterButton"));
    this.usernameField.setPromptText(L10nService.getInstance().getString("UsernamePrompt"));
    this.passwordField.setPromptText(L10nService.getInstance().getString("PasswordPrompt"));
    this.usernameLabel.setText(L10nService.getInstance().getString("UsernameLabel"));
    this.passwordLabel.setText(L10nService.getInstance().getString("PasswordLabel"));
    this.settingsButton.setText(L10nService.getInstance().getString("Settings"));
  }
}
