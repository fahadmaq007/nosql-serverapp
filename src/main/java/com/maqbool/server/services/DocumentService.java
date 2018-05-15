/**
 * 
 */
package com.maqbool.server.services;

import java.util.List;
import java.util.Map;

import com.maqbool.server.commons.PageContent;
import com.maqbool.server.commons.PageDto;
import com.maqbool.server.exception.DataAccessException;
import com.maqbool.server.exception.ServiceException;

/**
 * @author maqboolahmed
 *
 */
public interface DocumentService {

	/**
	 * List documents by given filter in the sort order if any.
	 * 
	 * @param filters
	 * @param sortParams
	 * @param page
	 * @param size
	 * @return pageContent object having list of documents
	 * @throws ServiceException
	 */
	public PageContent<Map> listDocuments(String[] filters, String[] sortParams, Integer page, Integer size) throws ServiceException;
	
	/**
	 * Get the document by id.
	 * @param id - non null primary key
	 * @return map object having documents details in key value pair
	 * @throws ServiceException throws in case fail
	 * @throws DataAccessException throws in case unable to find document
	 */
	public Map getDocumentById(String id)
			throws ServiceException, DataAccessException;

	/**
	 * Bulk upsert the given list of documents.
	 * @param orgId - Set via token.
	 * @param docType - non null mandatory input
	 * @param docs - non null list of map
	 * @return list of response map having id & status
	 * @throws ServiceException throws in case fail
	 */
	public List<Map> upsert(List<Map> docs) throws ServiceException;
}
