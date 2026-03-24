package com.example.tasksystem.dto;

import com.example.tasksystem.model.TaskStatus;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TaskRequestDTO {
    @NotBlank(message = "Title is required")
    private String title;
    
    private String description;
    
    private TaskStatus status;
    
    @NotNull(message = "Creator ID is required")
    private Long createdById;
    
    private Long assignedToId;
    
    @FutureOrPresent(message = "Due date cannot be in the past")
    private LocalDate dueDate;
}
