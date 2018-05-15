/**
 * 
 */
package com.maqbool.server.commons;

import java.io.Serializable;

/**
 * @author maqboolahmed
 *
 */
public class PageDto implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5478666769670395815L;

	private int pageNumber;

	private int pageSize;

	private long totalRecords;

	public static final int DEFAULT_PAGE = 1;

	public static final int DEFAULT_PAGESIZE = 25;

	/**
	 * Constructs with the default page & default page size.
	 */
	public PageDto() {
		this(DEFAULT_PAGE, DEFAULT_PAGESIZE);
	}

	/**
	 * Constructs with the specified page size & default page.
	 * 
	 * @param pageSize
	 *            page size
	 */
	public PageDto(int pageSize) {
		this(DEFAULT_PAGE, pageSize);
	}

	/**
	 * Constructs with the specified page size & page.
	 * 
	 * @param pageNumber
	 *            page number
	 * @param pageSize
	 *            page size
	 */
	public PageDto(int pageNumber, int pageSize) {
		this.pageNumber = pageNumber;
		this.pageSize = pageSize;
	}

	/**
	 * Returns the current page number.
	 * 
	 * @return current pageNumber
	 */
	public int getPageNumber() {
		return pageNumber;
	}

	/**
	 * Set the pageNumber.
	 * 
	 * @param pageNumber
	 */
	public void setPageNumber(int pageNumber) {
		this.pageNumber = pageNumber;
	}

	/**
	 * Returns the pageSize.
	 * 
	 * @return pageSize
	 */
	public int getPageSize() {
		return pageSize;
	}

	/**
	 * PageSize setter
	 * 
	 * @param pageSize
	 */
	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}

	/**
	 * Returns total records the paging has.
	 * 
	 * @return totalRecords
	 */
	public long getTotalRecords() {
		return totalRecords;
	}

	/**
	 * Set the totalRecords
	 * 
	 * @param totalRecords
	 */
	public void setTotalRecords(long totalRecords) {
		this.totalRecords = totalRecords;
	}

	/**
	 * Traverse to next page if available.
	 */
	public void nextPage() {
		final PageDto page = this;
		int pageNum = page.getPageNumber();
		int pageSize = page.getPageSize();
		long totalRecords = page.getTotalRecords();
		if (totalRecords == 0)
			return;
		int totalPages = (int) totalRecords / pageSize;
		if (pageNum < totalPages)
			page.setPageNumber(++pageNum);
	}

	/**
	 * Traverse to previous page if available.
	 * 
	 */
	public void prevPage() {
		PageDto page = this;
		int pageNum = page.getPageNumber();
		if (pageNum <= 1)
			return;

		page.setPageNumber(--pageNum);
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("{pageNumber=" + pageNumber
				+ ", pageSize=" + pageSize);
		if (totalRecords > 0)
			builder.append(", totalRecords=").append(totalRecords);
		builder.append("}");
		return builder.toString();
	}

}
