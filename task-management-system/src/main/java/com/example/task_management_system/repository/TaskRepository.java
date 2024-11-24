package com.example.task_management_system.repository;

import com.example.task_management_system.entity.Task;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface TaskRepository extends JpaRepository<Task, Long> {

    @Query("SELECT t FROM Task t WHERE " +
            "(:authorId IS NULL OR t.author.id = :authorId) AND " +
            "(:assigneeId IS NULL OR t.assignee.id = :assigneeId)")
    Page<Task> findAllWithFilters(Long authorId, Long assigneeId, Pageable pageable);

    @Query("SELECT CASE WHEN COUNT(t) > 0 THEN true ELSE false END " +
            "FROM Task t WHERE t.id = :taskId AND " +
            "(t.author.email = :email OR t.assignee.email = :email)")
    boolean isAssigneeOrAuthor(Long taskId, String email);
}