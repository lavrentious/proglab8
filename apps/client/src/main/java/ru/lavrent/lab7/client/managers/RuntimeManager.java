package ru.lavrent.lab7.client.managers;

import ru.lavrent.lab7.client.commands.Add;
import ru.lavrent.lab7.client.commands.Clear;
import ru.lavrent.lab7.client.commands.Command;
import ru.lavrent.lab7.client.commands.CountLessThanDifficulty;
import ru.lavrent.lab7.client.commands.ExecuteScript;
import ru.lavrent.lab7.client.commands.Exit;
import ru.lavrent.lab7.client.commands.Help;
import ru.lavrent.lab7.client.commands.History;
import ru.lavrent.lab7.client.commands.Info;
import ru.lavrent.lab7.client.commands.Login;
import ru.lavrent.lab7.client.commands.Logout;
import ru.lavrent.lab7.client.commands.Register;
import ru.lavrent.lab7.client.commands.Show;
import ru.lavrent.lab7.client.utils.ClientEnvConfig;
import ru.lavrent.lab7.client.utils.Reader;
import ru.lavrent.lab7.client.utils.TCPClient;
import ru.lavrent.lab7.common.exceptions.InvalidConfigException;
import ru.lavrent.lab7.common.utils.Credentials;

import java.io.IOException;
import java.net.UnknownHostException;

/**
 * RuntimeManager
 */
public class RuntimeManager {
  private CommandManager commandManager;
  private Reader reader;
  private TCPClient tcpClient;
  private ClientEnvConfig config;
  private Credentials credentials;

  public RuntimeManager(TCPClient tcpClient, String filePath, Credentials credentials)
      throws UnknownHostException, IOException {
    this.commandManager = new CommandManager();
    this.tcpClient = tcpClient;

    this.reader = new Reader(commandManager, filePath, null);
    if (!this.reader.getFileMode()) {
      tcpClient.setOnAuth((Credentials c) -> {
        this.reader.setPrefix(c.username);
      });
      tcpClient.setOnDeuth((Credentials c) -> {
        this.reader.setPrefix(null);
      });
    }
    try {
      this.config = ClientEnvConfig.getInstance();
      System.out.println("port=%d".formatted(config.getPort()));
      getReader().setOnHalt(tcpClient::disconnect);
      loadCommands();
    } catch (InvalidConfigException e) {
      System.out.println("invalid config: " + e.getMessage());
    }
  }

  private void loadCommands() {
    Command[] commands = new Command[] {
        new Info(tcpClient),
        new Show(tcpClient),
        new Clear(tcpClient),
        new ExecuteScript(tcpClient),
        new Help(commandManager),
        new History(commandManager),
        new Exit(reader),
        new Add(reader, tcpClient),
        new CountLessThanDifficulty(tcpClient),
        new Login(reader, tcpClient),
        new Logout(tcpClient),
        new Register(reader, tcpClient),
    };
    for (Command cmd : commands) {
      commandManager.addCommand(cmd);
    }
  }

  public void run() {
    getReader().read();
  }

  public CommandManager getCommandManager() {
    return commandManager;
  }

  public Reader getReader() {
    return reader;
  }

  public Credentials getCredentials() {
    return credentials;
  }

  public void setCredentials(Credentials credentials) {
    this.credentials = credentials;
  }
}