package com.alibaba.fastjson.serializer;

import com.dashu.lazyapidoc.annotation.Doc;
import org.basecode.common.criterion.annotations.DateRaw;
import org.basecode.common.criterion.annotations.DictField;

import java.lang.reflect.Field;

public class ExtendSerializeConfig extends SerializeConfig {

    @Override
    public ObjectSerializer getObjectWriter(Class<?> clazz) {
        //首先从父类的serializers缓存中取
        ObjectSerializer writer = get(clazz);

        //如果取不到，从自身向父类扫描注解，一但找到，创建自定义Serializer
        if(writer == null){
            Class tempClass = clazz;
            out:while (tempClass != null){//当父类为NULL时，说明到达了最上层的object类
                Field[] fields = tempClass.getDeclaredFields();
                for(Field field :fields){
                    if(field.isAnnotationPresent(Doc.class) || field.isAnnotationPresent(DictField.class) || field.isAnnotationPresent(DateRaw.class)){
                        writer = new ExtendJavaBeanSerializer(clazz);
                        break out;
                    }
                }
                tempClass = tempClass.getSuperclass();
            }
        }
        //放入缓存中
        if(writer != null){
            put(clazz, writer);
            return writer;
        }
        return super.getObjectWriter(clazz);
    }
}
