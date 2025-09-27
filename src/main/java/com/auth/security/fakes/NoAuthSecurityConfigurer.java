package com.auth.security.fakes;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;


@Configuration
@EnableWebSecurity
@Profile("no-auth")
public class NoAuthSecurityConfigurer {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        // Ne fait rien, toutes les requêtes sont autorisées sans authentification
        http.csrf(AbstractHttpConfigurer::disable).authorizeHttpRequests(authorizeRequests ->
                authorizeRequests.anyRequest().permitAll());
        return http.build();
    }
}
