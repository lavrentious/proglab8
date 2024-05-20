package ru.lavrent.lab8.common.network.responses;

public class ErrorResponse extends Response {
  public final String message;

  public ErrorResponse(String message) {
    super("ERROR");
    this.message = message;
  }

}
