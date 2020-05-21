package com.dashu.base.base.common.generic.repository;

import java.io.Serializable;

/**
 * 基本数据表操作接口,一般有主键的表使用
 * @author charles email:baitly002@gmail.com
 *
 * @param <T>
 * @param <PK>
 */
public interface GenericBaseMapper<T, PK extends Serializable> extends GenericMapper<T, Serializable>{

		/**
	     * 根据表主键ID来删
	     */
	    public int ${conf_deleteByPrimaryKey}(Integer id);

	    /**
	     * 根据表主键ID查询,返回实体
	     */
	    public T ${conf_selectByPrimaryKey}(Integer id);

	    /**
	     * 根据ID更新数据，参数为实体类，只更新有数据的字段
	     */
	    public int ${conf_updateByPrimaryKeySelective}(T entity);

	    /**
	     * 根据ID更新数据，参数为实体类，可把字段数据更新为null
	     */
	    public int ${conf_updateByPrimaryKey}(T entity);
	}
