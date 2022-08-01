package com.jgdabc;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@EnableTransactionManagement
@Slf4j
//加入以后可以是有log
@SpringBootApplication
/*
在SpringBootApplication上使用@ServletComponentScan注解后，
Servlet、Filter、Listener可以直接通过@WebServlet、@WebFilter、@WebListener注解自动注册，无需其他代码。
 */
@ServletComponentScan
public class ReggleDemoJgdabcApplication {

    public static void main(String[] args) {
        log.info("项目启动成功");

        SpringApplication.run(ReggleDemoJgdabcApplication.class, args);
    }

}
