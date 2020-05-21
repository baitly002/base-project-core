package org.basecode.core;
import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = {"org.basecode.core"})
@MapperScan("com.baomidou.mybatisplus.samples.quickstart.mapper")
@EnableDubbo
public class RunTest {

    public static void main(String[] args) {
        SpringApplication.run(RunTest.class, args);
    }

}