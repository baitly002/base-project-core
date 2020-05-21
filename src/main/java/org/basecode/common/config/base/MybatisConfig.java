package org.basecode.common.config.base;

import com.baomidou.mybatisplus.extension.spring.MybatisSqlSessionFactoryBean;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnSingleCandidate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

import javax.sql.DataSource;

@Configuration
@ConditionalOnSingleCandidate(DataSource.class)
@ConditionalOnProperty(value = {"spring.datasource.druid.url"/*, "mybatis.mapper-locations"*/}, matchIfMissing = false)
public class MybatisConfig {

    @Autowired
    private DataSource dataSource;

//    @Autowired
//    private MybatisProperties properties;

    //配置类型别名
    /*@Value("${mybatis.mapper-locations}")*/
    private String mapperLocations = "classpath*:/mybatis/*/*Mapper.xml";

    /*@Value("${mybatis.type-aliases-package}")*/
    private String typeAliasesPackage;

    @Bean
    @ConditionalOnMissingClass(value = {"com.baomidou.mybatisplus.extension.spring.MybatisSqlSessionFactoryBean"})
    public SqlSessionFactory sqlSessionFactory2() throws Exception {

        SqlSessionFactoryBean bean = new SqlSessionFactoryBean();
//        MybatisSqlSessionFactoryBean bean = new MybatisSqlSessionFactoryBean();
        bean.setDataSource(dataSource);
        bean.setConfiguration(getGlobalConfiguration());
//        bean.setMapperLocations(new PathMatchingResourcePatternResolver().getResources(mapperLocations));
        bean.setMapperLocations(new PathMatchingResourcePatternResolver().getResources(mapperLocations));
//        bean.setTypeAliasesPackage(typeAliasesPackage);
        return bean.getObject();
    }

    @Bean
    @ConditionalOnClass(MybatisSqlSessionFactoryBean.class)
    public SqlSessionFactory sqlSessionFactory() throws Exception {

//        SqlSessionFactoryBean bean = new SqlSessionFactoryBean();
        MybatisSqlSessionFactoryBean bean = new MybatisSqlSessionFactoryBean();
        bean.setDataSource(dataSource);
//        bean.setConfiguration(getGlobalConfiguration());
//        bean.setMapperLocations(new PathMatchingResourcePatternResolver().getResources(mapperLocations));
        bean.setMapperLocations(new PathMatchingResourcePatternResolver().getResources(mapperLocations));
//        bean.setTypeAliasesPackage(typeAliasesPackage);
        return bean.getObject();
    }



    /**
     * 这里全部使用mybatis-autoconfigure 已经自动加载的资源。不手动指定
     * 配置文件和mybatis-boot的配置文件同步
     * @return
     */
//    @Bean
//    public MybatisSqlSessionFactoryBean mybatisSqlSessionFactoryBean() {
//        MybatisSqlSessionFactoryBean mybatisPlus = new MybatisSqlSessionFactoryBean();
//        mybatisPlus.setDataSource(dataSource);
//        mybatisPlus.setVfs(SpringBootVFS.class);
//        if (StringUtils.hasText(this.properties.getConfigLocation())) {
//            mybatisPlus.setConfigLocation(this.resourceLoader.getResource(this.properties.getConfigLocation()));
//        }
//        mybatisPlus.setConfiguration(properties.getConfiguration());
//        if (!ObjectUtils.isEmpty(this.interceptors)) {
//            mybatisPlus.setPlugins(this.interceptors);
//        }
//        MybatisConfiguration mc = new MybatisConfiguration();
//        mc.setDefaultScriptingLanguage(MybatisXMLLanguageDriver.class);
//        mybatisPlus.setConfiguration(mc);
//        if (this.databaseIdProvider != null) {
//            mybatisPlus.setDatabaseIdProvider(this.databaseIdProvider);
//        }
//        if (StringUtils.hasLength(this.properties.getTypeAliasesPackage())) {
//            mybatisPlus.setTypeAliasesPackage(this.properties.getTypeAliasesPackage());
//        }
//        if (StringUtils.hasLength(this.properties.getTypeHandlersPackage())) {
//            mybatisPlus.setTypeHandlersPackage(this.properties.getTypeHandlersPackage());
//        }
//        if (!ObjectUtils.isEmpty(this.properties.resolveMapperLocations())) {
//            mybatisPlus.setMapperLocations(this.properties.resolveMapperLocations());
//        }
//        return mybatisPlus;
//    }


    public org.apache.ibatis.session.Configuration getGlobalConfiguration() {
        return new org.apache.ibatis.session.Configuration();
    }



//    @Bean
//    public SqlSessionFactory sqlSessionFactory() throws Exception {
//        SqlSessionFactoryBean factoryBean = new SqlSessionFactoryBean();
//        factoryBean.setDataSource(dataSource); // 使用titan数据源, 连接titan库
//        return factoryBean.getObject();
//    }

    @Bean
    public SqlSessionTemplate sqlSessionTemplate() throws Exception {
        SqlSessionTemplate template = new SqlSessionTemplate(sqlSessionFactory()); // 使用上面配置的Factory
        return template;
    }
}
