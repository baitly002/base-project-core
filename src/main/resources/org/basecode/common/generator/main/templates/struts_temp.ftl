<?xml version="1.0" encoding="UTF-8" ?>
<!DOCTYPE struts PUBLIC "-//Apache Software Foundation//DTD Struts Configuration 2.1//EN" "http://struts.apache.org/dtds/struts-2.1.dtd">
<struts>
	<!-- ${cnName}数据管理 -->
	<package name="${beanName}" extends="json-default,login">
		<!-- 显示管理页面 -->
		<action name="${beanname}_manager" class="${class}" method="manager">
			<result name="SUCCESS">/user/competenceMng.jsp</result>
			<interceptor-ref name="adminlogin"></interceptor-ref>
		</action>
		
		<!-- 显示添加页面 -->
		<action name="${beanname}_add" class="${class}" method="add">
			<result name="SUCCESS">/common/msg.jsp</result>
			<interceptor-ref name="adminlogin"></interceptor-ref>
		</action>
		<!-- 保存添加页面数据 -->
		<action name="${beanname}_save_add" class="${class}" method="saveAdd">
			<result name="SUCCESS">/common/msg.jsp</result>
			<interceptor-ref name="adminlogin"></interceptor-ref>
		</action>
		
		<!-- 显示编辑页面 -->
		<action name="${beanname}_edit" class="${class}" method="edit">
			<result name="SUCCESS">/common/msg.jsp</result>
			<interceptor-ref name="adminlogin"></interceptor-ref>
		</action>
		<!-- 保存编辑页面数据 -->
		<action name="${beanname}_save_edit" class="${class}" method="saveEdit">
			<result name="SUCCESS">/common/msg.jsp</result>
			<interceptor-ref name="adminlogin"></interceptor-ref>
		</action>
		<!-- 删除数据 -->
		<action name="${beanname}_delete" class="${class}" method="delete">
			<result name="SUCCESS">/common/msg.jsp</result>
			<interceptor-ref name="adminlogin"></interceptor-ref>
		</action>
		
		<!-- 显示详细信息数据 -->
		<action name="${beanname}_show" class="${class}" method="show">
			<result name="SUCCESS">/common/msg.jsp</result>
			<interceptor-ref name="adminlogin"></interceptor-ref>
		</action>
		
		<!-- 查询数据，返回JSON -->
	    <action name="${beanname}_json" class="${class}" method="json">
			<result type="json" name="success">
				<param name="contentType">text/json</param>  
				<param name="excludeNullProperties">true</param>
				<param name="root">result_</param>
			</result>
			<interceptor-ref name="adminlogin"></interceptor-ref>
		</action>
	</package>
</struts>
