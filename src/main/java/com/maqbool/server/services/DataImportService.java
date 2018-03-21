/**
 * 
 */
package com.maqbool.server.services;

import java.io.File;

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
	
}
