package org.basecode.common.config.web;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

//@Component
@Slf4j
public class WebControllerHandlerMapping extends RequestMappingHandlerMapping {

    @Override
    protected boolean isHandler(Class<?> beanType) {
//        return false;
        if(beanType.isInterface()){
            return false;
        }else{
            boolean flag = (ExtendAnnotatedElementUtils.hasAnnotation(beanType, Controller.class) ||
                    ExtendAnnotatedElementUtils.hasAnnotation(beanType, RequestMapping.class));
            if(flag){
                log.debug("init controller -->:{}", beanType.getName());
            }
            return flag;
            //如果父类有Controller RequestMapping注解，忽略 因为系统已经为接口生成了相应的controller
//            Class<?>[] classes =  beanType.getInterfaces();
//            for(Class<?> clazz : classes){
//                if(AnnotatedElementUtils.hasAnnotation(clazz, Controller.class) ||
//                        AnnotatedElementUtils.hasAnnotation(clazz, RequestMapping.class)){
//                    return false;
//                }
//            }
        }

    }
}
