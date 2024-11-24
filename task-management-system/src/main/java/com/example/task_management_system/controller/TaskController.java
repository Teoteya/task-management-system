package com.example.task_management_system.controller;

import com.example.task_management_system.dto.TaskRequestDto;
import com.example.task_management_system.dto.TaskResponseDto;
import com.example.task_management_system.service.TaskService;
import javax.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/tasks")
@Tag(name = "Task API", description = "Управление задачами")
public class TaskController {

    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @Operation(summary = "Создать задачу", description = "Позволяет создать новую задачу")
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<TaskResponseDto> createTask(@Valid @RequestBody TaskRequestDto request) {
        return ResponseEntity.ok(taskService.createTask(request));
    }

    @Operation(summary = "Получить список задач", description = "Возвращает список задач с фильтрацией и пагинацией")
    @GetMapping("/all")
    public ResponseEntity<Page<TaskResponseDto>> getTasks(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            @RequestParam(value = "authorId", required = false) Long authorId,
            @RequestParam(value = "assigneeId", required = false) Long assigneeId) {
        return ResponseEntity.ok(taskService.getTasks(page, size, authorId, assigneeId));
    }

    @Operation(summary = "Получить задачу", description = "Позволяет получить задачу по ID")
    @GetMapping("/{id}")
    public ResponseEntity<TaskResponseDto> getTaskById(@PathVariable Long id) {
        return ResponseEntity.ok(taskService.getTaskById(id));
    }

    @Operation(summary = "Обновить задачу", description = "Позволяет обновить задачу")
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<TaskResponseDto> updateTask(@PathVariable Long id, @Valid @RequestBody TaskRequestDto request) {
        return ResponseEntity.ok(taskService.updateTask(id, request));
    }

    @Operation(summary = "Обновить статус задачи", description = "Позволяет обновить статус задачи")
    @PreAuthorize("hasRole('ADMIN') or @taskService.isAssignee(#id, authentication.name)")
    @PatchMapping("/{id}/status")
    public ResponseEntity<TaskResponseDto> updateTaskStatus(@PathVariable Long id, @RequestParam String status) {
        return ResponseEntity.ok(taskService.updateTaskStatus(id, status));
    }

    @Operation(summary = "Удалить задачу", description = "Позволяет удалить задачу")
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteTask(@PathVariable Long id) {
        taskService.deleteTask(id);
        return ResponseEntity.ok("Task deleted successfully");
    }
}