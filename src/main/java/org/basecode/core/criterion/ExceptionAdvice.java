package org.basecode.core.criterion;

import com.dashu.lazyapidoc.annotation.ReturnFilter;
import org.apache.dubbo.rpc.RpcException;
import org.apache.skywalking.apm.toolkit.trace.TraceContext;
import org.basecode.core.criterion.exception.BusinessException;
import org.basecode.core.criterion.model.ResultMsg;
import org.basecode.core.criterion.model.ValidationMessage;
import org.hibernate.validator.internal.engine.path.PathImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.HandlerMethod;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Path;
import javax.validation.ValidationException;
import java.lang.reflect.UndeclaredThrowableException;
import java.util.ArrayList;
import java.util.List;

@RestControllerAdvice
public class ExceptionAdvice {

    private static final Logger logger = LoggerFactory.getLogger(ExceptionAdvice.class);
    
    @ExceptionHandler(UndeclaredThrowableException.class)//全部
    public Object targetExp(UndeclaredThrowableException exception, HttpServletRequest request, HttpServletResponse response, HandlerMethod handlerMethod){
        Throwable throwable = exception.getUndeclaredThrowable().getCause();
        if(throwable instanceof BusinessException) {
        	return exp((BusinessException)throwable, request, response, handlerMethod);
        }
        if(throwable instanceof RpcException) {
        	return ignoreCriterionForException(throwable, handlerMethod);
        }
        return ignoreCriterionForException(exception, handlerMethod);
    }
    @ExceptionHandler({ValidationException.class, BindException.class})//定义的业务异常
    public Object validation(Exception exception, HttpServletRequest request, HttpServletResponse response, HandlerMethod handlerMethod){
        logger.error("参数验证失败", exception);
        List<ValidationMessage> validationMessages = new ArrayList<>();
//        response.setHeader("jackson", "true");
        if(exception instanceof ConstraintViolationException){
            ConstraintViolationException e = (ConstraintViolationException) exception;
            for(ConstraintViolation constraintViolation : e.getConstraintViolations()){
                String propertyName = constraintViolation.getPropertyPath().toString();
                Path path = constraintViolation.getPropertyPath();
                if(path instanceof  PathImpl){
                    propertyName = ((PathImpl) path).getLeafNode().asString();
                }
                logger.warn("参数验证失败-->[{}]:[{}]", propertyName, constraintViolation.getMessage());
                validationMessages.add(new ValidationMessage(propertyName, constraintViolation.getMessage()));
            }
        }
        if(exception instanceof BindException){
            for(FieldError fieldError : ((BindException) exception).getFieldErrors()){
                logger.warn("参数验证失败-->[{}].[{}]:[{}]",fieldError.getObjectName(), fieldError.getField(), fieldError.getDefaultMessage());
                validationMessages.add(new ValidationMessage(fieldError.getField(), fieldError.getDefaultMessage(), fieldError.getObjectName()));
            }
        }

        if(handlerMethod.hasMethodAnnotation(ReturnFilter.class)){
            ReturnFilter returnFilter = handlerMethod.getMethodAnnotation(ReturnFilter.class);
            if(returnFilter.ignoreCriterion()){
                return validationMessages;
            }
        }
        return new ResultMsg<Object>(0, "3000000", "参数校验有误！", null, validationMessages);
    }

    @ExceptionHandler(BusinessException.class)//定义的业务异常
    public Object exp(BusinessException exception, HttpServletRequest request, HttpServletResponse response, HandlerMethod handlerMethod){
        logger.warn(exception.getExpDesc(), exception);
        response.setStatus(exception.getHttpStatus());
        return ignoreCriterionForException(exception, handlerMethod);
    }

    //@ControllerAdvice(annotations = {RestController.class}) 配置你需要拦截的类，
    //@ControllerAdvice(basePackages = "com.demo") 这也可以
    @ExceptionHandler(Exception.class)//全部
    public Object exp(Exception exception, HttpServletRequest request, HttpServletResponse response, HandlerMethod handlerMethod){
        logger.error(String.format("【TraceId:%s; 异常提示是：%s】", TraceContext.traceId(), exception.getMessage()), exception);
//        if(exception.getClass().getName().startsWith("org.springframework.security.oauth2")){
//            return exception;
//        }
        return ignoreCriterionForException(exception, handlerMethod);
    }

//		@ModelAttribute //去掉注解才生效
//	    public Token bindToken(HttpServletRequest request) {
//	        System.out.println("============应用到所有@RequestMapping注解方法，在其执行之前把返回值放入Model");
//	        String tt = request.getHeader("token");
//	        System.out.println(tt);
//	        Token token = new Token();
//	        token.setToken(tt);
//	        return token;
//	    }

    //@InitBinder　去掉注解才生效
    public void initBinder(WebDataBinder binder) {
        System.out.println("============应用到所有@RequestMapping注解方法，在其执行之前初始化数据绑定器");
    }

    public Object ignoreCriterionForException(Object object, HandlerMethod handlerMethod){
        if(handlerMethod.hasMethodAnnotation(ReturnFilter.class)){
            ReturnFilter returnFilter = handlerMethod.getMethodAnnotation(ReturnFilter.class);
            if(returnFilter.ignoreCriterion()){
                return object;
            }
        }
        return ResultMsgUtil.response(object);
    }

}
