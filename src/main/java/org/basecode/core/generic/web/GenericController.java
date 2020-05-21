package org.basecode.core.generic.web;

import org.basecode.core.generic.service.GenericService;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

//@RestController
public abstract class GenericController<T extends Serializable> {

	@Autowired
	protected GenericService<T> service;
	
	/**
     * 统计数量
     */
    public int countBase(Map<String,Object> map){
    	return service.countBase(map);
    }

    /**
     * 自定义删除
     */
    public int deleteBase(Map<String,Object> map){
    	return service.deleteBase(map);
    }

    /**
     * 添加数据，包含空数据
     */
    public T insertBase(T entity){
    	service.insertBase(entity);
    	return entity;
    }
    
    /**
     * 自定义查询
     */
    public List<T> selectBase(Map<String,Object> map){
    	return service.selectBase(map);
    }
}
