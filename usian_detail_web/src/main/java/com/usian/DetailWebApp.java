package com.usian;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

import javax.xml.soap.Detail;

/**
 * @ClassName : DetailWebApp
 * @Author : lenovo
 * @Date: 2021/1/20 9:58
 */
@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients
public class DetailWebApp {
    public static void main(String[] args) {
        SpringApplication.run(DetailWebApp.class,args);
    }
}
