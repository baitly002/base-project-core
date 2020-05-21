package org.basecode.common.config.base;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.boot.autoconfigure.AutoConfigurationPackages;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.web.servlet.context.AnnotationConfigServletWebServerApplicationContext;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.core.type.filter.TypeFilter;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Configuration
@ConditionalOnClass(RestController.class)
public class WebBeanDefinitionRegistryPostProcessor implements BeanDefinitionRegistryPostProcessor, ApplicationContextAware {
    private ApplicationContext applicationContext;
    private final static Logger logger = LoggerFactory.getLogger(WebBeanDefinitionRegistryPostProcessor.class);
    @Override
    public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) throws BeansException {
    	if(logger.isDebugEnabled()) {
    		logger.debug("postProcessBeanDefinitionRegistry执行！");
    	}

//        Set<AnnotationAttributes> componentScans = AnnotationConfigUtils.attributesForRepeatable(
//                sourceClass.getMetadata(), ComponentScans.class, ComponentScan.class);
//        if (!componentScans.isEmpty() &&
//                !this.conditionEvaluator.shouldSkip(sourceClass.getMetadata(), ConfigurationPhase.REGISTER_BEAN)) {
//            for (AnnotationAttributes componentScan : componentScans) {
//
//            }
//        }
//    	ComponentScanAnnotationParser parser = new ComponentScanAnnotationParser(applicationContext.getEnvironment(), new DefaultResourceLoader(), new AnnotationBeanNameGenerator(), registry);

        AutowireCapableBeanFactory factory = applicationContext.getAutowireCapableBeanFactory();
        List<String> packages = null;
        ComponentScan.Filter[] excludeFilters = {};
        ComponentScan.Filter[] includeFilters = {};

        String names[] = registry.getBeanDefinitionNames();
        for(String name:names) {
        	logger.debug("SPRING BEAN:{}加载了", name);
        }
        Map<String, Object> componentScan = applicationContext.getBeansWithAnnotation(ComponentScan.class);
        for(Map.Entry<String, Object> entry : componentScan.entrySet()){
            Object instance = entry.getValue();
            Set<ComponentScan> scans = AnnotatedElementUtils.getMergedRepeatableAnnotations(instance.getClass(), ComponentScan.class);
            for (ComponentScan scan : scans) {
//                System.out.println(Arrays.toString(scan.basePackageClasses()));
//                System.out.println(Arrays.toString(scan.basePackages()));
                if(scan!=null && scan.basePackages().length>0){
                    packages = Arrays.asList(scan.basePackages());
                    if(logger.isDebugEnabled()){
                        logger.debug("扫描包路径：{}", Arrays.toString(scan.basePackages()));
                    }
                }
//                excludeFilters = scan.excludeFilters();
//                includeFilters = scan.includeFilters();
            }
        }
        if(packages == null){
            packages = AutoConfigurationPackages.get(factory);
            if(logger.isDebugEnabled()){
                logger.debug("默认扫描包路径：{}", packages);
            }
        }

//        applicationContext.getBeansWithAnnotation(ComponentScan.class).forEach((name, instance) -> {
//            Set<ComponentScan> scans = AnnotatedElementUtils.getMergedRepeatableAnnotations(instance.getClass(), ComponentScan.class);
//            for (ComponentScan scan : scans) {
//                System.out.println(Arrays.toString(scan.basePackageClasses()));
//                System.out.println(Arrays.toString(scan.basePackages()));
//            }
//        });

//        if(!byName("org.springframework.data.redis.connection.RedisConnectionFactory")) {
//        	registry.removeBeanDefinition("redisClientTemplate");
//        }
//        if(!byName("com.alibaba.druid.pool.DruidDataSource")) {
//        	registry.removeBeanDefinition("druidDataSourceConfig");
//        	registry.removeBeanDefinition("druidDataSourceProperty");
//        }

//        Environment environment = applicationContext.getEnvironment();
//        if(environment!=null){
//            String defaultDatasource = environment.getProperty("spring.datasource.druid.url");
//            if(StringUtils.isBlank(defaultDatasource)){
//                if(registry.containsBeanDefinition("mybatisConfig")){
//                    registry.removeBeanDefinition("mybatisConfig");
//                }
//            }
//        }


        
        //查找自定义的注解 也就是自动生成controller的注解配置
//        for(String basePackage:packages){
//
//            boolean useDefaultFilters = false;//是否使用默认的filter，使用默认的filter意味着只扫描那些类上拥有Component、Service、Repository或Controller注解的类。
////            WebCodegenScanner beanScanner = new WebCodegenScanner(registry, useDefaultFilters);
//            ClassPathScanningCandidateComponentProvider beanScanner = new ClassPathScanningCandidateComponentProvider(useDefaultFilters);
////            TypeFilter includeFilter = new AnnotationTypeFilter(RequestMapping.class);
//            TypeFilter includeFilterComponentScan = new AnnotationTypeFilter(ComponentScan.class);
//            beanScanner.addIncludeFilter(includeFilterComponentScan);
////            beanScanner.addIncludeFilter(includeFilter);
//            Set<BeanDefinition> beanDefinitions = beanScanner.findCandidateComponents(basePackage);
//            for (BeanDefinition beanDefinition : beanDefinitions) {
//                //beanName通常由对应的BeanNameGenerator来生成，比如Spring自带的AnnotationBeanNameGenerator、DefaultBeanNameGenerator等，也可以自己实现。
//                String beanName = beanDefinition.getBeanClassName();
////                System.out.println(beanName);
//                //registry.registerBeanDefinition(beanName, beanDefinition);
//            }
//        }

        for(String basePackage:packages){
            boolean useDefaultFilters = false;//是否使用默认的filter，使用默认的filter意味着只扫描那些类上拥有Component、Service、Repository或Controller注解的类。
            WebCodegenScanner beanScanner = new WebCodegenScanner(registry, useDefaultFilters, applicationContext.getEnvironment());
//            ClassPathScanningCandidateComponentProvider beanScanner = new ClassPathScanningCandidateComponentProvider(useDefaultFilters);
//            TypeFilter includeFilterWeb = new AnnotationTypeFilter(Web.class);
            TypeFilter includeFilter = new AnnotationTypeFilter(RestController.class);
//            TypeFilter includeFilterComponentScan = new AnnotationTypeFilter(ComponentScan.class);
//            beanScanner.addIncludeFilter(includeFilterComponentScan);
//            beanScanner.addIncludeFilter(includeFilterWeb);
            beanScanner.addIncludeFilter(includeFilter);
            if(applicationContext instanceof AnnotationConfigServletWebServerApplicationContext){
                AnnotationConfigServletWebServerApplicationContext application = (AnnotationConfigServletWebServerApplicationContext)applicationContext;
                System.out.println(application.getApplicationName());
            }
            Set<BeanDefinition> beanDefinitions = beanScanner.findCandidateComponents(basePackage);
            for (BeanDefinition beanDefinition : beanDefinitions) {
                //beanName通常由对应的BeanNameGenerator来生成，比如Spring自带的AnnotationBeanNameGenerator、DefaultBeanNameGenerator等，也可以自己实现。
                String beanName = beanDefinition.getBeanClassName();
                registry.registerBeanDefinition(beanName, beanDefinition);
            }

            //处理Mybatis 的多个 mapperscan
        }


        //new ClassPathBeanDefinitionScanner();
        //new MybatisAutoConfiguration();
        //new AutoConfigurationPackages();
    /*RootBeanDefinition rootBeanDefinition = new RootBeanDefinition(UserDomain.class);
    rootBeanDefinition.setAutowireMode(RootBeanDefinition.AUTOWIRE_BY_TYPE);
    rootBeanDefinition.getPropertyValues().add("name","pepsi");
    registry.registerBeanDefinition("userDomain",rootBeanDefinition);*/

        //使用不同beanDefinition
//        Class<?> cls = UserDomain.class;
//        BeanDefinitionBuilder builder = BeanDefinitionBuilder.genericBeanDefinition(cls);
//        GenericBeanDefinition definition = (GenericBeanDefinition) builder.getRawBeanDefinition();
//        definition.setAutowireMode(GenericBeanDefinition.AUTOWIRE_BY_TYPE);
//        definition.getPropertyValues().add("name","pepsi02");
//        // 注册bean名,一般为类名首字母小写
//        registry.registerBeanDefinition("userDomain", definition);
    }
    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
        
        if(logger.isDebugEnabled()) {
        	logger.debug("postProcessBeanFactory() 执行！");
        }

        //((DefaultListableBeanFactory) beanFactory).registerBeanDefinition("myBeanName", bd);
    }
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
    
    public static boolean byName(String className) {
    	boolean flag = true;
    	try {
    		flag = null != Class.forName(className);
        } catch (Exception e) {
        	if(logger.isWarnEnabled()) {
        		logger.warn("系统中找不到{}类，请检查是否要用到相应的功能，如果没用到请忽略此警告！", className, e);
        	}
            flag = false;
        }
    	return flag;
    }

}
