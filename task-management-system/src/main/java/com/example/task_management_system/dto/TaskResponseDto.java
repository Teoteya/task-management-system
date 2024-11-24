package com.example.task_management_system.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TaskResponseDto {
    Long id;
    String title;
    String description;
    String status;
    String priority;
    String authorEmail;
    String assigneeEmail;
}