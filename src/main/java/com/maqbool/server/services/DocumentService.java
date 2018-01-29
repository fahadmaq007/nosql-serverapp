/**
 * 
 */
package com.maqbool.server.services;

import java.util.Map;

import com.maqbool.server.commons.PageContent;
import com.maqbool.server.commons.PageDto;
import com.maqbool.server.exception.ServiceException;

/**
 * @author maqboolahmed
 *
 */
public interface DocumentService {

	/**
	 * API for listing all the documents by applying filter
	 * @param orgId
	 * @param filters
	 * @param sortParams
	 * @param page
	 * @return
	 * @throws ServiceException
	 */
	public PageContent<Map<String, Object>> listDocuments(String[] filters, String[] sortParams,
			PageDto page) throws ServiceException;
	
}
