package org.basecode.common.criterion.model;

import com.dashu.lazyapidoc.annotation.Doc;
import lombok.Data;
import org.basecode.common.config.base.CommonConfig;
import org.basecode.common.util.StringUtils;

import java.io.Serializable;

@Data
public class ValidationMessage implements Serializable {

    public ValidationMessage(String paramter, String messageRaw){
        //1000000
        this.paramter = paramter;
        this.messageRaw = messageRaw;
        if(messageRaw.contains("=")){
            this.code = messageRaw.substring(0, 7);
            if(!StringUtils.isBlank(code)){
                String msg = CommonConfig.getValue("spring.basecode.error["+code+"]");
                if(StringUtils.isNotBlank(msg)){
                    this.message = msg;
                }else{
                    this.message = messageRaw.substring(8);
                }
            }else {
                this.message = messageRaw.substring(8);
            }
        }else {
            this.message = messageRaw;
        }
    }
    public ValidationMessage(String paramter, String messageRaw, String objectName){
        this(paramter, messageRaw);
        this.objectName = objectName;
    }
    @Doc("参数名称")
    private String paramter;
    @Doc("检验的错误码")
    private String code;
    @Doc("错误信息")
    private String message;
    @Doc("源错误信息")
    private String messageRaw;
    @Doc("参数所属对象,可能为空")
    private String objectName;
}
