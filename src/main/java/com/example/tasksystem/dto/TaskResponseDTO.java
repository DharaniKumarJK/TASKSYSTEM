package com.example.tasksystem.dto;

import com.example.tasksystem.model.TaskStatus;
import lombok.*;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TaskResponseDTO {
    private Long id;
    private String title;
    private String description;
    private TaskStatus status;
    private String createdByEmployee;
    private String assignedToEmployee;
    private LocalDate dueDate;
}
