package com.example.task_management_system.service;

import com.example.task_management_system.dto.TaskRequestDto;
import com.example.task_management_system.dto.TaskResponseDto;
import com.example.task_management_system.entity.Task;
import com.example.task_management_system.entity.User;
import com.example.task_management_system.exception.CustomException;
import com.example.task_management_system.repository.TaskRepository;
import com.example.task_management_system.repository.UserRepository;
import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@Schema(description = "Сервис для управления задачами")
public class TaskService {

    private final TaskRepository taskRepository;
    private final UserRepository userRepository;

    public TaskService(TaskRepository taskRepository, UserRepository userRepository) {
        this.taskRepository = taskRepository;
        this.userRepository = userRepository;
    }

    @Schema(description = "Выполняет создание задачи. При вызове метода createTask автор будет автоматически " +
            "установлен на текущего аутентифицированного пользователя.")
    public TaskResponseDto createTask(TaskRequestDto request) {
        User author = getCurrentUser();
        User assignee = userRepository.findById(request.getAssigneeId())
                .orElseThrow(() -> new CustomException("Assignee not found", HttpStatus.NOT_FOUND));

        Task task = new Task();
        task.setTitle(request.getTitle());
        task.setDescription(request.getDescription());
        task.setPriority(request.getPriority());
        task.setStatus("Pending");
        task.setAuthor(author);
        task.setAssignee(assignee);

        Task savedTask = taskRepository.save(task);
        return mapToResponse(savedTask);
    }

    @Schema(description = "Получение всех задач")
    public Page<TaskResponseDto> getTasks(int page, int size, Long authorId, Long assigneeId) {
        Page<Task> tasks = taskRepository.findAllWithFilters(authorId, assigneeId, PageRequest.of(page, size));
        return tasks.map(this::mapToResponse);
    }

    @Schema(description = "Получение задачи по ID")
            public TaskResponseDto getTaskById(Long id) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new CustomException("Task not found", HttpStatus.NOT_FOUND));
        return mapToResponse(task);
    }

    @Schema(description = "Обновляет задачу")
    public TaskResponseDto updateTask(Long id, TaskRequestDto request) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new CustomException("Task not found", HttpStatus.NOT_FOUND));

        User assignee = userRepository.findById(request.getAssigneeId())
                .orElseThrow(() -> new CustomException("Assignee not found", HttpStatus.NOT_FOUND));

        task.setTitle(request.getTitle());
        task.setDescription(request.getDescription());
        task.setPriority(request.getPriority());
        task.setAssignee(assignee);

        Task updatedTask = taskRepository.save(task);
        return mapToResponse(updatedTask);
    }

    @Schema(description = "Обновляет статус задачи")
    public TaskResponseDto updateTaskStatus(Long id, String status) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new CustomException("Task not found", HttpStatus.NOT_FOUND));
        task.setStatus(status);
        Task updatedTask = taskRepository.save(task);
        return mapToResponse(updatedTask);
    }

    @Schema(description = "Удаляет задачу")
    public void deleteTask(Long id) {
        if (!taskRepository.existsById(id)) {
            throw new CustomException("Task not found", HttpStatus.NOT_FOUND);
        }
        taskRepository.deleteById(id);
    }

    @Schema(description = "Проверяет, является ли пользователь автором или исполнителем задачи")
    public boolean isAssigneeOrAuthor(Long taskId, String email) {
        return taskRepository.isAssigneeOrAuthor(taskId, email);
    }

    @Schema(description = "Выполняет преобразование объекта типа Task в объект типа TaskResponse")
    private TaskResponseDto mapToResponse(Task task) {
        TaskResponseDto response = new TaskResponseDto();
        response.setId(task.getId());
        response.setTitle(task.getTitle());
        response.setDescription(task.getDescription());
        response.setStatus(task.getStatus());
        response.setPriority(task.getPriority());
        response.setAuthorEmail(task.getAuthor().getEmail());
        response.setAssigneeEmail(task.getAssignee().getEmail());
        return response;
    }

    @Schema(description = "Реализация получения текущего пользователя (например, из контекста безопасности)")
    private User getCurrentUser() {
        // Получаем пользователя напрямую из контекста безопасности
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null && authentication.getPrincipal() instanceof User) {
            User user = (User) authentication.getPrincipal();
            return user;
        } else {
            throw new CustomException("User not found", HttpStatus.UNAUTHORIZED);
        }
    }
}