package com.softuni.easypeasyrecipes_app.aop;

import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;

@Aspect
@Configuration
public class UserRegistrationAspect {

    private static final Logger log = LoggerFactory.getLogger(UserRegistrationAspect.class);

    @Pointcut("execution(* com.softuni.easypeasyrecipes_app.service.impl.UserServiceImpl.registerUser(..))")
    void userRegister() {
    }

    @Before("userRegister()")
    public void logBeforeUserRegistration() {
        log.info("User registration process started.");
    }
}