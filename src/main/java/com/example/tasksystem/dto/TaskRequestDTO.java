package com.example.tasksystem.dto;

import com.example.tasksystem.model.TaskStatus;
import lombok.*;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TaskRequestDTO {
    private String title;
    private String description;
    private TaskStatus status;
    private Long createdById;
    private Long assignedToId;
    private LocalDate dueDate;
}
