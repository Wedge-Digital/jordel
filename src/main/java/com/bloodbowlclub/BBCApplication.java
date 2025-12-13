package com.bloodbowlclub;

import com.bloodbowlclub.lib.config.MessageSourceConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.annotation.Import;

@Import(MessageSourceConfig.class)
@SpringBootApplication
public class BBCApplication {

	public static void main(String[] args) {
		SpringApplication.run(BBCApplication.class, args);
	}

}
