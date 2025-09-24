package com.example.dynamicconsent.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Dynamic Consent API")
                        .version("v1")
                        .description("""
                                Dynamic Consent Management System REST API
                                
                                ## Main Features
                                - Notice Management
                                - Organization List/Search
                                - User Profile/Settings
                                - Notification Convenience
                                - Consent Event Management
                                - Third-Party Sharing Graph
                                - Synchronization (Mobile Optimized)
                                - Authentication (Simple Cert, Public Cert, SMS)
                                - Risk Analysis
                                
                                ## Authentication
                                All requests require X-UserId header, and after successful login, 
                                Authorization header must be included.
                                """)
                        .contact(new Contact()
                                .name("Dynamic Consent Team")
                                .email("team@dynamicconsent.com")
                                .url("https://dynamicconsent.com"))
                        .license(new License()
                                .name("MIT License")
                                .url("https://opensource.org/licenses/MIT")))
                .servers(List.of(
                        new Server()
                                .url("http://localhost:8080")
                                .description("Development Server"),
                        new Server()
                                .url("https://api.dynamicconsent.com")
                                .description("Production Server")
                ))
                .components(new Components()
                        .addSecuritySchemes("BearerAuth", new SecurityScheme()
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")
                                .description("JWT Token Authentication"))
                        .addSecuritySchemes("UserIdHeader", new SecurityScheme()
                                .type(SecurityScheme.Type.APIKEY)
                                .in(SecurityScheme.In.HEADER)
                                .name("X-UserId")
                                .description("User ID Header")))
                .addSecurityItem(new SecurityRequirement()
                        .addList("BearerAuth")
                        .addList("UserIdHeader"));
    }
}