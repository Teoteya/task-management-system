package com.example.task_management_system.controller;

import com.example.task_management_system.dto.AuthRequestDto;
import com.example.task_management_system.dto.AuthResponseDto;
import com.example.task_management_system.dto.RegisterRequestDto;
import com.example.task_management_system.service.AuthService;
import javax.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
@RestController
@RequestMapping("/auth")
@Tag(name = "Auth API", description = "Управление регистрацией и аутентификацией пользователей")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }
    @Operation(summary = "Регистрация пользователя", description = "Позволяет зарегистрировать нового пользователя")
    @PostMapping("/register")
    public ResponseEntity<String> register(@Valid @RequestBody RegisterRequestDto request) {
        authService.register(request);
        return ResponseEntity.ok("Пользователь успешно зарегистрирован");
    }
    @Operation(summary = "Авторизация пользователя", description = "Возвращает JWT токен для аутентифицированного пользователя")
    @PostMapping("/login")
    public ResponseEntity<AuthResponseDto> login(@Valid @RequestBody AuthRequestDto request) {
        return ResponseEntity.ok(authService.login(request));
    }

}
