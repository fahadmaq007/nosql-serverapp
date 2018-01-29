package com.maqbool.server.exception;

/**
 * 
 * Represents the service layer exception.
 * @author niraj.gupta
 *
 */
public class DataAccessException extends GenericException {

  private static final long serialVersionUID = -7657111991371365664L;

  /**
   * Constructs the exception with the message.
   * 
   * @param message exception message
   */
  public DataAccessException(String message) {
    this(message, null);
  }

  /**
   * Constructs with the supplied message & the cause.
   * 
   * @param message exception message
   * @param cause Cause of the exception
   */
  public DataAccessException(String message, Throwable cause) {
    super(message, cause);
  }

  /**
   * Constructs with the supplied message & the cause.
   * 
   * @param cause Cause of the exception
   */
  public DataAccessException(Throwable cause) {
    this(cause.getMessage(), cause);
  }

}
