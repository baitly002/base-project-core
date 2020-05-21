package org.basecode.core.generator.web;


import javassist.*;
import javassist.bytecode.*;
import javassist.bytecode.annotation.Annotation;
import javassist.bytecode.annotation.MemberValue;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

/**
 * 以自定义注解方式生成controller代码
 */
public class WebCodegen {

    public static CtClass overClass(String classPath){
        try {
            ClassPool pool = ClassPool.getDefault();
            CtClass oldClass = pool.get(classPath);
            ClassFile oldFile = oldClass.getClassFile();
            oldFile.removeAttribute(AnnotationsAttribute.visibleTag);
            return oldClass;
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    /**
     *
     * @param classPath
     * @param autowiredType
     */
    public static Class<?> gen(String classPath, String autowiredType){
        try{
            ClassPool pool = ClassPool.getDefault();
            CtClass oldClass = pool.get(classPath);
            ClassFile oldFile = oldClass.getClassFile();
            //List<AttributeInfo> attributes = oldFile.getAttributes();
            AnnotationsAttribute oldAttribute = (AnnotationsAttribute) oldFile.getAttribute(AnnotationsAttribute.visibleTag);

            //String sourceName = sourceClass.getName();
            String oldName = oldClass.getName();
            oldName = oldName.replaceFirst("service.", "web.");
            oldName = oldName.replaceFirst("Service", "Controller");
            CtClass newClass = pool.makeClass(oldName.endsWith("Controller")?oldName:oldName+"Controller");


            ClassFile newFile = newClass.getClassFile();
            ConstPool constpool = newFile.getConstPool();

            // 类附上注解
            AnnotationsAttribute classAttr = new AnnotationsAttribute(constpool, AnnotationsAttribute.visibleTag);
            List<AttributeInfo> oas = oldFile.getAttributes();
            for(AttributeInfo attributeInfo : oas){
                if(attributeInfo instanceof AnnotationsAttribute){
                    Annotation annotation = ((AnnotationsAttribute) attributeInfo).getAnnotation("org.basecode.core.generator.web.Web");
                    MemberValue memberValue = annotation.getMemberValue("value");
                    Annotation request = new Annotation("org.springframework.web.bind.annotation.RequestMapping",constpool);
                    request.addMemberValue("value", memberValue);

                    classAttr.addAnnotation(request);
                }
            }
            if(oldAttribute!=null) {

                Annotation[] annotations = oldAttribute.getAnnotations();
                for (Annotation annotation : annotations) {
                    //先添加原来类年的注解
                    Annotation a = new Annotation(annotation.getTypeName(), constpool);
                    Set<String> names = annotation.getMemberNames();
                    if (names != null) {
                        for (String name : names) {
                            a.addMemberValue(name, annotation.getMemberValue(name));
                        }
                    }

                    classAttr.addAnnotation(a);
                }
            }
            //再增加restcontroller注解  采用自定义注解时才要
            Annotation controller = new Annotation("org.springframework.web.bind.annotation.RestController",constpool);
            classAttr.addAnnotation(controller);

            newFile.addAttribute(classAttr);
            //oldFile.removeAttribute(AnnotationsAttribute.visibleTag);


            CtField field = CtField.make("private "+oldClass.getName()+" service;", newClass);
            FieldInfo fieldInfo = field.getFieldInfo();
            // 属性附上注解 spring
            if("spring".equalsIgnoreCase(autowiredType)) {
                AnnotationsAttribute fieldAttr = new AnnotationsAttribute(constpool, AnnotationsAttribute.visibleTag);
                Annotation autowired = new Annotation("org.springframework.beans.factory.annotation.Autowired", constpool);
                fieldAttr.addAnnotation(autowired);
                fieldInfo.addAttribute(fieldAttr);
                newClass.addField(field);
            }else {
                AnnotationsAttribute fieldAttr = new AnnotationsAttribute(constpool, AnnotationsAttribute.visibleTag);
                Annotation reference = new Annotation("com.alibaba.dubbo.config.annotation.Reference", constpool);
                fieldAttr.addAnnotation(reference);
                fieldInfo.addAttribute(fieldAttr);
                newClass.addField(field);
            }

            //反射方式获取参数名称
//            Method[] ms = clazz.getDeclaredMethods();
//            for(Method m : ms){
//                Parameter[] ps = m.getParameters();
//                for(Parameter parameter:ps) {
//                	System.out.println("pppp---->"+parameter.getName());
//                }
//            }

            //javassist方式获取参数名称
            CtMethod[] oldMethods = oldClass.getDeclaredMethods();
            for(CtMethod oldMethod:oldMethods){

                if(oldMethod.getAnnotation(RequestMapping.class)!=null || oldMethod.getAnnotation(GetMapping.class)!=null
                        || oldMethod.getAnnotation(PostMapping.class)!=null || oldMethod.getAnnotation(PutMapping.class)!=null
                        || oldMethod.getAnnotation(DeleteMapping.class)!=null){
                    if(!"java.io.Serializable".equals(oldMethod.getReturnType().getName())) {
                        String parameterNames = getInterfaceParamterName(oldMethod);
                        MethodInfo methodInfo = oldMethod.getMethodInfo();
                        List<AttributeInfo> attributeInfos = methodInfo.getAttributes();

                        StringBuffer body = new StringBuffer();
//                        System.out.println(oldMethod.getReturnType().getName());
//                        body.append("public ").append(oldMethod.getReturnType().getName()).append(" ").append(oldMethod.getName()).append("(");
//                        CtClass[] pts = oldMethod.getParameterTypes();
//                        String[] pns = parameterNames.split(", ");
//
//                        for(int i=0; i<pts.length; i++){
//                            //newMethod.addLocalVariable(pns[i], pts[i]);
//                            //body.append(pns[i]).append("=$").append(i+1).append(";");
//                            if(i==0){
//                                body.append(pts[i].getName()).append(" ").append(pns[i]);
//                            }else{
//                                body.append(", ").append(pts[i].getName()).append(" ").append(pns[i]);
//                            }
//
//                        }
//                        body.append(")").append("{");
//                        body.append("return service.").append(oldMethod.getName()).append("("+parameterNames+");").append("}");
                        //MethodParametersAttribute methodParameters= (MethodParametersAttribute)methodInfo.getAttribute("MethodParameters");
                        CtMethod newMethod = new CtMethod(oldMethod, newClass, null);
                        //CtMethod newMethod = new CtMethod(oldMethod.getReturnType(), oldMethod.getName(), oldMethod.getParameterTypes(), newClass);
//                        System.out.println("=================");
//                        System.out.println(body.toString());

                        //CtMethod newMethod = CtMethod.make(body.toString(), newClass);
                        //newMethod.getMethodInfo().addAttribute(methodParameters);
                        newMethod.setModifiers(Modifier.PUBLIC);
                        for (AttributeInfo attributeInfo : attributeInfos) {
                            newMethod.getMethodInfo().addAttribute(attributeInfo);
                        }
                        body.append("{").append("return service.").append(newMethod.getName()).append("($$);").append("}");
                        newMethod.setBody(body.toString());
                        newClass.addMethod(newMethod);
                    }else{
                        //返回 java.io.Serializable 的忽略
                    }
                }else{
//                    System.out.println(oldMethod.getLongName()+"不是controller方法");
                }

            }
//            if("spring".equals(autowiredType)){
                return newClass.toClass();
//            }else{
                //spring
//                ConstPool oldConstPool =  oldFile.getConstPool();
//                Annotation con = new Annotation("org.springframework.web.bind.annotation.RestController",oldConstPool);
//                AnnotationsAttribute classAttrOld = new AnnotationsAttribute(oldConstPool, AnnotationsAttribute.visibleTag);
//                classAttrOld.addAnnotation(con);
//                oldClass.toClass();
//            }

            //newClass.writeFile(rootPath());
        }catch (Exception e){
            System.out.println("动态创建Controller类失败，详细如下：");
            e.printStackTrace();
            return null;
        }
    }

    public static String rootPath(){
        String rootPath = ClassLoader.getSystemResource("").toString();
        if(rootPath.startsWith("file:")) {
            rootPath = rootPath.replace("file:/", "");
        }else{
            rootPath = System.getProperty("user.dir");
        }
        return rootPath;
    }

    /**
     * 获取接口方法参数名称
     */
    public static String getInterfaceParamterName(CtMethod ctMethod){
        try {
            String parameterName = "";
            // 使用javassist的反射方法的参数名
            MethodInfo methodInfo = ctMethod.getMethodInfo();

            MethodParametersAttribute methodParameters= (MethodParametersAttribute)methodInfo.getAttribute("MethodParameters");

            //获取参数的个数
            int count =ctMethod.getParameterTypes().length;

            CtClass[] pCtClass= ctMethod.getParameterTypes();

            for(int i=0;i<count;i++) {
                String str = methodParameters.getConstPool().getUtf8Info(ByteArray.readU16bit(methodParameters.get(), i * 4 + 1));
//                System.out.println(ctMethod.getName()+"-------------->参数名称:" + str + ",参数类型" + pCtClass[i].getName());
                parameterName += ", " + str;
            }
            return parameterName.substring(2);
        } catch (NotFoundException e) {
            e.printStackTrace();
            return "";
        }
    }
}