package com.sheruba.research_agent.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("AI Research Agent API")
                        .version("1.0.0")
                        .description("Production-grade AI Research Agent REST API with web search, source extraction, and AI-powered report synthesis.")
                        .contact(new Contact()
                                .name("Sheruba Research Team")
                                .email("dev@sheruba.com"))
                        .license(new License()
                                .name("Apache 2.0")
                                .url("https://springdoc.org")));
    }
}
