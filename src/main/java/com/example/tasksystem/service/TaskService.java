package com.example.tasksystem.service;

import com.example.tasksystem.model.Task;
import com.example.tasksystem.model.TaskStatus;
import java.util.List;

public interface TaskService {
    Task createTask(Task task);
    Task assignTask(Long taskId, Long employeeId);
    Task updateTaskStatus(Long taskId, TaskStatus status);
    void deleteTask(Long taskId, Long requesterId);
    List<Task> getTasksByEmployee(Long employeeId);
}
