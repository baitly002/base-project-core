package org.basecode.common;

import org.apache.commons.beanutils.PropertyUtilsBean;
import org.apache.commons.lang3.ArrayUtils;
import org.basecode.common.util.StringUtils;

import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.util.*;

public class PropertyFilter {


    public List<FilterInfo> excludeFilterInfo(PropertyUtilsBean pub, Object obj, List<FilterInfo> filterList, String parentKey, String parentMatchKey, String[] keys, boolean exclude, int level, boolean simple) throws Exception{
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
                iteratorNextObject(pub, nextObj, filterList, pkey, pMatchKey, keys, exclude, level, simple);
            }
//            for(Object key : map.keySet()){
//
//            }
        }else if(obj instanceof Collection){
            Collection collection = (Collection) obj;
            Iterator iterator = collection.iterator();
            int i=0;
            while (iterator.hasNext()){
                excludeFilterInfo(pub, iterator.next(), filterList, parentKey+".["+i+"]", parentMatchKey, keys, exclude, level+1, simple);
                i++;
            }
        }else if(obj instanceof Enum){

        }else {
//            PropertyDescriptor[] propertyDescriptors = getPropertyDescriptors(obj);
            PropertyDescriptor[] propertyDescriptors = pub.getPropertyDescriptors(obj);
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
                   writeMethod.invoke(obj, (Object)null);
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
                iteratorNextObject(pub, nextObj, filterList, key, matchKey, keys, exclude, level, simple);

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

    private void iteratorNextObject(PropertyUtilsBean pub, Object nextObj, List<FilterInfo> filterList, String parentKey, String parentMatchKey, String[] keys, boolean exclude, int level, boolean simple) throws  Exception{
        if (nextObj != null) {
            if (nextObj instanceof Map) {
                excludeFilterInfo(pub, nextObj, filterList, parentKey, parentMatchKey, keys, exclude, level+1, simple);
            } else if (nextObj instanceof Collection) {
                excludeFilterInfo(pub, nextObj, filterList, parentKey, parentMatchKey, keys, exclude, level, simple);
            } else if (nextObj instanceof Enum) {

            } else if (!isJDKType(nextObj.getClass())) {
                excludeFilterInfo(pub, nextObj, filterList, parentKey, parentMatchKey, keys, exclude, level + 1, simple);
            }else{

            }
        }
    }

    private String propertyName(Method m) {
        String methodName = m.getName().substring(3);
        return methodName.length() > 1 ? Introspector.decapitalize(methodName) : methodName.toLowerCase(Locale.ENGLISH);
    }
}
