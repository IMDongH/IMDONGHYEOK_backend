package com.practice.core.api.config;


import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import java.util.List;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {
    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
            .info(new Info()
                .title("송금 서비스 API")
                .description("계좌 관리 및 송금 서비스 API 문서")
                .version("1.0.0"))
            .servers(List.of(
                new Server().url("http://localhost:8080").description("Local Server"),
                new Server().url("https://dev-api.example.com").description("Development Server")));
    }
}
