package com.maqbool.server.dao;

import java.io.File;
import java.net.URL;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;

import com.cloudant.client.api.ClientBuilder;
import com.cloudant.client.api.CloudantClient;
import com.cloudant.client.api.Database;
import com.cloudant.client.api.DesignDocumentManager;
import com.cloudant.client.api.model.DesignDocument;
import com.cloudant.client.api.model.Response;
import com.cloudant.client.api.query.JsonIndex;
import com.cloudant.client.api.query.Operation;
import com.cloudant.client.api.query.QueryBuilder;
import com.cloudant.client.api.query.QueryResult;
import com.cloudant.client.api.query.Selector;
import com.cloudant.client.api.query.Sort;
import com.cloudant.http.Http;
import com.maqbool.server.commons.PageContent;
import com.maqbool.server.commons.PageDto;
import com.maqbool.server.commons.Util;
import com.maqbool.server.exception.DataAccessException;

/**
 * Implementation of the Repository interface that uses the synchronous API
 * exposed by the Couchbase Java SDK.
 * 
 * @author maqboolahmed
 */
@org.springframework.stereotype.Repository
@Qualifier("couchCloudantDao")
public class CouchCloudantDao implements IDao {
	Logger logger = LoggerFactory.getLogger(getClass());

	// ec2-34-213-181-90.us-west-2.compute.amazonaws.com
	@Value("${couch.nodes:localhost}")
	private String nosqlnodes;
	
	private CloudantClient client;

	private String dataSource;
	
	private String[] indiciesOn = { "type", "parentId", "createdDate" };
	
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
		try {
			client =  ClientBuilder.url(new URL("http://localhost:5984"))
	          .username("admin")
	          .password("briter")
	          .build();
		} catch (Exception e) {
			logger.error("Either cluster creation or bucket open operation failed!!");
			throw new DataAccessException(e);
		}
	}

	@Override
	public PageContent<Map> listDocuments(
			String[] filters, String[] sortParams,
			PageDto page) throws DataAccessException {
		PageContent<Map> pageContent = null;
		try {
			Database db = getDb(this.dataSource);
			List<Selector> selectors = CouchCloudantQueryTranslator.buildSelector(filters);
			QueryBuilder queryBuilder = null;
			if (! Util.nullOrEmpty(selectors)) {
				if (selectors.size() > 1) {
					queryBuilder = new QueryBuilder(Operation.and(selectors.toArray(new Selector[selectors.size()])));
				} else {
					queryBuilder = new QueryBuilder(selectors.get(0));
				}
			}
			Sort sort = CouchCloudantQueryTranslator.buildSort(sortParams);
			if (sort != null) {
				queryBuilder = queryBuilder.sort(sort);
			}
			String query = queryBuilder.
					   build();
			System.out.println("query is: " + query);
			QueryResult<Map> result = db.query(query, Map.class);
			if (result != null) {
				pageContent = new PageContent<Map>();
				pageContent.setList(result.getDocs());
				pageContent.setPageDto(page);
			}
		} catch (Exception e) {
			logger.error("problem while retrieving data from couchbase", e);
			throw new DataAccessException(e);
		}
		return pageContent;
	}
	
	private Database getDb(String ds) {
		Database db = null;
		try {
			client.createDB(this.dataSource);
			db = client.database(this.dataSource, false);
			runTasksPostDbCreation(db);
		} catch (Exception e) {
			db = client.database(this.dataSource, false);
		}
		
//		db = client.database(this.dataSource, false);
//		try {
//		  client.executeRequest(Http.HEAD(db.getDBUri()));
//		} catch(Exception e) {
//		  // Didn't exist, create and initialize
//		  db = client.database(this.dataSource, true);
//		  runTasksPostDbCreation(db);
//		  // initialize
//		}
		
		return db;
	}

	private void runTasksPostDbCreation(Database db) {
		for (String indexOn : indiciesOn) {
			String indexDefinition = JsonIndex.builder().asc(indexOn).name("index_" + indexOn).definition();
			db.createIndex(indexDefinition);
		}
		
		DesignDocumentManager designManager = db.getDesignDocumentManager();
		try {
			File f = new File(this.getClass().getResource("/design-dir/app_design_doc.js").toURI());
			DesignDocument designDocument = designManager.fromFile(f);
			designManager.put(designDocument);
		} catch (Exception e) {
			logger.error("design-dir is not found in the classpath");
		}
	}

	@Override
	public List<Map> bulkUpsert(List<Map> documents) throws DataAccessException {
		Database db = getDb(this.dataSource);
		List<Response> bulkResponse = db.bulk(documents); 
		logger.info("bulk upsert has created " + bulkResponse.size() + " records");
//		String json = Util.toJson(bulkResponse);
//		List l = Util.fromJson(json, List.class);
		return null ;
	}

	@Override
	public Map getDocumentById(String id) throws DataAccessException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setDataSource(String dataSource) {
		this.dataSource = dataSource;
	}

	@Override
	public String getDataSource() {
		return this.dataSource;
	}
	
}