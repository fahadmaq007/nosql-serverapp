/**
 * 
 */
package com.maqbool.server.services;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.maqbool.server.commons.PageContent;
import com.maqbool.server.commons.PageDto;
import com.maqbool.server.dao.IDao;
import com.maqbool.server.exception.DataAccessException;
import com.maqbool.server.exception.ServiceException;

/**
 * @author maqboolahmed
 */
@Service
public class DocumentServiceImpl implements DocumentService {

	private final Logger logger = LoggerFactory.getLogger(getClass());

	private final String DEFAULT_SORT_PARAM = "-creationDate";

	@Autowired
	private IDao dao;
	
	@Override
	public PageContent<Map<String, Object>> listDocuments(String[] filters, String[] sortParams, PageDto page) throws ServiceException {
		logger.debug("listDocuments() API is been called..");
		try {
			return dao.listDocuments(filters, sortParams, page);
		} catch (DataAccessException e) {
			logger.error("listDocuments() API is failed to execute!!", e);
			throw new ServiceException(
					"Unable to list documents from database!!", e);
		}
	}
	
	private List<String> applyDefaultFilter(String[] filters) {
		return (filters == null || filters.length == 0) ? null : Arrays.asList(filters);
	}

}
