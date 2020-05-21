package org.basecode.core.config.base;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.annotation.Configuration;

//@Component
@Configuration
public class WebBeanFactoryPostProcessor implements BeanFactoryPostProcessor {
    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
        try {
//            List<String> packages = AutoConfigurationPackages.get(beanFactory);
//            for(String p : packages){
//                System.out.println(p);
//            }
//            String[] names = beanFactory.getBeanDefinitionNames();
//            for(String name : names){
//                System.out.println(name);
//
//                if(name.indexOf("addressSercice") > -1){
//                    System.out.println(name);
//                }
//            }
//            System.out.println("WebBeanFactoryPostProcessor");
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
