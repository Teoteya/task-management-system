package com.example.task_management_system.config;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@Schema(description = "Класс конфигурации для настройки Swagger, для дополнительной кастомизации")
public class SwaggerConfig {

 /*   @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Task Management System API")
                        .version("1.0")
                        .description("Документация для API. API предоставляет доступ к управлению задачами и комментариями с использованием Spring Boot")
                        .contact(new Contact()
                                .name("Команда разработки приложения")
                                .email("teyarad@bk.ru"))
//                        .license(new License()
//                                .name("Apache 2.0")
//                                .url("http://www.apache.org/licenses/LICENSE-2.0.html"))
                );
    }  */
}