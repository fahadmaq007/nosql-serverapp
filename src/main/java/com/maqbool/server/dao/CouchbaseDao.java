package com.maqbool.server.dao;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;

import com.couchbase.client.core.CouchbaseException;
import com.couchbase.client.java.Bucket;
import com.couchbase.client.java.Cluster;
import com.couchbase.client.java.CouchbaseCluster;
import com.couchbase.client.java.document.JsonDocument;
import com.couchbase.client.java.document.json.JsonObject;
import com.couchbase.client.java.env.CouchbaseEnvironment;
import com.couchbase.client.java.env.DefaultCouchbaseEnvironment;
import com.couchbase.client.java.error.TemporaryFailureException;
import com.couchbase.client.java.query.N1qlQueryResult;
import com.couchbase.client.java.query.N1qlQueryRow;
import com.couchbase.client.java.query.ParameterizedN1qlQuery;
import com.couchbase.client.java.util.retry.RetryBuilder;
import com.maqbool.server.commons.PageContent;
import com.maqbool.server.commons.PageDto;
import com.maqbool.server.commons.PropertySpec;
import com.maqbool.server.commons.QuerySpec;
import com.maqbool.server.commons.Util;
import com.maqbool.server.exception.DataAccessException;

import rx.Observable;
import rx.functions.Func1;

/**
 * Implementation of the Repository interface that uses the synchronous API
 * exposed by the Couchbase Java SDK.
 * 
 * @author maqboolahmed
 */
//@org.springframework.stereotype.Repository
//@Qualifier("couchbaseDao")
public class CouchbaseDao implements IDao {
	Logger logger = LoggerFactory.getLogger(getClass());

	private Cluster cluster;

	@Value("${couchbase.nodes:localhost}")
	private String nosqlnodes;

	@Value("${couchbase.bucket:cmf_data}")
	private String defaulBucketName;

	private final String DEFAULT_SORT_PARAM = "-creationDate";

	public String getNosqlnodes() {
		return nosqlnodes;
	}

	public void setNosqlnodes(String nosqlnodes) {
		this.nosqlnodes = nosqlnodes;
	}

	@PostConstruct
	public void initIt() throws Exception {
		logger.info("CouchbaseRepository instance is creating...");
		logger.info("nosqlnodes ==" + nosqlnodes);
		final List<String> SEED_IPS = Arrays.asList(nosqlnodes);

		try {
			CouchbaseEnvironment env = DefaultCouchbaseEnvironment
					.builder()
					.connectTimeout(10000)
					.build();

			cluster = CouchbaseCluster.create(env, SEED_IPS);
//			cluster.authenticate("admin", "briter");
		} catch (CouchbaseException e) {
			logger.error("Either cluster creation or bucket open operation failed!!");
			throw new DataAccessException(e);
		}
	}

	@PreDestroy
	public void cleanUp() throws Exception {
		logger.info("Respository instance is destroying....");
		if (cluster != null) {
			cluster.disconnect();
		}
	}

	@Override
	public PageContent<Map> listDocuments(
			String[] filters, String[] sortParams,
			PageDto page) throws DataAccessException {
		try {
			QuerySpec spec = Util.buildQuerySpec(applyDefaultFilter(filters),
					applyDefaultSortParam(sortParams));

			String query = CouchbaseQueryTranslator.createQuery(spec,
					defaulBucketName, null, null);

			if (page != null) {
				query = CouchbaseQueryTranslator.setPaginationParam(query,
						page);
			}

			List<PropertySpec> propSpec = spec.getPropertySpecs();
			JsonObject namedParams = JsonObject.create();
			CouchbaseQueryTranslator.setParameterByName(namedParams, propSpec);
			try {
				Bucket bucket = cluster.openBucket(defaulBucketName);
				ParameterizedN1qlQuery pq = ParameterizedN1qlQuery
						.parameterized(query, namedParams);
				logger.debug(pq.toString());
				N1qlQueryResult result = bucket.query(pq);
				if (result.finalSuccess()) {
					List<Map> list = new ArrayList<Map>();
					for (N1qlQueryRow row : result) {
						JsonObject o = row.value();
						Map m = o.toMap();
						list.add(m);
					}
					PageContent<Map> pageData = new PageContent<Map>();
					pageData.setList(list);
					return pageData;
				} else {
					logger.warn("Query returned with errors: "
							+ result.errors());
					throw new DataAccessException("Query error: "
							+ result.errors());
				}
			} catch (CouchbaseException e) {
				logger.error("problem while retrieving data from couchbase", e);
				throw new DataAccessException(e);
			}

		} catch (CouchbaseException e) {
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
	public List<Map> bulkUpsert(List<Map> documents) throws DataAccessException {
		if (documents != null && documents.size() > 0) {
			List<JsonDocument> jsonDocs = toJsonDocuments(documents);
			final Bucket bucket = cluster.openBucket(defaulBucketName);
			Observable
		    .from(jsonDocs)
		    .flatMap(new Func1<JsonDocument, Observable<JsonDocument>>() {
				@SuppressWarnings("unchecked")
				@Override
				public Observable<JsonDocument> call(JsonDocument docToInsert) {
					return bucket.async().upsert(docToInsert)
							.timeout(3000, TimeUnit.MILLISECONDS)
							.retryWhen(RetryBuilder.anyOf(TemporaryFailureException.class)
									.max(2).build());
				}
		    })
		    .last()
		    .toBlocking()
		    .single();
		}
		return documents;
	}
	
	private List<JsonDocument> toJsonDocuments(List<Map> docs) {
		List<JsonDocument> documents = new ArrayList<JsonDocument>();
		for (Map map : docs) {
			JsonObject jsonObject = JsonObject.from(map);
			String id = (String) map.get("_id");
			if (id == null || id.length() == 0) {
				id = Util.newUuid();
			}
			JsonDocument doc = JsonDocument.create(id, jsonObject);
			documents.add(doc);
		}
		return documents;
	}

	@Override
	public Map getDocumentById(String id) throws DataAccessException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setDataSource(String ds) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String getDataSource() {
		// TODO Auto-generated method stub
		return null;
	}
}