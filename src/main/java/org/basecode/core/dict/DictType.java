package org.basecode.core.dict;

import com.dashu.lazyapidoc.annotation.Doc;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class DictType implements Serializable {
    @Doc(value="字典分类编码", remark="")
    private String key;
    @Doc(value="字典分类名称", remark="")
    private String type;
    @Doc(value="字典分类描述", remark="")
    private String desc;
    @Doc(value="字典各数据详细", remark="")
    private List<DictInfo> value;
}
