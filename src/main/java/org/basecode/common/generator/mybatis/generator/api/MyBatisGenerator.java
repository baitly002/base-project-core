/*
 *  Copyright 2005 The Apache Software Foundation
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
package org.basecode.common.generator.mybatis.generator.api;

import org.basecode.common.generator.main.CodeFactory;
import org.basecode.common.generator.mybatis.generator.config.Configuration;
import org.basecode.common.generator.mybatis.generator.config.Context;
import org.basecode.common.generator.mybatis.generator.config.MergeConstants;
import org.basecode.common.generator.mybatis.generator.config.TableConfiguration;
import org.basecode.common.generator.mybatis.generator.config.xml.ConfigurationParser;
import org.basecode.common.generator.mybatis.generator.exception.InvalidConfigurationException;
import org.basecode.common.generator.mybatis.generator.exception.ShellException;
import org.basecode.common.generator.mybatis.generator.internal.DefaultShellCallback;
import org.basecode.common.generator.mybatis.generator.internal.NullProgressCallback;
import org.basecode.common.generator.mybatis.generator.internal.ObjectFactory;
import org.basecode.common.generator.mybatis.generator.internal.XmlFileMergerJaxp;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.Node;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import java.io.*;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.basecode.common.generator.mybatis.generator.internal.util.ClassloaderUtility.getCustomClassloader;
import static org.basecode.common.generator.mybatis.generator.internal.util.messages.Messages.getString;

//import com.sun.tools.javac.jvm.Code;

/**
 * 修改于20140626:格式化输出
 * This class is the main interface to MyBatis generator. A typical execution of
 * the tool involves these steps:
 * 
 * <ol>
 * <li>Create a Configuration object. The Configuration can be the result of a
 * parsing the XML configuration file, or it can be created solely in Java.</li>
 * <li>Create a MyBatisGenerator object</li>
 * <li>Call one of the generate() methods</li>
 * </ol>
 * 
 * @see ConfigurationParser
 * 
 * @author Jeff Butler
 */
public class MyBatisGenerator {

    private Configuration configuration;

    private ShellCallback shellCallback;

    private List<GeneratedJavaFile> generatedJavaFiles;

    private List<GeneratedXmlFile> generatedXmlFiles;

    private List<String> warnings;

    private Set<String> projects;

    /**
     * Constructs a MyBatisGenerator object.
     * 
     * @param configuration
     *            The configuration for this invocation
     * @param shellCallback
     *            an instance of a ShellCallback interface. You may specify
     *            <code>null</code> in which case the DefaultShellCallback will
     *            be used.
     * @param warnings
     *            Any warnings generated during execution will be added to this
     *            list. Warnings do not affect the running of the tool, but they
     *            may affect the results. A typical warning is an unsupported
     *            data type. In that case, the column will be ignored and
     *            generation will continue. You may specify <code>null</code> if
     *            you do not want warnings returned.
     * @throws InvalidConfigurationException
     *             if the specified configuration is invalid
     */
    public MyBatisGenerator(Configuration configuration, ShellCallback shellCallback,
            List<String> warnings) throws InvalidConfigurationException {
        super();
        if (configuration == null) {
            throw new IllegalArgumentException(getString("RuntimeError.2")); //$NON-NLS-1$
        } else {
            this.configuration = configuration;
        }

        if (shellCallback == null) {
            this.shellCallback = new DefaultShellCallback(false);
        } else {
            this.shellCallback = shellCallback;
        }

        if (warnings == null) {
            this.warnings = new ArrayList<String>();
        } else {
            this.warnings = warnings;
        }
        generatedJavaFiles = new ArrayList<GeneratedJavaFile>();
        generatedXmlFiles = new ArrayList<GeneratedXmlFile>();
        projects = new HashSet<String>();

        this.configuration.validate();
    }

    /**
     * This is the main method for generating code. This method is long running,
     * but progress can be provided and the method can be canceled through the
     * ProgressCallback interface. This version of the method runs all
     * configured contexts.
     * 
     * @param callback
     *            an instance of the ProgressCallback interface, or
     *            <code>null</code> if you do not require progress information
     * @throws SQLException
     * @throws IOException
     * @throws InterruptedException
     *             if the method is canceled through the ProgressCallback
     */
    public void generate(ProgressCallback callback) throws SQLException,
            IOException, InterruptedException {
        generate(callback, null, null);
    }

    /**
     * This is the main method for generating code. This method is long running,
     * but progress can be provided and the method can be canceled through the
     * ProgressCallback interface.
     * 
     * @param callback
     *            an instance of the ProgressCallback interface, or
     *            <code>null</code> if you do not require progress information
     * @param contextIds
     *            a set of Strings containing context ids to run. Only the
     *            contexts with an id specified in this list will be run. If the
     *            list is null or empty, than all contexts are run.
     * @throws InvalidConfigurationException
     * @throws SQLException
     * @throws IOException
     * @throws InterruptedException
     *             if the method is canceled through the ProgressCallback
     */
    public void generate(ProgressCallback callback, Set<String> contextIds)
            throws SQLException, IOException, InterruptedException {
        generate(callback, contextIds, null);
    }

    /**
     * This is the main method for generating code. This method is long running,
     * but progress can be provided and the method can be cancelled through the
     * ProgressCallback interface.
     * 
     * @param callback
     *            an instance of the ProgressCallback interface, or
     *            <code>null</code> if you do not require progress information
     * @param contextIds
     *            a set of Strings containing context ids to run. Only the
     *            contexts with an id specified in this list will be run. If the
     *            list is null or empty, than all contexts are run.
     * @param fullyQualifiedTableNames
     *            a set of table names to generate. The elements of the set must
     *            be Strings that exactly match what's specified in the
     *            configuration. For example, if table name = "foo" and schema =
     *            "bar", then the fully qualified table name is "foo.bar". If
     *            the Set is null or empty, then all tables in the configuration
     *            will be used for code generation.
     * @throws InvalidConfigurationException
     * @throws SQLException
     * @throws IOException
     * @throws InterruptedException
     *             if the method is canceled through the ProgressCallback
     */
    public void generate(ProgressCallback callback, Set<String> contextIds,
            Set<String> fullyQualifiedTableNames) throws SQLException,
            IOException, InterruptedException {

        if (callback == null) {
            callback = new NullProgressCallback();
        }

        generatedJavaFiles.clear();
        generatedXmlFiles.clear();

        // calculate the contexts to run
        List<Context> contextsToRun;
        if (contextIds == null || contextIds.size() == 0) {
            contextsToRun = configuration.getContexts();
        } else {
            contextsToRun = new ArrayList<Context>();
            for (Context context : configuration.getContexts()) {
                if (contextIds.contains(context.getId())) {
                    contextsToRun.add(context);
                }
            }
        }

        // setup custom classloader if required
        if (configuration.getClassPathEntries().size() > 0) {
            ClassLoader classLoader = getCustomClassloader(configuration.getClassPathEntries());
            ObjectFactory.addExternalClassLoader(classLoader);
        }

        // now run the introspections...
        int totalSteps = 0;
        for (Context context : contextsToRun) {
            totalSteps += context.getIntrospectionSteps();
        }
        callback.introspectionStarted(totalSteps);

        for (Context context : contextsToRun) {
            context.introspectTables(callback, warnings,
                    fullyQualifiedTableNames);
        }

        // now run the generates
        totalSteps = 0;
        for (Context context : contextsToRun) {
            totalSteps += context.getGenerationSteps();
        }
        callback.generationStarted(totalSteps);

        for (Context context : contextsToRun) {
            context.generateFiles(callback, generatedJavaFiles,
                    generatedXmlFiles, warnings);
        }

        // now save the files
        callback.saveStarted(generatedXmlFiles.size()
                + generatedJavaFiles.size());

        if(CodeFactory.isServiceModel){
	        for (GeneratedXmlFile gxf : generatedXmlFiles) {
	            projects.add(gxf.getTargetProject());
	
	            File targetFile;
	            String source;
	            boolean exists = true;//是否提取公共信息覆盖
	            try {
	                File directory = shellCallback.getDirectory(gxf
	                        .getTargetProject(), gxf.getTargetPackage());
	                targetFile = new File(directory, gxf.getFileName());
	                if (targetFile.exists()) {
	                    if (gxf.isMergeable()) {
	                        source = XmlFileMergerJaxp.getMergedSource(gxf,
	                                targetFile);
	                    } else if (shellCallback.isOverwriteEnabled()) {
	                        source = gxf.getFormattedContent();
//	                        xmlOverwrite = true;
	                        warnings.add(getString("Warning.11", //$NON-NLS-1$
	                                targetFile.getAbsolutePath()));
	                    } else {
	                        source = gxf.getFormattedContent();
	                        targetFile = getUniqueFileName(directory, gxf
	                                .getFileName());
	                        warnings.add(getString(
	                                "Warning.2", targetFile.getAbsolutePath())); //$NON-NLS-1$
	                    }
	                } else {
	                    source = gxf.getFormattedContent();
	                    exists = false;
	                }
	            } catch (ShellException e) {
	                warnings.add(e.getMessage());
	                continue;
	            }
	            
	            callback.checkCancel();
	            callback.startTask(getString(
	                    "Progress.15", targetFile.getName())); //$NON-NLS-1$
	            //处理替换
	            source = source.replaceAll("\"record\\.", "\"");
	            source = source.replaceAll("\\{record\\.", "\\{");
//	            source = source.replaceAll("\"bean\\.", "\"");
//	            source = source.replaceAll("\\{bean\\.", "\\{");

	//            source = source.replaceAll("  ", "");
	//            source = source.replaceAll("ByExample", "");
	//            source = source.replaceAll("Example", "");
	            Document documentNew = null;
	            Document documentOld = null;
	            try {
					Context context = configuration.getContext("mysql-mybatis");
					TableConfiguration tableConfiguration = null;
					for(TableConfiguration tc : context.getTableConfigurations()){
						tableConfiguration = tc;
						break;
					}

	            	String commonFilePath = targetFile.getPath().replaceAll("Mapper.xml", "CommonMapper.xml");
	            	File commonFile = new File(commonFilePath);
	            	Document documentCommon=DocumentHelper.createDocument();//建立document对象，用来操作xml文件
	            	documentCommon.addDocType("mapper", "-//mybatis.org//DTD Mapper 3.0//EN","http://mybatis.org/dtd/mybatis-3-mapper.dtd");
	            	documentNew=Dom4jUtil.read(source);
	            	Element root = documentCommon.addElement("mapper");// 创建根节点
	            	String namespace = gxf.getFileName().replaceAll("Mapper.xml", "Common");
	            	root.addAttribute("namespace", namespace);
	            	
	            	//<sql>标签里面不能含<id><result>标签，故取消操作
	//            	Element eleBean = Dom4jUtil.chang(documentNew, "/mapper/resultMap[@id='BaseResultMap']", namespace, "bean");
	//            	root.add(eleBean);
	            	Node resultMapNode = documentNew.selectSingleNode("/mapper/resultMap[@id='"+ tableConfiguration.getProperty("conf_BaseResultMap")+"']");
	            	resultMapNode.setText("<!-- 此标签(BaseResultMap)是自动生成最新的，请勿修改 -->");
	            	
	            	Element eleWhere = Dom4jUtil.chang(documentNew, "/mapper/sql[@id='"+ tableConfiguration.getProperty("conf_Where_Clause")+"']/where", namespace, "where");
	            	Element eleWhereIf = eleWhere.addElement("if");
	            	eleWhereIf.addAttribute("test", "extendSql != null");
	            	eleWhereIf.addText("and ${extendSql}");
//	            	Element eleWhereOrderBy = eleWhere.addElement("if");
//	            	eleWhereOrderBy.addAttribute("test", "(orderBy != null and sord == null) or (orderBy != null and sord == 'asc') or (orderBy != null and sord == 'desc')");
//	            	eleWhereOrderBy.addText(" order by ${orderBy} ${sord}");
	            	Element eleWhereIds = eleWhere.addElement("if");
	            	eleWhereIds.addAttribute("test", "ids != null");
	            	eleWhereIds.addText("and id in");
	            	
	            	Element eleWhereIdsForeach = eleWhereIds.addElement("foreach");
	            	eleWhereIdsForeach.addAttribute("item", "item");
	            	eleWhereIdsForeach.addAttribute("collection", "ids");
	            	eleWhereIdsForeach.addAttribute("separator", ",");
	            	eleWhereIdsForeach.addAttribute("open", "(");
	            	eleWhereIdsForeach.addAttribute("close", ")");
	            	eleWhereIdsForeach.addAttribute("index", "");
	            	eleWhereIdsForeach.addText("#{item, jdbcType=NUMERIC}");
	            	
	            	root.add(eleWhere);



	            	Element eleColumn = Dom4jUtil.chang(documentNew, "/mapper/sql[@id='"+ tableConfiguration.getProperty("conf_Base_Column_List")+"']", namespace, "column");
	            	root.add(eleColumn);
	            	
	            	Element eleInsert = Dom4jUtil.chang(documentNew, "/mapper/insert[@id='"+ tableConfiguration.getProperty("conf_insert")+"']", namespace, "insert");
	            	root.add(eleInsert);
	            	
	            	Element eleInsertSelective = Dom4jUtil.chang(documentNew, "/mapper/insert[@id='"+ tableConfiguration.getProperty("conf_insertSelective")+"']", namespace, "insertSelective");
	            	root.add(eleInsertSelective);
	            	
	            	Element eleUpdate = Dom4jUtil.chang(documentNew, "/mapper/update[@id='"+ tableConfiguration.getProperty("conf_update")+"']", namespace, "update");
	            	root.add(eleUpdate);
	            	
	            	Element eleUpdateSelective = Dom4jUtil.chang(documentNew, "/mapper/update[@id='"+ tableConfiguration.getProperty("conf_updateSelective")+"']", namespace, "updateSelective");
	            	root.add(eleUpdateSelective);
	            	
	            	Element eleUpdatePKS = Dom4jUtil.chang(documentNew, "/mapper/update[@id='"+ tableConfiguration.getProperty("conf_updateByPrimaryKeySelective")+"']", namespace, "updatePKSelective");
	            	if(eleUpdatePKS!=null){
	            		root.add(eleUpdatePKS);
	            	}
	            	
	            	Element eleUpdatePK = Dom4jUtil.chang(documentNew, "/mapper/update[@id='"+ tableConfiguration.getProperty("conf_updateByPrimaryKey")+"']", namespace, "updatePK");
	            	if(eleUpdatePK!=null){
	            		root.add(eleUpdatePK);
	            	}
	            	
	            	if(CodeFactory.isOverrideMapperXML && exists){
	            		//提取公共信息并覆盖原文件的公共信息
	            		documentOld = Dom4jUtil.read(targetFile);
	            		Dom4jUtil.replace(documentNew, "/mapper/resultMap[@id='"+ tableConfiguration.getProperty("conf_BaseResultMap")+"']", documentOld, "/mapper/resultMap[@id='"+ tableConfiguration.getProperty("conf_BaseResultMap")+"']");
	            		Dom4jUtil.merger(documentNew, documentOld);
	            		formatXmlWriter(targetFile, Dom4jUtil.formatDocument(documentOld), "UTF-8");
	//            		System.out.println(Dom4jUtil.formatDocument(documentNew));
	            	}else{
	            		//提取公共信息,第一次生成执行
	            		formatXmlWriter(targetFile, Dom4jUtil.formatDocument(documentNew), "UTF-8");
	            	}
	            	//公共的xml文件无论怎样都会生成最新的
	            	Dom4jUtil.formatXmlFormatWriter(commonFile, documentCommon, "UTF-8");
				} catch (Exception e) {
					System.out.println("解析mybatis自动生成的mapper XML文件内容出错！");
					e.printStackTrace();
				}
            }
//            writeFile(targetFile, source, "UTF-8"); //$NON-NLS-1$
//            formatXmlWriter(targetFile, documentOld, "UTF-8");
        }

        //判断是否覆盖实体类与dao操作类
        for (GeneratedJavaFile gjf : generatedJavaFiles) {
        	
            projects.add(gjf.getTargetProject());

            File targetFile;
            String source;
            String fileType = "ALL";
            boolean exists = true;
            if(gjf.getFileName().endsWith("Mapper.java")){
            	fileType = "SERVICE";
            	if(!CodeFactory.isServiceModel){
            		continue;
            	}
            }else{
            	fileType = "COMMON";
            	if(!CodeFactory.isCommonModel){
            		continue;
            	}
            }
            try {
                File directory = shellCallback.getDirectory(gjf
                        .getTargetProject(), gjf.getTargetPackage());
                targetFile = new File(directory, gjf.getFileName());
                
                if (targetFile.exists()) {
                    if (shellCallback.isMergeSupported()) {
                        source = shellCallback.mergeJavaFile(gjf
                                .getFormattedContent(), targetFile
                                .getAbsolutePath(),
                                MergeConstants.OLD_ELEMENT_TAGS,
                                gjf.getFileEncoding());
                    } else if (shellCallback.isOverwriteEnabled()) {
                    	source = shellCallback.mergeJavaFile(gjf
                                .getFormattedContent(), targetFile
                                .getAbsolutePath(),
                                MergeConstants.OLD_ELEMENT_TAGS,
                                gjf.getFileEncoding());
//                        source = gjf.getFormattedContent();
                        warnings.add(getString("Warning.11", //$NON-NLS-1$
                                targetFile.getAbsolutePath()));
                    } else {
                        source = gjf.getFormattedContent();
                        targetFile = getUniqueFileName(directory, gjf
                                .getFileName());
                        warnings.add(getString(
                                "Warning.2", targetFile.getAbsolutePath())); //$NON-NLS-1$
                    }
                } else {
                    source = gjf.getFormattedContent();
                    exists = false;
//                    if("COMMON".equals(fileType)){
//                    	CodeFactory.isCommonModel=true;
//                    }else{
//                    	CodeFactory.isServiceModel=true;
//                    }
                }

                callback.checkCancel();
                callback.startTask(getString(
                        "Progress.15", targetFile.getName())); //$NON-NLS-1$
                
                //处理替换类型
//                source = source.replaceAll("java.math.BigDecimal;", "java.lang.Integer;");
//                source = source.replaceAll("BigDecimal", "Integer");
                if(exists){
                	if("COMMON".equals(fileType)){
                		if(CodeFactory.isCommonModel && CodeFactory.isOverrideModel){
                			writeFile(targetFile, source, gjf.getFileEncoding());
                		}
                	}else{
                		if(CodeFactory.isServiceModel && CodeFactory.isOverrideMapper){
                			writeFile(targetFile, source, gjf.getFileEncoding());
                		}
                	}
                }else{
                	if("COMMON".equals(fileType)){
                		if(CodeFactory.isCommonModel){
                			writeFile(targetFile, source, gjf.getFileEncoding());
                		}
                	}else{
                		if(CodeFactory.isServiceModel){
                			writeFile(targetFile, source, gjf.getFileEncoding());
                		}
                	}
                }
            } catch (ShellException e) {
                warnings.add(e.getMessage());
            }
        }

        for (String project : projects) {
            shellCallback.refreshProject(project);
        }

        callback.done();
    }
    
    /**
     * Writes, or overwrites, the contents of the specified file
     * 
     * @param file
     * @param content
     */
    private void writeFile(File file, String content, String fileEncoding) throws IOException {
        FileOutputStream fos = new FileOutputStream(file, false);
        OutputStreamWriter osw;
        if (fileEncoding == null) {
            osw = new OutputStreamWriter(fos);
        } else {
            osw = new OutputStreamWriter(fos, fileEncoding);
        }
        
        BufferedWriter bw = new BufferedWriter(osw);
        bw.write(content);
        bw.close();
    }

    private File getUniqueFileName(File directory, String fileName) {
        File answer = null;

        // try up to 1000 times to generate a unique file name
        StringBuilder sb = new StringBuilder();
        for (int i = 1; i < 1000; i++) {
            sb.setLength(0);
            sb.append(fileName);
            sb.append('.');
            sb.append(i);

            File testFile = new File(directory, sb.toString());
            if (!testFile.exists()) {
                answer = testFile;
                break;
            }
        }

        if (answer == null) {
            throw new RuntimeException(getString(
                    "RuntimeError.3", directory.getAbsolutePath())); //$NON-NLS-1$
        }

        return answer;
    }

	public Configuration getConfiguration() {
		return configuration;
	}

	public void setConfiguration(Configuration configuration) {
		this.configuration = configuration;
	}

	public ShellCallback getShellCallback() {
		return shellCallback;
	}

	public void setShellCallback(ShellCallback shellCallback) {
		this.shellCallback = shellCallback;
	}

	public List<GeneratedJavaFile> getGeneratedJavaFiles() {
		return generatedJavaFiles;
	}

	public void setGeneratedJavaFiles(List<GeneratedJavaFile> generatedJavaFiles) {
		this.generatedJavaFiles = generatedJavaFiles;
	}

	public List<GeneratedXmlFile> getGeneratedXmlFiles() {
		return generatedXmlFiles;
	}

	public void setGeneratedXmlFiles(List<GeneratedXmlFile> generatedXmlFiles) {
		this.generatedXmlFiles = generatedXmlFiles;
	}

	public List<String> getWarnings() {
		return warnings;
	}

	public void setWarnings(List<String> warnings) {
		this.warnings = warnings;
	}

	public Set<String> getProjects() {
		return projects;
	}

	public void setProjects(Set<String> projects) {
		this.projects = projects;
	}
	
	public void formatXmlWriter(File file, String source, String charSet){
		PrintWriter pw;
		try { 
			pw = new PrintWriter(file, charSet);
//			pw.println("<?xml version=\"1.0\" encoding=\"UTF-8\" ?>");
//			pw.println("<!DOCTYPE mapper PUBLIC \"-//mybatis.org//DTD Mapper 3.0//EN\" \"http://mybatis.org/dtd/mybatis-3-mapper.dtd\" >");
//			pw.println();
			pw.write(source);
			pw.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
    
}

class IgnoreDTDEntityResolver implements EntityResolver {

	@Override
	public InputSource resolveEntity(String publicId, String systemId)
	  throws SAXException, IOException {
	       return new InputSource(new ByteArrayInputStream("<?xml version='1.0' encoding='UTF-8'?>".getBytes()));
	}

}