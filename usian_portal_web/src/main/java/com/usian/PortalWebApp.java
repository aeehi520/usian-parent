package com.usian;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * @ClassName : PortalWebApp
 * @Author : lenovo
 * @Date: 2021/1/8 16:24
 */
@SpringBootApplication
@EnableFeignClients
@EnableDiscoveryClient
public class PortalWebApp {
    public static void main(String[] args) {
        SpringApplication.run(PortalWebApp.class,args);
    }
}
