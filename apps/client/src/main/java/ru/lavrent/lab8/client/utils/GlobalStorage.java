package ru.lavrent.lab8.client.utils;

import com.google.common.base.Predicate;
import javafx.application.Platform;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleMapProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.stage.Stage;
import ru.lavrent.lab8.common.models.LabWork;
import ru.lavrent.lab8.common.utils.Credentials;
import ru.lavrent.lab8.common.utils.PublicUser;

import java.util.Map;

public class GlobalStorage {
  static private GlobalStorage instance;
  private SimpleObjectProperty<Credentials> credentials = new SimpleObjectProperty<>();
  private SimpleObjectProperty<PublicUser> user = new SimpleObjectProperty<>();
  private Stage mainStage;
  private TCPClient tcpClient;
  private SimpleMapProperty<Long, LabWork> labWorks = new SimpleMapProperty<>(FXCollections.observableHashMap());
  private ObservableList<LabWork> labWorkList;
  private FilteredList<LabWork> filteredList;

  private GlobalStorage() {
    this.labWorkList = new SimpleListProperty<>(FXCollections.observableArrayList(this.labWorks.get().values()));
    this.filteredList = new FilteredList<>(this.labWorkList);
  }

  static public synchronized GlobalStorage getInstance() {
    if (instance == null) {
      instance = new GlobalStorage();
    }
    return instance;
  }

  public synchronized void setFiltersPredicate(Predicate<LabWork> predicate) {
    this.filteredList.setPredicate(predicate);
  }

  public synchronized TCPClient getTCPClient() {
    return tcpClient;
  }

  public synchronized void setTcpClient(TCPClient tcpClient) {
    this.tcpClient = tcpClient;
  }

  public synchronized Credentials getCredentials() {
    return credentials.get();
  }

  public SimpleMapProperty<Long, LabWork> getLabWorks() {
    return labWorks;
  }

  public synchronized void clearLabWorks() {
    Platform.runLater(() -> {
      this.labWorks.clear();
      this.labWorkList.clear();
    });
  }

  public synchronized ObservableList<LabWork> getObservableLabWorks() {
    return this.labWorkList;
  }

  public FilteredList<LabWork> getFilteredList() {
    return filteredList;
  }

  public synchronized LabWork getLabWorkById(long id) {
    return this.labWorks.get(id);
  }

  public synchronized void addLabWork(LabWork labWork) {
    Platform.runLater(() -> {
      System.out.println("[service] adding " + labWork.getId());
      this.labWorks.put(labWork.getId(), labWork);
      this.labWorkList.add(labWork);
    });
  }

  public synchronized void updateLabWork(long id, LabWork labWork) {
    Platform.runLater(() -> {
      System.out.println("[service] updating " + id);
      this.labWorks.put(id, labWork);
      this.labWorks.remove(id);
      this.labWorkList.add(labWork);
    });
  }

  public synchronized void removeLabWork(long id) {
    Platform.runLater(() -> {
      System.out.println("[service] removing " + id);
      this.labWorkList.remove(this.labWorks.get(id));
      this.labWorks.remove(id);
    });
  }

  public synchronized void setLabWorks(Map<Long, LabWork> newLabworks) {
    Platform.runLater(() -> {
      this.labWorks.clear();
      this.labWorks.putAll(newLabworks);
      this.labWorkList.clear();
      this.labWorkList.addAll(newLabworks.values());
    });
  }

  public synchronized SimpleObjectProperty<Credentials> getObservableCredentials() {
    return credentials;
  }

  public synchronized void setCredentials(Credentials credentials) {
    this.credentials.set(credentials);
  }

  public synchronized PublicUser getUser() {
    return user.get();
  }

  public synchronized SimpleObjectProperty<PublicUser> getObservableUser() {
    return user;
  }

  public synchronized void setUser(PublicUser user) {
    this.user.set(user);
  }

  public synchronized void setMainStage(Stage mainStage) {
    this.mainStage = mainStage;
  }

  public synchronized Stage getMainStage() {
    return mainStage;
  }
}
