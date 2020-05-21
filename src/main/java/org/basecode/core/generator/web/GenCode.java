package org.basecode.core.generator.web;

import org.basecode.core.generator.web.WebCodegen;
import org.basecode.core.util.ClassUtil;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class GenCode {
    public static void gen(Class<?> clazz, String autowiredType){
        if(autowiredType == null){
            autowiredType = "dubbo";
        }
        Set<String> classPaths = new HashSet<>();
        List<Class<?>> classList = new ArrayList<>();

        classPaths.add(clazz.getPackage().getName());

        ComponentScan componentScan = clazz.getAnnotation(ComponentScan.class);
        //System.out.println(componentScan.basePackages());
        if(componentScan!=null) {
            String[] basePackages = componentScan.basePackages();
            for (String basePackage : basePackages) {
                if (basePackage.endsWith(".\\*")) {
                    basePackage = basePackage.replace(".\\*", "");
                    classPaths.add(basePackage);
                } else {
                    classPaths.add(basePackage);
                }
            }
        }
        for(String p : classPaths){
            classList.addAll(ClassUtil.getClasses(p));
        }
        System.out.println("=======================    正在动态生成Controller类     ===========================");
        for(Class clz : classList){
            if(clz.isInterface()){
                if(clz.getAnnotation(RequestMapping.class)!=null) {
                    System.out.println("正在处理接口类 ==>" + clz.getName());
                    if("spring".equals(autowiredType)){
                        //spring 原生支持，无需生成
                        WebCodegen.gen(clz.getName(), autowiredType);
                    }else{
                        //dubbo 生成调用
                        WebCodegen.gen(clz.getName(), autowiredType);
                    }

                }
            }

        }
        System.out.println("=======================           动态生成结束          ===========================");

    }
    public static void main(String[] args) {
//        String autowiredType = "dubbo";
//
//        if(args[0]!=null){
//            autowiredType = "dubbo";
//        }
//        Set<String> classPaths = new HashSet<>();
//        List<Class<?>> classList = new ArrayList<>();
//        classPaths.add("org.basecode");
//        if(args[1]!=null){
//            String path = args[1];
//            String[] ps = path.split(",");
//            for(String p : ps){
//                classPaths.add(p);
//            }
//        }
//        for(String p : classPaths){
//            classList.addAll(ClassUtil.getClasses(p));
//        }
//        for(Class clazz : classList){
//            WebCodegen.gen(clazz.getName(), autowiredType);
//        }
    }
}
