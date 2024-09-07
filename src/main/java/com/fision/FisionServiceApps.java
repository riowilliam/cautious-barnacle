package com.fision;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.util.List;

/**
 * @author LordDev
 */
@SpringBootApplication
@EnableScheduling
public class FisionServiceApps {
    public static void main(String[] args) {
        SpringApplication.run(FisionServiceApps.class, args);
    }
}