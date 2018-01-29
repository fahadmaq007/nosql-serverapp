package com.maqbool.server.dao;

import java.util.ArrayList;
import java.util.List;

import com.couchbase.client.java.document.json.JsonArray;
import com.couchbase.client.java.document.json.JsonObject;
import com.maqbool.server.commons.Constants;
import com.maqbool.server.commons.GroupBySpec;
import com.maqbool.server.commons.Operation;
import com.maqbool.server.commons.OrderByOperation;
import com.maqbool.server.commons.OrderBySpec;
import com.maqbool.server.commons.PageDto;
import com.maqbool.server.commons.PropertySpec;
import com.maqbool.server.commons.QuerySpec;
import com.maqbool.server.commons.Util;
import com.maqbool.server.exception.DataAccessException;

/**
 * QueryTranslator is responsible for creating queries out of {@link QuerySpec}.
 * To-Do : This class is having many redundant methods which need to be clean later
 * @author niraj.gupta
 * 
 */
public class CouchbaseQueryTranslator {
	
	private static final String COUNT_CLAUSE = "count(*) ";

	private static final String AND_CLAUSE = " and ";

	private static final String FROM_CLAUSE = "from ";

	private static final String WHERE_CLAUSE = " where ";

	private static final String EQUALS = " = ";

	private static final String LIKE_TEXT = " like ";

	private static final String GT_TEXT = " > ";

	private static final String LT_TEXT = " < ";

	private static final String BETWEEN_TEXT = " between ";

	private static final String GTE_TEXT = " >= ";

	private static final String LTE_TEXT = " <= ";

	private static final String NOTEQ_TEXT = " != ";

	private static final String ISNULL_TEXT = " is null ";

	private static final String IS_NOT_NULL_TEXT = " is not null ";

	private static final String IN_TEXT = " in ";

	private static final String NOTIN_TEXT = " not in ";

	private static final String COMMA = ",";

	private static final Object SPACE = " ";

	private static final String OPEN_PARANTHESIS = "(";

	private static final String CLOSE_PARANTHESIS = ")";

	private static final String SELECT_CLAUSE = "select ";

	private static final String ORDER_BY_CLAUSE = " order by ";

	private static final String START = "start";

	private static final String END = "end";

	private static final String GROUP_BY_CLAUSE = " group by ";
	
	private static final Object DOLLAR = "$";
	
	private static final String LIMIT = "limit";
	
	private static final String OFFSET = "offset";
	
	private static final String AS = "as ";
	
	private static final String COUNT = "count ";

	private CouchbaseQueryTranslator() {

	}

	public static String createCountQuery(QuerySpec spec, String bucketName){
		StringBuilder countQuery = new StringBuilder(SELECT_CLAUSE);
		countQuery.append(COUNT_CLAUSE).append(AS).append(COUNT);
		countQuery.append(FROM_CLAUSE).append(bucketName);
		countQuery.append(createWhereClause(spec.getPropertySpecs()));
		String query = countQuery.toString();
		return query;
	}

	/**
	 * Adds the Order-By-Clause as per the given {@link OrderBySpec}
	 * 
	 * @param c
	 *            - generated criteria
	 * @param orderBySpec
	 *            - given {@link OrderBySpec}
	 */
	public static StringBuilder createOrderByClause(
			List<OrderBySpec> orderBySpecs) {
		StringBuilder orderByClause = new StringBuilder();
		if (!Util.nullOrEmpty(orderBySpecs)) {
			orderByClause.append(ORDER_BY_CLAUSE);
			int count = 0;
			for (OrderBySpec orderBySpec : orderBySpecs) {
				String propName = orderBySpec.getPropertyName();
				OrderByOperation operation = orderBySpec.getOperation();
				if (operation != null & OrderByOperation.ASC == operation) {
					orderByClause.append(propName + SPACE
							+ OrderByOperation.ASC);
				} else {
					orderByClause.append(propName + SPACE
							+ OrderByOperation.DESC);
				}
				if (++count < orderBySpecs.size()) {
					orderByClause.append(COMMA);
				}
			}
		}
		return orderByClause;
	}

	/**
	 * Adds the Group-By-Clause as per the given {@link GroupBySpec}
	 * 
	 * @param groupBySpecs
	 *            - given {@link GroupBySpec}
	 * @return Group by clause with given parameters.
	 */
	public static StringBuilder createGroupByClause(
			List<GroupBySpec> groupBySpecs) {
		StringBuilder groupByClause = new StringBuilder();
		if (!Util.nullOrEmpty(groupBySpecs)) {
			groupByClause.append(GROUP_BY_CLAUSE);
			int count = 0;
			for (GroupBySpec groupBySpec : groupBySpecs) {
				String propName = groupBySpec.getPropertyName();
				groupByClause.append(propName);
				if (++count < groupBySpecs.size()) {
					groupByClause.append(COMMA);
				}
			}
		}
		return groupByClause;
	}

	/**
	 * Creates the query string from the QuerySpec
	 * 
	 * @param spec
	 * @return query string
	 * @throws DataAccessException
	 */
	public static String createQuery(QuerySpec spec, String bucketName, String[] selectParams, List<String> keys) {
		StringBuilder selectQuery = new StringBuilder(SELECT_CLAUSE);
		
		if (selectParams == null) {
			//Select all
			selectQuery.append("meta().id as id, ");
			selectQuery.append(bucketName).append(".* ");
		} else {
			boolean first = true;
			for (String select : selectParams) {
 				if (!first) {
 					selectQuery.append(",");
 				}
 				selectQuery.append(bucketName).append(".").append(select);
 				first = false;
			}
		}
		selectQuery.append(" ");
		selectQuery.append(FROM_CLAUSE);
		selectQuery.append(bucketName);
		
		if(!Util.nullOrEmpty(keys)){
			selectQuery.append(" USE KEYS ");
			List<String> keyValues = new ArrayList<String>();
			for(String key:keys){
				keyValues.add("\"" + key + "\"");
			}
			selectQuery.append(keyValues);
		}
		
		
		List<PropertySpec> propSpecs = spec.getPropertySpecs();
		selectQuery.append(createWhereClause(propSpecs));

		List<GroupBySpec> groupSpecs = spec.getGroupBySpecs();
		if (!Util.nullOrEmpty(groupSpecs)) {
			selectQuery.append(createGroupByClause(groupSpecs));
		}
		
		List<OrderBySpec> orderSpecs = spec.getOrderBySpecs();
		if (!Util.nullOrEmpty(orderSpecs)) {
			selectQuery.append(createOrderByClause(orderSpecs));
		}
		String query = selectQuery.toString();
		return query;
	}

	/**
	 * Creates a where clause using given propSpecs
	 * 
	 * @param propSpecs
	 * @return where clause string
	 */
	public static String createWhereClause(List<PropertySpec> propSpecs) {
		StringBuilder whereClause = new StringBuilder();

		if (!Util.nullOrEmpty(propSpecs)) {
			whereClause.append(WHERE_CLAUSE);
			int n = propSpecs.size();
			for (int i = 0; i < n; i++) {
				PropertySpec propSpec = propSpecs.get(i);
				String propertyName = propSpec.getPropertyName();
				whereClause.append(propertyName).append(
							getOperationString(propSpec));	
				if (i < n - 1) {
					whereClause.append(AND_CLAUSE);
				}
			}
		}
		return whereClause.toString();
	}

	private static String getOperationString(PropertySpec propSpec) {
		String propertyName = propSpec.getPropertyName();
		Object value = propSpec.getValue();
		Operation operation = propSpec.getOperation();
		String opText = EQUALS + DOLLAR + propertyName;
		switch (operation) {
		case EQ:
			break;

		case LIKE:
			opText = LIKE_TEXT + DOLLAR + propertyName;
			break;

		case GT:
			opText = GT_TEXT + DOLLAR + propertyName;
			break;

		case LT:
			opText = LT_TEXT + DOLLAR + propertyName;
			break;

		case BETWEEN:
			opText = BETWEEN_TEXT + DOLLAR + START + AND_CLAUSE + DOLLAR + END;
			break;

		case GTE:
			opText = GTE_TEXT + DOLLAR + propertyName;
			break;

		case LTE:
			opText = LTE_TEXT + DOLLAR + propertyName;
			break;

		case NOTEQUAL:
			opText = NOTEQ_TEXT + DOLLAR + propertyName;
			break;

		case ISNULL:
			opText = ISNULL_TEXT;
			break;

		case IS_NOT_NULL:
			opText = IS_NOT_NULL_TEXT;
			break;

		case IN:
//			opText = IN_TEXT + DOLLAR + propertyName;
//			break;
			
		case NOT_IN:
			if (value == null) {
				throw new IllegalArgumentException(
						"Operation IN requires either Collection or Object[]");
			}

			if (operation == Operation.IN) {
				opText = IN_TEXT + DOLLAR + propertyName;
			} else if (operation == Operation.NOT_IN) {
				opText = NOTIN_TEXT + DOLLAR + propertyName;
			}
//			opText += OPEN_PARANTHESIS + propertyName
//					+ CLOSE_PARANTHESIS;
			break;

		default:
			throw new IllegalArgumentException("invalid operation " + operation);
		}
		return opText;
	}
	
	public static void setParameterByName(JsonObject namedParams, List<PropertySpec> propList) {
		if (Util.nullOrEmpty(propList)) {
			return;
		}
		for (PropertySpec prop : propList) {
			Operation op = prop.getOperation();
			String propName = prop.getPropertyName();
			Object v = prop.getValue();
			switch (op) {
			case BETWEEN:
				if (v instanceof Object[]) {
					Object[] array = (Object[]) v;
					if (array.length < 2) {
						throw new IllegalArgumentException(
								"Operation BETWEEN requires START & END values...");
					}
					Object start = array[0];
					Object end = array[1];
					namedParams.put(START,start);
					namedParams.put(END, end);
				}
				break;

			case LIKE:
				if (v == null) {
					v = Constants.EMPTY_STRING;
				}
				if (!(v instanceof String)) {
					v = v.toString();
				}
				namedParams.put(propName, Constants.PERCENTAGE + v
						+ Constants.PERCENTAGE);
				break;
				
			case ISNULL:
			case IS_NOT_NULL:
				break;
				
			case IN:
				String v1 = v.toString().replace("[", "").replace("]", "").replace("\"", "").replace("'", "");
				String[] arr = v1.split(",");
				JsonArray array = JsonArray.empty();
				for (String str : arr) {
					array.add(str);
				}
				namedParams.put(propName, array);
				break;
			default:
				namedParams.put(propName, v);
				break;
			}
		}
	}
	
	public static String setPaginationParam(String query, PageDto pageDto) {
		return new StringBuffer(query)
		.append(SPACE)
		.append(LIMIT)
		.append(SPACE)
				.append(pageDto.getPageSize()).append(SPACE).append(OFFSET).append(SPACE).append(pageDto.getPageSize()*(pageDto.getPageNumber()-1)).toString();
	}
}
