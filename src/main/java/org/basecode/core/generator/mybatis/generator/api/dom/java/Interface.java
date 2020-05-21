/*
 *  Copyright 2006 The Apache Software Foundation
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
package org.basecode.core.generator.mybatis.generator.api.dom.java;

import org.basecode.core.generator.mybatis.generator.api.dom.java.CompilationUnit;
import org.basecode.core.generator.mybatis.generator.api.dom.java.FullyQualifiedJavaType;
import org.basecode.core.generator.mybatis.generator.api.dom.java.JavaElement;

import static org.basecode.core.generator.mybatis.generator.api.dom.OutputUtilities.calculateImports;
import static org.basecode.core.generator.mybatis.generator.api.dom.OutputUtilities.javaIndent;
import static org.basecode.core.generator.mybatis.generator.api.dom.OutputUtilities.newLine;
import static org.basecode.core.generator.mybatis.generator.internal.util.StringUtility.stringHasValue;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

/**
 * @author Jeff Butler
 */
public class Interface extends JavaElement implements CompilationUnit {
    private Set<FullyQualifiedJavaType> importedTypes;
    
    private Set<String> staticImports;

    private FullyQualifiedJavaType type;

    private Set<FullyQualifiedJavaType> superInterfaceTypes;

    private List<Method> methods;

    private List<String> fileCommentLines;

    /**
     *  
     */
    public Interface(FullyQualifiedJavaType type) {
        super();
        this.type = type;
        superInterfaceTypes = new LinkedHashSet<FullyQualifiedJavaType>();
        methods = new ArrayList<Method>();
        importedTypes = new TreeSet<FullyQualifiedJavaType>();
        fileCommentLines = new ArrayList<String>();
        staticImports = new TreeSet<String>();
    }

    public Interface(String type) {
        this(new FullyQualifiedJavaType(type));
    }

    public Set<FullyQualifiedJavaType> getImportedTypes() {
        return Collections.unmodifiableSet(importedTypes);
    }

    public void addImportedType(FullyQualifiedJavaType importedType) {
        if (importedType.isExplicitlyImported()
                && !importedType.getPackageName().equals(type.getPackageName())) {
            importedTypes.add(importedType);
        }
    }

    public String getFormattedContent() {
        StringBuilder sb = new StringBuilder();

        for (String commentLine : fileCommentLines) {
            sb.append(commentLine);
            newLine(sb);
        }

        if (stringHasValue(getType().getPackageName())) {
            sb.append("package "); //$NON-NLS-1$
            sb.append(getType().getPackageName());
            sb.append(';');
            newLine(sb);
            newLine(sb);
        }

        for (String staticImport : staticImports) {
            sb.append("import static "); //$NON-NLS-1$
            sb.append(staticImport);
            sb.append(';');
            newLine(sb);
        }
        
        if (staticImports.size() > 0) {
            newLine(sb);
        }
        
        Set<String> importStrings = calculateImports(importedTypes);
        for (String importString : importStrings) {
            sb.append(importString);
            newLine(sb);
        }

        if (importStrings.size() > 0) {
            newLine(sb);
        }

        int indentLevel = 0;

        addFormattedJavadoc(sb, indentLevel);
        addFormattedAnnotations(sb, indentLevel);

        sb.append(getVisibility().getValue());

        if (isStatic()) {
            sb.append("static "); //$NON-NLS-1$
        }

        if (isFinal()) {
            sb.append("final "); //$NON-NLS-1$
        }

        sb.append("interface "); //$NON-NLS-1$
        sb.append(getType().getShortName());

        if (getSuperInterfaceTypes().size() > 0) {
            sb.append(" extends "); //$NON-NLS-1$

            boolean comma = false;
            for (FullyQualifiedJavaType fqjt : getSuperInterfaceTypes()) {
                if (comma) {
                    sb.append(", "); //$NON-NLS-1$
                } else {
                    comma = true;
                }

                sb.append(fqjt.getShortName());
            }
        }

        sb.append(" {"); //$NON-NLS-1$
        indentLevel++;

        Iterator<Method> mtdIter = getMethods().iterator();
        while (mtdIter.hasNext()) {
        	
        	Method method = mtdIter.next();
        	switch (method.getName()) {
			case "count":
				newLine(sb);
				javaIndent(sb, indentLevel);
				sb.append("/**");
				newLine(sb);
				javaIndent(sb, indentLevel);
				sb.append(" * 统计数量");
				newLine(sb);
				javaIndent(sb, indentLevel);
				sb.append(" */");
				break;
			case "delete":
				newLine(sb);
				javaIndent(sb, indentLevel);
				sb.append("/**");
				newLine(sb);
				javaIndent(sb, indentLevel);
				sb.append(" * 自定义删除");
				newLine(sb);
				javaIndent(sb, indentLevel);
				sb.append(" */");
				break;
			case "deleteByPrimaryKey":
				newLine(sb);
				javaIndent(sb, indentLevel);
				sb.append("/**");
				newLine(sb);
				javaIndent(sb, indentLevel);
				sb.append(" * 根据表主键ID来删除");
				newLine(sb);
				javaIndent(sb, indentLevel);
				sb.append(" */");
				break;
			case "insert":
				newLine(sb);
				javaIndent(sb, indentLevel);
				sb.append("/**");
				newLine(sb);
				javaIndent(sb, indentLevel);
				sb.append(" * 添加数据，包含空数据");
				newLine(sb);
				javaIndent(sb, indentLevel);
				sb.append(" */");
				break;
			case "insertSelective":
				newLine(sb);
				javaIndent(sb, indentLevel);
				sb.append("/**");
				newLine(sb);
				javaIndent(sb, indentLevel);
				sb.append(" * 添加数据，不包含空数据");
				newLine(sb);
				javaIndent(sb, indentLevel);
				sb.append(" */");
				break;
			case "select":
				newLine(sb);
				javaIndent(sb, indentLevel);
				sb.append("/**");
				newLine(sb);
				javaIndent(sb, indentLevel);
				sb.append(" * 自定义查询");
				newLine(sb);
				javaIndent(sb, indentLevel);
				sb.append(" */");
				break;
			case "selectByPrimaryKey":
				newLine(sb);
				javaIndent(sb, indentLevel);
				sb.append("/**");
				newLine(sb);
				javaIndent(sb, indentLevel);
				sb.append(" * 根据表主键ID查询,返回实体");
				newLine(sb);
				javaIndent(sb, indentLevel);
				sb.append(" */");
				break;
			case "updateSelective":
				newLine(sb);
				javaIndent(sb, indentLevel);
				sb.append("/**");
				newLine(sb);
				javaIndent(sb, indentLevel);
				sb.append(" * 更新数据，参数为Map，只更新有数据的字段");
				newLine(sb);
				javaIndent(sb, indentLevel);
				sb.append(" */");
				break;
			case "update":
				newLine(sb);
				javaIndent(sb, indentLevel);
				sb.append("/**");
				newLine(sb);
				javaIndent(sb, indentLevel);
				sb.append(" * 更新数据，参数为Map，可把字段数据更新为null");
				newLine(sb);
				javaIndent(sb, indentLevel);
				sb.append(" */");
				break;
			case "updateByPrimaryKeySelective":
				newLine(sb);
				javaIndent(sb, indentLevel);
				sb.append("/**");
				newLine(sb);
				javaIndent(sb, indentLevel);
				sb.append(" * 根据ID更新数据，参数为实体类，只更新有数据的字段");
				newLine(sb);
				javaIndent(sb, indentLevel);
				sb.append(" */");
				break;
			case "updateByPrimaryKey":
				newLine(sb);
				javaIndent(sb, indentLevel);
				sb.append("/**");
				newLine(sb);
				javaIndent(sb, indentLevel);
				sb.append(" * 根据ID更新数据，参数为实体类，可把字段数据更新为null");
				newLine(sb);
				javaIndent(sb, indentLevel);
				sb.append(" */");
				break;

			default:
				break;
			}
        	newLine(sb);
            sb.append(method.getFormattedContent(indentLevel, true));
            if (mtdIter.hasNext()) {
                newLine(sb);
            }
        }

        indentLevel--;
        newLine(sb);
        javaIndent(sb, indentLevel);
        sb.append('}');

        return sb.toString();
    }

    public void addSuperInterface(FullyQualifiedJavaType superInterface) {
        superInterfaceTypes.add(superInterface);
    }

    /**
     * @return Returns the methods.
     */
    public List<Method> getMethods() {
        return methods;
    }

    public void addMethod(Method method) {
        methods.add(method);
    }

    /**
     * @return Returns the type.
     */
    public FullyQualifiedJavaType getType() {
        return type;
    }

    public FullyQualifiedJavaType getSuperClass() {
        // interfaces do not have superclasses
        return null;
    }

    public Set<FullyQualifiedJavaType> getSuperInterfaceTypes() {
        return superInterfaceTypes;
    }

    public boolean isJavaInterface() {
        return true;
    }

    public boolean isJavaEnumeration() {
        return false;
    }

    public void addFileCommentLine(String commentLine) {
        fileCommentLines.add(commentLine);
    }

    public List<String> getFileCommentLines() {
        return fileCommentLines;
    }

    public void addImportedTypes(Set<FullyQualifiedJavaType> importedTypes) {
        this.importedTypes.addAll(importedTypes);
    }

    public Set<String> getStaticImports() {
        return staticImports;
    }

    public void addStaticImport(String staticImport) {
        staticImports.add(staticImport);
    }

    public void addStaticImports(Set<String> staticImports) {
        this.staticImports.addAll(staticImports);
    }
}
