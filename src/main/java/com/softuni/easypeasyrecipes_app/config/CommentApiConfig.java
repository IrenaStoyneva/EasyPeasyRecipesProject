package com.softuni.easypeasyrecipes_app.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "comments.api")

public class CommentApiConfig {
    private String baseUrl;

    public String getBaseUrl() {
        return baseUrl;
    }

    public CommentApiConfig setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
        return this;
    }
}
