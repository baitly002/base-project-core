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
package org.basecode.core.generator.mybatis.generator.codegen.mybatis3.model;

import org.basecode.core.generator.main.CodeFactory;
import org.basecode.core.generator.mybatis.generator.api.CommentGenerator;
import org.basecode.core.generator.mybatis.generator.api.FullyQualifiedTable;
import org.basecode.core.generator.mybatis.generator.api.IntrospectedColumn;
import org.basecode.core.generator.mybatis.generator.api.Plugin;
import org.basecode.core.generator.mybatis.generator.api.dom.java.*;
import org.basecode.core.generator.mybatis.generator.codegen.AbstractJavaGenerator;
import org.basecode.core.generator.mybatis.generator.codegen.RootClassInfo;
import org.basecode.core.sequence.SequenceSingle;

import java.util.ArrayList;
import java.util.List;

import static org.basecode.core.generator.mybatis.generator.internal.util.JavaBeansUtil.getJavaBeansField;
import static org.basecode.core.generator.mybatis.generator.internal.util.messages.Messages.getString;


/**
 * 
 * @author Jeff Butler
 * 
 */
public class BaseRecordGenerator extends AbstractJavaGenerator {

    public BaseRecordGenerator() {
        super();
    }

    @Override
    public List<CompilationUnit> getCompilationUnits() {
        FullyQualifiedTable table = introspectedTable.getFullyQualifiedTable();
        progressCallback.startTask(getString(
                "Progress.8", table.toString())); //$NON-NLS-1$
        Plugin plugins = context.getPlugins();
        CommentGenerator commentGenerator = context.getCommentGenerator();

        FullyQualifiedJavaType type = new FullyQualifiedJavaType(
                introspectedTable.getBaseRecordType());
        TopLevelClass topLevelClass = new TopLevelClass(type);
        topLevelClass.setVisibility(JavaVisibility.PUBLIC);
        FullyQualifiedJavaType impl = new FullyQualifiedJavaType("java.io.Serializable");
        topLevelClass.addSuperInterface(impl);
        topLevelClass.addImportedType(impl);
        topLevelClass.addImportedType("com.dashu.lazyapidoc.annotation.Doc");
        topLevelClass.addImportedType("com.baomidou.mybatisplus.annotation.TableField");
        topLevelClass.addImportedType("com.baomidou.mybatisplus.annotation.TableId");
        topLevelClass.addImportedType("lombok.Data");
        topLevelClass.addImportedType("com.baomidou.mybatisplus.annotation.TableName");
        topLevelClass.addAnnotation("@Data");
        topLevelClass.addAnnotation("@TableName(\""+table.getIntrospectedTableName()+"\")");
        topLevelClass.addJavaDocLine("//数据库对应的字段必须有TableField或TableId注解，否则会影响下次生成的文件");
        commentGenerator.addJavaFileComment(topLevelClass);

        FullyQualifiedJavaType superClass = getSuperClass();
        if (superClass != null) {
            topLevelClass.setSuperClass(superClass);
            topLevelClass.addImportedType(superClass);
        }
        
        //添加序列化ID
        Field fieldSerializable = new Field();
        fieldSerializable.addJavaDocLine("");
        fieldSerializable.setVisibility(JavaVisibility.PRIVATE);
        FullyQualifiedJavaType fType = new FullyQualifiedJavaType("long");
        fieldSerializable.setStatic(true);
        fieldSerializable.setFinal(true);
        fieldSerializable.setType(fType);
        fieldSerializable.setName("serialVersionUID");
        fieldSerializable.setInitializationString(SequenceSingle.getIstance().nextId()+"L");
        topLevelClass.addField(fieldSerializable);

        List<IntrospectedColumn> introspectedColumns = getColumnsInThisClass();

        if (introspectedTable.isConstructorBased()) {
            addParameterizedConstructor(topLevelClass);
            
            if (!introspectedTable.isImmutable()) {
                addDefaultConstructor(topLevelClass);
            }
        }
        
        String rootClass = getRootClass();
        for (IntrospectedColumn introspectedColumn : introspectedColumns) {
            if (RootClassInfo.getInstance(rootClass, warnings)
                    .containsProperty(introspectedColumn)) {
                continue;
            }

            Field field = getJavaBeansField(introspectedColumn, context, introspectedTable);
            if (plugins.modelFieldGenerated(field, topLevelClass,
                    introspectedColumn, introspectedTable,
                    Plugin.ModelClassType.BASE_RECORD)) {
                topLevelClass.addField(field);
                topLevelClass.addImportedType(field.getType());
            }

            //不再生成set get方法

//            Method method = getJavaBeansGetter(introspectedColumn, context, introspectedTable);
//            if (plugins.modelGetterMethodGenerated(method, topLevelClass,
//                    introspectedColumn, introspectedTable,
//                    Plugin.ModelClassType.BASE_RECORD)) {
//                topLevelClass.addMethod(method);
//            }
//
//            if (!introspectedTable.isImmutable()) {
//                method = getJavaBeansSetter(introspectedColumn, context, introspectedTable);
//                method.setReturnType(type);
//                if (plugins.modelSetterMethodGenerated(method, topLevelClass,
//                        introspectedColumn, introspectedTable,
//                        Plugin.ModelClassType.BASE_RECORD)) {
//                    topLevelClass.addMethod(method);
//                }
//            }
        }

        //将实体类各列返回去
        List<Field> listField = topLevelClass.getFields();
        CodeFactory.data.put("listField", listField);
        
        List<CompilationUnit> answer = new ArrayList<CompilationUnit>();
        if (context.getPlugins().modelBaseRecordClassGenerated(
                topLevelClass, introspectedTable)) {
            answer.add(topLevelClass);
        }
        return answer;
    }

    private FullyQualifiedJavaType getSuperClass() {
        FullyQualifiedJavaType superClass;
        if (introspectedTable.getRules().generatePrimaryKeyClass()) {
            superClass = new FullyQualifiedJavaType(introspectedTable
                    .getPrimaryKeyType());
        } else {
            String rootClass = getRootClass();
            if (rootClass != null) {
                superClass = new FullyQualifiedJavaType(rootClass);
            } else {
                superClass = null;
            }
        }

        return superClass;
    }

    private boolean includePrimaryKeyColumns() {
        return !introspectedTable.getRules().generatePrimaryKeyClass()
                && introspectedTable.hasPrimaryKeyColumns();
    }

    private boolean includeBLOBColumns() {
        return !introspectedTable.getRules().generateRecordWithBLOBsClass()
                && introspectedTable.hasBLOBColumns();
    }

    private void addParameterizedConstructor(TopLevelClass topLevelClass) {
        Method method = new Method();
        method.setVisibility(JavaVisibility.PUBLIC);
        method.setConstructor(true);
        method.setName(topLevelClass.getType().getShortName());
        context.getCommentGenerator().addGeneralMethodComment(method, introspectedTable);

        List<IntrospectedColumn> constructorColumns =
            includeBLOBColumns() ? introspectedTable.getAllColumns() :
                introspectedTable.getNonBLOBColumns();
            
        for (IntrospectedColumn introspectedColumn : constructorColumns) {
            method.addParameter(new Parameter(introspectedColumn.getFullyQualifiedJavaType(),
                    introspectedColumn.getJavaProperty()));
            topLevelClass.addImportedType(introspectedColumn.getFullyQualifiedJavaType());
        }
        
        StringBuilder sb = new StringBuilder();
        if (introspectedTable.getRules().generatePrimaryKeyClass()) {
            boolean comma = false;
            sb.append("super("); //$NON-NLS-1$
            for (IntrospectedColumn introspectedColumn : introspectedTable
                    .getPrimaryKeyColumns()) {
                if (comma) {
                    sb.append(", "); //$NON-NLS-1$
                } else {
                    comma = true;
                }
                sb.append(introspectedColumn.getJavaProperty());
            }
            sb.append(");"); //$NON-NLS-1$
            method.addBodyLine(sb.toString());
        }

        List<IntrospectedColumn> introspectedColumns = getColumnsInThisClass();
        
        for (IntrospectedColumn introspectedColumn : introspectedColumns) {
            sb.setLength(0);
            sb.append("this."); //$NON-NLS-1$
            sb.append(introspectedColumn.getJavaProperty());
            sb.append(" = "); //$NON-NLS-1$
            sb.append(introspectedColumn.getJavaProperty());
            sb.append(';');
            method.addBodyLine(sb.toString());
        }

        topLevelClass.addMethod(method);
    }
    
    private List<IntrospectedColumn> getColumnsInThisClass() {
        List<IntrospectedColumn> introspectedColumns;
        if (includePrimaryKeyColumns()) {
            if (includeBLOBColumns()) {
                introspectedColumns = introspectedTable.getAllColumns();
            } else {
                introspectedColumns = introspectedTable.getNonBLOBColumns();
            }
        } else {
            if (includeBLOBColumns()) {
                introspectedColumns = introspectedTable
                        .getNonPrimaryKeyColumns();
            } else {
                introspectedColumns = introspectedTable.getBaseColumns();
            }
        }
        
        return introspectedColumns;
    }
}
