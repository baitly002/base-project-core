package org.basecode.common;

import java.io.Serializable;

public class FilterInfo implements Serializable {

    private String key;
    private Integer level;
    private Class<?> type;
    private Object value;
    private Boolean exclude;
    private String matchKey;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public Class<?> getType() {
        return type;
    }

    public void setType(Class<?> type) {
        this.type = type;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    public Boolean getExclude() {
        return exclude;
    }

    public void setExclude(Boolean exclude) {
        this.exclude = exclude;
    }

    public Integer getLevel() {
        return level;
    }

    public void setLevel(Integer level) {
        this.level = level;
    }

    public String getMatchKey() {
        return matchKey;
    }

    public void setMatchKey(String matchKey) {
        this.matchKey = matchKey;
    }

    @Override
    public String toString() {
//        String endStr = "\r\n";
        String endStr = "\t\t\t\t";
        StringBuffer sb = new StringBuffer();
        sb.append("key:").append(key).append(endStr);
        sb.append("matchKey:").append(matchKey).append(endStr);
        sb.append("level:").append(level).append(endStr);
        sb.append("type:").append(type.getName()).append(endStr);
        sb.append("exclude:").append(exclude).append(endStr);
        if(value!=null){
            sb.append("value:").append(value.getClass().getName()).append(endStr);
        }else{
            sb.append("value:").append("null").append(endStr);
        }
        sb.append("\r\n");
        return sb.toString();
    }
}
