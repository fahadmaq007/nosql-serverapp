/**
 * 
 */
package com.maqbool.server.dao;

import java.util.Map;

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
	 * API for listing all the documents by applying filter
	 * @param filters
	 * @param sortParams
	 * @param page
	 * @return
	 * @throws ServiceException
	 */
	public PageContent<Map<String, Object>> listDocuments(String[] filters, String[] sortParams,
			PageDto page) throws DataAccessException;
	
}
