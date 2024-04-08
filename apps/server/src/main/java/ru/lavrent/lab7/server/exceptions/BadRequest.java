package ru.lavrent.lab7.server.exceptions;


public class BadRequest extends RuntimeException {
  public BadRequest(String message, Throwable cause) {
    super(message, cause);
  }

  public BadRequest(String message) {
    super(message);
  }

  public BadRequest(Throwable e) {
    super(e);
  }

  public BadRequest() {
    super();
  }
}
