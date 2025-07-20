package ru.tbank.safedeckteam.safedeckemailservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;

@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class, SecurityAutoConfiguration.class})
public class SafedeckEmailServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(SafedeckEmailServiceApplication.class, args);
    }
}
