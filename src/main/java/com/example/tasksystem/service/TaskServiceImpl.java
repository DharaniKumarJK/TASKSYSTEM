package com.example.tasksystem.service;

import com.example.tasksystem.model.Employee;
import com.example.tasksystem.model.Task;
import com.example.tasksystem.model.TaskStatus;
import com.example.tasksystem.repository.EmployeeRepository;
import com.example.tasksystem.repository.TaskRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TaskServiceImpl implements TaskService {

    private final TaskRepository taskRepository;
    private final EmployeeRepository employeeRepository;

    @Override
    @Transactional
    public Task createTask(Task task) {
        if (task.getDueDate() != null && task.getDueDate().isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("Due date cannot be in the past");
        }
        if (task.getStatus() == null) {
            task.setStatus(TaskStatus.TODO);
        }
        return taskRepository.save(task);
    }

    @Override
    @Transactional
    public Task assignTask(Long taskId, Long employeeId) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new IllegalArgumentException("Task not found"));
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new IllegalArgumentException("Employee not found"));
        
        task.setAssignedTo(employee);
        return taskRepository.save(task);
    }

    @Override
    @Transactional
    public Task updateTaskStatus(Long taskId, TaskStatus status) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new IllegalArgumentException("Task not found"));
        task.setStatus(status);
        return taskRepository.save(task);
    }

    @Override
    @Transactional
    public void deleteTask(Long taskId, Long requesterId) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new IllegalArgumentException("Task not found"));
        
        if (!task.getCreatedBy().getId().equals(requesterId)) {
            throw new IllegalStateException("Only the creator can delete this task");
        }
        
        taskRepository.delete(task);
    }

    @Override
    public List<Task> getTasksByEmployee(Long employeeId) {
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new IllegalArgumentException("Employee not found"));
        return taskRepository.findByAssignedTo(employee);
    }
}
