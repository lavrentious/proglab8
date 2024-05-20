package ru.lavrent.lab8.common.exceptions;

/**
 * exception meaning that some script at some point invokes itself (endlessly)
 */
public class AuthException extends RuntimeException {
  public AuthException(String message, Throwable cause) {
    super(message, cause);
  }

  public AuthException(String message) {
    super(message);
  }

  public AuthException(Throwable e) {
    super(e);
  }

  public AuthException() {
    super();
  }
}
