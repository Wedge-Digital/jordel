package com;

import com.lib.services.MessageSourceConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;

//@Import(MessageSourceConfig.class)
//@SpringBootApplication
public class ShellApplication {
    static void main(String[] args) {
        SpringApplication.run(ShellApplication.class, args);
    }
}
