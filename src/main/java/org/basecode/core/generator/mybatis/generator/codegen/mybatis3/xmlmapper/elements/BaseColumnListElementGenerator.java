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

import java.util.Iterator;

import org.basecode.core.generator.mybatis.generator.api.IntrospectedColumn;
import org.basecode.core.generator.mybatis.generator.api.dom.xml.Attribute;
import org.basecode.core.generator.mybatis.generator.api.dom.xml.TextElement;
import org.basecode.core.generator.mybatis.generator.api.dom.xml.XmlElement;
import org.basecode.core.generator.mybatis.generator.codegen.mybatis3.MyBatis3FormattingUtilities;

/**
 * 
 * @author Jeff Butler
 * 
 */
public class BaseColumnListElementGenerator extends AbstractXmlElementGenerator {

    public BaseColumnListElementGenerator() {
        super();
    }

    @Override
    public void addElements(XmlElement parentElement) {
        //以下为自定义的where语句
    	XmlElement whereAnswer = new XmlElement("sql");
    	whereAnswer.addAttribute(new Attribute("id", introspectedTable.getExampleWhereClauseId()));
    	context.getCommentGenerator().addComment(whereAnswer);
    	XmlElement whereElement = new XmlElement("where");
    	whereAnswer.addElement(whereElement);
    	
    	StringBuilder sbWhere = new StringBuilder();
    	for (IntrospectedColumn introspectedColumn : introspectedTable
                .getAllColumns()) {
            XmlElement isNotNullElement = new XmlElement("if"); //$NON-NLS-1$
            sbWhere.setLength(0);
            sbWhere.append(introspectedColumn.getJavaProperty("record.")); //$NON-NLS-1$
            sbWhere.append(" != null"); //$NON-NLS-1$
            isNotNullElement.addAttribute(new Attribute("test", sbWhere.toString())); //$NON-NLS-1$
            whereElement.addElement(isNotNullElement);

            sbWhere.setLength(0);
            sbWhere.append("and ");
            sbWhere.append(MyBatis3FormattingUtilities
                    .getAliasedEscapedColumnName(introspectedColumn));
            sbWhere.append(" = "); //$NON-NLS-1$
            sbWhere.append(MyBatis3FormattingUtilities.getParameterClause(
                    introspectedColumn, "record.")); //$NON-NLS-1$

            isNotNullElement.addElement(new TextElement(sbWhere.toString()));
        }
    	if (context.getPlugins()
                .sqlMapUpdateByExampleSelectiveElementGenerated(whereAnswer,
                        introspectedTable)) {
            parentElement.addElement(whereAnswer);
        }
    	
    	//以下为sql查询字段
        XmlElement answer = new XmlElement("sql"); //$NON-NLS-1$

        answer.addAttribute(new Attribute("id", //$NON-NLS-1$
                introspectedTable.getBaseColumnListId()));

        context.getCommentGenerator().addComment(answer);

        StringBuilder sb = new StringBuilder();
        Iterator<IntrospectedColumn> iter = introspectedTable
                .getNonBLOBColumns().iterator();
        while (iter.hasNext()) {
            sb.append(MyBatis3FormattingUtilities.getSelectListPhrase(iter
                    .next()));

            if (iter.hasNext()) {
                sb.append(", "); //$NON-NLS-1$
            }

            if (sb.length() > 80) {
                answer.addElement(new TextElement(sb.toString()));
                sb.setLength(0);
            }
        }

        if (sb.length() > 0) {
            answer.addElement((new TextElement(sb.toString())));
        }

        if (context.getPlugins().sqlMapBaseColumnListElementGenerated(
                answer, introspectedTable)) {
            parentElement.addElement(answer);
        }
        

    	
    }
}
