package com.utkarsh.journalApp.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.Components;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI journalAppOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Journal Application REST API")
                        .description(
                                "A comprehensive Spring Boot application for managing personal journal entries with authentication, role-based access control, and external API integrations.")
                        .version("1.0")
                        .contact(new Contact()
                                .name("Utkarsh Dubey")
                                .email("utkarshdubey0242@gmail.com"))
                        .license(new License()
                                .name("Apache 2.0")
                                .url("https://www.apache.org/licenses/LICENSE-2.0.html")))
                .components(new Components()
                        .addSecuritySchemes("basicAuth",
                                new SecurityScheme()
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("basic")
                                        .description("Enter your username and password")))
                .addSecurityItem(new SecurityRequirement().addList("basicAuth"));
    }
}
