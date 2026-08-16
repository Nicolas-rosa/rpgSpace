package com.rpgspace.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    OpenAPI rpgSpaceOpenApi() {
        return new OpenAPI().info(new Info()
                .title("RPGSpace API")
                .version("v1")
                .description("API da plataforma RPGSpace."));
    }
}
