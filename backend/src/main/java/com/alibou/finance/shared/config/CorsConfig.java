package com.alibou.finance.shared.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.util.Arrays;
import java.util.Collections;
@Configuration
public class CorsConfig {

    @Bean
    public CorsFilter corsFilter() {
        final UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        final CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(true);//Permet l'envoi/la réception de cookies
        config.setAllowedOrigins(Collections.singletonList("http://localhost:4200"));
        config.setAllowedHeaders(Arrays.asList(
                "Origin",
                "Content-Type",
                "Accept",
                "Authorization",
                "X-XSRF-TOKEN"
        ));
        config.setAllowedMethods(Arrays.asList(
                "GET",
                "POST",
                "DELETE",
                "PUT",
                "PATCH",
                "OPTIONS"
        ));
        source.registerCorsConfiguration("/**", config);
        return new CorsFilter(source);
    }
}

