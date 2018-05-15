/**
 * 
 */
package com.maqbool.server.services;

import java.io.File;
import java.util.Map;

import com.maqbool.server.commons.PageContent;
import com.maqbool.server.commons.PageDto;
import com.maqbool.server.exception.ServiceException;

/**
 * @author maqboolahmed
 *
 */
public interface DataImportService {

	/**
	 * Imports the file data into the database.
	 * @param file
	 * @param type
	 * @param separator
	 * @return
	 * @throws ServiceException
	 */
	 
	public void importFile(File file, String type, 
			String[] columns, int[] idColumns, String separator) throws ServiceException;
	
	public PageContent<Map> list(String[] filters, String[] sort, PageDto page) throws ServiceException;

	public void importCouchbaseFile(File file, String datasource, String comma) throws ServiceException;
	
	public void importJson(File file, String datasource) throws ServiceException;
}
