package org.basecode.common.criterion.model;

import com.dashu.lazyapidoc.annotation.Doc;

import java.io.Serializable;

public class PageParameter implements Serializable{

	private static final long serialVersionUID = 1465463514638L;

	@Doc("当前页数")
	private Integer page;
	@Doc("每页数据量")
	private Integer rows;
	@Doc("根据什么排序")
	private String orderBy;
	
	public PageParameter() {
		this.page=1;
		this.rows=30;
	}
	public Integer getPage() {
		return page;
	}
	public PageParameter setPage(Integer page) {
		this.page = page;
		return this;
	}
	public Integer getRows() {
		return rows;
	}
	public PageParameter setRows(Integer rows) {
		this.rows = rows;
		return this;
	}
	public String getOrderBy() {
		return orderBy;
	}
	public PageParameter setOrderBy(String orderBy) {
		this.orderBy = orderBy == null ? null : orderBy.trim();
		return this;
	}
	
	
}
