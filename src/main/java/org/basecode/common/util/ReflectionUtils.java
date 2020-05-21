package org.basecode.common.util;

import java.lang.reflect.Method;

public class ReflectionUtils {

    public static Method getDeclaredMethod(Class<?> clazz, String methodName, Class<?> ... parameterTypes){
        Method method = null ;
        Class<?> c = clazz;
        do {
            if(c == null){
                break;
            }
            try {
                method = c.getDeclaredMethod(methodName, parameterTypes);
                break;
            } catch (Exception e) {}
            c = c.getSuperclass();
        }while (c != Object.class);

        return method;
//        for(Class<?> c = clazz; c != Object.class; c = c.getSuperclass()){
//            try {
//                method = c.getDeclaredMethod(methodName, parameterTypes);
//                return method;
//            } catch (Exception e) {
//
//            }
//        }
//        return null;
    }

}
