package com.usian;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * @ClassName : ContentServiceApp
 * @Author : lenovo
 * @Date: 2021/1/7 11:35
 */
@SpringBootApplication
@EnableDiscoveryClient
@MapperScan("com.usian.mapper")
public class ContentServiceApp {
    public static void main(String[] args) {
        SpringApplication.run(ContentServiceApp.class,args);
    }
}
