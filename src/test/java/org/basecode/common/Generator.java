package org.basecode.common;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.LoggerContext;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.alibaba.fastjson.serializer.SimplePropertyPreFilter;
import org.apache.commons.beanutils.BeanUtilsBean;
import org.apache.commons.beanutils.ConvertUtilsBean;
import org.apache.commons.beanutils.FluentPropertyBeanIntrospector;
import org.apache.commons.beanutils.PropertyUtilsBean;
import org.apache.commons.beanutils.converters.IntegerConverter;
import org.basecode.common.criterion.model.PageParameter;
import org.basecode.common.dict.Dict;
import org.basecode.common.generator.main.CodeFactory;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.util.*;

public class Generator {


    public static void main(String[] args) {
        LoggerContext loggerContext = (LoggerContext)LoggerFactory.getILoggerFactory();

        User user = new User();
        user.setName("张三");
        user.setStatus(Dict.STATE.ON);

        loggerContext.getLogger("org.apache.commons").setLevel(Level.ERROR);
        loggerContext.getLogger("org.basecode").setLevel(Level.ERROR);
//        Dict.PAY.PAYED
        CodeFactory cf = new CodeFactory();
        Map<String, String> config = new HashMap<String, String>();
        config.put("basePackage", "com.dashu.repair");
        config.put("isFullBeanName", "true");//默认为false 是否以全表名命名javabean
        config.put("modelNames", "core,order");//多个用逗号隔开
        config.put("isDubbo", "false");
        config.put("jdbc.url","jdbc:mysql://localhost:3306/repair?useUnicode=true&characterEncoding=utf8&serverTimezone=Asia/Shanghai");
        config.put("jdbc.username", "root");
        config.put("jdbc.password", "admin123");
        config.put("isOverrideMapperXML", "true");//开发为true，其于为false
        config.put("isOverrideModel", "true");
        config.put("isOverrideMapper", "false");
        config.put("isOverrideService", "false");
        config.put("isOverrideServiceImpl", "false");
        config.put("isCommonModel", "true");//是否公共模块，公共模块只生成实体与业务接口类
        config.put("isServiceModel", "true");//业务模块会生成业务实现类，数据库操作mapper及xml
        config.put("isLocal", "true");//本类调用才用到，freemark可根据不一样的运行来加载模板文件夹
        cf.modelCodeGenerator(config);
    	
//    	SequenceGenerator sequence = SequenceSingle.getIstance();
//    	long uid = sequence.nextId();
//    	long u = sequence.nextId();
//    	System.out.println(sequence.parseId(uid));
//    	System.out.println(sequence.parseId(u));
//    	
//    	long s = System.currentTimeMillis();
//    	for(int i=0; i<60000; i++) {
//    		sequence.nextId();
//    	}
//    	long e = System.currentTimeMillis();
//    	System.out.println(e-s);
//    	
//    	long ss = sequence.nextId();
//    	System.out.println(sequence.parseId(ss));
//    	
//    	System.out.println(DateUtil.formatDate("2019-01-01").getTime());
    	
//    	WebCodegenCompatible.gen("org.basecode.common.TestController", "spring", "/api");
//        String os = System.getProperty("os.name");
//        if(os.startsWith("Windows")){
//            System.out.println("Windows");
//        }else if(os.startsWith("Mac")){
//            System.out.println("MAC");
//        }else{
//
//        }
//        System.out.println(os);

//        String str = "\"abaklk\t\nlklklkl123\"";
//        str = str.substring(1, str.length()-1);
//        System.out.println(str);

//        Document doc = Dom4jUtil.read(new File("D:/a.xml"));
//        String s = Dom4jUtil.formatDocument(doc);
//        System.out.println(s);

        String name = "ddd_llk_iiii";
        String a[] = name.split("_");
        for(String s : a){
            s = "data."+s;
        }
        for(String s : a){
            System.out.println(s);
        }
//        ResultMsg result = new ResultMsg<>();
        ResultTest result = new ResultTest();
        result.setState(1);
        result.setStatus(0);
        result.setMessage("hahaha");
        List<PageParameter> pl = new ArrayList<>();
        PageParameter pp = new PageParameter();
        pp.setPage(10);
        pp.setRows(20);
        pl.add(pp);
        PageParameter pp2 = new PageParameter();
        pp2.setPage(30);
        pp2.setRows(40);
        pl.add(pp2);
        result.setData(pl);

        System.out.println(JSON.toJSONString(result, 0, SerializerFeature.PrettyFormat));
//        PropertyUtilsBean p = new PropertyUtilsBean();

        try{
//            Object obj = p.getIndexedProperty(result, "message");

//            Object obj = p.getProperty(result, "data");
//            p.setProperty(result, "data", );
//            System.out.println(obj.toString());
        }catch (Exception e){
            e.printStackTrace();
        }
        List<PageParameter> pageParameters = new ArrayList<>();
        PageParameter pageParameter = new PageParameter();
        pageParameter.setPage(1).setRows(10);
        pageParameter.setOrderBy("kkkk");
        PageParameter pageParameter2 = new PageParameter();
        pageParameter2.setPage(33).setRows(10);
        pageParameter2.setOrderBy("www");
        pageParameters.add(pageParameter);
        pageParameters.add(pageParameter2);

        Map<String, Object> data = new HashMap<>();
        data.put("page", 44);
        data.put("rows", 11);
        data.put("data", pageParameters);
//        ResultMsg<PageParameter> resultMsg = new ResultMsg<>();
        ResultTest resultMsg = new ResultTest();
        resultMsg.setStatus(1);
        resultMsg.setMessage("success");
        resultMsg.setData(data);
//        resultMsg.setData(pageParameters);
        BeanFilter bf = new BeanFilter();
        bf.emptyValue(result.getState());
        System.out.println(resultMsg.getMessage());
        try {
            IntegerConverter ic = new IntegerConverter();
////            // 获取bean信息
//            long t1 = System.currentTimeMillis();
////            for(int i=0; i<100000; i++) {
//                BeanInfo beanInfo = Introspector.getBeanInfo(resultMsg.getClass());
//                // 获取bean的所有属性列表
//                PropertyDescriptor[] proDescrtptors = beanInfo.getPropertyDescriptors();
////            }
//            long t2 = System.currentTimeMillis();
//            System.out.println(t2-t1);
////            for(PropertyDescriptor pro : proDescrtptors){
////                System.out.println(pro.getName());
////            }
//
//            //getFieldValueByName(resultMsg);
//            t1 = System.currentTimeMillis();
//            BeanUtilsBean bu = BeanUtilsBean.getInstance();
////            for(int i=0; i<100000; i++) {
//                Map<String, String> describe = bu.describe(result);
////            }
//            t2 = System.currentTimeMillis();
//            System.out.println(t2-t1);
//
//            PropertyUtilsBean pu = new PropertyUtilsBean();
//            t1 = System.currentTimeMillis();
////            for(int i=0; i<100000; i++) {
//                PropertyDescriptor[] pd = pu.getPropertyDescriptors(resultMsg);
//                for(PropertyDescriptor d : pd){
////                    Enumeration<String> ee = d.attributeNames();
////                    System.out.println(ee);
//                }
////            }
//
////            pu.addBeanIntrospector(new FluentPropertyBeanIntrospector());
////            pu.setProperty(resultMsg, "age", 2);
//            t2 = System.currentTimeMillis();
//            System.out.println(t2-t1);

//            PropertyUtils.addBeanIntrospector(new FluentPropertyBeanIntrospector());
//            this.defaultBoolean = Boolean.FALSE;
//            this.defaultByte = new Byte((byte)0);
//            this.defaultCharacter = new Character(' ');
//            this.defaultDouble = new Double(0.0D);
//            this.defaultFloat = new Float(0.0F);
//            this.defaultInteger = new Integer(0);
//            this.defaultLong = new Long(0L);
//            this.converters.setFast(false);
            ConvertUtilsBean convertUtilsBean = BeanUtilsBean.getInstance().getConvertUtils();
            convertUtilsBean.register(new NullValueConverter(), Integer.class);
            convertUtilsBean.register(new NullValueConverter(), Double.class);
            convertUtilsBean.register(new NullValueConverter(), Long.class);
            convertUtilsBean.register(new NullValueConverter(), Float.class);
            convertUtilsBean.register(new NullValueConverter(), String.class);
//            BeanUtils.setProperty(resultMsg, "data.page", 2 );
            PropertyUtilsBean pub = new PropertyUtilsBean();
            pub.resetBeanIntrospectors();
            pub.addBeanIntrospector(new FluentPropertyBeanIntrospector());
//            Object objj = pub.getProperty(resultMsg, "data");
//            Object objj = BeanUtils.getProperty(resultMsg, "data");
//            System.out.println(objj.getClass().getName());
            PropertyDescriptor[] pds = pub.getPropertyDescriptors(resultMsg);
//            pub.setProperty(resultMsg, "age", 2);
            System.out.println(JSON.toJSONString(resultMsg, true));


            PropertyFilterTest pft = new PropertyFilterTest();


            BeanInfo beanInfo = Introspector.getBeanInfo(ResultTest.class);
            System.out.println(Introspector.decapitalize("name"));
            System.out.println(pft.getMethodNameFormField("sds", "set"));
            System.out.println(pft.getMethodNameFormField("Age", "set"));
            System.out.println(pft.getMethodNameFormField("_Say", "set"));
            PropertyDescriptor[] beanInfoPropertyDescriptors = beanInfo.getPropertyDescriptors();

            pft.getWriteMethod(beanInfo.getMethodDescriptors(), "status");
            SimplePropertyPreFilter simplePropertyPreFilter = new SimplePropertyPreFilter();
//            simplePropertyPreFilter.getIncludes().add("page");
            System.out.println(JSON.toJSONString(resultMsg, simplePropertyPreFilter));

            Long t1,t2;
            String keys[] = {"data", "page", "sord"};


            simplePropertyPreFilter.getIncludes().addAll(Arrays.asList(keys));
            t1 = System.currentTimeMillis();
//            for(int i=0; i<100000; i++) {
                String json = JSON.toJSONString(resultMsg, simplePropertyPreFilter);
                ResultTest re = JSON.parseObject(json, ResultTest.class);
//            }
            t2 = System.currentTimeMillis();
            System.out.println(t2-t1);
            System.out.println(JSON.toJSONString(re));


            t1 = System.currentTimeMillis();
//            Object testObj = BeanUtils.getProperty(resultMsg, "data.[1].page");
//            for(int i=0; i<100000; i++) {
            List<FilterInfo> filterInfoList = pft.excludeFilterInfo(resultMsg, null, "", "", keys, false, 1, true);
//                for(FilterInfo filterInfo : filterInfoList){
//                    BeanUtils.setProperty(resultMsg, filterInfo.getKey(), null );
////                    pub.setProperty(resultMsg, filterInfo.getKey(), null);
//                }
//                System.out.println(JSON.toJSONString(resultMsg, true));
//                System.out.println(filterInfoList.size());
//            }
            t2 = System.currentTimeMillis();
            System.out.println(t2-t1);
            System.out.println(JSON.toJSONString(resultMsg));
//            for(FilterInfo f : filterInfoList){
//                System.out.println(f.toString());
//            }
        }catch (Exception e){
            e.printStackTrace();
        }


//        ApiConfig.rootClass="org.basecode.common.criterion.model.ResultMsg";
//        List<Class<?>> classes = new ArrayList<>();
//        classes.add(ApiPermissionService.class);
//        CreateApi.parser(classes);

//        String str = "[{\n" +
//                "\t\"traceId\": \"35.66.15604164213990001\",\n" +
//                "\t\"segmentId\": \"34.111.15604164214010000\",\n" +
//                "\t\"spanId\": 3,\n" +
//                "\t\"parentSpanId\": 2,\n" +
//                "\t\"refs\": [],\n" +
//                "\t\"serviceCode\": \"name-service\",\n" +
//                "\t\"startTime\": 1560416421462,\n" +
//                "\t\"endTime\": 1560416421464,\n" +
//                "\t\"endpointName\": \"Mysql/JDBI/PreparedStatement/execute\",\n" +
//                "\t\"type\": \"Exit\",\n" +
//                "\t\"peer\": \"localhost:3306\",\n" +
//                "\t\"component\": \"mysql-connector-java\",\n" +
//                "\t\"isError\": false,\n" +
//                "\t\"layer\": \"Database\",\n" +
//                "\t\"tags\": [\n" +
//                "\t\t{\n" +
//                "\t\t\t\"key\": \"db.type\",\n" +
//                "\t\t\t\"value\": \"sql\"\n" +
//                "\t\t},\n" +
//                "\t\t{\n" +
//                "\t\t\t\"key\": \"db.instance\",\n" +
//                "\t\t\t\"value\": \"repair\"\n" +
//                "\t\t},\n" +
//                "\t\t{\n" +
//                "\t\t\t\"key\": \"db.statement\",\n" +
//                "\t\t\t\"value\": \"select\\n         \\n         id, account, password, name, mobile, email, photo, open_id, union_id, super_code, organ_code, appid, secret, create_time, login_count, last_time, status, remark, ext_attr, post_id \\n     \\n        from core_auth_account\\n        where id = ?\"\n" +
//                "\t\t}\n" +
//                "\t],\n" +
//                "\t\"logs\": [],\n" +
//                "\t\"name\": {\n" +
//                "\t\t\t\t\"traceId\": \"35.66.15604164213990001\",\n" +
//                "\t\t\t\t\"parentSegmentId\": \"35.66.15604164213990000\",\n" +
//                "\t\t\t\t\"parentSpanId\": 1,\n" +
//                "\t\t\t\t\"type\": \"CROSS_PROCESS\"\n" +
//                "\t\t\t}\n" +
//                "}]";
//        Object obj = JSON.parse(str);
//        SimplePropertyPreFilter simplePropertyPreFilter = new SimplePropertyPreFilter();
////        simplePropertyPreFilter.getIncludes().add("traceId");
////        simplePropertyPreFilter.getIncludes().add("spanId");
//        simplePropertyPreFilter.getExcludes().add("value");
//        simplePropertyPreFilter.getExcludes().add("endTime");
//        System.out.println(JSON.toJSONString(obj, simplePropertyPreFilter, SerializerFeature.MapSortField));
    }

    /**
     * @功能描述：顺序获取javaBean中的属性和对应的值,并格式化为"'','',...'',''"
     */
    public static String getFieldValueByName(Object obj) {
        StringBuffer valueStr = new StringBuffer();
        Field fields[] = obj.getClass().getDeclaredFields();
        String[] beanName = new String[fields.length];
        Object[] beanValue = new Object[fields.length];
        Object[] beanSourceType = new Object[fields.length];
        try {
            Field.setAccessible(fields, true);
            for (int i = 0; i < beanName.length; i++) {
                beanName[i] = fields[i].getName();
                beanValue[i] = fields[i].get(obj);
                beanSourceType[i] = fields[i].getType().getName();
                fields[i].set(obj, null);
//                if ("java.lang.Integer".equals(beanSourceType[i]) || "java.lang.Long".equals(beanSourceType[i]) || "java.lang.BigInteger".equals(beanSourceType[i]) || "java.lang.Float".equals(beanSourceType[i]) || "java.lang.Double".equals(beanSourceType[i]) || "java.lang.BigDecimal".equals(beanSourceType[i])) {
//                    valueStr.append(beanValue[i]);
//                } else {
//                    valueStr.append("'");
//                    valueStr.append(converEmpty((String) beanValue[i]));
//                    valueStr.append("'");
//                }
//                valueStr.append(",");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        String sqlValueStr = valueStr.toString();
        if (sqlValueStr.length() > 0) {
            sqlValueStr = sqlValueStr.substring(0, sqlValueStr.lastIndexOf(","));
        }
        return sqlValueStr;
    }

    /**
     * @功能描述：如果字符串是null,则返回""字符串
     */
    public static String converEmpty(String str) {
        return (str == null || "null".equals(str)) ? "" : str;
    }

    /**
     * zhangpf :因为getFieldValue()方法，无法读取super class的属性的值；
     *                  所以本方法做出扩展，允许读取super class的属性的值；
     * @param object
     * @param propertyName
     * @return
     * @throws IllegalAccessException
     * @throws NoSuchFieldException
     */
    public static Object getFieldValueInAllSuper(Object object, String propertyName)
            throws IllegalAccessException, NoSuchFieldException {
        Assert.notNull(object);
        Assert.hasText(propertyName);
        Class claszz=object.getClass();
        Field field = null;

        do
        {
            try{
                field = claszz.getDeclaredField(propertyName);
            }
            catch(NoSuchFieldException e)
            {
                //e.printStackTrace();
                field=null;
            }
            claszz=claszz.getSuperclass();
        }
        while(field==null&&claszz!=null);

        if(field==null) return null;

        field.setAccessible(true);
        return field.get(object);
    }

}

class User {
    String name;
    Integer status;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }
}
