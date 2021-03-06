package ${conf_genericMapperClassPackage};

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import ${superClass};

/**
 * 顶层数据表操作接口，一般没有主键的表继承使用
 * @author charles email:baitly002@gmail.com
 *
 * @param <T>
 * @param <PK>
 */
public interface GenericMapper<T,PK extends Serializable> extends SqlMapper{
    /**
     * 统计数量
     */
    public int count(Map map);

    /**
     * 自定义删除
     */
    public int delete(Map map);

    /**
     * 添加数据，包含空数据
     */
    public int insert(T entity);

    /**
     * 添加数据，不包含空数据
     */
    public int insertSelective(T entity);

    /**
     * 自定义查询
     */
    public List<T> select(Map map);

    /**
     * 更新数据，参数为Map，只更新有数据的字段
     */
    public int updateSelective(Map map);

    /**
     * 更新数据，参数为Map，可把字段数据更新为null
     */
    public int update(Map map);
}