package com.example.task_management_system.service;

import com.example.task_management_system.entity.User;
import com.example.task_management_system.repository.UserRepository;
import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
@Schema(description = "Класс для загрузки пользователей из базы данных")
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Schema(description = "Загружает пользователя из БД по email. username соответствует email в базе данных.")
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));

        return new org.springframework.security.core.userdetails.User(
                user.getEmail(),
                user.getPassword(),
                Collections.singleton(() -> user.getRole())
        );
    }
}