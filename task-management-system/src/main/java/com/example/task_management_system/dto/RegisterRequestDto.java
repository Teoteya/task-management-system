package com.example.task_management_system.dto;

import javax.validation.constraints.NotBlank;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RegisterRequestDto {

    @NotBlank
    String password;

    @NotBlank
    String email;

    @NotBlank
    String role;

}