package org.basecode.core.generic.web;

import org.basecode.core.generic.service.GenericBaseService;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.Serializable;
import java.util.Map;

//@RestController
public abstract class GenericBaseController<T extends Serializable> extends GenericController<T> {

	@Autowired
    protected GenericBaseService<T> service;
	
	
	/**
     * 根据表主键ID来删除
     */
    public int deleteByPrimaryKey(Long id){
    	return service.deleteByPrimaryKey(id);
    }

    /**
     * 根据表主键ID查询,返回实体
     */
    public T selectByPrimaryKey(Long id){
    	return service.selectByPrimaryKey(id);
    }
    
    /**
     * 根据表多个字段查询,返回实体
     */
    public T selectOneBase(Map<String, Object> map){
    	return service.selectOneBase(map);
    }

    /**
     * 根据ID更新数据，参数为实体类，只更新有数据的字段
     */
    public int updateByPrimaryKeySelective(T entity){
    	return service.updateByPrimaryKeySelective(entity);
    }

    /**
     * 根据ID更新数据，参数为实体类，可把字段数据更新为null
     */
    public int updateByPrimaryKey(T entity){
    	return service.updateByPrimaryKey(entity);
    }
}
