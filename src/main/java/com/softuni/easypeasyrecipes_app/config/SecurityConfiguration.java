package com.softuni.easypeasyrecipes_app.config;

//package com.softuni.easypeasyrecipes_app.config;
////
//import com.softuni.easypeasyrecipes_app.service.session.UserDetailService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
////import org.springframework.security.config.annotation.web.builders.HttpSecurity;
////import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
//import org.springframework.security.config.annotation.web.builders.HttpSecurity;
//import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
//import org.springframework.security.web.SecurityFilterChain;
//
//import static org.springframework.security.config.Customizer.withDefaults;
//
////import org.springframework.security.web.SecurityFilterChain;
////
@Configuration
//@EnableWebSecurity
public class SecurityConfiguration {
//
//    private final UserDetailService userDetailService;
//
//    public SecurityConfiguration(UserDetailService userDetailService) {
//        this.userDetailService = userDetailService;
//    }
//
//    @Bean
//    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
//        http
//                .authorizeRequests(authorizeRequests ->
//                        authorizeRequests
//                                .requestMatchers("/create/recipe", "/css/**", "/js/**").permitAll()
//                                .anyRequest().authenticated()
//                )
//                .formLogin(formLogin ->
//                        formLogin
//                                .loginPage("/login")
//                                .permitAll()
//                )
//                .logout(logout ->
//                        logout
//                                .permitAll()
//                );
//        return http.build();
//    }
//    @Bean
//    public UserDetailService userDetailsService() {
//        return userDetailService;
//    }
//
    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
