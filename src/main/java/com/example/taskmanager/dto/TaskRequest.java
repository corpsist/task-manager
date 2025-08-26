package com.example.taskmanager.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Task request object")
public class TaskRequest {
    @NotBlank(message = "Title is required")
    private String title;

    @Size(max = 1000, message = "Description can be at most 1000 characters")
    private String description;

    private Boolean completed;
}


