package org.basecode.core.generator.mybatis.generator.api;

import org.basecode.core.generator.mybatis.generator.api.IgnoreDTDEntityResolver;
import org.basecode.core.generator.mybatis.generator.api.dom.OutputUtilities;
import org.basecode.core.util.StringUtils;
import org.dom4j.*;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Dom4jUtil {

	/**
     * xml字符串内容转化为document对象
     * @param source
     * @return
     */
    public static Document read(String source){
    	try {
	    	Document document = null;
	    	//提取公共信息并覆盖原文件的公共信息
	    	SAXReader saxReader = new SAXReader();
	    	saxReader.setValidation(false);
	    	saxReader.setEntityResolver(new IgnoreDTDEntityResolver());
	    	long s = System.currentTimeMillis();
	    	InputStream in = new ByteArrayInputStream(source.getBytes("UTF-8"));   
	    	document = saxReader.read(in);
	    	return document;
    	} catch (Exception e) {
			System.out.println("解析mybatis自动生成的mapper XML文件内容出错！");
			e.printStackTrace();
			return null;
		}
    }
    /**
     * xml文件内容转化为document对象
     * @param source
     * @return
     */
    public static Document read(File file){
    	try {
    		Document document = null;
    		//提取公共信息并覆盖原文件的公共信息
    		SAXReader saxReader = new SAXReader();
    		saxReader.setValidation(false);
    		saxReader.setEntityResolver(new IgnoreDTDEntityResolver());
    		document = saxReader.read(file);
    		return document;
    	} catch (Exception e) {
    		System.out.println("解析mybatis自动生成的mapper XML文件内容出错！");
    		e.printStackTrace();
    		return null;
    	}
    }
    
    public static void formatXmlFormatWriter(File file, Document document, String charSet){
		PrintWriter pw;
		try {
			pw = new PrintWriter(file);
			//创建字符串缓冲区
			//        StringWriter stringWriter = new StringWriter(); 
			//设置文件编码 
			OutputFormat xmlFormat = new OutputFormat(); 
			xmlFormat.setEncoding("UTF-8");
			xmlFormat.setIndent(true);
			xmlFormat.setIndentSize(4);
			xmlFormat.setNewlines(true);
			xmlFormat.setTrimText(true);
			xmlFormat.setPadText(true);
			xmlFormat.setNewLineAfterNTags(1);
			// 设置换行
//			xmlFormat.setNewlines(false);
			// 生成缩进
			// 使用4个空格进行缩进, 可以兼容文本编辑器
			
			//创建写文件方法 
			XMLWriter xmlWriter = new XMLWriter(pw,xmlFormat); 
			//写入文件 
//			context = "<?xml version=\"1.0\" encoding=\"UTF-8\" ?>\n<!DOCTYPE mapper PUBLIC \"-//mybatis.org//DTD Mapper 3.0//EN\" \"http://mybatis.org/dtd/mybatis-3-mapper.dtd\" >\n"+context;
//			document.addComment("<?xml version=\"1.0\" encoding=\"UTF-8\" ?>");
//			document.addDocType("mapper", "-//mybatis.org//DTD Mapper 3.0//EN","http://mybatis.org/dtd/mybatis-3-mapper.dtd"); 
//			document.add(comment);
			xmlWriter.write(document); 
//			xmlWriter.write(context); 
			//关闭 
			xmlWriter.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
    public static void replace(Document documentSource, String sourceXpath, Document documentTarget, String targetXpath){
    	Node sourceNode = documentSource.selectSingleNode(sourceXpath);
    	Node targetNode = documentTarget.selectSingleNode(targetXpath);
    	boolean flag = true;
    	if(sourceNode != null && targetNode != null){
    		if(sourceNode instanceof Element && targetNode instanceof Element){
    			Element sourceEle = (Element) sourceNode;
    			Element targetEle = (Element) targetNode;
    			List parent = targetEle.getParent().content();
    			parent.set(parent.indexOf(targetEle),sourceEle);
    			flag = false;
//    			targetEle.clearContent();
//    			targetEle.appendContent(sourceEle.content());
    		}
    	}
    	if(flag){
    		System.out.println("节点替换出错,源节点路径：<"+sourceXpath+">,目标节点路径：<"+targetXpath+">。");
    	}
    }
	public static void merger(Document documentSource, Document documentTarget){
    	Element rootElementNew = documentSource.getRootElement();
    	Element rootElementOld = documentTarget.getRootElement();
		Set<String> keys = new HashSet<>();
		for(Element e : rootElementOld.elements()){
			String eid = e.attributeValue("id");
			keys.add(eid);
		}
    	for(Element e : rootElementNew.elements()){
			String eid = e.attributeValue("id");
//			e.asXML();
			Element element = e.createCopy();
			if(!keys.contains(eid)){
				rootElementOld.add(element);
			}
		}

	}
    
    public static Element chang(Document document, String xpath, String namespace, String refid){
    	Node node = document.selectSingleNode(xpath);
    	Element eleClone = null;
		if(node != null && node instanceof Element){
			//取得BaseResultMap
			Element ele = (Element) node;
			eleClone = (Element) ele.clone();
			//BaseResultMap添加一个子节点
			ele.clearContent();
			Element include = ele.addElement("include");
			include.addAttribute("refid", namespace+"."+refid);
			
			List<Attribute> attributes = eleClone.attributes();
			int attrSize = eleClone.attributeCount();
			for(int i=0; i<attrSize; i++){
				Attribute attribute = eleClone.attribute(0);
				eleClone.remove(attribute);
			}
			eleClone.setName("sql");
			eleClone.addAttribute("id", refid);
		}
////    	List<Element> baseResultMap = documentOld.selectNodes("/mapper/resultMap[@id='BaseResultMap']");
//    	List<Element> elements = document.selectNodes(xpath);
//    	Element eleClone = null;
//		if(elements!=null && elements.size()>0){
//			//取得BaseResultMap
////			Element root = document.getRootElement();
////			Element baseElement = root.addElement("resultMap");
//			Element ele = elements.get(0);
//			eleClone = (Element) ele.clone();
////			baseElement.addAttribute("id", ele.attributeValue("id"));
////			baseElement.addAttribute("type", ele.attributeValue("type"));
//			//BaseResultMap添加一个子节点
//			ele.clearContent();
////			System.out.println(document.asXML());
////			ele.addText("<include refid='"+refid+"'");
//			Element include = ele.addElement("include");
//			include.addAttribute("refid", namespace+"."+refid);
//			
////			ele.addAttribute("id", "uuuuu");
////			ele.setName("sql");
////			ele.remove(ele.attribute("type"));
//			
////			//复制一个BaseResultMap
////			Element clone = (Element) ele.clone();
////			clone.addAttribute("id", "uuuuu");
////			clone.setName("sql");
////			clone.remove(clone.attribute("type"));
////			
////			ele.clearContent();//清除原来的BaseResultMap内容
////			
////			//BaseResultMap添加一个子节点
////			Element include = ele.addElement("include");
////			include.addAttribute("refid", "testtest");
////			
////			clone.remove(clone.element("include"));
////			documentOld.getRootElement().add(clone);
////			source = "\n"+root.asXML();
////			Dom4jUtil.formatXmlFormatWriter(targetFile, documentOld, "UTF-8");
//			
//			List<Attribute> attributes = eleClone.attributes();
//			int attrSize = eleClone.attributeCount();
//			for(int i=0; i<attrSize; i++){
//				Attribute attribute = eleClone.attribute(0);
//				eleClone.remove(attribute);
//			}
////			for(Iterator it=eleClone.attributeIterator();it.hasNext();){
////				Attribute attribute = (Attribute) it.next();
////				eleClone.remove(attribute);
////			}
//			eleClone.setName("sql");
//			eleClone.addAttribute("id", refid);
//		}
		return eleClone; 
    }
    
    public static String getFormattedContent(Element element, int indentLevel) {
        StringBuilder sb = new StringBuilder();

        OutputUtilities.xmlIndent(sb, indentLevel);
        sb.append('<');
        sb.append(element.getName());
        List<Attribute> attributes = element.attributes();
        for (Attribute att : attributes) {
            sb.append(' ');
            sb.append(getFormattedAttriture(att));
        }
//        List<Element> nodes = element.content();
//        System.out.println(nodes.size());
        List list = element.content();
        if (list.size() > 0) {
            sb.append(" >"); //$NON-NLS-1$
            for(int i=0; i<list.size(); i++){
            	Object obj = list.get(i);
            	if(obj instanceof Text){
            		Text subText = (Text) obj;
            		String tex = subText.getText();
            		if(StringUtils.isNotBlank(tex.trim())){
            			OutputUtilities.newLine(sb);
            			OutputUtilities.xmlIndent(sb, indentLevel+1);
            			sb.append(tex.trim());
            		}
            	}
				if(obj instanceof CDATA){
					CDATA subCDATA = (CDATA) obj;
					String tex = subCDATA.asXML();
					if(StringUtils.isNotBlank(tex.trim())){
						OutputUtilities.newLine(sb);
						OutputUtilities.xmlIndent(sb, indentLevel+1);
						sb.append(tex.trim());
					}
				}
            	if(obj instanceof Comment){
            		Comment subComment = (Comment) obj;
            		String tex = subComment.asXML();
            		if(StringUtils.isNotBlank(tex.trim())){
            			OutputUtilities.newLine(sb);
            			OutputUtilities.xmlIndent(sb, indentLevel+1);
            			sb.append(tex.trim());
            		}
            	}
            	if(obj instanceof Element){
                  Element subElement = (Element) obj;
//                  sb.append(subElement.getTextTrim());
                  OutputUtilities.newLine(sb);
                  sb.append(getFormattedContent(subElement, indentLevel + 1));
            	}
            }
//            for (Element subElement : elements) {
//                OutputUtilities.newLine(sb);
//                sb.append(subElement.getTextTrim());
//                sb.append(getFormattedContent(subElement, indentLevel + 1));
//            }
            OutputUtilities.newLine(sb);
            OutputUtilities.xmlIndent(sb, indentLevel);
            sb.append("</"); //$NON-NLS-1$
            sb.append(element.getName());
            sb.append('>');

        } else {
            sb.append(" />"); //$NON-NLS-1$
        }

        return sb.toString();
    }
    
    public static String getFormattedHeader(Document document) {
        StringBuilder sb = new StringBuilder();

        sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\" ?>"); //$NON-NLS-1$
        OutputUtilities.newLine(sb);
        sb.append("<!DOCTYPE mapper PUBLIC \"-//mybatis.org//DTD Mapper 3.0//EN\" \"http://mybatis.org/dtd/mybatis-3-mapper.dtd\" >");

        OutputUtilities.newLine(sb);
        OutputUtilities.newLine(sb);
        sb.append(getFormattedContent(document.getRootElement(), 0));

        return sb.toString();
    }
    
    public static String getFormattedAttriture(Attribute attr) {
        StringBuilder sb = new StringBuilder();
        sb.append(attr.getName());
        sb.append("=\""); //$NON-NLS-1$
        sb.append(attr.getValue());
        sb.append('\"');

        return sb.toString();
    }
    
    public static String formatDocument(Document document){
    	return getFormattedHeader(document);
    }
}
