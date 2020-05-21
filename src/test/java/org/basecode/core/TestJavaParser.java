package org.basecode.core;

import org.basecode.core.generator.mybatis.generator.api.Dom4jUtil;
import org.dom4j.Document;
import org.dom4j.Element;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class TestJavaParser {
    public static void main(String[] args) throws Exception{
//        JavaParser javaParser = new JavaParser();
//        ParseResult pr =  javaParser.parse(new File("D:\\workspace\\new_common\\src\\test\\java\\org\\basecode\\common\\ResultTest.java"));
//        System.out.println(JSON.toJSONString(pr.getCommentsCollection()));
//
//        String source = " record.123,recorde.lkk";
//        source = source.replaceAll("record\\.", "");
//        System.out.println(source);

        Dom4jUtil dom4jUtil = new Dom4jUtil();
        Document doc = dom4jUtil.read(new File("d:/AddressMapper.xml"));
        Map<String, String> mapOld = new HashMap<>();
        Element mapper = doc.getRootElement();
        for(Element e : mapper.elements()){
//            System.out.println();
            mapOld.put(e.attributeValue("id"), "1");
        }
        System.out.println(mapper.selectSingleNode("update[@id='updateByPrimaryKeySelective']").getText());
        System.out.println(doc.getRootElement().toString());
    }
}
