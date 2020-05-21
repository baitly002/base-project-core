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

/**
 * 
 * @author Jeff Butler
 * 
 */
public class DeleteByExampleMethodGenerator extends
        AbstractJavaMapperMethodGenerator {

    public DeleteByExampleMethodGenerator() {
        super();
    }

    @Override
    public void addInterfaceElements(Interface interfaze) {
        Set<FullyQualifiedJavaType> importedTypes = new TreeSet<FullyQualifiedJavaType>();
        FullyQualifiedJavaType type = new FullyQualifiedJavaType(
                introspectedTable.getExampleType());
        importedTypes.add(type);
        FullyQualifiedJavaType map = new FullyQualifiedJavaType("java.utils.Map");
        Method method = new Method();
        method.setVisibility(JavaVisibility.PUBLIC);
        method.setReturnType(FullyQualifiedJavaType.getIntInstance());
        method.setName(introspectedTable.getDeleteByExampleStatementId());
//        method.addParameter(new Parameter(type, StringUtility.lowerStr(type.getFullyQualifiedName().substring(type.getFullyQualifiedName().lastIndexOf(".")));)); //$NON-NLS-1$
        method.addParameter(new Parameter(map, "map")); //$NON-NLS-1$

        context.getCommentGenerator().addGeneralMethodComment(method,
                introspectedTable);

        addMapperAnnotations(interfaze, method);
        
        if (context.getPlugins().clientDeleteByExampleMethodGenerated(
                method, interfaze, introspectedTable)) {
            interfaze.addImportedTypes(importedTypes);
            interfaze.addMethod(method);
        }
    }

    public void addMapperAnnotations(Interface interfaze, Method method) {
        return;
    }
}
