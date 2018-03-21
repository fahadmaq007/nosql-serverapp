package com.maqbool.server.service;

import java.io.File;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.maqbool.server.commons.Constants;
import com.maqbool.server.exception.ServiceException;
import com.maqbool.server.services.DataImportService;

public class DataImportServiceTest extends BaseTest {

	protected final Logger logger = LoggerFactory.getLogger(getClass());

	@Autowired
	private DataImportService dataImportService;

	@Before
	public void setUp() {
		
	}

	@After
	public void tearDown() {
		
	}
	
	private String parentDir = "/Users/maqbool/Documents/workspace/movielens/";
//	@Test
	public void testImportMovieFile() {
		File file = new File(parentDir + "ml-100k/u.item"); // movies data
		String type = "movie";
		String [] columns = { "id", "title", "releaseDate" };
		try {
			int[] idColumns = new int[] { 0 };
			dataImportService.importFile(file, type, columns, idColumns, null);
		} catch (ServiceException e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void testImportRatingFile() {
		File file = new File(parentDir + "ml-100k/u.data"); // ratings data
		String type = "rating";
//		userId | movieId | rating | timestamp
		String [] columns = { "userId", "movieId", "rating", "ratedOn" };
		try {
			int[] idColumns = new int[] { 0, 1 };
			dataImportService.importFile(file, type, columns, idColumns, "\t");
		} catch (ServiceException e) {
			e.printStackTrace();
		}
	}
	
//	@Test
	public void testImportUserFile() {
		File file = new File(parentDir + "ml-100k/u.user"); // users data
		String type = "user";
//		userId | age | gender | occupation | zip
		String [] columns = { "id", "age:int", "gender", "occupation", "zip:int" };
		try {
			int[] idColumns = new int[] { 0 };
			dataImportService.importFile(file, type, columns, idColumns, null);
		} catch (ServiceException e) {
			e.printStackTrace();
		}
	}
	
//	@Test
	public void testImportMovieCsvFile() {
		File file = new File(parentDir + "ml-20m/movies.csv"); // movies data
		String type = "movie";
		String [] columns = { "id:int", "title", "genre" };
		try {
			int[] idColumns = new int[] { 0 };
			dataImportService.importFile(file, type, columns, idColumns, Constants.COMMA);
		} catch (ServiceException e) {
			e.printStackTrace();
		}
	}
	
//	@Test
	public void testImportRatingCsvFile() {
		File file = new File(parentDir + "ml-20m/003_ratings.csv"); // ratings data
		String type = "rating";
		String [] columns = { "userId:int", "movieId:int", "rating:float", "ratedOn:int" };
		try {
			int[] idColumns = new int[] { 0, 1 };
			dataImportService.importFile(file, type, columns, idColumns, Constants.COMMA);
		} catch (ServiceException e) {
			e.printStackTrace();
		}
	}
	
}
