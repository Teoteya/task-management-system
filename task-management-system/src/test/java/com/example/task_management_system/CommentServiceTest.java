package com.example.task_management_system;

import com.example.task_management_system.dto.CommentRequestDto;
import com.example.task_management_system.dto.CommentResponseDto;
import com.example.task_management_system.entity.Comment;
import com.example.task_management_system.entity.Task;
import com.example.task_management_system.entity.User;
import com.example.task_management_system.exception.CustomException;
import com.example.task_management_system.repository.CommentRepository;
import com.example.task_management_system.repository.TaskRepository;
import com.example.task_management_system.repository.UserRepository;
import com.example.task_management_system.service.CommentService;
import io.swagger.v3.oas.annotations.media.Schema;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.http.HttpStatus;

import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class CommentServiceTest {

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private UserRepository userRepository;
    @Spy
    @InjectMocks
    private CommentService commentService;

    private Task task;
    @Mock
    private User user;
    private CommentRequestDto commentRequest;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // Инициализация тестовых данных
        task = new Task();
        task.setId(1L);
        task.setTitle("Test Task");

        user = new User();
        user.setId(1L);
        user.setEmail("test@example.com");

        commentRequest = new CommentRequestDto();
        commentRequest.setContent("This is a comment.");
    }

    @Schema(description = "Проверка успешного добавления комментария. " +
            "Мы мокаем репозитории, чтобы taskRepository.findById возвращал задачу, " +
            "а метод commentRepository.save сохранял комментарий. " +
            "Затем проверяем, что возвращаемый ответ содержит правильное содержимое и email автора")
    @Test
    void testAddComment_Success() {
        // Мокаем поведение репозитория
        when(taskRepository.findById(task.getId())).thenReturn(java.util.Optional.of(task));
        when(userRepository.findById(user.getId())).thenReturn(java.util.Optional.of(user));

        // Мокаем комментарий, чтобы метод getUser() не возвращал null
        Comment comment = mock(Comment.class);
        when(comment.getUser()).thenReturn(user);
        when(comment.getContent()).thenReturn(commentRequest.getContent());  // Мокаем getContent
        when(commentRepository.save(any(Comment.class))).thenReturn(comment);

        // Мокаем метод getCurrentUser
        doReturn(user).when(commentService).getCurrentUser();

        CommentResponseDto response = commentService.addComment(task.getId(), commentRequest);

        assertNotNull(response);
        assertEquals("This is a comment.", response.getContent());  // Проверяем content
        assertEquals("test@example.com", response.getAuthorEmail());

        verify(taskRepository).findById(task.getId());
        verify(commentRepository).save(any(Comment.class));
    }

    @Schema(description = "Проверка добавления комментария, когда задача не найдена в базе данных. " +
            "Мы мокаем поведение репозитория, чтобы taskRepository.findById возвращал пустой результат, " +
            "что приводит к выбросу исключения CustomException")
    @Test
    void testAddComment_TaskNotFound() {
        // Мокаем поведение репозитория
        when(taskRepository.findById(task.getId())).thenReturn(java.util.Optional.empty());

        CustomException exception = assertThrows(CustomException.class, () -> commentService.addComment(task.getId(), commentRequest));

        assertEquals("Task not found", exception.getMessage());
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
    }

    @Schema(description = "Проверка получения комментариев для задачи. " +
            "Мы мокаем репозитории, чтобы задача была найдена, а комментарии для этой задачи возвращались корректно")
    @Test
    void testGetComments_Success() {
        // Мокаем поведение репозитория
        when(taskRepository.existsById(task.getId())).thenReturn(true);

        // Мокаем комментарий, чтобы метод getUser() не возвращал null
        Comment comment = mock(Comment.class);
        when(comment.getUser()).thenReturn(user);  // Замоканный getUser()

        when(commentRepository.findByTaskId(task.getId())).thenReturn(Collections.singletonList(comment));

        List<CommentResponseDto> comments = commentService.getCommentsByTask(task.getId());

        assertNotNull(comments);
        assertEquals(1, comments.size());

        verify(taskRepository).existsById(task.getId());
        verify(commentRepository).findByTaskId(task.getId());
    }

    @Schema(description = "Проверка получения комментариев для задачи, когда задача не найдена в базе данных. " +
            "Если задача не существует, метод выбрасывает исключение CustomException")
    @Test
    void testGetComments_TaskNotFound() {
        // Мокаем поведение репозитория
        when(taskRepository.existsById(task.getId())).thenReturn(false);

        CustomException exception = assertThrows(CustomException.class, () -> commentService.getCommentsByTask(task.getId()));

        assertEquals("Task not found", exception.getMessage());
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
    }
}