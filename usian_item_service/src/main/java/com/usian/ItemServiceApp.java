package com.usian;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * @ClassName : ItemServiceApp
 * @Author : lenovo
 * @Date: 2021/1/5 16:44
 */
@SpringBootApplication
@EnableDiscoveryClient
@MapperScan("com.usian.mapper")
public class ItemServiceApp {
    public static void main(String[] args) {
        SpringApplication.run(ItemServiceApp.class,args);
    }
}
