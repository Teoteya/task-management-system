package com.example.task_management_system;

import com.example.task_management_system.dto.TaskRequestDto;
import com.example.task_management_system.dto.TaskResponseDto;
import com.example.task_management_system.entity.Task;
import com.example.task_management_system.entity.User;
import com.example.task_management_system.exception.CustomException;
import com.example.task_management_system.repository.TaskRepository;
import com.example.task_management_system.repository.UserRepository;
import com.example.task_management_system.service.TaskService;
import io.swagger.v3.oas.annotations.media.Schema;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class TaskServiceTest {

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private TaskService taskService;

    private User author;
    private User assignee;
    private Task task;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // Инициализация тестовых данных
        author = new User();
        author.setId(1L);
        author.setEmail("author@example.com");

        assignee = new User();
        assignee.setId(2L);
        assignee.setEmail("assignee@example.com");

        task = new Task();
        task.setId(1L);
        task.setTitle("Test Task");
        task.setDescription("Task Description");
        task.setStatus("Pending");
        task.setPriority("High");
        task.setAuthor(author);
        task.setAssignee(assignee);
    }

    @Schema(description = "Проверяет создание задачи с корректными данными")
    @Test
    void testCreateTask() {
        // Мокаем ответы от репозиториев
        TaskRequestDto request = new TaskRequestDto();
        request.setTitle("Test Task");
        request.setDescription("Task Description");
        request.setPriority("High");
        request.setAssigneeId(2L);

        // Подготавливаем тестового пользователя и аутентификацию
        User author = new User();  // Создайте или замокайте объект пользователя
        author.setEmail("author@example.com");
        Authentication authentication = new UsernamePasswordAuthenticationToken(author, null, Collections.emptyList());
        SecurityContextHolder.getContext().setAuthentication(authentication);  // Устанавливаем аутентификацию

        when(userRepository.findById(2L)).thenReturn(Optional.of(assignee));
        when(taskRepository.save(any(Task.class))).thenReturn(task);

        TaskResponseDto response = taskService.createTask(request);

        assertNotNull(response);
        assertEquals("Test Task", response.getTitle());
        assertEquals("Task Description", response.getDescription());
        assertEquals("High", response.getPriority());
        assertEquals("Pending", response.getStatus());
        assertEquals("author@example.com", response.getAuthorEmail());
        assertEquals("assignee@example.com", response.getAssigneeEmail());

        verify(userRepository).findById(2L);
        verify(taskRepository).save(any(Task.class));
    }

    @Schema(description = "Проверяет создание задачи, если исполнитель не найден")
    @Test
    void testCreateTask_AssigneeNotFound() {
        TaskRequestDto request = new TaskRequestDto();
        request.setTitle("Test Task");
        request.setDescription("Task Description");
        request.setPriority("High");
        request.setAssigneeId(2L);

        when(userRepository.findById(2L)).thenReturn(Optional.empty());

        CustomException exception = assertThrows(CustomException.class, () -> taskService.createTask(request));

        assertEquals("Assignee not found", exception.getMessage());
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
    }

    @Schema(description = "Проверяет получение списка задач с пагинацией")
    @Test
    void testGetTasks() {
        Page<Task> taskPage = new PageImpl<>(Arrays.asList(task));
        when(taskRepository.findAllWithFilters(null, null, PageRequest.of(0, 10))).thenReturn(taskPage);

        Page<TaskResponseDto> tasks = taskService.getTasks(0, 10, null, null);

        assertNotNull(tasks);
        assertEquals(1, tasks.getContent().size());
        assertEquals("Test Task", tasks.getContent().get(0).getTitle());
        assertEquals("author@example.com", tasks.getContent().get(0).getAuthorEmail());
        assertEquals("assignee@example.com", tasks.getContent().get(0).getAssigneeEmail());

        verify(taskRepository).findAllWithFilters(null, null, PageRequest.of(0, 10));
    }

    @Schema(description = "Проверяет получение задачи по ID")
    @Test
    void testGetTaskById() {
        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));

        TaskResponseDto response = taskService.getTaskById(1L);

        assertNotNull(response);
        assertEquals("Test Task", response.getTitle());
        assertEquals("author@example.com", response.getAuthorEmail());

        verify(taskRepository).findById(1L);
    }

    @Schema(description = "Проверяет исключение, если задача не найдена")
    @Test
    void testGetTaskById_TaskNotFound() {
        when(taskRepository.findById(1L)).thenReturn(Optional.empty());

        CustomException exception = assertThrows(CustomException.class, () -> taskService.getTaskById(1L));

        assertEquals("Task not found", exception.getMessage());
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
    }

    @Schema(description = "Проверяет обновление задачи")
    @Test
    void testUpdateTask() {
        TaskRequestDto request = new TaskRequestDto();
        request.setTitle("Updated Task");
        request.setDescription("Updated Description");
        request.setPriority("Medium");
        request.setAssigneeId(2L);

        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));
        when(userRepository.findById(2L)).thenReturn(Optional.of(assignee));
        when(taskRepository.save(any(Task.class))).thenReturn(task);

        TaskResponseDto response = taskService.updateTask(1L, request);

        assertNotNull(response);
        assertEquals("Updated Task", response.getTitle());
        assertEquals("Updated Description", response.getDescription());
        assertEquals("Medium", response.getPriority());
        assertEquals("assignee@example.com", response.getAssigneeEmail());

        verify(taskRepository).findById(1L);
        verify(userRepository).findById(2L);
        verify(taskRepository).save(any(Task.class));
    }

    @Schema(description = "Проверяет удаление задачи")
    @Test
    void testDeleteTask() {
        when(taskRepository.existsById(1L)).thenReturn(true);

        taskService.deleteTask(1L);

        verify(taskRepository).deleteById(1L);
    }

    @Schema(description = "Проверяет исключение при удалении несуществующей задачи")
    @Test
    void testDeleteTask_TaskNotFound() {
        when(taskRepository.existsById(1L)).thenReturn(false);

        CustomException exception = assertThrows(CustomException.class, () -> taskService.deleteTask(1L));

        assertEquals("Task not found", exception.getMessage());
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
    }

    @Schema(description = "Проверяет обновление статуса задачи")
    @Test
    void testUpdateTaskStatus() {
        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));
        when(taskRepository.save(any(Task.class))).thenReturn(task);

        TaskResponseDto response = taskService.updateTaskStatus(1L, "In Progress");

        assertNotNull(response);
        assertEquals("In Progress", response.getStatus());

        verify(taskRepository).findById(1L);
        verify(taskRepository).save(any(Task.class));
    }

    @Schema(description = "Проверяет, является ли пользователь автором или исполнителем задачи")
    @Test
    void testIsAssigneeOrAuthor() {
        when(taskRepository.isAssigneeOrAuthor(1L, "author@example.com")).thenReturn(true);

        boolean result = taskService.isAssigneeOrAuthor(1L, "author@example.com");

        assertTrue(result);

        verify(taskRepository).isAssigneeOrAuthor(1L, "author@example.com");
    }
}