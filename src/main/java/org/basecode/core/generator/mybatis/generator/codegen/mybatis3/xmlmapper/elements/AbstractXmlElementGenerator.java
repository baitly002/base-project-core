/*
 *  Copyright 2009 The Apache Software Foundation
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.basecode.core.generator.mybatis.generator.codegen.mybatis3.xmlmapper.elements;

import org.basecode.core.generator.mybatis.generator.api.IntrospectedColumn;
import org.basecode.core.generator.mybatis.generator.api.dom.xml.Attribute;
import org.basecode.core.generator.mybatis.generator.api.dom.xml.TextElement;
import org.basecode.core.generator.mybatis.generator.api.dom.xml.XmlElement;
import org.basecode.core.generator.mybatis.generator.codegen.AbstractGenerator;
import org.basecode.core.generator.mybatis.generator.codegen.mybatis3.MyBatis3FormattingUtilities;
import org.basecode.core.generator.mybatis.generator.config.GeneratedKey;

/**
 * 
 * @author Jeff Butler
 * 
 */
public abstract class AbstractXmlElementGenerator extends AbstractGenerator {
    public abstract void addElements(XmlElement parentElement);

    public AbstractXmlElementGenerator() {
        super();
    }

    /**
     * This method should return an XmlElement for the select key used to
     * automatically generate keys.
     * 
     * @param introspectedColumn
     *            the column related to the select key statement
     * @param generatedKey
     *            the generated key for the current table
     * @return the selectKey element
     */
    protected XmlElement getSelectKey(IntrospectedColumn introspectedColumn,
                                      GeneratedKey generatedKey) {
        String identityColumnType = introspectedColumn
                .getFullyQualifiedJavaType().getFullyQualifiedName();

        XmlElement answer = new XmlElement("selectKey"); //$NON-NLS-1$
        answer.addAttribute(new Attribute("resultType", identityColumnType)); //$NON-NLS-1$
        answer.addAttribute(new Attribute(
                "keyProperty", introspectedColumn.getJavaProperty())); //$NON-NLS-1$
        answer.addAttribute(new Attribute("order", //$NON-NLS-1$
                generatedKey.getMyBatis3Order())); 
        
        answer.addElement(new TextElement(generatedKey
                        .getRuntimeSqlStatement()));

        return answer;
    }

    protected XmlElement getBaseColumnListElement() {
        XmlElement answer = new XmlElement("include"); //$NON-NLS-1$
        answer.addAttribute(new Attribute("refid", //$NON-NLS-1$
                introspectedTable.getBaseColumnListId()));
        return answer;
    }

    protected XmlElement getBlobColumnListElement() {
        XmlElement answer = new XmlElement("include"); //$NON-NLS-1$
        answer.addAttribute(new Attribute("refid", //$NON-NLS-1$
                introspectedTable.getBlobColumnListId()));
        return answer;
    }

    protected XmlElement getExampleIncludeElement() {
//    	if(introspectedTable.hasPrimaryKeyColumns()){
//	    	XmlElement whereElement = new XmlElement("where");
//	    	XmlElement ifElement = new XmlElement("if");
//	    	ifElement.addAttribute(new Attribute("test", introspectedTable.getPrimaryKeyColumns().get(0).getJavaProperty("record.")+" != null"));
//	    	ifElement.addElement(new TextElement("and "+introspectedTable.getPrimaryKeyColumns().get(0).getJavaProperty("record.")+" = "+
//	    	MyBatis3FormattingUtilities.getParameterClause(introspectedTable.getPrimaryKeyColumns().get(0), "record.")));
//	    	whereElement.addElement(ifElement);
//	    	return whereElement;
//    	}
    	XmlElement answer = new XmlElement("include"); //$NON-NLS-1$
        answer.addAttribute(new Attribute("refid", //$NON-NLS-1$
                introspectedTable.getExampleWhereClauseId()));
        return answer;
    	/*
        XmlElement ifElement = new XmlElement("if"); //$NON-NLS-1$
        ifElement.addAttribute(new Attribute("test", "_parameter != null")); //$NON-NLS-1$ //$NON-NLS-2$

        XmlElement includeElement = new XmlElement("include"); //$NON-NLS-1$
        includeElement.addAttribute(new Attribute("refid", //$NON-NLS-1$
                introspectedTable.getExampleWhereClauseId()));
        ifElement.addElement(includeElement);
        return ifElement;
    	 */
    }

    protected XmlElement getUpdateByExampleIncludeElement() {
    	XmlElement whereElement = new XmlElement("where");
    	if(introspectedTable.hasPrimaryKeyColumns()){
	    	XmlElement ifElement = new XmlElement("if");
	    	ifElement.addAttribute(new Attribute("test", introspectedTable.getPrimaryKeyColumns().get(0).getJavaProperty("record.")+" != null"));
	    	ifElement.addElement(new TextElement("and "+introspectedTable.getPrimaryKeyColumns().get(0).getJavaProperty("record.")+" = "+
	    	MyBatis3FormattingUtilities.getParameterClause(introspectedTable.getPrimaryKeyColumns().get(0), "record.")));
	    	whereElement.addElement(ifElement);
    	}
    	/*
        XmlElement ifElement = new XmlElement("if"); //$NON-NLS-1$
        ifElement.addAttribute(new Attribute("test", "_parameter != null")); //$NON-NLS-1$ //$NON-NLS-2$

        XmlElement includeElement = new XmlElement("include"); //$NON-NLS-1$
        includeElement.addAttribute(new Attribute("refid", //$NON-NLS-1$
                introspectedTable.getMyBatis3UpdateByExampleWhereClauseId()));
        ifElement.addElement(includeElement);
        return ifElement;
		*/
    	return whereElement;
    }
    
    protected XmlElement getPageHeaderIncludeElement() {
    	XmlElement pageHeaderElement = new XmlElement("include");
    	pageHeaderElement.addAttribute(new Attribute("refid","PageMapper.pageHeader"));
    	return pageHeaderElement;
    }
    protected XmlElement getPageFooterIncludeElement() {
    	XmlElement pageFooterElement = new XmlElement("include");
    	pageFooterElement.addAttribute(new Attribute("refid","PageMapper.pageFooter"));
    	return pageFooterElement;
    }
    
    protected XmlElement setCommonIncludeElement(String refid) {
    	XmlElement commonIncludeElement = new XmlElement("include");
    	commonIncludeElement.addAttribute(new Attribute("refid",refid));
    	return commonIncludeElement;
    }
}
