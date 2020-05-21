package org.basecode.core.generic.repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * 顶层数据表操作接口，一般没有主键的表继承使用
 * @author charles email:baitly002@gmail.com
 *
 * @param <T>
 */
//@Repository
//@Mapper
public interface GenericMapper<T extends Serializable> extends BaseMapper<T> {
    /**
     * 统计数量
     */
    public int countBase(Map<String, Object> map);

    /**
     * 自定义删除
     */
    public int deleteBase(Map<String, Object> map);

    /**
     * 添加数据，包含空数据
     */
    public int insertBase(T entity);
    
    @Deprecated
    public int insertSelective(T entity);

    /**
     * 自定义查询
     */
    public List<T> selectBase(Map<String, Object> map);
    
    /**
     * 更新数据，<"bean", javabean>//要更新的数据值是实体类存放在key为bean中
     * 条件用普通的<"key", value">即可
     */
    @Deprecated
    public int updateBase(Map<String, Object> map);
    
    @Deprecated
    public int updateSelective(Map<String, Object> map);
}