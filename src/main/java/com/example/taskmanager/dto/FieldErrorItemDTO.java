package com.example.taskmanager.dto;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FieldErrorItemDTO {
    private String field; // eg: title
    private String message; // eg: title is required
}
