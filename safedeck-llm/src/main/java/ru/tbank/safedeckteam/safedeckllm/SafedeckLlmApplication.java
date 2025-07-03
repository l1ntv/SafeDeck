package ru.tbank.safedeckteam.safedeckllm;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
public class SafedeckLlmApplication {

    public static void main(String[] args) {
        SpringApplication.run(SafedeckLlmApplication.class, args);
    }

}
