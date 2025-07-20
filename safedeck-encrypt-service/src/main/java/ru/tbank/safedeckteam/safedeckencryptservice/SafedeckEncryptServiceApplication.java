package ru.tbank.safedeckteam.safedeckencryptservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;

@SpringBootApplication(exclude = {SecurityAutoConfiguration.class})
public class SafedeckEncryptServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(SafedeckEncryptServiceApplication.class, args);
    }

}
