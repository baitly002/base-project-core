package org.basecode.common.generator.mybatis.generator.tools;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.github.javaparser.JavaParser;
import com.github.javaparser.ParseResult;
import com.github.javaparser.TokenRange;
import com.github.javaparser.ast.*;
import com.github.javaparser.ast.body.*;
import com.github.javaparser.ast.comments.Comment;
import org.basecode.common.generator.mybatis.generator.api.dom.OutputUtilities;

import java.io.File;
import java.io.FileNotFoundException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class JavaFileMerger {

    public String getNewJavaFile(String newFileSource, String existingFileFullPath) throws FileNotFoundException {
        JavaParser javaParser = new JavaParser();
        ParseResult<CompilationUnit> newCompilationUnit = javaParser.parse(newFileSource);
        ParseResult<CompilationUnit> existingCompilationUnit = javaParser.parse(new File(existingFileFullPath));
        if (newCompilationUnit.getResult().isPresent() || existingCompilationUnit.getResult().isPresent()) {
            return mergerFile(newCompilationUnit.getResult().get(), existingCompilationUnit.getResult().get());

        }
        return newFileSource;
    }

    public String getNewJavaFileForSource(String newFileSource, String oldFileSource) throws FileNotFoundException {
        JavaParser javaParser = new JavaParser();
        ParseResult<CompilationUnit> newCompilationUnit = javaParser.parse(newFileSource);
        ParseResult<CompilationUnit> existingCompilationUnit = javaParser.parse(oldFileSource);
        if (newCompilationUnit.getResult().isPresent() || existingCompilationUnit.getResult().isPresent()) {
            return mergerFile(newCompilationUnit.getResult().get(), existingCompilationUnit.getResult().get());

        }
        return newFileSource;
    }

    public String mergerFile(CompilationUnit newCompilationUnit, CompilationUnit existingCompilationUnit) {

        System.out.println("开始合并java代码");
        CompilationUnit compilationUnit = new CompilationUnit();
        if (newCompilationUnit.getPackageDeclaration().isPresent()) {
            PackageDeclaration packageDeclaration = newCompilationUnit.getPackageDeclaration().get();
            compilationUnit.setPackageDeclaration(packageDeclaration);
        }
        StringBuilder sb = new StringBuilder(compilationUnit.getPackageDeclaration().get().toString());
        Map<String, ImportDeclaration> imports = new Hashtable<>();
        for (ImportDeclaration i : newCompilationUnit.getImports()) {
            compilationUnit.addImport(i);
            imports.put(i.getNameAsString(), i);
        }
        for (ImportDeclaration i : existingCompilationUnit.getImports()) {
            if (!imports.containsKey(i.getNameAsString())) {
                compilationUnit.addImport(i);
            }
        }
        for (ImportDeclaration i : compilationUnit.getImports()) {
            sb.append(i.toString());
        }
        OutputUtilities.newLine(sb);
        for (Comment c : existingCompilationUnit.getOrphanComments()) {
            compilationUnit.addOrphanComment(c);
            sb.append(c.toString());
        }
        OutputUtilities.newLine(sb);
//        StringBuilder sb = new StringBuilder(newCompilationUnit.getPackageDeclaration().get().toString());
//        newCompilationUnit.removePackageDeclaration();

//        //合并imports
//        NodeList<ImportDeclaration> imports = newCompilationUnit.getImports();
//        imports.addAll(existingCompilationUnit.getImports());

        NodeList<TypeDeclaration<?>> types = newCompilationUnit.getTypes();
        NodeList<TypeDeclaration<?>> oldTypes = existingCompilationUnit.getTypes();

        for (int i = 0; i < types.size(); i++) {
            ClassOrInterfaceDeclaration typeDeclaration = (ClassOrInterfaceDeclaration) types.get(i);
            ClassOrInterfaceDeclaration typeDeclarationOld = (ClassOrInterfaceDeclaration) oldTypes.get(i);


            //保留旧代码的类注释
            if (typeDeclarationOld.getComment().isPresent()) {
                sb.append(typeDeclarationOld.getComment().get().toString());
            }

            //保留旧代码的类注解
//            for(AnnotationExpr a : typeDeclarationOld.getAnnotations()){
//                sb.append(a.toString());
//                OutputUtilities.newLine(sb);
//            }

            TokenRange tr = typeDeclarationOld.getTokenRange().get();
            String classNameInfo = tr.toString().substring(0, tr.toString().indexOf("{") + 1);
            sb.append(classNameInfo);
            OutputUtilities.newLine(sb);


            //合并fields
            List<FieldDeclaration> fields = typeDeclaration.getFields();
            List<FieldDeclaration> oldFields = typeDeclarationOld.getFields();
            Map<String, FieldDeclaration> fieldOldMap = new Hashtable<>();
            Map<String, FieldDeclaration> fieldNewMap = new Hashtable<>();
            List<FieldDeclaration> fieldList = new ArrayList<>();
            for (FieldDeclaration f : oldFields) {
                VariableDeclarator variableDeclarator = f.getVariable(0);
                String varName = variableDeclarator.getNameAsString();
                String varType = variableDeclarator.getTypeAsString();
                fieldOldMap.put(varName, f);
            }
            for (FieldDeclaration f : fields) {
                VariableDeclarator variableDeclarator = f.getVariable(0);
                String varName = variableDeclarator.getNameAsString();
                String varType = variableDeclarator.getTypeAsString();
                fieldNewMap.put(varName, f);
            }
            for (FieldDeclaration f : fields) {
                VariableDeclarator variableDeclarator = f.getVariable(0);
                String varName = variableDeclarator.getNameAsString();
                String varType = variableDeclarator.getTypeAsString();
                if (fieldOldMap.containsKey(varName)) {
                    //新字段保留旧字段的注解及注释
                    FieldDeclaration o = fieldOldMap.get(varName);
                    f.setAnnotations(o.getAnnotations());
                    if (o.getComment().isPresent()) {
                        f.setComment(o.getComment().get());
                    }
                }
                fieldList.add(f);
            }
            for (FieldDeclaration o : oldFields) {
                VariableDeclarator variableDeclarator = o.getVariable(0);
                String varName = variableDeclarator.getNameAsString();
                String varType = variableDeclarator.getTypeAsString();
                if (!fieldNewMap.containsKey(varName)) {
                    //新代码中没有的字段
                    if (o.getAnnotationByClass(TableField.class).isPresent() || o.getAnnotationByClass(TableId.class).isPresent()) {
                        //旧数据库表的字段，丢弃
                    } else {
                        fieldList.add(o);
                    }
                }
            }
            //写文件
            for (FieldDeclaration f : fieldList) {
                OutputUtilities.newLine(sb);
                OutputUtilities.javaIndent(sb, 1);
                String res = f.toString();
                res = res.replaceAll("\t", "");
                res = res.replaceAll("     \\*", " \\*");
                res = res.replaceAll(System.getProperty("line.separator"), System.getProperty("line.separator") + "    ");
                sb.append(res);
                OutputUtilities.newLine(sb);
            }


            OutputUtilities.newLine(sb);
            //合并methods
            List<MethodDeclaration> methods = types.get(i).getMethods();
            List<MethodDeclaration> existingMethods = oldTypes.get(i).getMethods();
            Map<String, MethodDeclaration> methodMap = new HashMap<>();
            for (MethodDeclaration m : methods) {
                String name = m.getNameAsString();
                String type = m.getTypeAsString();
                String tp = "";
                for (Parameter p : m.getParameters()) {
                    tp = tp + "-" + p.getTypeAsString();
                }
                methodMap.put(name + "-" + type + tp, m);
            }
            for (MethodDeclaration m : existingMethods) {
                String name = m.getNameAsString();
                String type = m.getTypeAsString();
                String tp = "";
                for (Parameter p : m.getParameters()) {
                    tp = tp + "-" + p.getTypeAsString();
                }
                methodMap.put(name + "-" + type + tp, m);
            }
            for (MethodDeclaration m : methodMap.values()) {
                String res = m.toString();
                res = res.replaceAll("\t", "");
                res = res.replaceAll("     \\*", " \\*");
                res = res.replaceAll(System.getProperty("line.separator"), System.getProperty("line.separator") + "    ");
                OutputUtilities.javaIndent(sb, 1);
                sb.append(res);
            }


            OutputUtilities.newLine(sb);
            OutputUtilities.newLine(sb);
            for (Comment c : typeDeclarationOld.getOrphanComments()) {
                OutputUtilities.writeSpace(sb, c.getRange().get().begin.column);
                sb.append(c.toString());
            }


            //判断是否有内部类
            types.get(i).getChildNodes();
            for (Node n : types.get(i).getChildNodes()) {
                if (n.toString().contains("static class")) {
                    OutputUtilities.newLine(sb);
                    String res = n.toString();
                    res = res.replaceAll("\t", "");
                    res = res.replaceAll("     \\*", " \\*");
                    res = res.replaceAll(System.getProperty("line.separator"), System.getProperty("line.separator") + "    ");
                    sb.append("\t" + res);
//                	javaIndent(sb, 1);
                    sb.append(n.toString());
                }
            }
        }

        sb.append(System.getProperty("line.separator") + "}");
        return sb.toString();
    }

    public String readFileContext(String path) {
        try {
            Path p = Paths.get(path);
            byte[] bytes = Files.readAllBytes(p);
            return new String(bytes);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void main(String[] args) {
        String s = "yyy\n\t tttt\n\t\tuuu\n\t\tiii";
        s = s.replace("\t", "    ");
        s = s.toString().replaceAll("\n    ", "\n");
//		s = s.toString().replace("\n    ", "\n");
        s = s.replaceAll(System.getProperty("line.separator"), System.getProperty("line.separator") + "    ");
//		s = s.replaceAll("\n", "\n    ");
        System.out.println(s);
    }
}