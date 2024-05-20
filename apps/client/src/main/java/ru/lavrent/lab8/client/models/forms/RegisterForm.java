package ru.lavrent.lab8.client.models.forms;

import ru.lavrent.lab8.common.exceptions.ValidationException;
import ru.lavrent.lab8.common.models.User;

import java.util.Scanner;

public class RegisterForm extends Form<User> {
  public User run(Scanner scanner) throws ValidationException {
    System.out.println("authorization");
    String username = getString("username: ", scanner, null);
    String password = getString("password: ", scanner, null);
    return new User(username, password);
  }
}