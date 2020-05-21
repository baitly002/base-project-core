package ${serviceImplPackage};

//import org.springframework.stereotype.Service;
import org.springframework.stereotype.Component;
import ${beanPath};
<#if isDubbo>
import ${package}.common.dubboProvider.${modelName}.service.${BeanName}Service;
<#else>
import ${package}.${modelName}.service.${BeanName}Service;
</#if>

<#if hasPK>
import org.basecode.common.generic.service.impl.GenericBaseServiceImpl;

@Component
//@Service
//@com.alibaba.dubbo.config.annotation.Service
public class ${BeanName}ServiceImpl extends GenericBaseServiceImpl<${BeanName}> implements ${BeanName}Service{
<#else>
import org.basecode.common.generic.service.impl.GenericServiceImpl;

@Component
//@Service
//@com.alibaba.dubbo.config.annotation.Service
public class ${BeanName}ServiceImpl extends GenericServiceImpl<${BeanName}> implements ${BeanName}Service{
</#if>

}