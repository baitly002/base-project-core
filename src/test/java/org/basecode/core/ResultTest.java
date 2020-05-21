package org.basecode.core;

import com.dashu.lazyapidoc.annotation.Doc;
import org.basecode.core.criterion.model.ResultMsg;

/**
 * sdklskdjls
 */
@Doc("dddd")
public class ResultTest extends ResultMsg {

    //kdlsk
    private Integer state;
    private String age;
    private String name;
    private boolean flag;
    private Boolean open;

    public Integer getState() {
        return state;
    }

    public void setState(Integer state) {
        this.state = state;
    }

    public String getAge() {
        return age;
    }

    public ResultTest setAge(String age) {
        this.age = age;
        return this;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isFlag() {
        return flag;
    }

    public void setFlag(boolean flag) {
        this.flag = flag;
    }

    public Boolean getOpen() {
        return open;
    }

    public void setOpen(Boolean open) {
        this.open = open;
    }
}
