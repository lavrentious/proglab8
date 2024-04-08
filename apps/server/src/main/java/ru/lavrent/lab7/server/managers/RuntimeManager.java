package ru.lavrent.lab7.server.managers;

import ru.lavrent.lab7.common.exceptions.InvalidConfigException;
import ru.lavrent.lab7.server.TCPServer;
import ru.lavrent.lab7.server.commands.Add;
import ru.lavrent.lab7.server.commands.Clear;
import ru.lavrent.lab7.server.commands.Command;
import ru.lavrent.lab7.server.commands.CountLessThanDifficulty;
import ru.lavrent.lab7.server.commands.Info;
import ru.lavrent.lab7.server.commands.Register;
import ru.lavrent.lab7.server.commands.Show;
import ru.lavrent.lab7.server.database.DBSessionManager;
import ru.lavrent.lab7.server.utils.ServerEnvConfig;

import java.io.IOException;
import java.util.logging.ConsoleHandler;
import java.util.logging.FileHandler;
import java.util.logging.Formatter;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

/**
 * RuntimeManager
 */
public class RuntimeManager {
  private RequestManager requestManager;
  private TCPServer tcpServer;
  private CollectionManager collectionManager;
  private AuthManager authManager;
  private ServerEnvConfig config;
  public static Logger logger;

  public RuntimeManager() throws IOException {
    this.requestManager = new RequestManager();
    try {
      this.config = ServerEnvConfig.getInstance();
      setupLogger();
      logger
          .config("port=%d, logPath=%s, dbUrl=%s, dbUser=%s, dbPassword=%s".formatted(config.getPort(),
              config.getLogPath(), config.getDbUrl(), config.getDbUser(), config.getDbPassword()));
      DBSessionManager.setCredentials(config.getDbUrl(), config.getDbUser(), config.getDbPassword());
      this.authManager = new AuthManager(DBSessionManager.getSessionFactory());
      this.collectionManager = new CollectionManager(DBSessionManager.getSessionFactory());
      this.tcpServer = new TCPServer(config.getPort(), requestManager, authManager);
      loadCommands();
      loadDb();
      Runtime.getRuntime().addShutdownHook(new Thread(this::onShutdown));
    } catch (InvalidConfigException e) {
      logger.severe("invalid config: " + e.getMessage());
    }
  }

  private void setupLogger() {
    // remove root console logger
    Logger rootLogger = Logger.getLogger("");
    for (java.util.logging.Handler handler : rootLogger.getHandlers()) {
      if (handler instanceof ConsoleHandler) {
        rootLogger.removeHandler(handler);
      }
    }

    // set up custom logger
    logger = Logger.getLogger("ru.lavrent.lab7.server");
    Formatter formatter = new Formatter() {
      @Override
      public String format(final LogRecord record) {
        return "[%s] %s\n".formatted(record.getLevel(), record.getMessage());
      }
    };

    logger.setLevel(Level.ALL);
    ConsoleHandler ch = new ConsoleHandler();
    ch.setFormatter(formatter);
    ch.setLevel(Level.ALL);
    logger.addHandler(ch);
    if (config.getLogPath() != null) {
      logger.config("adding file log handler " + config.getLogPath());
      try {
        FileHandler fh = new FileHandler(config.getLogPath());
        fh.setFormatter(formatter);
        fh.setLevel(Level.ALL);
        logger.addHandler(fh);
      } catch (IOException e) {
        logger.config("could not register file log handler in %s (%s)".formatted(config.getLogPath(), e.getMessage()));
      }
    }

  }

  private void loadCommands() {
    Command[] commands = new Command[] {
        new Add(this.collectionManager),
        new Clear(this.collectionManager),
        new Show(this.collectionManager),
        new Info(this.collectionManager),
        new CountLessThanDifficulty(this.collectionManager),
        new Register(authManager),
    };
    for (Command cmd : commands) {
      this.requestManager.addCommand(cmd);
    }
  }

  private void loadDb() {
    try {
      collectionManager.loadToMemory();
    } catch (Exception e) {
      logger.warning("could not load db: " + e.getMessage());
      e.printStackTrace();
    }
  }

  private void onShutdown() {
    logger.info("shutting down...");
    DBSessionManager.shutdown();
  }

  public void run() throws IOException {
    this.tcpServer.listen();
  }
}