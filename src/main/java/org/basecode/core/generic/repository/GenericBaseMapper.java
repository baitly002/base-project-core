package org.basecode.core.generic.repository;

import java.io.Serializable;
import java.util.Map;

/**
 * 基本数据表操作接口,一般有主键的表使用
 * @author charles email:baitly002@gmail.com
 *
 * @param <T>
 * @param <PK>
 */
//@Repository
//@Mapper
public interface GenericBaseMapper<T extends Serializable> extends GenericMapper<T>{

		/**
	     * 根据表主键ID来删
	     */
	    public int deleteByPrimaryKey(Long id);

	    /**
	     * 根据表主键ID查询,返回实体
	     */
	    public T selectByPrimaryKey(Long id);
	    
	    /**
	     * 根据表多个字段查询,返回实体
	     */
	    public T selectOneBase(Map<String, Object> map);

	    /**
	     * 根据ID更新数据，参数为实体类，只更新有数据的字段
	     */
	    public int updateByPrimaryKeySelective(T entity);

	    /**
	     * 根据ID更新数据，参数为实体类，可把字段数据更新为null
	     */
	    public int updateByPrimaryKey(T entity);
	}
