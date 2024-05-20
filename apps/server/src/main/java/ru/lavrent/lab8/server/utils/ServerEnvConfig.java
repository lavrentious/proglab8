package ru.lavrent.lab8.server.utils;

import io.github.cdimascio.dotenv.Dotenv;
import ru.lavrent.lab8.common.exceptions.InvalidConfigException;
import ru.lavrent.lab8.common.interfaces.IConfig;

public class ServerEnvConfig implements IConfig {
  private static ServerEnvConfig instance;
  private int port = 5555;
  private String logPath;
  private String dbUrl;
  private String dbUser;
  private String dbPassword;

  private ServerEnvConfig() throws InvalidConfigException {
    onLoad();
    validate();
  }

  public static ServerEnvConfig getInstance() throws InvalidConfigException {
    if (instance == null) {
      instance = new ServerEnvConfig();
    }
    return instance;
  }

  @Override
  public void onLoad() {
    String envPath = System.getenv("ENV_PATH");
    Dotenv dotenv = Dotenv.configure().filename(envPath != null ? envPath : ".env").load();
    this.logPath = dotenv.get("LOG_PATH");
    this.dbUrl = dotenv.get("DB_URL");
    this.dbUser = dotenv.get("DB_USER");
    this.dbPassword = dotenv.get("DB_PASSWORD");
    String port = dotenv.get("PORT");
    if (port != null)
      this.port = Integer.parseInt(port);
  }

  @Override
  public void validate() throws InvalidConfigException {
    if (port < 1 || port > 65535) {
      throw new InvalidConfigException("'port' must be within [1 ; 65535]");
    }
  }

  public int getPort() {
    return port;
  }

  public String getLogPath() {
    return logPath;
  }

  public String getDbUrl() {
    return dbUrl;
  }

  public String getDbPassword() {
    return dbPassword;
  }

  public String getDbUser() {
    return dbUser;
  }
}
