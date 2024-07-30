package com.softuni.easypeasyrecipes_app.config;



import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestClient;

@Configuration
public class RestConfig {

    @Bean("commentsRestClient")
    public RestClient commentsRestClient(CommentApiConfig commentApiConfig) {
        return RestClient
                .builder()
                .baseUrl(commentApiConfig.getBaseUrl())
                .defaultHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                .build();
    }

    @Bean
    public RestClient genericRestClient() {
    return RestClient.create();
    }
}