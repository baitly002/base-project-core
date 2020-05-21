package ${serviceImplPackage};

import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Service;
import ${beanPath};
import ${package}.common.dubboProvider.${modelName}.service.${BeanName}Service;
<#if hasPK>
import com.dashu.cloudOA.common.generic.service.impl.GenericBaseServiceImpl;

@Service
@com.alibaba.dubbo.config.annotation.Service
public class ${BeanName}ServiceImpl extends GenericBaseServiceImpl<${BeanName}> implements ${BeanName}Service{
<#else>
import com.dashu.cloudOA.common.generic.service.impl.GenericServiceImpl;

@Service
@com.alibaba.dubbo.config.annotation.Service
public class ${BeanName}ServiceImpl extends GenericServiceImpl<${BeanName}> implements ${BeanName}Service{
</#if>

<#if hasPK>
	
    /**
     * 根据表主键ID来删除
     */
    public int deleteByPrimaryKey(Object id){
    	return mapper.deleteByPrimaryKey(id);
    }

    /**
     * 根据表主键ID查询,返回实体
     */
    public ${BeanName} selectByPrimaryKey(Object id){
    	return mapper.selectByPrimaryKey(id);
    }
    
    /**
     * 根据表多个字段查询,返回实体
     */
    public ${BeanName} selectOne(Map<String, Object> map){
    	return mapper.selectOne(map);
    }

    /**
     * 根据ID更新数据，参数为实体类，只更新有数据的字段
     */
    public int updateByPrimaryKeySelective(${BeanName} entity){
    	return mapper.updateByPrimaryKeySelective(entity);
    }

    /**
     * 根据ID更新数据，参数为实体类，可把字段数据更新为null
     */
    public int updateByPrimaryKey(${BeanName} entity){
    	return mapper.updateByPrimaryKey(entity);
    }
<#else>
</#if>
	/**
     * 统计数量
     */
    public int count(Map<String,Object> map){
    	return mapper.count(map);
    }

    /**
     * 自定义删除
     */
    public int delete(Map<String,Object> map){
    	return mapper.delete(map);
    }

    /**
     * 添加数据，包含空数据
     */
    public int insert(${BeanName} entity){
    	return mapper.insert(entity);
    }

    /**
     * 自定义查询
     */
    public List<${BeanName}> select(Map<String,Object> map){
    	return mapper.select(map);
    }
}