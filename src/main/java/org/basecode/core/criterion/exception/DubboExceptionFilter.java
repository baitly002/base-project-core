package org.basecode.core.criterion.exception;

//import com.alibaba.dubbo.common.Constants;

import com.dashu.lazyapidoc.annotation.ReturnFilter;
import org.apache.dubbo.common.constants.CommonConstants;
import org.apache.dubbo.common.extension.Activate;
import org.apache.dubbo.common.utils.ReflectUtils;
import org.apache.dubbo.rpc.*;
import org.apache.dubbo.rpc.service.GenericService;
import org.basecode.core.util.BeanPropertyFilter;
import org.basecode.core.util.ReflectionUtils;
import org.basecode.core.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;

@Activate(group = CommonConstants.PROVIDER)
public class DubboExceptionFilter extends ListenableFilter {

    private final Logger logger = LoggerFactory.getLogger(DubboExceptionFilter.class);

    public DubboExceptionFilter(){
        super.listener = new ExceptionListener();
    }
    static class ExceptionListener implements Listener {

        public void onResponse(Result appResponse, Invoker<?> invoker, Invocation invocation)         {

            ReturnFilter returnFilter = null;
            try {
                Method method = ReflectionUtils.getDeclaredMethod(invoker.getInterface(), invocation.getMethodName(), invocation.getParameterTypes());
//            Annotation[] annotations = method.getAnnotations();
                if(method != null) {
                    returnFilter = method.getAnnotation(ReturnFilter.class);
                }
            }catch (Exception e){
                e.printStackTrace();
            }
            Object data = appResponse.getValue();
            if(returnFilter!=null){
                boolean exclude = true;
                boolean simple = true;
                String[] keys = null;
                try {
                    simple = "simple".equals(returnFilter.type());
                    if(StringUtils.isNotBlank(returnFilter.value())){
                        keys = returnFilter.value().split(",");
                    }else if(returnFilter.excludes().length>0){
                        keys = returnFilter.excludes();
                    }else if(returnFilter.includes().length>0){
                        exclude = false;
                        keys = returnFilter.includes();
                    }else{

                    }
                    BeanPropertyFilter.excludeFilterInfo(data, "", "", keys, exclude, 1, simple);
                }catch (Exception e){

                }
            }
            if (appResponse.hasException() && GenericService.class != invoker.getInterface()) {

                try {
                    System.out.println("onresponse 执行了----------------");
                    Throwable exception = appResponse.getException();

                    if (exception instanceof RpcException) {

                        return;
                    }

                } catch (Throwable e) {
                    return;
                }
            }
        }

        @Override
        public void onError(Throwable t, Invoker<?> invoker, Invocation invocation) {
            System.out.println("onError 执行了----------------");
        }
//        @Override
//        public void onMessage(Result var1, Invoker<?> invoker, Invocation invocation){
//            System.out.println("onMessage 执行了---------------");
//        }
    }

    @Override
    public Result invoke(Invoker<?> invoker, Invocation invocation) throws RpcException {
        try {
            Result result = invoker.invoke(invocation);
            if (result.hasException() && GenericService.class != invoker.getInterface()) {
                try {
                    Throwable exception = result.getException();

                    // directly throw if it's checked exception
                    if (!(exception instanceof RuntimeException) && (exception instanceof Exception)) {
                        return result;
                    }
                    // directly throw if the exception appears in the signature
                    try {
                        Method method = invoker.getInterface().getMethod(invocation.getMethodName(), invocation.getParameterTypes());
                        Class<?>[] exceptionClassses = method.getExceptionTypes();
                        for (Class<?> exceptionClass : exceptionClassses) {
                            if (exception.getClass().equals(exceptionClass)) {
                                return result;
                            }
                        }
                    } catch (NoSuchMethodException e) {
                        return result;
                    }

                    // for the exception not found in method's signature, print ERROR message in server's log.
                    logger.error("Got unchecked and undeclared exception which called by " + RpcContext.getContext().getRemoteHost()
                            + ". service: " + invoker.getInterface().getName() + ", method: " + invocation.getMethodName()
                            + ", exception: " + exception.getClass().getName() + ": " + exception.getMessage(), exception);

                    // directly throw if exception class and interface class are in the same jar file.
                    String serviceFile = ReflectUtils.getCodeBase(invoker.getInterface());
                    String exceptionFile = ReflectUtils.getCodeBase(exception.getClass());
                    if (serviceFile == null || exceptionFile == null || serviceFile.equals(exceptionFile)) {
                        return result;
                    }
                    // directly throw if it's JDK exception
                    String className = exception.getClass().getName();
                    if (className.startsWith("java.") || className.startsWith("javax.")) {
                        return result;
                    }
                    if (className.startsWith("org.basecode")) {
                        return result;
                    }
                    // directly throw if it's dubbo exception
                    if (exception instanceof RpcException) {
                        return result;
                    }

                    // otherwise, wrap with RuntimeException and throw back to the client
                    return result;
//                    return new RpcResult(new RuntimeException(StringUtils.toString(exception)));
                } catch (Throwable e) {
                    logger.warn("Fail to ExceptionFilter when called by " + RpcContext.getContext().getRemoteHost()
                            + ". service: " + invoker.getInterface().getName() + ", method: " + invocation.getMethodName()
                            + ", exception: " + e.getClass().getName() + ": " + e.getMessage(), e);
                    return result;
                }
            }
            return result;
        } catch (RuntimeException e) {
            logger.error("Got unchecked and undeclared exception which called by " + RpcContext.getContext().getRemoteHost()
                    + ". service: " + invoker.getInterface().getName() + ", method: " + invocation.getMethodName()
                    + ", exception: " + e.getClass().getName() + ": " + e.getMessage(), e);
            throw e;
        }
    }
}