package io.app.my_app.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.models.OpenAPI;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.StringUtils;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;

import java.util.ArrayList;
import java.util.List;

@OpenAPIDefinition
@Configuration
@Slf4j
public class OpenApiConfig {
    @Bean
    public OpenAPI customOpenAPI(
            @Value("${openapi.service.title}") String serviceTitle,
            @Value("${openapi.service.version}") String serviceVersion,
            @Value("${openapi.service.url:}") String url) {
        final String securitySchemeName = "bearerAuth";

        try {
            OpenAPI openAPI = new OpenAPI()
                    .components(
                            new Components()
                                    .addSecuritySchemes(
                                            securitySchemeName,
                                            new SecurityScheme()
                                                    .type(SecurityScheme.Type.HTTP)
                                                    .scheme("bearer")
                                                    .bearerFormat("JWT")))
                    .security(List.of(new SecurityRequirement().addList(securitySchemeName)))
                    .info(new Info().title(serviceTitle).version(serviceVersion));

            if (StringUtils.hasText(url)) {
                try {
                    openAPI.setServers(List.of(new Server().url(url)));
                } catch (Exception e) {
                    log.warn("Invalid openapi.service.url='{}'. Skipping server configuration: {}", url, e.getMessage());
                    openAPI.setServers(new ArrayList<>());
                }
            } else {
                log.info("openapi.service.url not set or blank; Swagger UI will still work but server URL is not configured");
            }

            return openAPI;
        } catch (Exception ex) {
            log.error("Failed to build OpenAPI definition: {}", ex.getMessage(), ex);
            OpenAPI fallback = new OpenAPI().info(new Info().title(serviceTitle != null ? serviceTitle : "API").version(serviceVersion != null ? serviceVersion : "1.0.0"));
            return fallback;
        }
    }
}