package org.basecode.core.generator.main;

import com.alibaba.druid.util.JdbcUtils;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import org.basecode.core.generator.mybatis.generator.api.IntrospectedTable;
import org.basecode.core.generator.mybatis.generator.api.MyBatisGenerator;
import org.basecode.core.generator.mybatis.generator.config.*;
import org.basecode.core.generator.mybatis.generator.exception.InvalidConfigurationException;
import org.basecode.core.generator.mybatis.generator.internal.DefaultShellCallback;
import org.basecode.core.generator.mybatis.generator.internal.util.StringUtility;
import org.basecode.core.generator.mybatis.generator.tools.FileOperate;
import org.basecode.core.generator.mybatis.generator.tools.JavaFileMerger;
import org.basecode.core.generator.mybatis.generator.tools.JdbcConfig;
import org.basecode.core.generator.mybatis.generator.tools.JdbcUtil;
import org.basecode.core.util.StringUtils;

import java.io.*;
import java.net.URLDecoder;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.Map.Entry;

public class CodeFactory {

	public static Map data = new HashMap();
	public static boolean isDubbo = true;//是否是属于DUBBO类型项目
	public static boolean isOverrideMapperXML = true;//是否合并数据操作配置xml文件
	public static boolean isOverrideModel = false;//是否覆盖实体类
	public static boolean isOverrideMapper = false;//是否覆盖数据操作接口类
	public static boolean isOverrideService = false;//是否覆盖业务接口类，如果已存在的话
	public static boolean isOverrideServiceImpl = false;//是否覆盖业务实现类，如果已存在的话
	public static boolean isCommonModel = true;//是否公共模块，公共模块只生成实体与业务接口类
	public static boolean isServiceModel = true;//业务模块会生成业务实现类，数据库操作mapper及xml
	public static boolean isFullBeanName = false;//是否生成完整的表名实体类，默认为false，去除第一个下划线之前的名称

	
	public static void main(String[] args) {
//		System.out.println(System.getProperty("user.dir"));
//		String basePackage = "com.dashu.cloudOA";
//		String modelName = "core";
//		boolean isDubbo = true;
		Map<String, String> config = new HashMap<String, String>();
		config.put("basePackage", "com.dashu.repair");
		config.put("packageName", "repair");//包别名，如果不存在，将以表名第一个下划线前作为包名，如果表名不存在下划线，则以表名为包名
		config.put("modelNames", "repair_process");
		config.put("isDubbo", "false");
		config.put("jdbc.url","jdbc:mysql://192.168.10.252:3306/repair?useUnicode=true&characterEncoding=utf8");
		config.put("jdbc.username", "root");
		config.put("jdbc.password", "admin123");
		config.put("isOverrideMapperXML", "true");//开发为true，其于为false
		config.put("isOverrideModel", "true");
		config.put("isOverrideMapper", "false");
		config.put("isOverrideService", "false");
		config.put("isOverrideServiceImpl", "false");
		config.put("isCommonModel", "true");//是否公共模块，公共模块只生成实体与业务接口类
		config.put("isServiceModel", "true");//业务模块会生成业务实现类，数据库操作mapper及xml
		config.put("isLocal", "true");//本类调用才用到，freemark可根据不一样的运行来加载模板文件夹
		modelCodeGenerator(config);
//		running(isDubbo, "tb_", "AuthAccount", "");
	}
	
	public static Map packageSplitConnection(String basePackage, String modelName, boolean isDubbo, String rootPath){
		data.put("basePackage", basePackage);
		data.put("modelName", modelName);
		data.put("isDubbo", isDubbo);
		
		//实体类包路径
//		data.put("modelPackage", basePackage+".common.model."+modelName);
		data.put("modelPackage", basePackage+"."+modelName+".model");
		
		//数据交换层包路径
		data.put("mapperPackage", basePackage+"."+modelName+".repository");
		
		//服务实现层包路径
		data.put("serviceImplPackage", basePackage+"."+modelName+".service.impl");
		data.put("facadeImplPackage", basePackage+"."+modelName+".facade.impl");
		
		//service层接口包路径　非dubbo项目有效
		data.put("serviceInterface", basePackage+"."+modelName+".service");
		data.put("facadeInterface", basePackage+"."+modelName+".facade");

		//dubbo接口层包名，dubbo项目才有效
		data.put("dubboServiceInterface", basePackage+".common.dubboProvider."+modelName+".service");
		data.put("dubboFacadeInterface", basePackage+".common.dubboProvider."+modelName+".facade");

		//控制层包名
		data.put("webPackage", basePackage+"."+modelName+".web");

		//项目源码与配置文件路径
		if(StringUtils.isBlank(rootPath)){
			//rootPath = System.getProperty("java.class.path").split(System.getProperty("path.separator"))[0];
			rootPath = ClassLoader.getSystemResource("").toString();
			try {
				rootPath = URLDecoder.decode(rootPath, "UTF-8");
			}catch (Exception e) {
				e.printStackTrace();
			}
			String os = System.getProperty("os.name");
			if(rootPath.endsWith("/target/classes")) {
				rootPath = rootPath.replace("/target/classes", "");
				if(os.startsWith("Windows")){
					rootPath = rootPath.replace("file:/", "");
				}else{
					rootPath = rootPath.replace("file:", "");
				}
			}else if(rootPath.endsWith("/target/classes/")){
				rootPath = rootPath.replace("/target/classes/", "");
				if(os.startsWith("Windows")){
					rootPath = rootPath.replace("file:/", "");
				}else{
					rootPath = rootPath.replace("file:", "");
				}
			}else if(rootPath.endsWith("/target/test-classes")){
				rootPath = rootPath.replace("/target/test-classes", "");
				if(os.startsWith("Windows")){
					rootPath = rootPath.replace("file:/", "");
				}else{
					rootPath = rootPath.replace("file:", "");
				}
			}else if(rootPath.endsWith("/target/test-classes/")){
				rootPath = rootPath.replace("/target/test-classes/", "");
				if(os.startsWith("Windows")){
					rootPath = rootPath.replace("file:/", "");
				}else{
					rootPath = rootPath.replace("file:", "");
				}
			}else{
				rootPath = System.getProperty("user.dir");
			}
		}
		System.out.println("代码生成在："+rootPath+" 目录下");
		data.put("srcPath", rootPath + "/src/main/java");
		String resourcesPath = rootPath + "/src/main/resources";
		String webappPath = rootPath + "/src/main/webapp";
		data.put("resourcesPath", resourcesPath);
		data.put("mapperXmlPath", resourcesPath+"/mybatis/"+modelName);
		data.put("webappPath", webappPath+"/"+modelName);

		//service 与 repository层泛型公共类
//		String genericPackage = basePackage+".common.generic";
		String genericPackage = "org.basecode.core.generic";
		data.put("genericPackage", genericPackage);
		data.put("genericRepositoryPackage", genericPackage+".repository");
		data.put("genericServicePackage", genericPackage+".service");
		data.put("genericServiceImplPackage", genericPackage+".service.impl");
		return data;
	}
	
	public static void modelCodeGenerator(Map<String, String> config){
		String modelNames = config.get("modelNames")==null?null:config.get("modelNames").toString();
//		String packageName = config.get("packageName")==null?null:config.get("packageName").toString();
		if(modelNames==null){
			System.out.println("配置文件少了modelNames的配置，导致无法生成代码！");
		}
		String modelNameArr[] = modelNames.split(",");
		for(String mn : modelNameArr){
			String modelName = mn.split("_")[0];
//			if(StringUtils.isNotBlank(packageName)){
//				modelName = packageName;
//			}
			config.put("modelName", modelName);
			data.putAll(config);
			String basePackage = data.get("basePackage")==null?null:data.get("basePackage").toString();
			if(basePackage==null){
				System.out.println("配置文件少了basePackage的配置，导致无法生成代码！");
			}
//			String modelName = data.get("modelName")==null?null:data.get("modelName").toString();
//			if(modelName==null){
//				System.out.println("配置文件少了modelName的配置，导致无法生成代码！");
//			}
			String is_dubbo = data.get("isDubbo")==null?null:data.get("isDubbo").toString().trim();
			if(is_dubbo!=null && "false".equals(is_dubbo)){
				isDubbo = false;
			}
			
			String isOverrideServiceConfig = data.get("isOverrideService")==null?null:data.get("isOverrideService").toString().trim();
			if(isOverrideServiceConfig!=null){
				isOverrideService = "true".equals(isOverrideServiceConfig)?true:false;
			}
			
			String isOverrideServiceImplConfig = data.get("isOverrideServiceImpl")==null?null:data.get("isOverrideServiceImpl").toString().trim();
			if(isOverrideServiceImplConfig!=null){
				isOverrideServiceImpl = "true".equals(isOverrideServiceImplConfig) ? true : false;
			}
			
			String isCommonModelConfig = data.get("isCommonModel")==null?null:data.get("isCommonModel").toString().trim();
			if(isCommonModelConfig!=null){
				isCommonModel="true".equals(isCommonModelConfig) ? true : false;
			}
			
			String isServiceModelConfig = data.get("isServiceModel")==null?null:data.get("isServiceModel").toString().trim();
			if(isServiceModelConfig!=null){
				isServiceModel="true".equals(isServiceModelConfig)?true:false;
			}
			
			String isOverrideModelConfig = data.get("isOverrideModel")==null?null:data.get("isOverrideModel").toString().trim();
			if(isOverrideModelConfig!=null){
				isOverrideModel="true".equals(isOverrideModelConfig)?true:false;
			}
			
			String isOverrideMapperConfig = data.get("isOverrideMapper")==null?null:data.get("isOverrideMapper").toString().trim();
			if(isOverrideMapperConfig!=null){
				isOverrideMapper="true".equals(isOverrideMapperConfig)?true:false;
			}
			
			String isOverrideMapperXMLConfig = data.get("isOverrideMapperXML")==null?null:data.get("isOverrideMapperXML").toString().trim();
			if(isOverrideMapperXMLConfig!=null){
				isOverrideMapperXML="false".equals(isOverrideMapperXMLConfig)?false:true;
			}

			String isFullBeanNameConfig = data.get("isFullBeanName")==null?null:data.get("isFullBeanName").toString().trim();
			if(isFullBeanNameConfig!=null){
				isFullBeanName="false".equals(isFullBeanNameConfig)?false:true;
			}

			packageSplitConnection(basePackage, modelName, isDubbo, null);
			Map<String, String> tables = doGetTableComments();
			for(Entry<String, String> table : tables.entrySet()){
				String tableName = table.getKey();
				if(tableName.startsWith(mn+"_") || tableName.equals(mn)){
					String beanName = "";
					if(tableName.contains("_")){
						if(isFullBeanName){
							beanName = JdbcUtil.underlineToCamel2(tableName);
						}else{
							beanName = JdbcUtil.underlineToCamel2(tableName.substring(tableName.indexOf("_")+1));//第一个下划线前面为模块名，去掉
						}

					}else{
						beanName = tableName;
					}
//					if(tableName.equals(modelName)){
//						beanName = JdbcUtil.underlineToCamel2(tableName.substring(modelName.indexOf("_")+1));//第一个下划线前面为模块名，去掉
//					}else {
//						beanName = JdbcUtil.underlineToCamel2(tableName.substring(modelName.length()+1));//第一个下划线前面为模块名，去掉
//					}
					beanName = beanName.substring(0, 1).toUpperCase()+beanName.substring(1);//第一个字母大写
//				System.out.println("isDubbo:"+isDubbo+"==tableName:"+ tableName+"==beanName:"+ beanName+"==Comment:"+ table.getValue());
					running(isDubbo, tableName, beanName, table.getValue());
				}
				
			}
		}
	}
	
	public static Map<String, String> doGetTableComments() {
//		List<String> tableComments = new ArrayList<String>();
		Map<String, String> tableComments = new LinkedHashMap<String, String>();
        PreparedStatement stmt = null;
        ResultSet rs = null;
        String sql = "show table status where 1=1 ";
        try {
        	JdbcConfig jdbcConfig = new JdbcConfig();
        	jdbcConfig.setDriver(JdbcUtils.getDriverClassName(data.get("jdbc.url").toString()));
        	jdbcConfig.setUrl(data.get("jdbc.url").toString());
        	jdbcConfig.setUser(data.get("jdbc.username").toString());
        	jdbcConfig.setPassword(data.get("jdbc.password").toString());
            stmt =  JdbcUtil.getConn(jdbcConfig).prepareStatement(sql);
            rs = stmt.executeQuery(sql);
            while (rs.next()) {
//            	tableComments.add(rs.getString("Name"));
                tableComments.put(rs.getString("Name"), rs.getString("Comment"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            JdbcUtil.safelyClose(rs, stmt);
        }
        return tableComments;
    }
	
	/**
	 * 生成CRUD操作
	 * @param tableName 数据库表名称
	 * @param beanName 生成实体的名称
	 * @param cnName 表名的中文解释
	 */
	public static void running(boolean isDubbo, String tableName, String beanName, String cnName){
		FileOperate fo = new FileOperate();
		List<String> warnings = new ArrayList<String>();
		boolean overwrite = true;
		String modelName = "";
		if(tableName.contains("_")){
			modelName = tableName.substring(0, tableName.indexOf("_"));
		}else{
			modelName = tableName;
		}
		Configuration config = new Configuration();
		if(isCommonModel){//是公共模块才操作
			String ep = data.get("srcPath").toString()+"/"+data.get("modelPackage").toString().replaceAll("\\.", "/");
			File ef = new File(ep);
			if(!ef.exists()){
				fo.createFolders(ep);
			}
		}
		if(isServiceModel){//是业务模块才操作
			String dp = data.get("srcPath").toString()+"/"+data.get("mapperPackage").toString().replaceAll("\\.", "/");
			File df = new File(dp);
			if(!df.exists()){
				fo.createFolders(dp);
			}
		}
		if(isCommonModel){//是公共模块才操作
			String sp = null;
			if(isDubbo){
				sp = data.get("srcPath").toString()+"/"+data.get("dubboServiceInterface").toString().replaceAll("\\.", "/");
			}else{
				sp = data.get("srcPath").toString()+"/"+data.get("serviceInterface").toString().replaceAll("\\.", "/");
			}
			File sf = new File(sp);
			if(!sf.exists()){
				fo.createFolders(sp);
			}
		}
		if(isServiceModel){//是业务模块才操作
			String ip = data.get("srcPath").toString()+"/"+data.get("serviceImplPackage").toString().replaceAll("\\.", "/");
			File ipf = new File(ip);
			if(!ipf.exists()){
				fo.createFolders(ip);
			}
		}
		if(isServiceModel){//是业务模块才操作
			String mp = data.get("mapperXmlPath").toString();
			File mf = new File(mp);
			if(!mf.exists()){
				fo.createFolders(mp);
			}
		}
		//   ... fill out the config object as appropriate...
		//config.addClasspathEntry(savePath+"/ojdbc14.jar");
//		/home/charles/workspace_dubbo/cloudOA/cloudOA-common/src/main/resources/mybatis/core
//		/home/charles/workspace_dubbo/cloudOA/cloudOA-common/src/main/resources/mybatis/core
		
		ModelType defaultModelType = ModelType.getModelType("flat");
		Context context = new Context(defaultModelType);
		context.setId("mysql-mybatis");
		context.setTargetRuntime("MyBatis3");
		
		//是否生成注释
		CommentGeneratorConfiguration commentGeneratorConfiguration = new CommentGeneratorConfiguration();
		commentGeneratorConfiguration.addProperty("suppressAllComments", "false");
		commentGeneratorConfiguration.addProperty("suppressDate", "true");
		context.setCommentGeneratorConfiguration(commentGeneratorConfiguration);
		
		//配置数据库连接
		JDBCConnectionConfiguration jdbcConnectionConfiguration = new JDBCConnectionConfiguration();
		jdbcConnectionConfiguration.setDriverClass("com.mysql.jdbc.Driver");
		jdbcConnectionConfiguration.setConnectionURL(data.get("jdbc.url").toString());
		jdbcConnectionConfiguration.setUserId(data.get("jdbc.username").toString());
		jdbcConnectionConfiguration.setPassword(data.get("jdbc.password").toString());
		context.setJdbcConnectionConfiguration(jdbcConnectionConfiguration);
		
		//类型转换
		JavaTypeResolverConfiguration javaTypeResolverConfiguration = new JavaTypeResolverConfiguration();
		javaTypeResolverConfiguration.addProperty("forceBigDecimals", "false");
		context.setJavaTypeResolverConfiguration(javaTypeResolverConfiguration);
		
		
		//生成实体
		JavaModelGeneratorConfiguration javaModelGeneratorConfiguration = new JavaModelGeneratorConfiguration();
		if(isCommonModel){//是业务模块才操作
			javaModelGeneratorConfiguration.setTargetPackage(data.get("modelPackage").toString());
			javaModelGeneratorConfiguration.setTargetProject(data.get("srcPath").toString());
		}else{
			javaModelGeneratorConfiguration.setTargetPackage("./");
			javaModelGeneratorConfiguration.setTargetProject(data.get("srcPath").toString());
		}
		javaModelGeneratorConfiguration.addProperty("enableSubPackages", "false");
		javaModelGeneratorConfiguration.addProperty("trimStrings", "true");
		
		javaModelGeneratorConfiguration.addProperty("modelPackage", data.get("modelPackage").toString());
		context.setJavaModelGeneratorConfiguration(javaModelGeneratorConfiguration);
		
		//生成mapper XML
		SqlMapGeneratorConfiguration sqlMapGeneratorConfiguration = new SqlMapGeneratorConfiguration();
		sqlMapGeneratorConfiguration.setTargetPackage("./");
		sqlMapGeneratorConfiguration.setTargetProject(data.get("mapperXmlPath").toString());
		sqlMapGeneratorConfiguration.addProperty("enableSubPackages", "false");
		context.setSqlMapGeneratorConfiguration(sqlMapGeneratorConfiguration);
		
		
		//生成dao
		JavaClientGeneratorConfiguration javaClientGeneratorConfiguration = new JavaClientGeneratorConfiguration();
		javaClientGeneratorConfiguration.setConfigurationType("XMLMAPPER");
		if(isServiceModel){
			javaClientGeneratorConfiguration.setTargetPackage(data.get("mapperPackage").toString());
			javaClientGeneratorConfiguration.setTargetProject(data.get("srcPath").toString());
		}else{
			javaClientGeneratorConfiguration.setTargetPackage("./");
			javaClientGeneratorConfiguration.setTargetProject(data.get("srcPath").toString());
		}
		javaClientGeneratorConfiguration.addProperty("enableSubPackages", "false");
		context.setJavaClientGeneratorConfiguration(javaClientGeneratorConfiguration);
		
		
		//表
		TableConfiguration tc = new TableConfiguration(context);
		tc.setSchema("root");
//		tc.addProperty("ignoreQualifiersAtRuntime", "true");
		tc.setTableName(tableName);
//		tc.setTableName("DATA_INDEX_NUMBER_VALUE");
		tc.setDomainObjectName(beanName);
//		tc.setCountByExampleStatementEnabled(false);
//		tc.setUpdateByExampleStatementEnabled(true);
//		tc.setDeleteByExampleStatementEnabled(false);
//		tc.setSelectByExampleStatementEnabled(false);
//		tc.setSelectByPrimaryKeyStatementEnabled(false);
//		GeneratedKey key = new GeneratedKey("id", "mysql", true, "post");
//		tc.setGeneratedKey(key);
		tc.addProperty("useActualColumnNames", "false");
		tc.addProperty("useColumnIndexes", "true");
		String genericMapperClassPackage = data.get("genericRepositoryPackage").toString();
		tc.addProperty("superClass", genericMapperClassPackage+".SqlMapper");//完全自定义使用
		tc.addProperty("conf_genericMapperClassPackage", genericMapperClassPackage);//
		tc.addProperty("genericSuperClass", genericMapperClassPackage+".GenericMapper");//没有主键的表继承使用
		tc.addProperty("genericBaseSuperClass", genericMapperClassPackage+".GenericBaseMapper");//有主键的表继承使用
		tc.addProperty("cnName", cnName);
		tc.addProperty("CONF_BASE_RECORD_TYPE", data.get("modelPackage").toString());
		
		tc.addProperty("conf_count","countBase"); //统计数量
        tc.addProperty("conf_delete","deleteBase"); //自定义删除
        tc.addProperty("conf_deleteByPrimaryKey","deleteByPrimaryKey"); //根据表主键ID来删
        tc.addProperty("conf_insert","insertBase"); //添加数据，包含空数据
        tc.addProperty("conf_insertSelective","insertSelective"); //添加数据，不包含空数据
        tc.addProperty("conf_selectAll","selectAll"); //$NON-NLS-1$
        tc.addProperty("conf_select","selectBase"); //自定义查询
		tc.addProperty("conf_selectOne","selectOneBase"); //自定义查询
        tc.addProperty("conf_selectWithBLOBs","selectWithBLOBs"); //$NON-NLS-1$
        tc.addProperty("conf_selectByPrimaryKey","selectByPrimaryKey"); //根据表主键ID查询,返回实体
        tc.addProperty("conf_update","updateBase"); //更新数据，参数为Map，可把字段数据更新为null
        tc.addProperty("conf_updateSelective","updateSelective"); //更新数据，参数为Map，只更新有数据的字段
        tc.addProperty("conf_updateWithBLOBs","updateWithBLOBs"); //$NON-NLS-1$
        tc.addProperty("conf_updateByPrimaryKey","updateByPrimaryKey"); //根据ID更新数据，参数为实体类，可把字段数据更新为null
        tc.addProperty("conf_updateByPrimaryKeySelective","updateByPrimaryKeySelective"); //根据ID更新数据，参数为实体类，只更新有数据的字段
        tc.addProperty("conf_updateByPrimaryKeyWithBLOBs","updateByPrimaryKeyWithBLOBs"); //$NON-NLS-1$
        tc.addProperty("conf_BaseResultMap","BaseResultMap"); //mapper.xml中基本的映射
        tc.addProperty("conf_ResultMapWithBLOBs","ResultMapWithBLOBs"); //$NON-NLS-1$
        tc.addProperty("conf_Where_Clause","Where_Clause"); //mapper.xml中where语句，全部为and、=
        tc.addProperty("conf_Base_Column_List","Base_Column_List"); //mapper.xml中表字段
        tc.addProperty("conf_Blob_Column_List","Blob_Column_List"); //$NON-NLS-1$
        tc.addProperty("conf_Update_Where_Clause","Update_Where_Clause"); //$NON-NLS-1$
		Properties conf = tc.getProperties();
		data.putAll(conf);
		context.addTableConfiguration(tc);
		
		config.addContext(context);
		   
		DefaultShellCallback callback = new DefaultShellCallback(overwrite);
		
		MyBatisGenerator myBatisGenerator;
		
		try {
			myBatisGenerator = new MyBatisGenerator(config, callback, warnings);
			myBatisGenerator.generate(null);
			List<Context> contexts = myBatisGenerator.getConfiguration().getContexts();
			List<IntrospectedTable> tables = contexts.get(0).getIntrospectedTables();
			data.put("hasPK", tables.get(0).hasPrimaryKeyColumns());//是否有主键
//			String genericServicePackage = "com.basecode.common.base.common.generic.service";
			String genericServicePackage = data.get("genericServicePackage").toString();
			data.put("conf_genericServicePackage", genericServicePackage);
			data.put("genericService", genericServicePackage+".GenericService");
			data.put("genericBaseService", genericServicePackage+".GenericBaseService");
//			data.put("beanPath", tables.get(0).getBaseRecordType());
			data.put("beanPath", data.get("modelPackage").toString()+"."+beanName);
			
			//根据模板生成相应文件
			data.put("package", data.get("basePackage").toString());//类存放的包
			data.put("saveRoot", data.get("srcPath").toString());//在哪生成文件
			data.put("BeanName", beanName);//生成的实体类名称
			data.put("modelName", modelName);//模块名称
			data.put("beanName", StringUtility.lowerStr(data.get("BeanName").toString()));
			data.put("beanname", data.get("BeanName").toString().toLowerCase());
			data.put("class", data.get("package").toString()+".action."+data.get("BeanName").toString()+"Action");
			data.put("MapperName", data.get("BeanName").toString()+"Mapper");
			data.put("cnName", cnName);
			//生成ＨＴＭＬ操作页面的文件路径配置
			data.put("viewFile", data.get("webappPath")+"/"+data.get("beanName").toString()+"Mng.html");
			//生成struts配置文件的路径配置
			data.put("strutsFile", data.get("srcPath")+"/struts-"+data.get("beanName").toString()+".xml");
			//生成控制层的类的文件路径配置
			data.put("actionFile", data.get("webPackage")+"/"+data.get("BeanName").toString()+"Controller.java");
			//生成服务层接口层的类的配置
			if(isDubbo){
				data.put("serviceFile", data.get("srcPath").toString()+"/"+data.get("dubboServiceInterface").toString().replaceAll("\\.", "/")+"/"+data.get("BeanName").toString()+"Service.java");
			}else{
				data.put("serviceFile", data.get("srcPath").toString()+"/"+data.get("serviceInterface").toString().replaceAll("\\.", "/")+"/"+data.get("BeanName").toString()+"Service.java");
			}
			//生成服务层的类的配置
			data.put("serviceimplFile", data.get("srcPath").toString()+"/"+data.get("serviceImplPackage").toString().replaceAll("\\.", "/")+"/"+data.get("BeanName").toString()+"ServiceImpl.java");
			//生成控制层的类名称配置
			data.put("ActionName", data.get("BeanName").toString()+"Controller");
			
			view(data);
		} catch (InvalidConfigurationException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
	}
	
	public static void view(Map data){
		 /* 在整个应用的生命周期中，这个工作你应该只做一次。 */  
        /* 创建和调整配置。 */ 
		freemarker.template.Configuration cfg = new freemarker.template.Configuration(freemarker.template.Configuration.VERSION_2_3_22);
        try {
//        	System.out.println(CodeFactory.class.getResource ("").getFile());
        	Object isLocal = data.get("isLocal");
        	if(isLocal==null || !"true".equals(isLocal.toString())) {
        		cfg.setClassForTemplateLoading(CodeFactory.class, "templates");
        	}else {
				cfg.setClassForTemplateLoading(CodeFactory.class, "templates");
//        		System.out.println("++++++++++"+CodeFactory.class.getResource("templates"));
//        		System.out.println("++++++++++"+CodeFactory.class.getResource("templates").getFile());
//        		cfg.setDirectoryForTemplateLoading(new File(CodeFactory.class.getResource("templates").getFile()));
        	}
//			cfg.setDirectoryForTemplateLoading(new File(System.getProperty("user.dir").replaceAll("\\\\", "/")+"/src/main/java/org/main/templates"));
//			cfg.setObjectWrapper(new DefaultObjectWrapper()); 
			/* 在整个应用的生命周期中，这个工作你可以执行多次 */  
			/* 获取或创建模板*/ 
//			if(isServiceModel){//是业务模块才操作
//				Template v_temp = cfg.getTemplate("crud_temp.ftl");
//
//				OutputStream v_os = new FileOutputStream(createfile(data.get("viewFile").toString()));
//				Writer v_out = new OutputStreamWriter(v_os);
//				v_temp.process(data, v_out);
//				v_out.flush();
//			}
//			
//			Template s_temp = cfg.getTemplate("struts_temp.ftl");
//			OutputStream s_os = new FileOutputStream(createfile(data.get("strutsFile").toString()));
//			Writer s_out = new OutputStreamWriter(s_os);  
//			s_temp.process(data, s_out); 
//			s_out.flush();
//			
//			Template a_temp = cfg.getTemplate("action_temp.ftl");
//			OutputStream a_os = new FileOutputStream(createfile(data.get("actionFile").toString()));
//			Writer a_out = new OutputStreamWriter(a_os);  
//			a_temp.process(data, a_out); 
//			a_out.flush();

			JavaFileMerger javaFileMerger = new JavaFileMerger();
			if(isCommonModel){//是公共模块才操作
				String serviceInfoFilePath = data.get("serviceFile").toString();
				File serviceInfoFile = new File(serviceInfoFilePath);
				String source = null;
				if(serviceInfoFile.exists() && isOverrideService){
//					source = fileOperate.readTxt(serviceInfoFilePath, "UTF-8");
					source = javaFileMerger.readFileContext(serviceInfoFilePath);
				}
				if(!serviceInfoFile.exists() || isOverrideService){//不存在该文件或指定覆盖才执行
					Template sv_temp = cfg.getTemplate("service_temp.ftl");
					OutputStream sv_os = new FileOutputStream(createfile(serviceInfoFilePath));
					Writer sv_out = new OutputStreamWriter(sv_os);  
					sv_temp.process(data, sv_out); 
					sv_out.flush();
				}
				if(isOverrideService && StringUtils.isNotBlank(source)) {
					source = javaFileMerger.getNewJavaFileForSource(javaFileMerger.readFileContext(serviceInfoFilePath), source);
					System.out.println(source);
					PrintWriter printWriter = new PrintWriter(serviceInfoFile);
					printWriter.write(source);
					printWriter.flush();
					printWriter.close();
				}
			}
			
			if(isServiceModel){//是业务模块才操作
				String serviceimplInfoFilePath = data.get("serviceimplFile").toString();
				File serviceimplInfoFile = new File(serviceimplInfoFilePath);
				String source = null;
				if(serviceimplInfoFile.exists() && isOverrideService) {
					source = javaFileMerger.readFileContext(serviceimplInfoFilePath);
				}

				if(!serviceimplInfoFile.exists() || isOverrideServiceImpl){//不存在该文件或指定覆盖才执行
					Template si_temp = cfg.getTemplate("serviceimpl_temp.ftl");
					OutputStream si_os = new FileOutputStream(createfile(serviceimplInfoFilePath));
					Writer si_out = new OutputStreamWriter(si_os);  
					si_temp.process(data, si_out); 
					si_out.flush();
				}
				if(isOverrideServiceImpl && StringUtils.isNotBlank(source)) {
					source = javaFileMerger.getNewJavaFileForSource(javaFileMerger.readFileContext(serviceimplInfoFilePath), source);
					PrintWriter printWriter = new PrintWriter(serviceimplInfoFile);
					printWriter.write(source);
					printWriter.flush();
					printWriter.close();
				}
			}
//			String mapperFilePath = System.getProperty("user.dir").replaceAll("\\\\", "/")+"/src/main/java/"+data.get("genericSuperClass").toString().replaceAll("\\.", "/")+".java";
//			File mapperFile = new File(mapperFilePath);
//			if(!mapperFile.exists()){
//				Template mapper_temp = cfg.getTemplate("GenericMapper_temp.ftl");
//				OutputStream mapper_os = new FileOutputStream(createfile(mapperFilePath));
//				Writer mapper_out = new OutputStreamWriter(mapper_os);  
//				mapper_temp.process(data, mapper_out); 
//				mapper_out.flush();
//			}
//			
//			String baseMapperFilePath = System.getProperty("user.dir").replaceAll("\\\\", "/")+"/src/main/java/"+data.get("genericBaseSuperClass").toString().replaceAll("\\.", "/")+".java";
//			File baseMapperFile = new File(baseMapperFilePath);
//			if(!baseMapperFile.exists()){
//				Template mapper_temp = cfg.getTemplate("GenericBaseMapper_temp.ftl");
//				OutputStream mapper_os = new FileOutputStream(createfile(baseMapperFilePath));
//				Writer mapper_out = new OutputStreamWriter(mapper_os);  
//				mapper_temp.process(data, mapper_out); 
//				mapper_out.flush();
//			}
			
			//以下两个公共操作的业务父类模块不做自动生成，由创建项目的时候写死即可，放到公共代码中去，所有项目引用该代码
//			String serviceFilePath = System.getProperty("user.dir").replaceAll("\\\\", "/")+"/src/main/java/"+data.get("genericService").toString().replaceAll("\\.", "/")+".java";
//			File serviceFile = new File(serviceFilePath);
//			if(!serviceFile.exists()){
//				Template mapper_temp = cfg.getTemplate("GenericService_temp.ftl");
//				OutputStream mapper_os = new FileOutputStream(createfile(serviceFilePath));
//				Writer mapper_out = new OutputStreamWriter(mapper_os);  
//				mapper_temp.process(data, mapper_out); 
//				mapper_out.flush();
//			}
//			
//			String baseServiceFilePath = System.getProperty("user.dir").replaceAll("\\\\", "/")+"/src/main/java/"+data.get("genericBaseService").toString().replaceAll("\\.", "/")+".java";
//			File baseServiceFile = new File(baseServiceFilePath);
//			if(!baseServiceFile.exists()){
//				Template mapper_temp = cfg.getTemplate("GenericBaseService_temp.ftl");
//				OutputStream mapper_os = new FileOutputStream(createfile(baseServiceFilePath));
//				Writer mapper_out = new OutputStreamWriter(mapper_os);  
//				mapper_temp.process(data, mapper_out); 
//				mapper_out.flush();
//			}
			
		} catch (IOException e) {
			e.printStackTrace();
		} catch (TemplateException e) {
			e.printStackTrace();
		}
	}
	
	public static File createfile(String path){
		File file = new File(path);
		if(!file.exists()){
			File pf = new File(file.getParent());
			if(!pf.exists()){
				pf.mkdirs();
			}
			try {
				file.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return file;
	}

}