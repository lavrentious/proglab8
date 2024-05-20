package ru.lavrent.lab8.common.exceptions;

/**
 * exception meaning that some script at some point invokes itself (endlessly)
 */
public class NotFoundException extends RuntimeException {
  public NotFoundException(String message, Throwable cause) {
    super(message, cause);
  }

  public NotFoundException(String message) {
    super(message);
  }

  public NotFoundException(Throwable e) {
    super(e);
  }

  public NotFoundException() {
    super();
  }
}
