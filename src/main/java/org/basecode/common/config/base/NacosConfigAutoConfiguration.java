package org.basecode.common.config.base;

import org.springframework.beans.factory.BeanFactoryUtils;
import org.springframework.context.ApplicationContext;

//@Configuration
//@ConditionalOnProperty(name = "spring.basecode.nacos.config.enabled", matchIfMissing = true)
//@EnableConfigurationProperties({NacosConfigProperties.class})
public class NacosConfigAutoConfiguration {

    //@Bean
    public NacosConfigProperties nacosConfigProperties(ApplicationContext context) {
        if (context.getParent() != null
                && BeanFactoryUtils.beanNamesForTypeIncludingAncestors(
                context.getParent(), NacosConfigProperties.class).length > 0) {
            return BeanFactoryUtils.beanOfTypeIncludingAncestors(context.getParent(),
                    NacosConfigProperties.class);
        }
        NacosConfigProperties nacosConfigProperties = new NacosConfigProperties();

        return nacosConfigProperties;
    }
}
