/**
 * 
 */
package com.maqbool.server.web.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

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
import com.maqbool.server.commons.PageDto;
import com.maqbool.server.exception.ServiceException;
import com.maqbool.server.services.DocumentService;

/**
 * REST APIs of Document into database.
 * 
 * @author niraj.gupta
 *
 */
@Api(value = "documents", description = "Generic Document APIs")
@RestController
@RequestMapping(value = "/documents", produces = MediaType.APPLICATION_JSON_VALUE)
public class DocumentController extends BaseController{

	Logger logger = LoggerFactory.getLogger(getClass());
	
	@Autowired
	private DocumentService docService;
	
	@ApiOperation(value = "Creates list of documents", notes = "Returns a list having success/failure response")
	@RequestMapping(value = "/type/{type}", method = RequestMethod.POST)
	public List<Map<String, Object>> createDocument(HttpServletRequest request,
			@PathVariable("type") String docType,
			@RequestBody(required = true) List<Map<String, Object>> docs)
			throws ServiceException {
		return null;
	}

	@ApiOperation(value = "List the documents", notes = "Returns the filtered / ordered list of documents")
	@RequestMapping(value = "" , method = RequestMethod.GET)
	public PageContent<Map<String, Object>> listDocuments(HttpServletRequest request, @RequestParam(value="filters", required = false) String[] filters,
			@RequestParam(value="sort", required = false) String[] sortParams,
			@RequestParam(value="page", required = false) Integer page, 
			@RequestParam(value="limit", required = false) Integer limit) throws ServiceException {	
		PageDto pageDto = new PageDto(page, limit);
		return docService.listDocuments(filters, sortParams, pageDto);
	}
	
}
