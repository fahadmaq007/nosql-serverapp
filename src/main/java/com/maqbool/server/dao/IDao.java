/**
 * 
 */
package com.maqbool.server.dao;

import java.util.List;
import java.util.Map;

import com.couchbase.client.java.document.JsonDocument;
import com.maqbool.server.commons.PageContent;
import com.maqbool.server.commons.PageDto;
import com.maqbool.server.exception.DataAccessException;
import com.maqbool.server.exception.ServiceException;

/**
 * @author maqboolahmed
 *
 */
public interface IDao {

	/**
	 * Lists the documents by given filter
	 * @param filters
	 * @param sortParams
	 * @param page
	 * @return {@link PageContent} of documents
	 * @throws ServiceException
	 */
	public PageContent<Map<String, Object>> listDocuments(String[] filters, String[] sortParams,
			PageDto page) throws DataAccessException;
	
	/**
	 * Upserts the given list of documents
	 * @param documents of {@link JsonDocument}
	 * @throws DataAccessException
	 */
	public void bulkUpsert(List<Map<String, Object>> documents) throws DataAccessException;
}
