package com.example.task_management_system.entity;

import javax.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Entity
@Table(name = "comments")
@Getter
@Setter
@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(nullable = false, length = 1000)
    String content;

    @ManyToOne
    @JoinColumn(name = "task_id", nullable = false)
    Task task;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    User user;
}