package com.example.task_management_system.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import com.example.task_management_system.entity.User;

import java.util.Arrays;
import java.util.Collection;

@Schema(description = "Класс, представляющий пользователя в контексте Spring Security")
public class CustomUserDetails implements UserDetails { // UserDetails можно использовать для извлечения
    // текущего аутентифицированного пользователя в любом сервисе

    private final User user;

    public CustomUserDetails(User user) {
        this.user = user;
    }

    @Schema(description = "Возвращаем роли пользователя, например, с помощью коллекции SimpleGrantedAuthority")
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Arrays.asList(new SimpleGrantedAuthority(user.getRole()));
    }

    @Schema(description = "Возвращаем пароль пользователя")
    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Schema(description = "Возвращаем email пользователя вместо username, при авторизации вводить email")
    @Override
    public String getUsername() {
        return user.getEmail();
    }

    @Schema(description = "Возвращаем, что аккаунт не истек")
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Schema(description = "Возвращаем, что аккаунт не заблокирован")
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Schema(description = "Возвращаем, что учетные данные не истекли")
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Schema(description = "Возвращаем, что аккаунт активен")
    @Override
    public boolean isEnabled() {
        return true;
    }

    public User getUser() {
        return user;
    }
}