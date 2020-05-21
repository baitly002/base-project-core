package org.basecode.common.criterion.message;

import com.dashu.lazyapidoc.annotation.Doc;
import com.dashu.lazyapidoc.util.StringUtils;
import org.basecode.common.config.base.CommonConfig;

import java.io.Serializable;

public class BusinessMessage implements Serializable {

    @Doc(value = "消息编码", remark = "")
    private String messageCode;

    @Doc(value = "消息内容", remark = "")
    private String	message;

    public BusinessMessage(String messageCode, String message){
        this.messageCode = messageCode;
        this.message = message;
    }

    public BusinessMessage(String messageCode, String message, Object... params){
        this.messageCode = messageCode;
        if(StringUtils.isNotBlank(messageCode)) {
        	String msg = CommonConfig.getValue("spring.basecode.message["+messageCode+"]");
//        	String msg = CommonConfig.getValue(messageCode);
        	if(StringUtils.isNotBlank(msg)) {
        		message = msg;
        	}
        }
        for(Object param : params){
            message = message.replaceFirst("\\{}", String.valueOf(param));
        }
        this.message = message;
    }

	public String getMessageCode() {
		return messageCode;
	}

	public String getMessage() {
		return message;
	}
    
    
}
