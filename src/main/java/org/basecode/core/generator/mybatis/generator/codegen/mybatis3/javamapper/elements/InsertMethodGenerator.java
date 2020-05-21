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
package org.basecode.core.generator.mybatis.generator.codegen.mybatis3.javamapper.elements;

import java.util.Set;
import java.util.TreeSet;

import org.basecode.core.generator.mybatis.generator.api.dom.java.FullyQualifiedJavaType;
import org.basecode.core.generator.mybatis.generator.api.dom.java.Interface;
import org.basecode.core.generator.mybatis.generator.api.dom.java.JavaVisibility;
import org.basecode.core.generator.mybatis.generator.api.dom.java.Method;
import org.basecode.core.generator.mybatis.generator.api.dom.java.Parameter;
import org.basecode.core.generator.mybatis.generator.codegen.mybatis3.javamapper.elements.AbstractJavaMapperMethodGenerator;
import org.basecode.core.generator.mybatis.generator.internal.util.StringUtility;

/**
 * 
 * @author Jeff Butler
 * 
 */
public class InsertMethodGenerator extends AbstractJavaMapperMethodGenerator {

    boolean isSimple;

    public InsertMethodGenerator(boolean isSimple) {
        super();
        this.isSimple = isSimple;
    }

    @Override
    public void addInterfaceElements(Interface interfaze) {
        Set<FullyQualifiedJavaType> importedTypes = new TreeSet<FullyQualifiedJavaType>();
        Method method = new Method();

        method.setReturnType(FullyQualifiedJavaType.getIntInstance());
        method.setVisibility(JavaVisibility.PUBLIC);
        method.setName(introspectedTable.getInsertStatementId());

        FullyQualifiedJavaType parameterType;
        if (isSimple) {
            parameterType = new FullyQualifiedJavaType(
                    introspectedTable.getBaseRecordType());
        } else {
            parameterType = introspectedTable.getRules()
                    .calculateAllFieldsClass();
        }

        importedTypes.add(parameterType);
        method.addParameter(new Parameter(parameterType, StringUtility.lowerStr(parameterType.getFullyQualifiedName().substring(parameterType.getFullyQualifiedName().lastIndexOf(".")+1)))); //$NON-NLS-1$

        context.getCommentGenerator().addGeneralMethodComment(method,
                introspectedTable);

        addMapperAnnotations(interfaze, method);

        if (context.getPlugins().clientInsertMethodGenerated(method, interfaze,
                introspectedTable)) {
            interfaze.addImportedTypes(importedTypes);
            interfaze.addMethod(method);
        }
    }

    public void addMapperAnnotations(Interface interfaze, Method method) {
        return;
    }
}
