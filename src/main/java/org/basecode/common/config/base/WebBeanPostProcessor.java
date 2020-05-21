package org.basecode.common.config.base;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

@Configuration
public class WebBeanPostProcessor implements BeanPostProcessor {

    @Autowired

    private Environment environment;

    @Override

    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
//        if(bean.getClass().isInterface()){
//            System.out.println("--------------->"+bean.getClass().getAnnotations());
//        }
//        //String property = environment.getProperty("server.port");
//        if(bean.getClass().getName().indexOf("AddressService")>-1) {
//            System.out.println(bean.getClass().getName() + "777777777777777777777777777777777777777777777777");
//        }
        return bean;

    }

    @Override

    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {

        return bean;

    }
}
