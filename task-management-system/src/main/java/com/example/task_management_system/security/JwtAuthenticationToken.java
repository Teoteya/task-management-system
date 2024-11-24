package com.example.task_management_system.security;

import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;

@Schema(description = "Кастомный класс для токена аутентификации.")
public class JwtAuthenticationToken extends AbstractAuthenticationToken {

    private final UserDetails principal;
    private final Object credentials;

    public JwtAuthenticationToken(UserDetails principal, Object credentials) {
        super(principal.getAuthorities());
        this.principal = principal;
        this.credentials = credentials;
        setAuthenticated(true); // Можно установить, если аутентификация прошла успешно
    }

    @Override
    public Object getCredentials() {
        return credentials;
    }

    @Override
    public Object getPrincipal() {
        return principal;
    }
}