package org.basecode.common.util;

import com.alibaba.fastjson.JSON;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * javabean对象的操作工具类
 * 
 * @author charles email:baitly002@gmail.com
 *
 */
public class BeanUtil{

	public static final Logger logger = LoggerFactory.getLogger(BeanUtil.class);
	/**
	 * 将javabean转换成map对象
	 * 
	 * @param o1
	 * @return
	 */
	public static Map<String, Object> basebean2map(Object o1) {
		return basebean2map(o1, true);
	}
	public static Map<String, Object> basebean2map(Object o1, boolean filterNullString) {
		Map<String, Object> m = new HashMap<String, Object>();
		Method[] m1 = o1.getClass().getDeclaredMethods();
		for (Method mm1 : m1) {
			if (mm1.getName().startsWith("get")) {
				try {
					if(mm1.invoke(o1)!=null){//值不能为NULL
//						m.put(mm1.getName().substring(3).toLowerCase(), mm1.invoke(o1));
						if(filterNullString){
							if(!"".equals(mm1.invoke(o1))){
								String key = mm1.getName().substring(3);
								m.put(key.substring(0,1).toLowerCase()+key.substring(1), mm1.invoke(o1));
							}
						}else{
							String key = mm1.getName().substring(3);
							m.put(key.substring(0,1).toLowerCase()+key.substring(1), mm1.invoke(o1));
						}
					}
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				} catch (IllegalArgumentException e) {
					e.printStackTrace();
				} catch (InvocationTargetException e) {
					e.printStackTrace();
				}
			}
		}
		return m;
	}
	
	/**
	 * 父类对象转为子类对象
	 * @param clazzParent
	 * @param clazzChild
	 * @return
	 */
	public static <T> T toChildBean(Object clazzParent, Class<T> clazzChild) {
		return JSON.parseObject(JSON.toJSONString(clazzParent), clazzChild);
    }

	/**
	 * 转换bean为map
	 *
	 * @param source 要转换的bean
	 * @param <T>    bean类型
	 * @return 转换结果
	 */
	public static <T> Map<String, Object> bean2Map(T source) throws IllegalAccessException {
		Map<String, Object> result = new HashMap<>();

		Class<?> sourceClass = source.getClass();
		//拿到所有的字段,不包括继承的字段
		Field[] sourceFiled = sourceClass.getDeclaredFields();
		for (Field field : sourceFiled) {
			field.setAccessible(true);//设置可访问,不然拿不到private
			//配置了注解的话则使用注解名称,作为header字段
//			FieldName fieldName = field.getAnnotation(FieldName.class);
//			if (fieldName == null) {
//				result.put(field.getName(), field.get(source));
//			} else {
//				if (fieldName.Ignore()) continue;
//				result.put(fieldName.value(), field.get(source));
//			}
			result.put(field.getName(), field.get(source));
		}
		return result;
	}

	public static Map<String, Object> bean2Map(Map<String, Object> initMap, Object... sources) {
		try {
			for (Object source : sources) {
				Class<?> sourceClass = source.getClass();
				//拿到所有属性，包括继承的
				Field[] sourceFiled = sourceClass.getFields();
				for (Field field : sourceFiled) {
					field.setAccessible(true);//设置可访问,不然拿不到private
					initMap.put(field.getName(), field.get(source));
				}
			}
		}catch (Exception e){
			logger.error("javabean 转 map 出错", e);
		}
		return initMap;
	}
}
