package org.basecode.common.config.base;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanNameAware;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.util.StringUtils;

//@Configuration
public class ModelScannerConfigurer implements BeanDefinitionRegistryPostProcessor, InitializingBean, ApplicationContextAware, BeanNameAware {

//	static final String DEFAULT_RESOURCE_PATTERN = "**/*.class";
//	protected final Log logger = LogFactory.getLog(getClass());
//	private String resourcePattern = DEFAULT_RESOURCE_PATTERN;
//	private ResourcePatternResolver resourcePatternResolver;
	
	private String basePackage;
	private Class<?> markerInterface;
	private ApplicationContext applicationContext;
	private String beanName;
	
	public String getBasePackage() {
		return basePackage;
	}

	public void setBasePackage(String basePackage) {
		this.basePackage = basePackage;
	}

	public void setMarkerInterface(Class<?> superClass) {
		this.markerInterface = superClass;
	}

//	@Override
//	public Object postProcessBeforeInitialization(Object bean, String beanName)
//			throws BeansException {
//		return bean;
//	}
//
//	@Override
//	public Object postProcessAfterInitialization(Object bean, String beanName)
//			throws BeansException {
//		String className = bean.getClass().getName();
//		if(className.startsWith(basePackage)){
//			System.out.println("after----------->"+bean.getClass().getName());
//		}
//		
//		return bean;
//	}

	@Override
	public void postProcessBeanFactory(
			ConfigurableListableBeanFactory beanFactory) throws BeansException {
		
	}

	@Override
	public void setBeanName(String name) {
		this.beanName = name;
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext)
			throws BeansException {
		this.applicationContext = applicationContext;
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		//notNull(this.basePackage, "Property 'basePackage' is required");

	}
	
//	protected String resolveBasePackage(String basePackage) {
//		return ClassUtils.convertClassNameToResourcePath(this.environment.resolveRequiredPlaceholders(basePackage));
//	}

	@Override
	public void postProcessBeanDefinitionRegistry(
			BeanDefinitionRegistry registry) throws BeansException {
//		this.resourcePatternResolver = ResourcePatternUtils.getResourcePatternResolver(this.applicationContext);
//		try {
//			String packageSearchPath = ResourcePatternResolver.CLASSPATH_ALL_URL_PREFIX + resolveBasePackage(basePackage) + '/' + this.resourcePattern;
//			Resource[] resources = this.resourcePatternResolver.getResources(packageSearchPath);
//			boolean traceEnabled = logger.isTraceEnabled();
//			boolean debugEnabled = logger.isDebugEnabled();
//			for (Resource resource : resources) {
//				if (traceEnabled) {
//					logger.trace("Scanning " + resource);
//				}
//				if (resource.isReadable()) {
//					System.out.println("--------------->"+resource);
//				}else {
//					if (traceEnabled) {
//						logger.trace("Ignored because not readable: " + resource);
//					}
//				}
//			}
//		}catch (IOException ex) {
//			throw new BeanDefinitionStoreException("I/O failure during classpath scanning", ex);
//		}
		ClassPathModelScanner scanner = new ClassPathModelScanner();
		scanner.setMarkerInterface(this.markerInterface);
		scanner.setResourceLoader(applicationContext);
		scanner.scan(StringUtils.tokenizeToStringArray(this.basePackage, ConfigurableApplicationContext.CONFIG_LOCATION_DELIMITERS));
	}
}
