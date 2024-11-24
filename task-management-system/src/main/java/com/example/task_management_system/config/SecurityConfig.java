package com.example.task_management_system.config;

import com.example.task_management_system.security.JwtAuthenticationFilter;
import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@SpringBootApplication
@EnableWebSecurity
@EnableMethodSecurity
@Schema(description = "Настройка фильтров, ролей и прав доступа")
public class SecurityConfig {
    private final JwtAuthenticationFilter jwtAuthenticationFilter;


    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    @Schema(description = "Основной метод конфигурации безопасности")
    @Bean
    public SecurityFilterChain securityFilterChain(org.springframework.security.config.annotation.web.builders.HttpSecurity http) throws Exception {
        http    .csrf().disable()
                .authorizeRequests() // Открытые маршруты
                .antMatchers("/login", "/register").permitAll()
                .anyRequest().authenticated()  // Остальные маршруты требуют аутентификации
                .and()
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .formLogin()
                .defaultSuccessUrl("/tasks/all", true)
                .permitAll()
                .and()
                .logout()
                .logoutUrl("/logout")
                .permitAll()
                .and()
                .httpBasic();

        return http.build();
    }

    @Schema(description = "Настройка менеджера аутентификации")
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Schema(description = "Настройка шифрования паролей. Использование BCrypt для кодирования паролей")
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }


}