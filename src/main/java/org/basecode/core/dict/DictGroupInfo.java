package org.basecode.core.dict;

import com.dashu.lazyapidoc.annotation.Doc;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class DictGroupInfo implements Serializable {
    @Doc(value="版本号", remark="")
    private String versionId;
    @Doc(value="字典数据", remark="")
    private List<DictType> dict;
}
