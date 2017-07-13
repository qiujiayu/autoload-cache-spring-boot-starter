package com.jarvis.cache.demo;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.jarvis.cache.demo.mapper")
public class Appliection {

    public static void main(String[] args) {
        SpringApplication.run(Appliection.class, args);
    }
}
