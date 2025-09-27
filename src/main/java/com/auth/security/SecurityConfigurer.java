package com.auth.security;

import com.td.aion.io.web.auth.security.filters.JwtRequestFilter;
import com.td.aion.io.web.auth.security.filters.TraceFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;


@Configuration
@EnableWebSecurity
@Profile("with-auth")
public class SecurityConfigurer {

    @Value("${cors.allowed_hosts}")
    private List<String> allowedHosts;

    private final JwtRequestFilter jwtFilter;
    private final TraceFilter traceFilter;
    private static final Logger logger = LoggerFactory.getLogger(SecurityConfigurer.class);
    private static final String ADMIN_ROLE = "ION_ADMIN";
    private static final String SIMPLE_USER_ROLE = "ION";

    public SecurityConfigurer(JwtRequestFilter jwtRequestFilter, TraceFilter traceFilter) {
        this.jwtFilter = jwtRequestFilter;
        this.traceFilter = traceFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(AbstractHttpConfigurer::disable)
                .addFilterBefore(this.jwtFilter, UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(this.traceFilter, JwtRequestFilter.class)
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        .requestMatchers("/customer/**").hasRole(ADMIN_ROLE)
                        .requestMatchers("/pack/**").hasAnyRole(SIMPLE_USER_ROLE, ADMIN_ROLE)
                        .requestMatchers("/ion_detector/**").hasAnyRole(SIMPLE_USER_ROLE, ADMIN_ROLE)
                        .requestMatchers("/antijammer/**").hasAnyRole(SIMPLE_USER_ROLE, ADMIN_ROLE)
                        .requestMatchers("/error").permitAll()
                        .requestMatchers("/**").permitAll()
                )
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .build();
    }

//    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        logger.info("------------------------------------------------------");
        logger.info("Loading CorsConfigurationSource");
        logger.info("------------------------------------------------------");
        logger.info(allowedHosts.toString());
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(allowedHosts); // ou "*" pour tout autoriser
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("*")); // ou liste précise
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        logger.info("-----------------------Loaded CorsConfigurationSource");
        return source;
    }

//    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
