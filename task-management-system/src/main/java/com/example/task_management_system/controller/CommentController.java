package com.example.task_management_system.controller;

import com.example.task_management_system.dto.CommentRequestDto;
import com.example.task_management_system.dto.CommentResponseDto;
import com.example.task_management_system.service.CommentService;

import javax.validation.Valid;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/tasks/{taskId}/comments")
@Tag(name = "Comment API", description = "Управление комментариями к задачам")
public class CommentController {

    private final CommentService commentService;

    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    @Operation(summary = "Добавить комментарий к задаче",
            description = "Позволяет пользователю добавить комментарий к задаче")
    @PreAuthorize("hasRole('ADMIN') or @taskService.isAssigneeOrAuthor(#taskId, authentication.name)")
    @PostMapping
    public ResponseEntity<CommentResponseDto> addComment(@PathVariable Long taskId, @Valid @RequestBody CommentRequestDto request) {
        return ResponseEntity.ok(commentService.addComment(taskId, request));
    }

    @Operation(summary = "Получить комментарии к задаче",
            description = "Возвращает список комментариев, привязанных к указанной задаче")
    @PreAuthorize("hasRole('ADMIN') or @taskService.isAssigneeOrAuthor(#taskId, authentication.name)")
    @GetMapping
    public ResponseEntity<List<CommentResponseDto>> getCommentsByTask(@PathVariable Long taskId) {
        return ResponseEntity.ok(commentService.getCommentsByTask(taskId));
    }

    @GetMapping("/author/{authorId}")
    @Operation(summary = "Получить комментарии автора",
            description = "Возвращает список комментариев, оставленных указанным автором")
    public ResponseEntity<List<CommentResponseDto>> getCommentsByAuthor(@PathVariable Long authorId) {
        List<CommentResponseDto> comments = commentService.getCommentsByAuthor(authorId);
        return ResponseEntity.ok(comments);
    }

    @DeleteMapping("/{commentId}")
    @Operation(summary = "Удалить комментарий",
            description = "Удаляет комментарий по указанному идентификатору")
    public ResponseEntity<String> deleteComment(@PathVariable Long commentId) {
        commentService.deleteComment(commentId);
        return ResponseEntity.ok("Комментарий успешно удалён!");
    }
}