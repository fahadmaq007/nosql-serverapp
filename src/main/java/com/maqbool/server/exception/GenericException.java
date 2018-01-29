package com.maqbool.server.exception;


/**
 * Base exception for all the exceptions.
 * 
 * @author niraj.gupta
 * 
 */

public abstract class GenericException extends Exception {

  /**
	 * 
	 */
	private static final long serialVersionUID = 3871431405433765493L;

/**
   * Constructs the exception with the message.
   * 
   * @param message exception message
   */
  public GenericException(String message) {
    this(message, null);
  }

  /**
   * Constructs with the supplied message & the cause.
   * 
   * @param message exception message
   * @param cause Cause of the exception
   */
  public GenericException(String message, Throwable cause) {
    super(message, cause);    
  }

  /**
   * Constructs with the cause.
   * 
   * @param cause Cause of the exception
   */
  public GenericException(Throwable cause) {
    this(null, cause);
  }
}
