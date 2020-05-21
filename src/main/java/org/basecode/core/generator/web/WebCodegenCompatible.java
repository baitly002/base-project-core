package org.basecode.core.generator.web;


import javassist.*;
import javassist.bytecode.*;
import javassist.bytecode.annotation.*;
import org.basecode.core.config.base.WebCodegenScanner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

/**
 * 以兼容SPRING注解的方式生成controller类代码
 */
public class WebCodegenCompatible {
	private static final Logger logger = LoggerFactory.getLogger(WebCodegenCompatible.class);

    public static CtClass overClass(String classPath){
        try {
            ClassPool pool = ClassPool.getDefault();
            CtClass oldClass = pool.get(classPath);
            ClassFile oldFile = oldClass.getClassFile();
            oldFile.removeAttribute(AnnotationsAttribute.visibleTag);
            return oldClass;
        }catch (Exception e){
            if(logger.isErrorEnabled()) {
            	logger.error("替换Class:{}失败",  classPath, e);
            }
            return null;
        }
    }
    
    /**
     *
     * @param classPath
     * @param autowiredType spring:以spring的方式注入 dubbo:以dubbo的方式注入
     * @param requestPath 如果不为空，则为类上RequestMapping注解属性value或path增加路径
     */
    public static Class<?> gen(String classPath, String autowiredType, String requestPath){
        try{
            ClassPool pool = ClassPool.getDefault();
            pool.appendClassPath(new LoaderClassPath(WebCodegenScanner.class.getClassLoader()));
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
            if(oldAttribute!=null) {

                Annotation[] annotations = oldAttribute.getAnnotations();
                //类上是否存在RequestMapping的注解
                boolean existRequestMapping = false;
                String requestMappingName = "org.springframework.web.bind.annotation.RequestMapping";
                for (Annotation annotation : annotations) {
                	if(annotation.getTypeName().equals(requestMappingName)) {
                		existRequestMapping = true;
                    }
                	
                    //先添加原来类的注解
                    Annotation a = new Annotation(annotation.getTypeName(), constpool);
                    Set<String> names = annotation.getMemberNames();
                    if (names != null) {
                        for (String name : names) {
                            a.addMemberValue(name, annotation.getMemberValue(name));
                        }
                    }
                    

                    classAttr.addAnnotation(a);
                }
                
                if(requestPath != null) {
	                if(existRequestMapping) {
	                	//类存在RequestMapping的注解，修改属性值
	                	Annotation requestMapping = classAttr.getAnnotation(requestMappingName);
	                	String attrName = "value";
	                	ArrayMemberValue array = (ArrayMemberValue) requestMapping.getMemberValue(attrName);
	                	if(array == null) {
	                		array = (ArrayMemberValue) requestMapping.getMemberValue("path");
	                		attrName = "path";
	                	}
	                	MemberValue[] values = array.getValue();
	                	MemberValue[] memberValues = new StringMemberValue[values.length*2];
	                	for(int i=0; i<values.length; i++) {
	                		memberValues[i] = new StringMemberValue(((StringMemberValue)values[i]).getValue(), constpool);
	                	}
	                	for(int i=values.length; i<values.length*2; i++) {
	                		String oldPath = ((StringMemberValue)values[i-values.length]).getValue();
	                		if(!oldPath.startsWith("/")) {
	                			oldPath = "/"+oldPath;
	                		}
	                		memberValues[i] = new StringMemberValue(requestPath+oldPath, constpool);
	                	}
//	                	memberValues[values.length] = new StringMemberValue(requestPath, constpool);
	                	array.setValue(memberValues);
	                	
	                	requestMapping.addMemberValue(attrName, array);
	                	classAttr.addAnnotation(requestMapping);
	                } else {
	                	//增加注解
	                	Annotation requestMapping = new Annotation(requestMappingName, constpool);
	                	ArrayMemberValue array = new ArrayMemberValue(constpool);
	                	MemberValue[] memberValues = new StringMemberValue[2];
	                	memberValues[0] = new StringMemberValue("", constpool);
	                	memberValues[1] = new StringMemberValue(requestPath, constpool); 
	                	array.setValue(memberValues);
	                	requestMapping.addMemberValue("value", array);
	                	classAttr.addAnnotation(requestMapping);
	                	
	                }
                }
                //classAttr.removeAnnotation(requestMappingName);
            }

            newFile.addAttribute(classAttr);
            //oldFile.removeAttribute(AnnotationsAttribute.visibleTag);


            CtField field = CtField.make("private "+oldClass.getName()+" service;", newClass);
            FieldInfo fieldInfo = field.getFieldInfo();
            // 属性附上注解 dubbo
            if("dubbo".equalsIgnoreCase(autowiredType)) {
                AnnotationsAttribute fieldAttr = new AnnotationsAttribute(constpool, AnnotationsAttribute.visibleTag);
                Annotation reference = new Annotation("com.alibaba.dubbo.config.annotation.Reference", constpool);
                fieldAttr.addAnnotation(reference);
                fieldInfo.addAttribute(fieldAttr);
                newClass.addField(field);
            }else {
                AnnotationsAttribute fieldAttr = new AnnotationsAttribute(constpool, AnnotationsAttribute.visibleTag);
                Annotation autowired = new Annotation("org.springframework.beans.factory.annotation.Autowired", constpool);
                MemberValue required = new BooleanMemberValue(false, constpool);
                autowired.addMemberValue("required", required);
                fieldAttr.addAnnotation(autowired);
                fieldInfo.addAttribute(fieldAttr);
                newClass.addField(field);
            }

            //javassist方式获取参数名称
            CtMethod[] oldMethods = oldClass.getDeclaredMethods();
            for(CtMethod oldMethod:oldMethods){

                if(oldMethod.getAnnotation(RequestMapping.class)!=null || oldMethod.getAnnotation(GetMapping.class)!=null
                        || oldMethod.getAnnotation(PostMapping.class)!=null || oldMethod.getAnnotation(PutMapping.class)!=null
                        || oldMethod.getAnnotation(DeleteMapping.class)!=null){
                    if(!"java.io.Serializable".equals(oldMethod.getReturnType().getName())) {
//                        String parameterNames = getInterfaceParamterName(oldMethod);
                        MethodInfo methodInfo = oldMethod.getMethodInfo();
                        List<AttributeInfo> attributeInfos = methodInfo.getAttributes();

                        StringBuffer body = new StringBuffer();
                        CtMethod newMethod = new CtMethod(oldMethod, newClass, null);
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
//            if(classPath.equals("org.basecode.web.controllors.UserLogin")){
//                newClass.writeFile("d:/log");
//            }
            return newClass.toClass();


//            newClass.writeFile(rootPath());
//            return null;
        }catch (Exception e){
        	if(logger.isErrorEnabled()) {
        		logger.error("动态创建Controller类失败", e);
        	}
            return null;
        }
    }

    public static String rootPath(){
        String rootPath = ClassLoader.getSystemResource("").toString();
        if(rootPath.startsWith("file:")) {
            String os = System.getProperty("os.name");
            if(os.startsWith("Windows")){
                rootPath = rootPath.replace("file:/", "");
            }else{
                rootPath = rootPath.replace("file:", "");
            }

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
        	if(logger.isErrorEnabled()) {
        		logger.error("获取接口方法参数名称失败", e);
        	}
            return "";
        }
    }
}