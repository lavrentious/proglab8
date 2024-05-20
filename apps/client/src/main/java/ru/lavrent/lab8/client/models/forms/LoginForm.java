package ru.lavrent.lab8.client.models.forms;

import ru.lavrent.lab8.common.exceptions.ValidationException;
import ru.lavrent.lab8.common.utils.Credentials;

import java.util.Scanner;

public class LoginForm extends Form<Credentials> {
  public Credentials run(Scanner scanner) throws ValidationException {
    System.out.println("authorization");
    String username = getString("username: ", scanner, null);
    String password = getString("password: ", scanner, null);
    return new Credentials(username, password);
  }
}