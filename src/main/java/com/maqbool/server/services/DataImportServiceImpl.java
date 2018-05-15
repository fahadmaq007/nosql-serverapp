/**
 * 
 */
package com.maqbool.server.services;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.LineIterator;
import org.apache.commons.lang.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;
import com.maqbool.server.commons.Constants;
import com.maqbool.server.commons.PageContent;
import com.maqbool.server.commons.PageDto;
import com.maqbool.server.commons.Util;
import com.maqbool.server.dao.IDao;
import com.maqbool.server.exception.DataAccessException;
import com.maqbool.server.exception.ServiceException;

/**
 * @author maqboolahmed
 */
@Service
public class DataImportServiceImpl implements DataImportService {

	private final Logger logger = LoggerFactory.getLogger(getClass());

	private final static int SIZE_MB = 1024 * 1024;

	private final static int MAX_SIZE = 25 * SIZE_MB; // 25 mb

	private static final String DEFAULT_SEPARATOR = "|";

	private final static int BATCH_SIZE = 10000;

	@Autowired
	@Qualifier("couchCloudantDao")
	private IDao dao;

	@Override
	public void importFile(File file, String type, String[] columns, int[] idColumns, String separator)
			throws ServiceException {
		if (file.exists()) {
			dao.setDataSource("ml2m");
			if (isExceedingMaxSize(file)) {
				try {
					List<File> files = split(file);
					for (File f : files) {
						importFile0(f, type, columns, idColumns, separator);
					}
				} catch (Exception e) {
					throw new ServiceException(e.getMessage(), e);
				}
			} else {
				importFile0(file, type, columns, idColumns, separator);
			}
		} else {
			logger.error("no such file found: " + file);
		}
	}

	private void importFile0(File file, String type, String[] columns, int[] idColumns, String separator)
			throws ServiceException {
		System.out.println("about to import data from " + file.getAbsolutePath());
		LineIterator it = null;
		separator = getSeparator(separator);
		List<Map> documents = new ArrayList<Map>();
		try {
			it = FileUtils.lineIterator(file, "UTF-8");
			int batchCount = 1;

			while (it.hasNext()) {
				String line = it.nextLine();
				String[] columnsData = line.split(Pattern.quote(separator));
				if (columnsData == null || columnsData.length < columns.length) {
					continue;
				}
				int i = 0;
				Map document = getDocument(type, columnsData, idColumns);
				for (String col : columns) {
					int index = col.indexOf(Constants.COLAN);
					if (index > -1) {
						String colType = col.substring(index + 1);
						col = col.substring(0, index);
						if (NumberUtils.isNumber(columnsData[i])) {
							if (colType.equalsIgnoreCase("int")) {
								document.put(col, Integer.valueOf(columnsData[i]));
							} else if (colType.equalsIgnoreCase("float")) {
								document.put(col, Double.valueOf(columnsData[i]));
							}
						}
					} else {
						String data = columnsData[i];
						document.put(col, data);
					}
					i++;
				}
				documents.add(document);
				int size = documents.size();
				if (size % BATCH_SIZE == 0) {
					System.out.println("uploading batch#" + batchCount + " of " + BATCH_SIZE);
					dao.bulkUpsert(documents);
					documents = new ArrayList<Map>();
					batchCount++;
//					Thread.sleep(1000);
				}
			}
			if (!Util.nullOrEmpty(documents)) {
				System.out.println("last batch#" + batchCount + " of " + documents.size());
				dao.bulkUpsert(documents);
			}
		} catch (Exception e) {
			throw new ServiceException(e.getMessage(), e);
		} finally {
			if (it != null)
				LineIterator.closeQuietly(it);
		}
	}

	private Map<String, Object> getDocument(String type, String[] columnsData, int[] idColumns) {
		Map<String, Object> content = new HashMap<String, Object>();
		content.put(Constants.TYPE, type);
		String id = Constants.EMPTY_STRING;
		if (idColumns == null) {
			id = Util.newUuid();
		} else {
			int i = 0;
			for (int idColumn : idColumns) {
				String idData = columnsData[idColumn];
				id += idData;
				if (i < idColumns.length - 1) {
					id += Constants.UNDERSCORE;
				}
				i++;
			}
		}
		content.put(Constants.ID, id);
		List<String> channels = new ArrayList<String>();
		channels.add("public");
		content.put("channels", channels);
		content.put(Constants.CREATION_DATE, System.currentTimeMillis());
		String key = type + Constants.DBL_COLAN + id;
		content.put(Constants.UNDERSCORE + Constants.ID, key);
		return content;
	}

	private String getSeparator(String separator) {
		if (Util.nullOrEmpty(separator)) {
			return DEFAULT_SEPARATOR;
		}
		return separator;
	}

	private List<File> split(File f) throws FileNotFoundException, IOException {
		List<File> files = new ArrayList<File>();
		int partCounter = 1;
		// I like to name parts from 001, 002, 003, ...
		// you can change it to 0 if you want 000, 001, ...

		int sizeOfFiles = MAX_SIZE;
		byte[] buffer = new byte[sizeOfFiles];

		String fileName = f.getName();

		// try-with-resources to ensure closing stream
		try (FileInputStream fis = new FileInputStream(f); BufferedInputStream bis = new BufferedInputStream(fis)) {

			int bytesAmount = 0;
			while ((bytesAmount = bis.read(buffer)) > 0) {
				// write each chunk of data into separate file with different number in name
				String filePartName = String.format("%03d_%s", partCounter++, fileName);
				File newFile = new File(f.getParent(), filePartName);
				try (FileOutputStream out = new FileOutputStream(newFile)) {
					out.write(buffer, 0, bytesAmount);
					files.add(newFile);
				}
			}
		}
		return files;
	}

	private boolean isExceedingMaxSize(File file) {
		long fileSize = file.length();
		return fileSize > MAX_SIZE;
	}

	@Override
	public PageContent<Map> list(String[] filters, String[] sortParams, PageDto page) throws ServiceException {
		try {
			return dao.listDocuments(filters, sortParams, page);
		} catch (DataAccessException e) {
			throw new ServiceException(e.getMessage(), e);
		}
	}

	@Override
	public void importCouchbaseFile(File file, String dataSource, String separator) throws ServiceException {
		LineIterator it = null;
		separator = getSeparator(separator);
		List<Map> documents = new ArrayList<Map>();
		try {
			it = FileUtils.lineIterator(file, "UTF-8");
			int batchCount = 1;

			while (it.hasNext()) {
				String line = it.nextLine();
				line = line.replaceAll("\"\"", "'");
				line = line.replaceAll("\"", "");

				Map document = Util.fromJson(line, Map.class);
				if (document == null) {
					continue;
				}
				Object id = document.get(Constants.ID);
				if (id instanceof String && id.equals("0e4b3f5a-a095-90a1-2bfb-eed856c85388")) {
					System.out.println(id);
				} else if (!(id instanceof String)) {
					System.out.println(id);
				}
				if (!document.containsKey(Constants.TYPE)) {
					continue;
				}
				// List<String> orgIds = new ArrayList<String>();
				// orgIds.add("c0b04480-0057-4b95-ad29-23dc09c0a489");
				// String orgId = (String) document.get(Constants.ORG_ID);
				// if (orgIds.indexOf(orgId) == -1) {
				// continue;
				// }
				addId(document);
				documents.add(document);
				int size = documents.size();
				if (size % BATCH_SIZE == 0) {
					System.out.println("uploading batch#" + batchCount + " of " + BATCH_SIZE);
					// upsertPerDataSource(dataSource, documents);
					documents = new ArrayList<Map>();
					batchCount++;
					Thread.sleep(3000);
				}
			}
			if (!Util.nullOrEmpty(documents)) {
				System.out.println("last batch#" + batchCount + " of " + documents.size());
				// upsertPerDataSource(dataSource, documents);
			}
		} catch (Exception e) {
			throw new ServiceException(e.getMessage(), e);
		} finally {
			if (it != null)
				LineIterator.closeQuietly(it);
		}
	}

	private void addId(Map document) {
		String id = (String) document.get(Constants.ID);
		String type = (String) document.get(Constants.TYPE);
		String _id = null;
		if (type != null && id != null) {
			_id = type + "::" + id;
		} else {
			_id = Util.newUuid();
		}
		document.put("_id", _id);
	}

	private void upsertPerDataSource(String ds, List<Map> documents) throws DataAccessException {
		Map<String, List<Map>> dsMap = new HashMap<String, List<Map>>();
		for (Map map : documents) {
			String type = (String) map.get(Constants.TYPE);
			if (!Util.nullOrEmpty(type)) {
				String dsValue = (String) map.get(ds);
				if (type.startsWith(Constants.SYS_DOC)) {
					dsValue = Constants.SYS_DOC + (String) map.get(Constants.ORG_ID);
				}
				if (dsValue == null) {
					continue;
				}
				List<Map> list = dsMap.get(dsValue);
				if (list == null) {
					list = new ArrayList<Map>();
					dsMap.put(dsValue, list);
				}
				list.add(map);
			} else {
				System.out.println(type);
			}
		}

		for (String dataSource : dsMap.keySet()) {
			dao.setDataSource("cmf_" + dataSource);
			List<Map> list = dsMap.get(dataSource);
			System.out.println(dataSource + " has " + list.size() + " records");
			if (dataSource.startsWith("sys_module")) {
				System.out.println(list);
			}
			dao.bulkUpsert(list);
		}
	}

	@Override
	public void importJson(File file, String datasource) throws ServiceException {
		try {
			Gson gson = new Gson();
			InputStream in = new FileInputStream(file);
			JsonReader reader = new JsonReader(new InputStreamReader(in, "UTF-8"));
			List<Map> documents = new ArrayList<Map>();
			reader.beginArray();
			while (reader.hasNext()) {
				Map document = gson.fromJson(reader, Map.class);
				documents.add(document);
				if (! document.containsKey(Constants.TYPE)) {
					continue;
				}
				addId(document);
			}

			if (!Util.nullOrEmpty(documents)) {
				upsertPerDataSource(datasource, documents);
			}
			reader.endArray();
			reader.close();

		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
	}

}
