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

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
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
public class DocumentServiceImpl implements DocumentService {

	private final Logger logger = LoggerFactory.getLogger(getClass());
	
	@Autowired
	@Qualifier("couchCloudantDao")
	private IDao dao;

	@Override
	public PageContent<Map> listDocuments(String[] filters, String[] sortParams, Integer page, Integer size)
			throws ServiceException {
		try {
			PageDto pageDto = new  PageDto(page, size);
			return dao.listDocuments(filters, sortParams, pageDto);
		} catch (DataAccessException e) {
			throw new ServiceException(e.getMessage(), e);
		}
	}

	@Override
	public Map getDocumentById(String id) throws ServiceException, DataAccessException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Map> upsert(List<Map> documents) throws ServiceException {
		try {
			dao.bulkUpsert(documents);
		} catch (DataAccessException e) {
			throw new ServiceException(e.getMessage(), e);
		}
		return documents;
	}

	

}
