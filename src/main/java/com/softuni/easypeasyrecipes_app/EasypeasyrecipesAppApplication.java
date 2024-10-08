package com.softuni.easypeasyrecipes_app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@EnableConfigurationProperties
@SpringBootApplication
public class EasypeasyrecipesAppApplication {

    public static void main(String[] args) {
        SpringApplication.run(EasypeasyrecipesAppApplication.class, args);
    }


}
