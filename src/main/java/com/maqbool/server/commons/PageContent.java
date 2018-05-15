package com.maqbool.server.commons;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_NULL)
public class PageContent<E> {

	private List<E> list;
	
	private PageDto pageDto;
	
	public PageContent() {
		
	}
	
	public List<E> getList() {
		return list;
	}

	public void setList(List<E> list) {
		this.list = list;
	}

	public PageDto getPageDto() {
		return pageDto;
	}

	public void setPageDto(PageDto pageDto) {
		this.pageDto = pageDto;
	}

	@Override
	public String toString() {
		Map<String, Object> m = new HashMap<String, Object>();
		m.put("list", list == null ? 0 : list.size());
		m.put("page", pageDto);
		return m.toString();
	}
}
