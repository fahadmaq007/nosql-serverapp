package com.maqbool.server.commons;

import java.io.Serializable;
import java.util.Date;

/**
 * Entity is the base class for all the DTOs & it is {@link Serializable}.
 * 
 * @author niraj.gupta
 * 
 */
public abstract class Entity implements Serializable {

  /**
	 * 
	 */
  private static final long serialVersionUID = 2071376340849451365L;
  
  public static final String ID = "id";

  /**
   * Identifier of the entity.
   */
  protected String id;
  
  private long cas;
  
  private Date creationDate;

  /**
   * hashCode() is implemented to produce the hashCode for the id. If it requires to change the implementation, the
   * subclasses can do so.
   * 
   * @return hashCode
   */
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((id == null) ? 0 : id.hashCode());
    return result;
  }

  /**
   * equals() is implemented to check the equality of the id. If it requires to change the implementation, the
   * subclasses can do so.
   * 
   * @return true if it's equal otherwise false;
   */
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    Entity other = (Entity) obj;
    if (id == null) {
      if (other.id != null)
        return false;
    } else if (!id.equals(other.id))
      return false;
    return true;
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public long getCas() {
	return cas;
  }
	
  public void setCas(long cas) {
	this.cas = cas;
  }
  
  public Date getCreationDate() {
		return this.creationDate;
	}
  
  public void setCreationDate(Date creationDate) {
	  this.creationDate = creationDate;
  }
  
}
