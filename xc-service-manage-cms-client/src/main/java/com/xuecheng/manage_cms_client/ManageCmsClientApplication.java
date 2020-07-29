package com.xuecheng.manage_cms_client;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;

/**
 * @author WangPan
 * @date 2020/7/28 15:57
 */
@SpringBootApplication
@ComponentScan(basePackages = {"com.xuecheng.**"})
@EntityScan("com.xuecheng.framework.domain.cms")
public class ManageCmsClientApplication {
    public static void main(String[] args) {
        SpringApplication.run(ManageCmsClientApplication.class, args);
    }
}
