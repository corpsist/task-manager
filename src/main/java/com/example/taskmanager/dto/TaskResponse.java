package com.example.taskmanager.dto;

import lombok.*;
import java.time.Instant;

import io.swagger.v3.oas.annotations.media.Schema;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Task response object")
public class TaskResponse {
    private Long id;
    private String title;
    private String description;
    private Boolean completed;
    private Instant createdAt;
    private Instant updatedAt;
}


