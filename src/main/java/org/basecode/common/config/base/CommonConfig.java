package org.basecode.common.config.base;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.core.Appender;
import com.alibaba.fastjson.JSON;
import com.alibaba.nacos.api.NacosFactory;
import com.alibaba.nacos.api.PropertyKeyConst;
import com.alibaba.nacos.api.config.ConfigService;
import com.alibaba.nacos.api.config.listener.Listener;
import com.baomidou.mybatisplus.core.incrementer.IdentifierGenerator;
import org.apache.dubbo.config.ConfigCenterConfig;
import org.basecode.common.dict.DictGroupInfo;
import org.basecode.common.dict.DictInfo;
import org.basecode.common.dict.DictType;
import org.basecode.common.dict.LocalCache;
import org.basecode.common.sequence.MybatisSequenceGenerator;
import org.basecode.common.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.YamlPropertiesFactoryBean;
import org.springframework.boot.env.YamlPropertySourceLoader;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.env.*;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.ClassPathResource;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.io.StringReader;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.Executor;

@Configuration
public class CommonConfig {
	private static Logger logger = LoggerFactory.getLogger(CommonConfig.class);

//	@Bean
//	public static NacosConfigProperties init(){
//		return new NacosConfigProperties();
//	}
//
//	@Bean
//	public static ModelScannerConfigurer scan(){
//		System.out.println("iiiiii");
//		return null;
//	}
	@Autowired
	public Environment environmentTemp;
	
	public static Environment environment;
	public static ConfigCenterConfig configCenterConfig = new ConfigCenterConfig();
	
	@PostConstruct
	public void init() {
		CommonConfig.environment = environmentTemp;
	}

	@Bean
	public IdentifierGenerator idGenerator() {
		return new MybatisSequenceGenerator();
	}
	
	public static String getValue(String key) {
		return environment.getProperty(key);
	}
	/**
	 * 加载自定义配置文件
	 * 
	 * @param environment
	 * @return
	 * @throws IOException
	 */
	@Bean
	public static PropertySourcesPlaceholderConfigurer initCommonConfig(ConfigurableEnvironment environment) throws IOException {
//		// 应用自行加载配置
//		Map<String, String> dubboConfigurations = new HashMap<>();
//		dubboConfigurations.put("dubbo.registry.address", "zookeeper://127.0.0.1:2181");
//		dubboConfigurations.put("dubbo.registry.simplified", "true");
//
//		//将组织好的配置塞给Dubbo框架
//		ConfigCenterConfig configCenter = new ConfigCenterConfig();
//		configCenter.setExternalConfig(dubboConfigurations);

		PropertySourcesPlaceholderConfigurer configurer = new PropertySourcesPlaceholderConfigurer();
		YamlPropertiesFactoryBean yaml = new YamlPropertiesFactoryBean();
		ClassPathResource classPathResource = new ClassPathResource("application.yml", CommonConfig.class.getClassLoader());
		yaml.setResources(classPathResource);// File引入
		configurer.setLocation(classPathResource);
		configurer.setIgnoreUnresolvablePlaceholders(true);
		configurer.setOrder(-150);
		configurer.setEnvironment(environment);
		PropertySource<?> propertySource = new YamlPropertySourceLoader().load("application.yml", classPathResource).get(0);
//		Properties defaultConfig = new Properties();
//		defaultConfig.put("local.ip", getIpAddress());
//		PropertiesPropertySource dpp = new PropertiesPropertySource("defaultConfig", defaultConfig);
//		environment.getPropertySources().addLast(dpp);
		environment.getPropertySources().addLast(propertySource);
		nacosConfig(environment, propertySource);
		logger.debug(">>>>>>>>>>>>>>load " + classPathResource + "|exists:" + classPathResource.exists());

		return configurer;
	}

	public static void nacosConfig(ConfigurableEnvironment environment, PropertySource<?> propertySource){
		try {
			String server_addr_key = "spring.basecode.nacos.server-addr";
			if (propertySource.containsProperty(server_addr_key)) {
				String serverAddr = String.valueOf(propertySource.getProperty(server_addr_key));
				String namespace = propertySource.containsProperty("spring.basecode.nacos.namespace")?String.valueOf(propertySource.getProperty("spring.basecode.nacos.namespace")):"";
				String encode = propertySource.containsProperty("spring.basecode.nacos.encode")?String.valueOf(propertySource.getProperty("spring.basecode.nacos.encode")):"UTF-8";
				
				String prefix = "spring.basecode.nacos.";

				//处理扩展配置 最多处理20个
				for (int i = 0; i < 20; i++) {
					String data_id_key = prefix + "ext-config[" + i + "].data-id";
					String group_key = prefix + "ext-config[" + i + "].group";
					String refresh_key = prefix + "ext-config[" + i + "].refresh";
					String namespace_key = prefix + "ext-config[" + i + "].namespace";
					String encode_key = prefix + "ext-config[" + i + "].encode";
					String file_extension_key = prefix + "ext-config[" + i + "].file-extension";

					if (propertySource.containsProperty(data_id_key)) {
						//存在 spring.basecode.nacos.ext-config[n].data-id 才继续
						getConfig(environment, propertySource, serverAddr, data_id_key, group_key, refresh_key, namespace_key, encode_key, file_extension_key);
					}else{
						break;
					}
				}
				//处理基本配置
				String data_id_key = prefix + "data-id";
				String group_key = prefix + "group";
				String refresh_key = prefix + "refresh";
				String namespace_key = prefix + "namespace";
				String encode_key = prefix + "encode";
				String file_extension_key = prefix + "file-extension";

				if (propertySource.containsProperty(data_id_key)) {
					getConfig(environment, propertySource, serverAddr, data_id_key, group_key, refresh_key, namespace_key, encode_key, file_extension_key);
				}

				//处理字典配置
				if(propertySource.containsProperty(prefix+"dict.data-id")){
					Properties properties = new Properties();
					properties.setProperty(PropertyKeyConst.SERVER_ADDR, serverAddr);
					properties.setProperty(PropertyKeyConst.NAMESPACE, namespace);
					properties.setProperty(PropertyKeyConst.ENCODE, encode);
					ConfigService configService = NacosFactory.createConfigService(properties);

					String dictDataId = String.valueOf(propertySource.getProperty(prefix+"dict.data-id"));
					String dictGroup = propertySource.containsProperty(prefix+"dict.group")?String.valueOf(propertySource.getProperty(prefix+"dict.group")):"DEFAULT_GROUP";
					String dictContent = configService.getConfig(dictDataId, dictGroup, 3000);
					//logger.info("dict content ------------>");
					//System.out.println(dictContent);
					configService.addListener(dictDataId, dictGroup, new Listener() {
						@Override
						public void receiveConfigInfo(String configInfo) {
							//logger.info("dict--->", configInfo);
							if(logger.isDebugEnabled()) {
								logger.debug("获取的数据字典");
								logger.debug(configInfo);
							}
							passDict(configInfo);
						}
						@Override
						public Executor getExecutor() {
							return null;
						}
					});
					passDict(dictContent);
				}
			} else {
				if(logger.isInfoEnabled()) {
					logger.info("系统中没有配置使用远程配置中心");
				}
			}
		}catch (Exception e){
			e.printStackTrace();
		}

		//String dataId = String.valueOf(propertySource.getProperty("spring.basecode.nacos.data-id"));



	}

	public static void getConfig(ConfigurableEnvironment environment, PropertySource<?> propertySource, String serverAddr, String data_id_key, String group_key, String refresh_key, String namespace_key, String encode_key, String file_extension_key){
		try {
			
			String data_id = String.valueOf(propertySource.getProperty(data_id_key));
			String group = propertySource.containsProperty(group_key)?String.valueOf(propertySource.getProperty(group_key)):"DEFAULT_GROUP";
			String refresh = propertySource.containsProperty(refresh_key)?String.valueOf(propertySource.getProperty(refresh_key)):"true";
//			String namespace = propertySource.containsProperty(namespace_key)?String.valueOf(propertySource.getProperty(namespace_key)):"b3404bc0-d7dc-4855-b519-570ed34b62d7";
			String namespace = propertySource.containsProperty(namespace_key)?String.valueOf(propertySource.getProperty(namespace_key)):"";
			String encode = propertySource.containsProperty(encode_key)?String.valueOf(propertySource.getProperty(encode_key)):"UTF-8";
			String file_extension = propertySource.containsProperty(file_extension_key)?String.valueOf(propertySource.getProperty(file_extension_key)):"yaml";
			Properties properties = new Properties();
			properties.setProperty(PropertyKeyConst.SERVER_ADDR, serverAddr);
			properties.setProperty(PropertyKeyConst.NAMESPACE, namespace);
			properties.setProperty(PropertyKeyConst.ENCODE, encode);
			
			if(logger.isDebugEnabled()) {
				logger.debug("正在处理远程配置serverAddr={}, dataId={}, group={}, refresh={}, namespace={}, encode={}, fielExtension={}", serverAddr, data_id, group, refresh, namespace, encode, file_extension);
			}
			ConfigService configService = NacosFactory.createConfigService(properties);

			String content = configService.getConfig(data_id, group, 5000);
			receiveContext(content, data_id, file_extension, serverAddr, group, refresh, namespace, encode, environment.getPropertySources());
			configService.addListener(data_id, group, new Listener() {
				@Override
				public void receiveConfigInfo(String configInfo) {
					receiveContext(configInfo, data_id, file_extension, serverAddr, group, refresh, namespace, encode, environment.getPropertySources());
				}
				@Override
				public Executor getExecutor() {
					return null;
				}
			});
		}catch (Exception e){
			e.printStackTrace();
		}

	}

	public static void receiveContext(String configInfo, String data_id, String file_extension, String serverAddr, String group, String refresh, String namespace, String encode, MutablePropertySources propertySources) {
		if(StringUtils.isNotBlank(configInfo)) {
			Properties p = passNacosResult(file_extension, configInfo, data_id);
			Map<String, String> dubboConfigurations = new HashMap<>();
			p.forEach((k, v) -> {
				logger.debug("远程配置-------->{}={}", k, v);
				if(k.toString().startsWith("dubbo")){
					dubboConfigurations.put(k.toString(), v.toString());
				}
				if(k.toString().startsWith("logging.level.")){
					LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();
					String logging = k.toString().substring(14);
					loggerContext.getLogger(logging).setLevel(Level.toLevel(v.toString()));
				}
				if(k.toString().startsWith("logging.out.appender.")){
					LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();
					String appender = k.toString().substring(21);
					boolean enable = "true".equals(v.toString());
					Appender ad = loggerContext.getLogger("root").getAppender(appender);
					if(ad != null){
						boolean started = ad.isStarted();
						if(started && !enable){
							ad.stop();
						}
						if(!started && enable){
							ad.start();
						}
					}
				}
			});
			configCenterConfig.setExternalConfig(dubboConfigurations);
//			if(logger.isDebugEnabled()) {
//				p.forEach((k, v) -> {
//					logger.debug("远程配置-------->{}={}", k, v);
//				});
//			}
			PropertiesPropertySource pps = new PropertiesPropertySource(data_id, p);
			propertySources.addFirst(pps);
			if(logger.isDebugEnabled()) {
				logger.debug("远程配置处理完成 !   serverAddr={}, dataId={}, group={}, refresh={}, namespace={}, encode={}, fielExtension={}", serverAddr, data_id, group, refresh, namespace, encode, file_extension);
			}
			//logger.info(configInfo);
			//System.out.println("recieve1:" + configInfo);
		}
	}

	public static Properties passNacosResult(String fileExtension, String data, String dataId){
		try{
			if(logger.isDebugEnabled()) {
				logger.debug("paser config : fileExtension={}, data={}", fileExtension, data);
			}
			if (fileExtension.equalsIgnoreCase("properties")) {
				Properties properties = new Properties();

				properties.load(new StringReader(data));
				return properties;
			}else if (fileExtension.equalsIgnoreCase("yaml")
					|| fileExtension.equalsIgnoreCase("yml")) {
				YamlPropertiesFactoryBean yamlFactory = new YamlPropertiesFactoryBean();
				yamlFactory.setResources(new ByteArrayResource(data.getBytes("UTF-8")));
				return yamlFactory.getObject();
//			}else if (fileExtension.equalsIgnoreCase("json")) {
//				Properties properties = new Properties();
//				List<DictInfo> dictInfoList = JSON.parseArray(data, DictInfo.class);
//				for(DictInfo dictInfo : dictInfoList) {
//					properties.put(dictInfo.getKey(), dictInfo.getValue());
//				}
//				return properties;
			}else{
				Properties properties = new Properties();
				properties.put("spring.basecode.nacos.data-id."+dataId, data);
				properties.put("spring.basecode.nacos.data-id."+dataId+".file-extension", fileExtension);
				return properties;
			}

		}catch (Exception e){
			e.printStackTrace();
		}
		return null;
	}
	
	public static String getIpAddress() {
		InetAddress address = null;
        try {
            address = InetAddress.getLocalHost();
        } catch (UnknownHostException e) {
        	logger.error("无法解释服务器地址", e);
        	return "0.0.0.0";
        }
        return address.getHostAddress()==null?"0.0.0.0":address.getHostAddress();
	}

	public static void passDict(String dictContent){
		try {
			DictGroupInfo dictGroup = JSON.parseObject(dictContent, DictGroupInfo.class);
			if(dictGroup != null){
				LocalCache.dictGroups = dictGroup;
				if(dictGroup.getVersionId().startsWith("v")){
					LocalCache.versionId = dictGroup.getVersionId().substring(1);
				}else{
					LocalCache.versionId = dictGroup.getVersionId();
				}
				List<DictType> dicts = dictGroup.getDict();
				if(dicts!=null){
					for (DictType dict : dicts) {
						LocalCache.dictCache.put(dict.getKey(), dict.getValue());

						//
						Map<Object, Object> nameMap = new HashMap<>();
						Map<Object, Object> valueMap = new HashMap<>();
						for(DictInfo dictInfo : dict.getValue()){
							nameMap.put(dictInfo.getValue(), dictInfo.getName());
							valueMap.put(dictInfo.getName(), dictInfo.getValue());
						}

						LocalCache.nameCache.put(dict.getKey(), nameMap);
						LocalCache.valueCache.put(dict.getKey(), valueMap);
					}
				}
			}
		}catch (Exception e){
			logger.warn("解释数据字典出错，请检查字典格式是否符合标准！", e);
		}
	}
}