package com.example.task_management_system;

import com.example.task_management_system.dto.AuthRequestDto;
import com.example.task_management_system.dto.AuthResponseDto;
import com.example.task_management_system.dto.RegisterRequestDto;
import com.example.task_management_system.entity.User;
import com.example.task_management_system.exception.CustomException;
import com.example.task_management_system.repository.UserRepository;
import com.example.task_management_system.security.JwtTokenProvider;
import com.example.task_management_system.service.AuthService;
import io.swagger.v3.oas.annotations.media.Schema;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @InjectMocks
    private AuthService authService;

    private User user;
    private AuthRequestDto authRequest;
    private RegisterRequestDto registerRequest;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // Инициализация тестовых данных
        user = new User();
        user.setId(1L);
        user.setEmail("test@example.com");
        user.setPassword("encoded_password");
        user.setRole("ROLE_USER");

        authRequest = new AuthRequestDto();
        authRequest.setEmail("test@example.com");
        authRequest.setPassword("password");

        registerRequest = new RegisterRequestDto();
        registerRequest.setEmail("test@example.com");
        registerRequest.setPassword("password");
        registerRequest.setRole("ROLE_USER");
    }

    @Schema(description = "Проверка успешной регистрации нового пользователя. " +
            "Мы мокируем поведение репозитория, чтобы метод existsByEmail возвращал false, и затем вызываем register. " +
            "Ожидаем, что пользователь будет сохранен в репозитории")
    @Test
    void testRegister_Success() {
        // Мокаем поведение репозитория
        when(userRepository.existsByEmail(registerRequest.getEmail())).thenReturn(false);
        when(passwordEncoder.encode(registerRequest.getPassword())).thenReturn("encoded_password");

        authService.register(registerRequest);

        verify(userRepository).save(any(User.class));
    }

    @Schema(description = "Проверка регистрации пользователя, если email уже существует. " +
            "Метод existsByEmail возвращает true, и тогда выбрасывается исключение CustomException " +
            "с соответствующим сообщением и статусом CONFLICT")
    @Test
    void testRegister_UserAlreadyExists() {
        // Мокаем поведение репозитория
        when(userRepository.existsByEmail(registerRequest.getEmail())).thenReturn(true);

        CustomException exception = assertThrows(CustomException.class, () -> authService.register(registerRequest));

        assertEquals("User with this email already exists", exception.getMessage());
        assertEquals(HttpStatus.CONFLICT, exception.getStatus());
    }

    @Schema(description = "Проверка успешного входа. " +
            "Мокаем репозиторий, чтобы он возвращал пользователя по email, и проверяем, что пароли совпадают. " +
            "Далее проверяем, что токен JWT генерируется корректно")
    @Test
    void testLogin_Success() {
        // Мокаем поведение репозитория и другие сервисы
        when(userRepository.findByEmail(authRequest.getEmail())).thenReturn(java.util.Optional.of(user));
        when(passwordEncoder.matches(authRequest.getPassword(), user.getPassword())).thenReturn(true);
        when(jwtTokenProvider.generateToken(user.getEmail(), user.getRole())).thenReturn("jwt_token");

        AuthResponseDto response = authService.login(authRequest);

        assertNotNull(response);
        assertEquals("jwt_token", response.getToken());

        verify(userRepository).findByEmail(authRequest.getEmail());
        verify(passwordEncoder).matches(authRequest.getPassword(), user.getPassword());
        verify(jwtTokenProvider).generateToken(user.getEmail(), user.getRole());
    }

    @Schema(description = "Проверка входа с неверным email. " +
            "Если репозиторий не находит пользователя по указанному email, " +
            "выбрасывается исключение CustomException с сообщением \"Invalid email or password\" и статусом UNAUTHORIZED")
    @Test
    void testLogin_InvalidEmail() {
        // Мокаем поведение репозитория
        when(userRepository.findByEmail(authRequest.getEmail())).thenReturn(java.util.Optional.empty());

        CustomException exception = assertThrows(CustomException.class, () -> authService.login(authRequest));

        assertEquals("Invalid email or password", exception.getMessage());
        assertEquals(HttpStatus.UNAUTHORIZED, exception.getStatus());
    }

    @Schema(description = "Проверка входа с неверным паролем. " +
            "Если пароли не совпадают, выбрасывается исключение CustomException с тем же сообщением и статусом UNAUTHORIZED")
    @Test
    void testLogin_InvalidPassword() {
        // Мокаем поведение репозитория
        when(userRepository.findByEmail(authRequest.getEmail())).thenReturn(java.util.Optional.of(user));
        when(passwordEncoder.matches(authRequest.getPassword(), user.getPassword())).thenReturn(false);

        CustomException exception = assertThrows(CustomException.class, () -> authService.login(authRequest));

        assertEquals("Invalid email or password", exception.getMessage());
        assertEquals(HttpStatus.UNAUTHORIZED, exception.getStatus());
    }
}