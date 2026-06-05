package com.cts;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@SpringBootApplication
@EnableAspectJAutoProxy   
public class Travel360endpoints3Application {

    public static void main(String[] args) {
        SpringApplication.run(Travel360endpoints3Application.class, args);
    }
}