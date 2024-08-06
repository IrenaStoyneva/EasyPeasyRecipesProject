package com.softuni.easypeasyrecipes_app.config;

import com.softuni.easypeasyrecipes_app.service.JwtService;
import com.softuni.easypeasyrecipes_app.service.UserService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.client.RestClient;

import java.util.Map;

@Configuration
public class RestConfig {


    @Bean("commentsRestClient")
    public RestClient commentsRestClient(CommentApiConfig commentApiConfig,ClientHttpRequestInterceptor requestInterceptor) {
        return RestClient
                .builder()
                .baseUrl(commentApiConfig.getBaseUrl())
                .defaultHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                .requestInterceptor(requestInterceptor)
                .build();
    }

    @Bean
    public RestClient genericRestClient() {
        return RestClient.create();
    }

    @Bean
    public ClientHttpRequestInterceptor requestInterceptor(UserService userService, JwtService jwtService) {

        return (request, body, execution) -> {
            userService.getCurrentUser().ifPresent(user -> {
                String bearerToken = jwtService.generateToken(
                        user.getUuid().toString(),
                        Map.of(
                                "roles",
                                user.getAuthorities().stream().map(GrantedAuthority::getAuthority).toList(),
                                "username", user.getUsername()
                        )
                );

                request.getHeaders().setBearerAuth(bearerToken);
            });

            return execution.execute(request, body);
        };
    }
}
