/**
 * 
 */
package com.maqbool.server.web.controller;


import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.maqbool.server.commons.PageContent;
import com.maqbool.server.exception.DataAccessException;
import com.maqbool.server.exception.ServiceException;
import com.maqbool.server.services.DocumentService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;


/**
 * Documents controller.
 * 
 * @author maqbool
 *
 */
@Api(value = "documents")
@RestController
@RequestMapping(value = "/documents", produces = MediaType.APPLICATION_JSON_VALUE)
public class DocumentController extends BaseController{

	Logger logger = LoggerFactory.getLogger(getClass());
	
	@Autowired
	private DocumentService documentService;
	
	
	@ApiOperation(value = "Creates list of documents", notes = "Returns a list having success/failure response")
	@RequestMapping(value = "/type/{type}", method = RequestMethod.POST)
	public List<Map> bulkUpsert(HttpServletRequest request,
			@RequestBody(required = true) List<Map> documents)
			throws ServiceException {
		return documentService.upsert(documents);
	}

	@ApiOperation(value = "List the documents", notes = "Returns the list of documents")
	@RequestMapping(value = "" , method = RequestMethod.GET)
	public PageContent<Map> listDocuments(HttpServletRequest request, 
			@RequestParam(value="filters", required = false) String[] filters,
			@RequestParam(value="sort", required = false) String[] sortParams,
			@RequestParam(value="page", required = false) Integer page, 
			@RequestParam(value="limit", required = false) Integer limit) throws ServiceException {	
		return documentService.listDocuments(filters, sortParams, page, limit);
	}
	
	@ApiOperation(value = "Gets the document by ID", notes = "Returns the latest document")
	@RequestMapping(value = "/id/{id}", method = RequestMethod.GET)
	public Map getDocumentById(HttpServletRequest request, 
			@PathVariable("id") String id)
			throws ServiceException, DataAccessException {
		return documentService.getDocumentById(id);
	}
}
