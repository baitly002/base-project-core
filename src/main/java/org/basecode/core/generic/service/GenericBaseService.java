package org.basecode.core.generic.service;

//import io.vertx.core.http.HttpMethod;

import java.io.Serializable;
import java.util.Map;

//import com.dashu.codegen.annotation.RequestMapping;

/**
 * 基本数据表操作接口,一般有主键的表使用
 * @author charles email:baitly002@gmail.com
 *
 * @param <T>
 * @param <PK>
 */
//@Service
public interface GenericBaseService<T extends Serializable> extends GenericService<T>{

    /**
     * 根据表主键ID来删除
     */
//	@RequestMapping(value="/:id", method=HttpMethod.DELETE)
    public int deleteByPrimaryKey(Long id);

    /**
     * 根据表主键ID查询,返回实体
     */
//	@RequestMapping(value="/:id", method=HttpMethod.GET)
    public T selectByPrimaryKey(Long id);
    
    /**
     * 根据表多个字段查询,返回实体
     */
//	@RequestMapping(value="/selectOne/", method=HttpMethod.GET)
    public T selectOneBase(Map<String, Object> map);

    /**
     * 根据ID更新数据，参数为实体类，只更新有数据的字段
     */
//	@RequestMapping(value="/selective/:id", method=HttpMethod.PUT)
    public int updateByPrimaryKeySelective(T entity);

    /**
     * 根据ID更新数据，参数为实体类，可把字段数据更新为null
     */
//	@RequestMapping(value="/:id", method=HttpMethod.PUT)
    public int updateByPrimaryKey(T entity);
}
