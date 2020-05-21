package org.basecode.common.criterion.model;

import com.dashu.lazyapidoc.annotation.Doc;
import com.github.pagehelper.PageInfo;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class PageList<T> implements Serializable{
	
	@Doc("数据总数")
	private Long total;
	@Doc("当前分页的具体数据")
	private List<T> rows;

	public PageList(){

	}
	public PageList(long total, List<T> rows){
		this.total = total;
		this.rows = rows;
	}
	public PageList(PageInfo<T> pageInfo){
		this.total = pageInfo.getTotal();
		this.rows = pageInfo.getList();
	}
	
}
