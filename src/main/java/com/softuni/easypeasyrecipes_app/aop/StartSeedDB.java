package com.softuni.easypeasyrecipes_app.aop;

import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;


@Aspect
@Configuration
public class StartSeedDB {
    private static final Logger log = LoggerFactory.getLogger(StartSeedDB.class);

    @Pointcut("execution(* com.softuni.easypeasyrecipes_app.init.DataInitializer.run(..))")
    void dataInitializer() {
    }

    @Before("dataInitializer()")
    public void beforeDataInitialization() {
        log.info("Data initialization started. Please check if the admin user and categories are correctly configured.");
    }
}
