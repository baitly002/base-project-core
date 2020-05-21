<#if isDubbo>
package ${package}.common.dubboProvider.${modelName}.service;
<#else>
package ${package}.${modelName}.service;
</#if>

import ${beanPath};
//import com.dashu.codegen.annotation.RequestMapping;
<#if hasPK>
import ${genericBaseService};
<#else>
import ${genericService};
</#if>
//@RestController
//@RequestMapping(value="/${beanName}")
<#if hasPK>
public interface ${BeanName}Service extends GenericBaseService<${BeanName}>{

//	/**
//     * remark: 添加数据，包含空数据
//     */
//	@PostMapping("")
//	public ${BeanName} insert(${BeanName} ${beanName});
//
//	/**
//	 * remark: 根据表主键ID来删除
//	 */
//	@DeleteMapping("/{id}")
//	public int deleteByPrimaryKey(@PathVariable Long id);
//
//    /**
//     * remark: 根据表主键ID查询,返回实体
//     */
//	@GetMapping("/{id}")
//	public ${BeanName} selectByPrimaryKey(@PathVariable Long id);
//	
//	/**
//     * remark: 根据查询条件返回列表数据
//     * warn: 需要自己去添加实现方法
//     */
//	@GetMapping("")
//	public PageList<${BeanName}> select(${BeanName} ${beanName});
//
//    /**
//     * remark: 根据ID更新数据，参数为实体类，只更新有数据的字段
//     * warn: 需要自己去添加实现方法
//     */
//	@PatchMapping("/{id}")
//	public int updateByPrimaryKeySelective(@PathVariable Long id, ${BeanName} ${beanName});
//
//    /**
//     * remark: 根据ID更新数据，参数为实体类，可把字段数据更新为null
//     * warn: 需要自己去添加实现方法
//     */
//	@PutMapping("/{id}")
//	public int updateByPrimaryKey(@PathVariable Long id, ${BeanName} ${beanName});
<#else>
public interface ${BeanName}Service extends GenericService<${BeanName}>{
</#if>

}