package com.maqbool.server.commons;


import java.io.Serializable;

/**
 * GroupBySpec defines the group by clause on the property.
 * 
 * @author maqbool.ahmed
 * 
 */
public class GroupBySpec implements Serializable {
  /**
	 * 
	 */
  private static final long serialVersionUID = -5631109974811514424L;

  private String propertyName;

  public GroupBySpec() {

  }

  public GroupBySpec(String propertyName) {
    this.propertyName = propertyName;
  }

  public String getPropertyName() {
    return propertyName;
  }

  public void setPropertyName(String propertyName) {
    this.propertyName = propertyName;
  }

  @Override
  public String toString() {
    return Constants.OPEN_BRACKET + propertyName + Constants.CLOSE_BRACKET;
  }
}
