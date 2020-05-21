package org.basecode.common.generator.mybatis.generator;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.basecode.common.generator.mybatis.generator.api.MyBatisGenerator;
import org.basecode.common.generator.mybatis.generator.config.Configuration;
import org.basecode.common.generator.mybatis.generator.config.xml.ConfigurationParser;
import org.basecode.common.generator.mybatis.generator.internal.DefaultShellCallback;
import org.junit.Test;

import org.basecode.common.generator.mybatis.generator.exception.InvalidConfigurationException;

public class MyBatisGeneratorTest {

    @Test(expected=InvalidConfigurationException.class)
    public void testGenerateMyBatis3() throws Exception {
	   List<String> warnings = new ArrayList<String>();
	   boolean overwrite = true;
	   File configFile = new File("d:/auto/generatorConfigMyBatis3.xml");
	   ConfigurationParser cp = new ConfigurationParser(warnings);
	   Configuration config = cp.parseConfiguration(configFile);
	   DefaultShellCallback callback = new DefaultShellCallback(overwrite);
	   MyBatisGenerator myBatisGenerator = new MyBatisGenerator(config, callback, warnings);
	   myBatisGenerator.generate(null);
    }

//    @Test(expected=InvalidConfigurationException.class)
//    public void testGenerateIbatis2() throws Exception {
//        List<String> warnings = new ArrayList<String>();
//        ConfigurationParser cp = new ConfigurationParser(warnings);
//        Configuration config = cp.parseConfiguration(this.getClass().getClassLoader().getResourceAsStream("generatorConfigIbatis2.xml"));
//            
//        DefaultShellCallback shellCallback = new DefaultShellCallback(true);
//        
//        try {
//            MyBatisGenerator myBatisGenerator = new MyBatisGenerator(config, shellCallback, warnings);
//            myBatisGenerator.generate(null);
//        } catch (InvalidConfigurationException e) {
//            assertEquals(1, e.getErrors().size());
//            throw e;
//        }
//    }
}
