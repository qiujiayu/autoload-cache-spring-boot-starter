package com.jarvis.cache;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.jarvis.cache.autoconfigure.enable.EnableAutoloadCache;

@SpringBootApplication
@EnableAutoloadCache
public class Appliection {

    public static void main(String[] args) {
        SpringApplication.run(Appliection.class, args);
    }
}
