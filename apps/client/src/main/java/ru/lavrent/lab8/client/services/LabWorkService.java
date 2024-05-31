package ru.lavrent.lab8.client.services;

import javafx.application.Platform;
import ru.lavrent.lab8.client.utils.GlobalStorage;
import ru.lavrent.lab8.client.utils.TCPClient;
import ru.lavrent.lab8.common.models.DryLabWork;
import ru.lavrent.lab8.common.models.LabWork;
import ru.lavrent.lab8.common.network.requests.AddRequest;
import ru.lavrent.lab8.common.network.requests.BlankRequest;
import ru.lavrent.lab8.common.network.requests.DeleteByIdRequest;
import ru.lavrent.lab8.common.network.requests.UpdateByIdRequest;
import ru.lavrent.lab8.common.network.responses.AddResponse;
import ru.lavrent.lab8.common.network.responses.ShowResponse;
import ru.lavrent.lab8.common.utils.Commands;

import java.io.IOException;
import java.util.HashMap;
import java.util.Set;
import java.util.function.Consumer;

public class LabWorkService {
  private static LabWorkService instance;

  public static LabWorkService getInstance() {
    if (instance == null) {
      instance = new LabWorkService();
    }
    return instance;
  }

  public void fetch(Consumer<Set<Long>> rerenderIds) {
    TCPClient tcpClient = GlobalStorage.getInstance().getTCPClient();
    Platform.runLater(() -> {
      try {
        ShowResponse res = (ShowResponse) tcpClient.send(new BlankRequest(Commands.SHOW));
        var fetched = res.list;
        HashMap<Long, LabWork> fetchedMap = new HashMap<>();
        for (LabWork lw : fetched) {
          fetchedMap.put(lw.getId(), lw);
        }

        GlobalStorage.getInstance().setLabWorks(fetchedMap, rerenderIds);
      } catch (IOException e) {
        throw new RuntimeException(e);
      }
    });

  }

  public LabWork create(DryLabWork lw) {
    TCPClient tcpClient = GlobalStorage.getInstance().getTCPClient();
    try {
      var res = (AddResponse) tcpClient.send(new AddRequest(lw));
      GlobalStorage.getInstance().addLabWork(res.labWork);
      return res.labWork;
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  public LabWork updateLabWork(long id, DryLabWork lw) {
    TCPClient tcpClient = GlobalStorage.getInstance().getTCPClient();
    try {
      var res = (AddResponse) tcpClient.send(new UpdateByIdRequest(id, lw));
      Platform.runLater(() -> {
        GlobalStorage.getInstance().getObservableLabWorks().remove(GlobalStorage.getInstance().getLabWorkById(id));
        GlobalStorage.getInstance().getObservableLabWorks().add(res.labWork);
      });
      return res.labWork;
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  public void deleteById(long id) {
    TCPClient tcpClient = GlobalStorage.getInstance().getTCPClient();
    try {
      tcpClient.send(new DeleteByIdRequest(id));
      Platform.runLater(() -> {
        GlobalStorage.getInstance().getObservableLabWorks().remove(GlobalStorage.getInstance().getLabWorkById(id));
      });
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }
}
