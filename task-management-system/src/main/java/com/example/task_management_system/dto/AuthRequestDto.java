package com.example.task_management_system.dto;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import io.swagger.v3.oas.annotations.media.Schema;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Schema(description = "Запрос для аутентификации пользователя. Класс используется для передачи данных аутентификации (email и пароль) от клиента к серверу")
public class AuthRequestDto {

    @Schema(description = "Email пользователя", example = "user@example.com", required = true)
    @Email(message = "Неверный формат email")
    @NotBlank(message = "Email обязателен")
    String email;

    @Schema(description = "Пароль пользователя", example = "password123", required = true)
    @NotBlank(message = "Пароль обязателен")
    String password;

}