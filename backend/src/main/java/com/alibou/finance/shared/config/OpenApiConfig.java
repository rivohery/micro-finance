package com.alibou.finance.shared.config;

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


//http://localhost:8088/api/v1/swagger-ui/index.html
@Configuration
@OpenAPIDefinition(
        info = @Info(
                contact = @Contact(
                        name = "Alibou",
                        email = "rivoherydev01@gmail.com"
                ),
                description = "OpenApi documentation for Micro-finance Management System",
                title = "OpenApi specification - Alibou",
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
}
