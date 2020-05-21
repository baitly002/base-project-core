package org.basecode.common.config.web;

import com.alibaba.fastjson.serializer.ExtendSerializeConfig;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.alibaba.fastjson.serializer.ToStringSerializer;
import com.alibaba.fastjson.support.config.FastJsonConfig;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;

import java.util.ArrayList;
import java.util.List;

public class FastJsonHttpMessageConverterConfig {
    private FastJsonHttpMessageConverterConfig(){

    }

    private static volatile FastJsonConfig fastJsonConfigIstance;
    private static volatile HttpMessageConverter converterIstance;

    //定义一个共有的静态方法，返回该类型实例
    public static FastJsonConfig getFastJsonConfigIstance() {
        // 对象实例化时与否判断（不使用同步代码块，instance不等于null时，直接返回对象，提高运行效率）
        if (fastJsonConfigIstance == null) {
            //同步代码块（对象未初始化时，使用同步代码块，保证多线程访问时对象在第一次创建后，不再重复被创建）
            synchronized (FastJsonHttpMessageConverterConfig.class) {
                //未初始化，则初始instance变量
                if (fastJsonConfigIstance == null) {
//                    instance = new Sequence();
                    fastJsonConfigIstance = initFastJsonConfig();
                }
            }
        }
        return fastJsonConfigIstance;
    }
    public static HttpMessageConverter getConverterIstance() {
        // 对象实例化时与否判断（不使用同步代码块，instance不等于null时，直接返回对象，提高运行效率）
        if (converterIstance == null) {
            //同步代码块（对象未初始化时，使用同步代码块，保证多线程访问时对象在第一次创建后，不再重复被创建）
            synchronized (FastJsonHttpMessageConverterConfig.class) {
                //未初始化，则初始instance变量
                if (converterIstance == null) {
//                    instance = new Sequence();
                    converterIstance = init();
                }
            }
        }
        return converterIstance;
    }

    public static synchronized HttpMessageConverter init(){
        //针对字段的处理
        WebFastJsonHttpMessageConverter converter = new WebFastJsonHttpMessageConverter();
//    		FastJsonHttpMessageConverter converter = new FastJsonHttpMessageConverter();

        converter.setFastJsonConfig(initFastJsonConfig());

        //处理中文乱码问题
        List<MediaType> fastMediaTypes = new ArrayList<>();
        fastMediaTypes.add(MediaType.APPLICATION_JSON_UTF8);
        converter.setSupportedMediaTypes(fastMediaTypes);

        return converter;
    }

    public static synchronized FastJsonConfig initFastJsonConfig(){
        FastJsonConfig fastJsonConfig = new FastJsonConfig();

        fastJsonConfig.setSerializerFeatures(SerializerFeature.WriteNullListAsEmpty,// List字段如果为null,输出为[],而非null
                SerializerFeature.WriteMapNullValue,//加上后，字段为null的也会输出
                SerializerFeature.WriteNullStringAsEmpty,//字符类型字段如果为null,输出为”“,而非null
                SerializerFeature.WriteNullBooleanAsFalse,//Boolean字段如果为null,输出为false,而非null
                SerializerFeature.PrettyFormat  //结果是否格式化,默认为false
        );

        //Long to string
//		    SerializeConfig serializeConfig = SerializeConfig.globalInstance;
        ExtendSerializeConfig serializeConfig = new ExtendSerializeConfig();
        serializeConfig.put(Long.class , ToStringSerializer.instance);
        serializeConfig.put(Long.TYPE , ToStringSerializer.instance);

//        SerializeConfig serializeConfig = SerializeConfig.globalInstance;
//        serializeConfig.put(Long.class , LongToStringSerializer.instance);
//        serializeConfig.put(Long.TYPE , LongToStringSerializer.instance);

        //日期格式化
        fastJsonConfig.setDateFormat("yyyy-MM-dd HH:mm:ss");
        fastJsonConfig.setSerializeConfig(serializeConfig);

        return fastJsonConfig;
    }
}
