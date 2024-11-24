package com.example.task_management_system.service;

import com.example.task_management_system.dto.CommentRequestDto;
import com.example.task_management_system.dto.CommentResponseDto;
import com.example.task_management_system.entity.Comment;
import com.example.task_management_system.entity.Task;
import com.example.task_management_system.entity.User;
import com.example.task_management_system.exception.CustomException;
import com.example.task_management_system.repository.CommentRepository;
import com.example.task_management_system.repository.TaskRepository;
import com.example.task_management_system.repository.UserRepository;
import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Schema(description = "Сервис для управления комментариями")
public class CommentService {

    private final CommentRepository commentRepository;
    private final TaskRepository taskRepository;
    private final UserRepository userRepository;

    public CommentService(CommentRepository commentRepository, TaskRepository taskRepository, UserRepository userRepository) {
        this.commentRepository = commentRepository;
        this.taskRepository = taskRepository;
        this.userRepository = userRepository;
    }

    @Schema(description = "Позволяет пользователю добавить комментарий к задаче")
    public CommentResponseDto addComment(Long taskId, CommentRequestDto request) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new CustomException("Task not found", HttpStatus.NOT_FOUND));
        User user = getCurrentUser();

        Comment comment = new Comment();
        comment.setContent(request.getContent());
        comment.setTask(task);
        comment.setUser(user);

        Comment savedComment = commentRepository.save(comment);
        return mapToResponse(savedComment);
    }

    @Schema(description = "Возвращает список комментариев, привязанных к указанной задаче")
    public List<CommentResponseDto> getCommentsByTask(Long taskId) {
        if (!taskRepository.existsById(taskId)) {
            throw new CustomException("Task not found", HttpStatus.NOT_FOUND);
        }
        return commentRepository.findByTaskId(taskId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Schema(description = "Возвращает список комментариев, оставленных указанным автором")
    public List<CommentResponseDto> getCommentsByAuthor(Long authorId) {
        if (!userRepository.existsById(authorId)) {
            throw new CustomException("Author not found", HttpStatus.NOT_FOUND);
        }
        return commentRepository.findByUserId(authorId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Schema(description = "Выполняет преобразование объекта типа Comment в объект типа CommentResponse")
    private CommentResponseDto mapToResponse(Comment comment) {
        CommentResponseDto response = new CommentResponseDto();
        response.setId(comment.getId());
        response.setContent(comment.getContent());
        response.setAuthorEmail(comment.getUser().getEmail());
        return response;
    }

    @Schema(description = "Метод для получения текущего пользователя из контекста безопасности")
    public User getCurrentUser() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (principal instanceof UserDetails) {
            String email = ((UserDetails) principal).getUsername();
            return userRepository.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("User not found"));
        }
        throw new RuntimeException("User is not authenticated");
    }

    @Schema(description = "Удаляет комментарий по указанному идентификатору")
    public void deleteComment(Long commentId) {
        if (!commentRepository.existsById(commentId)) {
            throw new CustomException("Comment not found", HttpStatus.NOT_FOUND);
        }
        commentRepository.deleteById(commentId);
    }
}