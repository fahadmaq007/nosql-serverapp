package com.maqbool.server.commons;

import java.io.Serializable;

/**
 * SelectSpec defines the specifications on property to be selected. For eg. employeeName would mean select employeeName
 * from employee
 * 
 * @author maqbool.ahmed
 * 
 */
public class SelectSpec implements Serializable {
  /**
	 * 
	 */
  private static final long serialVersionUID = -5631109974811514424L;

  private String propertyName;

  private Operation operation;

  public SelectSpec() {
    this(null, null);
  }

  public SelectSpec(String propertyName) {
    this(propertyName, null);
  }

  public SelectSpec(String propertyName, Operation operation) {
    this.propertyName = propertyName;
    this.operation = operation;
  }

  public Operation getOperation() {
    return operation;
  }

  public void setOperation(Operation operation) {
    this.operation = operation;
  }

  public String getPropertyName() {
    return propertyName;
  }

  public void setPropertyName(String propertyName) {
    this.propertyName = propertyName;
  }

  @Override
  public String toString() {
    return Constants.OPEN_BRACKET + propertyName + "," + operation + Constants.CLOSE_BRACKET;
  }
}
