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

import static org.basecode.core.generator.mybatis.generator.internal.util.StringUtility.stringHasValue;

import org.basecode.core.generator.mybatis.generator.api.dom.xml.Attribute;
import org.basecode.core.generator.mybatis.generator.api.dom.xml.TextElement;
import org.basecode.core.generator.mybatis.generator.api.dom.xml.XmlElement;

/**
 * 
 * @author Jeff Butler
 * 
 */
public class SelectByExampleWithBLOBsElementGenerator extends
        AbstractXmlElementGenerator {

    public SelectByExampleWithBLOBsElementGenerator() {
        super();
    }

    @Override
    public void addElements(XmlElement parentElement) {
        String fqjt = introspectedTable.getExampleType();

        XmlElement answer = new XmlElement("select"); //$NON-NLS-1$
        answer
                .addAttribute(new Attribute(
                        "id", introspectedTable.getSelectByExampleWithBLOBsStatementId())); //$NON-NLS-1$
        answer.addAttribute(new Attribute(
                "resultMap", introspectedTable.getResultMapWithBLOBsId())); //$NON-NLS-1$
//        answer.addAttribute(new Attribute("parameterType", fqjt)); //$NON-NLS-1$
        answer.addAttribute(new Attribute("parameterType", "map")); //$NON-NLS-1$

        context.getCommentGenerator().addComment(answer);
        
        //加上分页
        //answer.addElement(getPageHeaderIncludeElement());
        answer.addElement(new TextElement("select")); //$NON-NLS-1$
//        XmlElement ifElement = new XmlElement("if"); //$NON-NLS-1$
//        ifElement.addAttribute(new Attribute("test", "distinct")); //$NON-NLS-1$ //$NON-NLS-2$
//        ifElement.addElement(new TextElement("distinct")); //$NON-NLS-1$
//        answer.addElement(ifElement);

        StringBuilder sb = new StringBuilder();
        if (stringHasValue(introspectedTable
                .getSelectByExampleQueryId())) {
            sb.append('\'');
            sb.append(introspectedTable.getSelectByExampleQueryId());
            sb.append("' as QUERYID,"); //$NON-NLS-1$
            answer.addElement(new TextElement(sb.toString()));
        }

        answer.addElement(getBaseColumnListElement());
        answer.addElement(new TextElement(",")); //$NON-NLS-1$
        answer.addElement(getBlobColumnListElement());

        sb.setLength(0);
        sb.append("from "); //$NON-NLS-1$
        sb.append(introspectedTable
                .getAliasedFullyQualifiedTableNameAtRuntime());
        answer.addElement(new TextElement(sb.toString()));
        answer.addElement(getExampleIncludeElement());

//        XmlElement ifElement = new XmlElement("if"); //$NON-NLS-1$
//        ifElement.addAttribute(new Attribute("test", "(orderBy != null and sord == null) or (orderBy != null and sord == 'asc') or (orderBy != null and sord == 'desc')")); //$NON-NLS-1$ //$NON-NLS-2$
//        ifElement.addElement(new TextElement("order by ${orderBy} ${sord}")); //$NON-NLS-1$
        
//        answer.addElement(ifElement);
        
        //加上分页
        //answer.addElement(getPageFooterIncludeElement());
        if (context.getPlugins()
                .sqlMapSelectByExampleWithBLOBsElementGenerated(answer,
                        introspectedTable)) {
            parentElement.addElement(answer);
        }
    }
}
