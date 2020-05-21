package org.basecode.core;

import org.apache.commons.lang3.ArrayUtils;
import org.basecode.core.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.MethodDescriptor;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.util.*;

public class PropertyFilterTest {
    private static final Logger logger = LoggerFactory.getLogger(PropertyFilterTest.class);


    public List<FilterInfo> excludeFilterInfo(Object obj, List<FilterInfo> filterList, String parentKey, String parentMatchKey, String[] keys, boolean exclude, int level, boolean simple) throws Exception{
//        if(filterList == null){
//            filterList = new ArrayList<>();
//        }
//        FilterInfo filterInfo = null;
        if(obj instanceof Map){
            Map map = (Map)obj;
            Iterator iterator = map.entrySet().iterator();
            while (iterator.hasNext()){
                Map.Entry entry = (Map.Entry) iterator.next();
                Object key = entry.getKey();
                String containKey = null;
                if(simple){//简单模式
                    containKey = String.valueOf(key);

                }else{
                    //指定模式
                    containKey = StringUtils.isNotBlank(parentMatchKey)?parentMatchKey+"."+String.valueOf(key):String.valueOf(key);
                }
                if(ArrayUtils.contains(keys, containKey) == exclude){//存在排除配置的key
//                    filterInfo = builderFilterInfo(level, parentKey, parentMatchKey, String.valueOf(key), map.get(key).getClass(), filterInfo);
//                    filterList.add(filterInfo);
//                    BeanUtils.setProperty(map.get(key), String.valueOf(key), null );
//                    map.put(key, null);
                    iterator.remove();
                    continue;
                }
                Object nextObj = map.get(key);
                String pkey = StringUtils.isNotBlank(parentKey)?parentKey+"."+String.valueOf(key):String.valueOf(key);
                String pMatchKey = StringUtils.isNotBlank(parentMatchKey)?parentMatchKey+"."+String.valueOf(key):String.valueOf(key);
                iteratorNextObject(nextObj, filterList, pkey, pMatchKey, keys, exclude, level, simple);
            }
//            for(Object key : map.keySet()){
//
//            }
        }else if(obj instanceof Collection){
            Collection collection = (Collection) obj;
            Iterator iterator = collection.iterator();
            int i=0;
            while (iterator.hasNext()){
                excludeFilterInfo(iterator.next(), filterList, parentKey+".["+i+"]", parentMatchKey, keys, exclude, level+1, simple);
                i++;
            }
        }else if(obj instanceof Enum){

        }else {
//            PropertyDescriptor[] propertyDescriptors = getPropertyDescriptors(obj);
//            PropertyDescriptor[] propertyDescriptors = pub.getPropertyDescriptors(obj);
            BeanInfo beanInfo = Introspector.getBeanInfo(obj.getClass());
            PropertyDescriptor[] propertyDescriptors = beanInfo.getPropertyDescriptors();
            for (PropertyDescriptor p : propertyDescriptors) {
                if(p.getName().equalsIgnoreCase("class") || obj==null){
                    continue;
                }
                String containKey = null;
                if(simple){//简单模式
                    containKey = p.getName();
                }else{
                    //指定模式
                    containKey = StringUtils.isNotBlank(parentMatchKey)?parentMatchKey+"."+p.getName():p.getName();
                }
                if(ArrayUtils.contains(keys, containKey) == exclude){//存在排除配置的key
//                    filterInfo = builderFilterInfo(level, parentKey, parentMatchKey, p.getName(), p.getPropertyType(), filterInfo);
//                    filterList.add(filterInfo);
//                    BeanUtils.setProperty(getProperty(obj, p.getName()), p.getName(), null );
                   Method writeMethod = p.getWriteMethod();
                   if(writeMethod == null){
                       MethodDescriptor[] methodDescriptors = beanInfo.getMethodDescriptors();
                       writeMethod = getWriteMethod(beanInfo.getMethodDescriptors(), getMethodNameFormField(p.getName(), "set"));
                   }
                   try {
                       writeMethod.invoke(obj, (Object) null);
                   }catch (IllegalArgumentException e){
                       if(logger.isDebugEnabled()){
                           String key = StringUtils.isNotBlank(parentKey)?parentKey+"."+p.getName():p.getName();
                           logger.debug("过滤字段["+key+"]出错！不符合javabean get/set 规则， 请使用包装类，而非基本数据类型（byte、short、int、long、float、double、char、boolean）", e);
                       }
                   }
                    continue;
                }

                Object nextObj = null;
                try {
                    Method readMethod = p.getReadMethod();
                    nextObj = readMethod.invoke(obj);
//                    nextObj = getProperty(obj, p.getName());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                String key = StringUtils.isNotBlank(parentKey)?parentKey+"."+p.getName():p.getName();
                String matchKey = StringUtils.isNotBlank(parentMatchKey)?parentMatchKey+"."+p.getName():p.getName();
                iteratorNextObject(nextObj, filterList, key, matchKey, keys, exclude, level, simple);

            }
        }
        return filterList;
    }

    public static boolean isPrimitive(String returnType){
        //boolean ， byte ， short ， int ， long ， char ， float和double
        if("boolean".equals(returnType)){
            return true;
        }else if("byte".equals(returnType)){
            return true;
        }else if("short".equals(returnType)){
            return true;
        }else if("int".equals(returnType)){
            return true;
        }else if("long".equals(returnType)){
            return true;
        }else if("char".equals(returnType)){
            return true;
        }else if("float".equals(returnType)){
            return true;
        }else if("double".equals(returnType)){
            return true;
        }else{
            return false;
        }
    }

    public static boolean hasNext(Class<?> clazz){
        return true;
    }

    /**
     * 是否jdk类型变量
     *
     * @param clazz
     * @return
     * @throws IllegalAccessException
     */
    private static boolean isJDKType(Class clazz) {
        return clazz.getPackage().getName().startsWith("javax.")
                || clazz.getPackage().getName().startsWith("java.")
                || clazz.getName().startsWith("javax.")
                || clazz.getName().startsWith("java.");
    }

    private static FilterInfo builderFilterInfo(Integer level, String parentKey, String parentMatchKey, String key, Class<?> type, FilterInfo filterInfo){
        filterInfo = new FilterInfo();
        filterInfo.setLevel(level);
        if (StringUtils.isNotBlank(parentKey)) {
            filterInfo.setKey(parentKey + "." + key);
        } else {
            filterInfo.setKey(key);
        }
        if (StringUtils.isNotBlank(parentMatchKey)) {
            filterInfo.setMatchKey(parentMatchKey + "." + key);
        } else {
            filterInfo.setMatchKey(key);
        }

        filterInfo.setType(type);

        return filterInfo;
    }

    private void iteratorNextObject(Object nextObj, List<FilterInfo> filterList, String parentKey, String parentMatchKey, String[] keys, boolean exclude, int level, boolean simple) throws  Exception{
        if (nextObj != null) {
            if (nextObj instanceof Map) {
                excludeFilterInfo(nextObj, filterList, parentKey, parentMatchKey, keys, exclude, level+1, simple);
            } else if (nextObj instanceof Collection) {
                excludeFilterInfo(nextObj, filterList, parentKey, parentMatchKey, keys, exclude, level, simple);
            } else if (nextObj instanceof Enum) {

            } else if (!isJDKType(nextObj.getClass())) {
                excludeFilterInfo(nextObj, filterList, parentKey, parentMatchKey, keys, exclude, level + 1, simple);
            }else{

            }
        }
    }

    private String propertyName(Method m) {
        String methodName = m.getName().substring(3);
        return methodName.length() > 1 ? Introspector.decapitalize(methodName) : methodName.toLowerCase(Locale.ENGLISH);
    }

    public String getMethodNameFormField(String name, String methodPrefix) {

        if (name == null || name.length() == 0) {
            return name;
        }
        if (name.length() > 1 && Character.isUpperCase(name.charAt(1)) &&
                Character.isUpperCase(name.charAt(0))){
            return methodPrefix+name;
        }
        char chars[] = name.toCharArray();
        chars[0] = Character.toUpperCase(chars[0]);
        return methodPrefix+new String(chars);
    }

    public Method getWriteMethod(MethodDescriptor[] methodDescriptors, String methodName){
        for(MethodDescriptor methodDescriptor : methodDescriptors){
            if(methodDescriptor.getName().equals(methodName)){
                return methodDescriptor.getMethod();
            }
        }
        return null;
    }
}
