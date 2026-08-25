package com.example.Insurance;

import com.example.Insurance.config.GoogleDriveProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties(GoogleDriveProperties.class)
public class ClaimApplication {
    public static void main(String[] args) {
        SpringApplication.run(ClaimApplication.class, args);
    }

}
