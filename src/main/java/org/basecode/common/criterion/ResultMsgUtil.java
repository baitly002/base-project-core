package org.basecode.common.criterion;

import com.alibaba.fastjson.serializer.PropertyPreFilter;
import org.basecode.common.criterion.exception.BaseException;
import org.basecode.common.criterion.exception.BusinessException;
import org.basecode.common.criterion.model.ResultMsg;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.List;

public class ResultMsgUtil {

    public static ResultMsg response(Object object){
        return response(object, null);
    }

	public static ResultMsg response(Object object, PropertyPreFilter filter) {
		ResultMsg<Object> msg = null;
//        if(object instanceof ResultMsg){
//            return (ResultMsg)object;
//        }
        if(object instanceof BusinessException){
            BusinessException exception = (BusinessException) object;
            msg = new ResultMsg<Object>(0, exception.getExpCode(), exception.getExpDesc(), filter, exception);
        }else if(object instanceof BaseException){
            BaseException exception = (BaseException) object;
            msg = new ResultMsg<Object>(0, "20000000", exception.getMessage(), filter, exception.getMessage());
        }else if((object instanceof MethodArgumentNotValidException) || (object instanceof BindException)) {
            BindingResult result = null;
            String message = null;
            if (object instanceof MethodArgumentNotValidException) {
                MethodArgumentNotValidException exception = (MethodArgumentNotValidException) object;
                result = exception.getBindingResult();
                message = exception.getMessage();
            } else if (object instanceof BindException) {
                BindException exception = (BindException) object;
                result = exception.getBindingResult();
                message = exception.getMessage();
            }
            final List<FieldError> fieldErrors = result.getFieldErrors();
            StringBuilder errorSb = new StringBuilder();
            String errorMessage = null;
            for (FieldError error : fieldErrors) {
                errorSb.append(error.getDefaultMessage());
                errorSb.append("，");
            }
            if (errorSb.length() > 0) {
                errorMessage = errorSb.substring(0, errorSb.length() - 1);
            }
            msg = new ResultMsg<Object>(0, "20000000", errorMessage, filter, message);
//        }else if(object instanceof OAuth2Exception){

        }else if(object instanceof RuntimeException){
            RuntimeException exception = (RuntimeException) object;
            //msg = new ResultMsg<Object>(0, "20000000", exception.getMessage(), "", "", exception.getClass().getName().concat(":==>").concat(exception.getLocalizedMessage()));
            msg = new ResultMsg<Object>(0, "20000000", exception.getMessage(), filter, exception);
        }else if(object instanceof Throwable){
            Throwable exception = (Throwable) object;
            msg = new ResultMsg<Object>(0, "20000000", exception.getMessage(), filter, exception.getMessage());
        }else if(object instanceof Boolean){
            boolean flag = (Boolean) object;
            if(flag){
                msg = new ResultMsg<Object>(1, "", "操作成功", filter, flag);
            }else{
                msg = new ResultMsg<Object>(0, "", "操作失败", filter, flag);
            }
//        }else if(object == null){
//            msg = new ResultMsg<Object>(0, "", "系统找不到相应数据返回，请确认参数是否错误，是否有效", filter, object);
        }else{
            msg = new ResultMsg<Object>(1, filter, object);
        }
        //ResultMsg resultMsg = new ResultMsg(1, "", "", body);
        return msg;
	}
}
