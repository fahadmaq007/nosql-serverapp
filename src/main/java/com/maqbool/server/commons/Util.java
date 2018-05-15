package com.maqbool.server.commons;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cloudant.client.api.model.Response;
import com.google.gson.Gson;


/**
 * Utility class with common operations.
 * 
 * @author maqboolahmed
 *
 */
public class Util {

	private static Logger logger = LoggerFactory.getLogger(Util.class);

	
	/**
	 * Checks whether given collection is not null or non-empty.
	 * 
	 * @param c
	 *            Colletion instance
	 * @return true if null or empty, otherwise false;
	 */
	public static boolean nullOrEmpty(@SuppressWarnings("rawtypes") Collection c) {
		return c == null || c.isEmpty();
	}

	/**
	 * Checks whether the passed string is a null or empty.
	 * 
	 * @param text
	 *            to check
	 * @return true is null or empty, otherwise false.
	 */
	public static boolean nullOrEmpty(String text) {
		return text == null || text.length() == 0;
	}

	/**
	 * Returns the random UUID.
	 * 
	 * @return
	 */
	public static String newUuid() {
		String uuid = UUID.randomUUID().toString();
		return uuid;
	}
	
	public static QuerySpec buildQuerySpec(List<String> filters,
			List<String> sortParams) {
		QuerySpec q = new QuerySpec();
		
		if (filters != null) {
			for (String filter : filters) {
				if (filter.contains(Constants.COLAN)) {
					String[] array = filter.split(Constants.COLAN);
					if (array.length > 2) {
						String propName = array[0];
						String op = array[1];
						Operation operation = Operation.valueOf(op);
						String propValue = array[2];
						q.addPropertySpec(new PropertySpec(propName, operation,
								propValue));
					} else {
						throw new IllegalArgumentException(filter
								+ " should have 3 segments delimited with "
								+ Constants.COLAN);
					}
				} else {
					throw new IllegalArgumentException(filter
							+ " doesn't contain the delimiter "
							+ Constants.COLAN);
				}
			}
		}
		if (sortParams != null) {
			for (String sortParam : sortParams) {
				OrderByOperation order = OrderByOperation.ASC;
				if (sortParam.startsWith(Constants.HYPHEN)) {
					sortParam = sortParam.substring(1);
					order = OrderByOperation.DESC;
				}
				q.addOrderBySpec(new OrderBySpec(sortParam, order));
			}
		}
		return q;
	}
	
	public static <T> T fromJson(String json, Class<T> clazz) {
		final Gson gson = new Gson();
		try {
			return gson.fromJson(json, clazz);
		} catch (com.google.gson.JsonSyntaxException ex) {
			return null;
		}
	}

	public static String toJson(Object o) {
		final Gson gson = new Gson();
		try {
			return gson.toJson(o);
		} catch (com.google.gson.JsonSyntaxException ex) {
			return null;
		}
	}
}