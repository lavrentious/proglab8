package ru.lavrent.lab8.client;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.stage.Stage;
import ru.lavrent.lab8.client.gui.controllers.LoginController;
import ru.lavrent.lab8.client.managers.RuntimeManager;
import ru.lavrent.lab8.client.utils.ClientEnvConfig;
import ru.lavrent.lab8.client.utils.GlobalExceptionHandler;
import ru.lavrent.lab8.client.utils.GlobalStorage;
import ru.lavrent.lab8.client.utils.TCPClient;
import ru.lavrent.lab8.common.utils.Block;

import java.io.IOException;
import java.net.UnknownHostException;

public class Main extends Application {
  public static Parent loadFxml(FXMLLoader loader) {
    Parent parent = null;
    try {
      parent = loader.load();
    } catch (IOException e) {
      // System.out.println(e);
      System.err.println("Can't load " + loader.toString());
      // System.exit(1);
    }
    return parent;
  }

  public void start(Stage stage) {
    Thread.setDefaultUncaughtExceptionHandler(new GlobalExceptionHandler());
    GlobalStorage.getInstance().setMainStage(stage);
    LoginController.launch();
    setAppIcon();
  }

  public static void main(String... args) {
    System.out.println("starting gui");
    Block<RuntimeManager> block = new Block<>();
    new Thread(() -> {
      launch(args);
      System.exit(0);
    }).start();
    try {
      System.out.println("starting net module");
      RuntimeManager runtimeManager = new RuntimeManager(
          new TCPClient("localhost", ClientEnvConfig.getInstance().getPort()), null);
      block.put(runtimeManager);
      runtimeManager.run();
    } catch (UnknownHostException e) {
      System.out.println("unknown host " + e.getMessage());
    } catch (Exception e) {
      System.out.println("error while connecting " + e.getMessage());
    }
  }

  private void setAppIcon() {
    // Image icon = new
    // Image(Objects.requirActionEvent
    // eventNull(getClass().getResourceAsStream("/icons/app.png")));
    // this.mainStage.getIcons().add(icon);
  }
}
