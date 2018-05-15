package com.maqbool.server.dao;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.beanutils.ConvertUtils;
import org.apache.commons.lang.math.NumberUtils;

import com.cloudant.client.api.query.Expression;
import com.cloudant.client.api.query.Selector;
import com.cloudant.client.api.query.Sort;
import com.maqbool.server.commons.Constants;
import com.maqbool.server.commons.Operation;
import com.maqbool.server.commons.QuerySpec;

/**
 * QueryTranslator is responsible for creating queries out of {@link QuerySpec}.
 * To-Do : This class is having many redundant methods which need to be clean later
 * @author niraj.gupta
 * 
 */
public class CouchCloudantQueryTranslator {
	
	private CouchCloudantQueryTranslator() {

	}

	public static List<Selector> buildSelector(String[] filters) {
		List<Selector> selectors = null;
		if (filters != null) {
			selectors = new ArrayList<Selector>();
			for (String filter : filters) {
				if (filter.contains(Constants.COLAN)) {
					String[] array = filter.split(Constants.COLAN);
					if (array.length > 2) {
						String propName = array[0];
						String op = array[1];
						Operation operation = Operation.valueOf(op);
						Object propValue = getObject(array[2]);
						selectors.add(getExpression(propName, operation, propValue));
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
		return selectors;
	}

	private static Expression getExpression(String key, Operation operation, Object value) {
		Expression e = null;
		switch (operation) {
		case EQ:
			e = Expression.eq(key, value);
			break;

		case LIKE:
			e = Expression.regex(key, "(?i)(" + value.toString() + ")");
			break;

		case GT:
			e = Expression.gt(key, value);
			break;

		case LT:
			e = Expression.lt(key, value);
			break;

		
		case GTE:
			e = Expression.gte(key, value);
			break;

		case LTE:
			e = Expression.lte(key, value);
			break;

		case NOTEQUAL:
			e = Expression.ne(key, value);
			break;

		case ISNULL:
			e = Expression.eq(key, null);
			break;

		case IS_NOT_NULL:
			e = Expression.ne(key, null);
			break;

		case IN:
			e = Expression.in(key, value);
			break;
			
		case NOT_IN:
			e = Expression.nin(key, value);
			break;

		default:
			throw new IllegalArgumentException("invalid operation " + operation);
		}
		return e;
	}

	private static Object getObject(String s) {
		Object v = s;
		if (s != null) {
			if (NumberUtils.isNumber(s)) {
				if (s.indexOf(".") > -1) {
					v = ConvertUtils.convert(s, BigDecimal.class);
				} else {
					v = ConvertUtils.convert(s, Long.class);
				}
			} 
		}
		return v;
	}

	/**
	 * This will build the sort clause.
	 * At present it supports only one param (1st)
	 * @param sortParams
	 * @return
	 */
	public static Sort buildSort(String[] sortParams) {
		Sort s = null;
		if (sortParams != null && sortParams.length > 0) {
			String param = sortParams[0];
			if (param.startsWith(Constants.HYPHEN)) {
				param = param.substring(1);
				s = Sort.desc(param);
			} else {
				s = Sort.asc(param);
			}
		}
		return s;
	}
}
