package com.loopers;

import jakarta.annotation.PostConstruct;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.util.TimeZone;

@ConfigurationPropertiesScan
@SpringBootApplication
@EnableScheduling
public class CommerceApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(CommerceApiApplication.class, args);
    }

    @PostConstruct
    public void started() {
        // set timezone
        TimeZone.setDefault(TimeZone.getTimeZone("Asia/Seoul"));
    }
}
