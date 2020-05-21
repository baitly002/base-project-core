package org.basecode.common.dict;

import com.dashu.lazyapidoc.annotation.Doc;
import lombok.Data;

import java.io.Serializable;

@Data
public class DictInfo implements Serializable {

    @Doc(value="字典成员编码", remark="")
    private String key;

    @Doc(value="字典值", remark="")
    private String value;

    @Doc(value="字典成员名称", remark="")
    private String name;

    @Doc(value="字典备注", remark="")
    private String remark;

    @Doc(value="字典状态", remark="1：有效 0：无效")
    private int status;

    @Doc(value="字典值类型", remark="1：int 0：字符串")
    private int valType;
}
