package ru.lavrent.lab8.common.exceptions;

/**
 * exception meaning that some script at some point invokes itself (endlessly)
 */
public class InsufficientPermissionsException extends RuntimeException {
  public InsufficientPermissionsException(String message, Throwable cause) {
    super(message, cause);
  }

  public InsufficientPermissionsException(String message) {
    super(message);
  }

  public InsufficientPermissionsException(Throwable e) {
    super(e);
  }

  public InsufficientPermissionsException() {
    super();
  }
}
