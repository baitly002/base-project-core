package org.basecode.core.criterion;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.PropertyPreFilter;
import com.alibaba.fastjson.serializer.SerializeFilter;
import com.alibaba.fastjson.serializer.SimplePropertyPreFilter;
import com.alibaba.fastjson.support.config.FastJsonConfig;
import com.dashu.lazyapidoc.annotation.ReturnFilter;
import com.dashu.lazyapidoc.util.LevelPropertyPreFilter;
import com.dashu.lazyapidoc.util.StringUtils;
import org.basecode.core.config.web.FastJsonHttpMessageConverterConfig;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

@RestControllerAdvice(annotations = RestController.class)
public class ResultMsgAdvice implements ResponseBodyAdvice {
    @Override
    public boolean supports(MethodParameter returnType, Class converterType) {
        return true;
    }

    @Override
    public Object beforeBodyWrite(Object object, MethodParameter returnType, MediaType selectedContentType, Class selectedConverterType, ServerHttpRequest request, ServerHttpResponse response) {
        boolean hasFilter = false;
        try {
            Class.forName("com.dashu.lazyapidoc.annotation.ReturnFilter");
            hasFilter = true;
        } catch (ClassNotFoundException e) {
            hasFilter = false;
        }
        if(hasFilter){
            ReturnFilter returnFilter = returnType.getMethodAnnotation(ReturnFilter.class);
            if(returnFilter != null) {
                if(returnFilter.ignoreCriterion()){
                    return object;//忽略统一返回包装，直接返回原样数据
                }
                if ("simple".equalsIgnoreCase(returnFilter.type())) {
                    SimplePropertyPreFilter filter = new SimplePropertyPreFilter();
                    String values = returnFilter.value();
                    for (String v : values.split(",")) {
//                    if(prefixNotEmpty){
//                        filter.getExcludes().add(prefix+"."+v.trim());
//                    }else {
                        filter.getExcludes().add(v.trim());
//                    }
                    }
                    for (String exclude : returnFilter.excludes()) {
//                    if(prefixNotEmpty){
//                        filter.getExcludes().add(prefix+"."+exclude);
//                    }else{
                        filter.getExcludes().add(exclude);
//                    }

                    }
                    for (String include : returnFilter.includes()) {
//                    if(prefixNotEmpty){
//                        filter.getIncludes().add(prefix+"."+include);
//                    }else{
                        filter.getIncludes().add(include);
//                    }

                    }
                    filter.setMaxLevel(returnFilter.maxLevel());
                    return processWrite(object, filter, returnType, selectedContentType, selectedConverterType);
//                    return ResultMsgUtil.response(object, filter);

                } else {
                    String glabPrefix = "data";
                    boolean notEmpty = StringUtils.isNotBlank(returnFilter.prefix());
                    String prefix = notEmpty ? glabPrefix + "." + returnFilter.prefix() : glabPrefix;
                    LevelPropertyPreFilter levelPropertyPreFilter = new LevelPropertyPreFilter();
                    boolean prefixNotEmpty = StringUtils.isNotBlank(prefix);
                    if (prefixNotEmpty) {
                        String[] excludes = returnFilter.excludes();
                        String[] includes = returnFilter.includes();
                        for (String exclude : excludes) {
                            levelPropertyPreFilter.addExcludes(prefix + "." + exclude);
                        }
                        for (String include : includes) {
                            levelPropertyPreFilter.addIncludes(prefix + "." + include);
                        }
                    } else {
                        levelPropertyPreFilter.addExcludes(returnFilter.excludes());
                        levelPropertyPreFilter.addIncludes(returnFilter.includes());
                    }

                    levelPropertyPreFilter.setMaxLevel(returnFilter.maxLevel());
                    String values = returnFilter.value();
                    for (String v : values.split(",")) {
                        if (prefixNotEmpty) {
                            levelPropertyPreFilter.getExcludes().add(prefix + "." + v.trim());
                        } else {
                            levelPropertyPreFilter.getExcludes().add(v.trim());
                        }

                    }
//                    return ResultMsgUtil.response(object, levelPropertyPreFilter);
                    return processWrite(object, levelPropertyPreFilter, returnType, selectedContentType, selectedConverterType);
                }
            }

        }

        return processWrite(object, null, returnType, selectedContentType, selectedConverterType);
//    	return ResultMsgUtil.response(object, null);
    }

    public Object processWrite(Object object, PropertyPreFilter filter, MethodParameter returnType, MediaType selectedContentType, Class selectedConverterType){
        if(selectedContentType.isCompatibleWith(MediaType.APPLICATION_JSON)){
            return ResultMsgUtil.response(object, filter);
        }

        if(selectedConverterType == StringHttpMessageConverter.class){
            object = ResultMsgUtil.response(object, null);
            FastJsonConfig fastJsonConfig = FastJsonHttpMessageConverterConfig.getFastJsonConfigIstance();
            //获取全局配置的filter
            SerializeFilter[] globalFilters = fastJsonConfig.getSerializeFilters();
            object = JSON.toJSONString(object, fastJsonConfig.getSerializeConfig(), globalFilters,
                    fastJsonConfig.getDateFormat(), JSON.DEFAULT_GENERATE_FEATURE, fastJsonConfig.getSerializerFeatures());
//            List<SerializeFilter> allFilters = new ArrayList<SerializeFilter>(Arrays.asList(globalFilters));
//            if(object instanceof ResultMsg){
//                ResultMsg val = (ResultMsg) object;
//                if(val.getFilter()!=null){
//                    allFilters.add(val.getFilter());
//                }
//            }
//            object = JSON.toJSONString(object, fastJsonConfig.getSerializeConfig(), allFilters.toArray(new SerializeFilter[allFilters.size()]),
//                    fastJsonConfig.getDateFormat(), JSON.DEFAULT_GENERATE_FEATURE, fastJsonConfig.getSerializerFeatures());
        }
//        if("String".equalsIgnoreCase(selectedConverterType.getSimpleName())){
//
//        }
        return object;
    }
}
