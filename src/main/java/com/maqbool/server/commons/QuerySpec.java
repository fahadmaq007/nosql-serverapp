package com.maqbool.server.commons;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * QuerySpec defines the whole query clause. It defines the className to be
 * operated on & the list of propertySpecs, orderBySpecs to be applied.
 * 
 * @author maqbool.ahmed
 * 
 */
public class QuerySpec implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -5631109974811514424L;

	private String className;

	private List<PropertySpec> propertySpecs = null;

	private List<OrderBySpec> orderBySpecs = null;

	private List<SelectSpec> selectSpecs = null;

	private List<GroupBySpec> groupBySpecs = null;
	
	public QuerySpec() {
		this(Entity.class.getName());
	}

	public QuerySpec(String className) {
		this.className = className;
	}

	public void addSelectSpec(SelectSpec s) {
		if (selectSpecs == null) {
			selectSpecs = new ArrayList<SelectSpec>();
		}
		getSelectSpecs().add(s);
	}
	
	public void setAsFirstPropertySpec(PropertySpec p) {
		if (propertySpecs == null) {
			propertySpecs = new ArrayList<PropertySpec>();
		}
		getPropertySpecs().add(0, p);
	}

	public void addPropertySpec(PropertySpec p) {
		if (propertySpecs == null) {
			propertySpecs = new ArrayList<PropertySpec>();
		}
		getPropertySpecs().add(p);
	}

	public void addOrderBySpec(OrderBySpec o) {
		if (orderBySpecs == null) {
			orderBySpecs = new ArrayList<OrderBySpec>();
		}
		getOrderBySpecs().add(o);
	}

	public void addGroupBySpec(GroupBySpec g) {
		if (groupBySpecs == null) {
			groupBySpecs = new ArrayList<GroupBySpec>();
		}
		getGroupBySpecs().add(g);
	}

	public String getClassName() {
		return className;
	}

	public void setClassName(String className) {
		this.className = className;
	}

	public List<PropertySpec> getPropertySpecs() {
		return propertySpecs;
	}

	public void setPropertySpecs(List<PropertySpec> propertySpecs) {
		this.propertySpecs = propertySpecs;
	}

	public List<OrderBySpec> getOrderBySpecs() {
		return orderBySpecs;
	}

	public void setOrderBySpecs(List<OrderBySpec> orderBySpecs) {
		this.orderBySpecs = orderBySpecs;
	}

	public List<SelectSpec> getSelectSpecs() {
		return selectSpecs;
	}

	public void setSelectSpecs(List<SelectSpec> selectSpecs) {
		this.selectSpecs = selectSpecs;
	}

	public List<GroupBySpec> getGroupBySpecs() {
		return groupBySpecs;
	}

	public void setGroupBySpecs(List<GroupBySpec> groupBySpecs) {
		this.groupBySpecs = groupBySpecs;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append(Constants.OPEN_BRACKET).append("classname=")
				.append(className).append(",\n");
		if (!Util.nullOrEmpty(propertySpecs)) {
			builder.append("propertySpecs=").append(propertySpecs)
					.append(",\n");
		}
		if (!Util.nullOrEmpty(orderBySpecs)) {
			builder.append("orderBySpecs=").append(orderBySpecs).append(",\n");
		}
		return builder.append(Constants.CLOSE_BRACKET).toString();
	}
}
