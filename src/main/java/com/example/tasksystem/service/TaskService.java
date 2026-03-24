package com.example.tasksystem.service;

import com.example.tasksystem.dto.TaskRequestDTO;
import com.example.tasksystem.dto.TaskResponseDTO;
import com.example.tasksystem.model.TaskStatus;

import java.util.List;

public interface TaskService {
    TaskResponseDTO createTask(TaskRequestDTO taskRequest, Long requesterId);
    TaskResponseDTO assignTask(Long taskId, Long employeeId);
    TaskResponseDTO updateTaskStatus(Long taskId, TaskStatus status);
    void deleteTask(Long taskId, Long requesterId);
    List<TaskResponseDTO> getTasksByEmployee(Long employeeId);
    List<TaskResponseDTO> getMyTasks(String email);
}
