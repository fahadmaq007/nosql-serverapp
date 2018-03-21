package com.maqbool.server.dao;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.lightcouch.CouchDbClient;
import org.lightcouch.CouchDbProperties;
import org.lightcouch.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;

import com.google.gson.JsonObject;
import com.maqbool.server.commons.PageContent;
import com.maqbool.server.commons.PageDto;
import com.maqbool.server.commons.PropertySpec;
import com.maqbool.server.commons.QuerySpec;
import com.maqbool.server.commons.Util;
import com.maqbool.server.exception.DataAccessException;

/**
 * Implementation of the Repository interface that uses the synchronous API
 * exposed by the Couchbase Java SDK.
 * 
 * @author maqboolahmed
 */
//@org.springframework.stereotype.Repository
//@Qualifier("couchDao")
public class CouchDao implements IDao {
	Logger logger = LoggerFactory.getLogger(getClass());

	// ec2-34-213-181-90.us-west-2.compute.amazonaws.com
	@Value("${couch.nodes:localhost}")
	private String nosqlnodes;

	@Value("${couch.db:ml100k}")
	private String defaultDbName;

	private final String DEFAULT_SORT_PARAM = "-creationDate";

	public String getNosqlnodes() {
		return nosqlnodes;
	}

	public void setNosqlnodes(String nosqlnodes) {
		this.nosqlnodes = nosqlnodes;
	}

	private CouchDbClient dbClient;
	
	@PostConstruct
	public void initIt() throws Exception {
		logger.info("CouchbaseRepository instance is creating...");
		logger.info("nosqlnodes ==" + nosqlnodes);
		try {
			CouchDbProperties properties = new CouchDbProperties()
					 .setDbName(defaultDbName)
					 .setCreateDbIfNotExist(true)
					 .setProtocol("http")
					 .setHost(nosqlnodes)
					 .setPort(5984)
					 .setMaxConnections(100)
					 .setConnectionTimeout(0);
			dbClient = new CouchDbClient(properties);
		} catch (Exception e) {
			logger.error("Either cluster creation or bucket open operation failed!!");
			throw new DataAccessException(e);
		}
	}

	@PreDestroy
	public void cleanUp() throws Exception {
		logger.info("Respository instance is destroying....");
		if (dbClient != null) {
			dbClient.shutdown();
		}
	}

	@Override
	public PageContent<Map<String, Object>> listDocuments(
			String[] filters, String[] sortParams,
			PageDto page) throws DataAccessException {
		try {
			QuerySpec spec = Util.buildQuerySpec(applyDefaultFilter(filters),
					applyDefaultSortParam(sortParams));

			String query = CouchbaseQueryTranslator.createQuery(spec,
					defaultDbName, null, null);

			if (page != null) {
				query = CouchbaseQueryTranslator.setPaginationParam(query,
						page);
			}

			List<PropertySpec> propSpec = spec.getPropertySpecs();
			
			return null;
			

		} catch (Exception e) {
			logger.error("problem while retrieving data from couchbase", e);
			throw new DataAccessException(e);
		}
	}

	private List<String> applyDefaultFilter(String[] filters) {
		return (filters == null || filters.length == 0) ? null : Arrays
				.asList(filters);
	}

	private List<String> applyDefaultSortParam(String[] sortParams) {
		List<String> sorts = new ArrayList<String>();
		if (sortParams == null) {
			logger.debug("'descending order by creation date', default sort is applied.");
			sorts.add(DEFAULT_SORT_PARAM);
		} else {
			sorts = Arrays.asList(sortParams);
		}
		return sorts;
	}
	
	@Override
	public void bulkUpsert(List<Map<String, Object>> documents) throws DataAccessException {
		List<Response> bulkResponse = dbClient.bulk(documents, true);
		logger.info("bulk upsert has created " + bulkResponse.size() + " records");
	}
	
}