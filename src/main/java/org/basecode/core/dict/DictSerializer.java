package org.basecode.core.dict;

import com.alibaba.fastjson.annotation.JSONField;
import com.alibaba.fastjson.serializer.*;
import org.basecode.core.util.StringUtils;

import java.io.IOException;
import java.lang.reflect.Type;

public class DictSerializer implements ContextObjectSerializer {

    public static final DictSerializer instance = new DictSerializer();

//    public ContextObjectSerializerNew newInstance(){
//        return instance;
//    }

    @Override
    public void write(JSONSerializer serializer, Object object, BeanContext context) throws IOException {
//        System.out.println(context.getName());
        SerializeWriter out = serializer.out;
        if(object instanceof Integer){
            out.writeInt((Integer) object);
        }else{
            out.writeString(object.toString());
        }
        JSONField jsonField = context.getField().getAnnotation(JSONField.class);
        if(jsonField != null && StringUtils.isNotBlank(jsonField.format())){
            try {
                Object dictNameVal = LocalCache.nameCache.getIfPresent(jsonField.format()).get(object);
                if (dictNameVal != null) {
                    out.append(",");
                    if (out.isEnabled(SerializerFeature.PrettyFormat)) {
                        serializer.println();
                    }
                    out.writeFieldName(context.getName()+"Text", true);
                    out.writeString(dictNameVal.toString());
//                    serializer.writeWithFieldName(dictNameVal.toString(), context.getName()+"Text");
//                    out.writeFieldValue(',', context.getName() + "Text", dictNameVal.toString());
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }
//        out.writeFieldValue(',', "age", 200);
//        out.writeFieldName("age");
//        out.writeString("200");
    }

    @Override
    public void write(JSONSerializer serializer, Object object, Object fieldName, Type fieldType, int features) throws IOException {
        System.out.println(serializer.getContext());
        System.out.println(fieldName);
        SerializeWriter out = serializer.out;
        out.write(object.toString());
//	    serializer.writeWithFieldName("kkk", "vvvvvvvvv");
//	    serializer.out.write(object);
    }
}