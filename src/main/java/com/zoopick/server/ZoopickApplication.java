package com.zoopick.server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@EnableAsync
@SpringBootApplication
public class ZoopickApplication {
    public static void main(String[] args) {
        SpringApplication.run(ZoopickApplication.class, args);
    }
}
