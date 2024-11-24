package com.example.task_management_system.exception;

import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.http.HttpStatus;
import lombok.Builder;
import lombok.Value;

@Value

@Schema(description = "Кастомное исключение для обработки специфичных ошибок в приложении")
public class CustomException extends RuntimeException {

    HttpStatus status;

    public CustomException(String message, HttpStatus status) {
        super(message);
        this.status = status;
    }

    public HttpStatus getStatus() {
        return status;
    }
}