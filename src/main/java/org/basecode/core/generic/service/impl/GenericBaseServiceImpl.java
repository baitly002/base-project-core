package org.basecode.core.generic.service.impl;

import org.basecode.core.generic.repository.GenericBaseMapper;
import org.basecode.core.generic.service.GenericBaseService;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.Serializable;
import java.util.Map;


/**
 * 基本数据表操作接口,一般有主键的表使用
 * @author charles email:baitly002@gmail.com
 *
 *
 */
//@Service
public abstract class GenericBaseServiceImpl<T extends Serializable> extends GenericServiceImpl<T> implements GenericBaseService<T> {

	@Autowired
    protected GenericBaseMapper<T> mapper;
	
    /**
     * 根据表主键ID来删除
     */
    public int deleteByPrimaryKey(Long id){
    	return mapper.deleteByPrimaryKey(id);
    }

    /**
     * 根据表主键ID查询,返回实体
     */
    public T selectByPrimaryKey(Long id){
    	return mapper.selectByPrimaryKey(id);
    }
    
    /**
     * 根据表多个字段查询,返回实体
     */
    public T selectOneBase(Map<String, Object> map){
    	return mapper.selectOneBase(map);
    }

    /**
     * 根据ID更新数据，参数为实体类，只更新有数据的字段
     */
    public int updateByPrimaryKeySelective(T entity){
    	return mapper.updateByPrimaryKeySelective(entity);
    }

    /**
     * 根据ID更新数据，参数为实体类，可把字段数据更新为null
     */
    public int updateByPrimaryKey(T entity){
    	return mapper.updateByPrimaryKey(entity);
    }
}
