package ${conf_genericServicePackage};

import java.io.Serializable;

public interface GenericBaseService<T, PK extends Serializable> extends GenericService<T, Serializable>{

    /**
     * 根据表主键ID来删除
     */
    public int deleteByPrimaryKey(Integer id);

    /**
     * 根据表主键ID查询,返回实体
     */
    public T selectByPrimaryKey(Integer id);

    /**
     * 根据ID更新数据，参数为实体类，只更新有数据的字段
     */
    public int updateByPrimaryKeySelective(T entity);

    /**
     * 根据ID更新数据，参数为实体类，可把字段数据更新为null
     */
    public int updateByPrimaryKey(T entity);
}
