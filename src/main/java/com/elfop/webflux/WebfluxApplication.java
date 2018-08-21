package com.elfop.webflux;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class WebfluxApplication {

    public static void main(String[] args) {
        SpringApplication app = new SpringApplication(WebfluxApplication.class);
        app.run(args);
    }
}
