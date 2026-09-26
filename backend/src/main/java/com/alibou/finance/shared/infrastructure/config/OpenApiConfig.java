package com.alibou.finance.shared.infrastructure.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.security.SecuritySchemes;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;

import java.util.ArrayList;


@Configuration
@OpenAPIDefinition(
        info = @Info(
                contact = @Contact(
                        name = "Rivohery",
                        email = "rivohery.randrian@gmail.com"
                ),
                description = "OpenApi documentation for Micro-finance Management System",
                title = "OpenApi specification - Rivohery",
                version = "1.0"
        ),
        servers = {
                @Server(
                        description = "Local ENV",
                        url = "http://localhost:8088/api/v1"
                )
        },
        security = {
                @SecurityRequirement(name = "cookieAuth"),
                @SecurityRequirement(name = "xsrfToken")
        }
)
@SecuritySchemes({
        @SecurityScheme(
                name = "cookieAuth",
                description = "Cookie JWT (HttpOnly)",
                type = SecuritySchemeType.APIKEY,
                in = SecuritySchemeIn.COOKIE,
                paramName = "accessToken"
        ),
        @SecurityScheme(
                name = "xsrfToken",
                description = "Jeton CSRF (nécessaire pour les requêtes POST/PUT/DELETE)",
                type = SecuritySchemeType.APIKEY,
                in = SecuritySchemeIn.COOKIE,
                paramName = "XSRF-TOKEN"
        )
})
public class OpenApiConfig {
        /**
         * Cette configuration est nécessaire pour permettre à Swagger UI d'envoyer
         * des parties JSON dans une requête multipart. Sans cela, Swagger UI omet
         * l'en-tête Content-Type: application/json et Spring reçoit la partie
         * avec le type par défaut 'application/octet-stream', ce qui provoque une
         * erreur 415 Unsupported Media Type.
         */
        public OpenApiConfig(MappingJackson2HttpMessageConverter converter) {
                var supportedMediaTypes = new ArrayList<>(converter.getSupportedMediaTypes());
                // Ajout du support pour 'application/octet-stream' afin de compenser
                // le bug de génération de requête de Swagger UI.
                supportedMediaTypes.add(new MediaType("application", "octet-stream"));
                converter.setSupportedMediaTypes(supportedMediaTypes);
        }
}
