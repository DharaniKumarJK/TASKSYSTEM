package com.example.tasksystem.service;

import com.example.tasksystem.model.Employee;
import com.example.tasksystem.model.Task;
import com.example.tasksystem.model.TaskStatus;
import com.example.tasksystem.repository.EmployeeRepository;
import com.example.tasksystem.repository.TaskRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class TaskServiceImpl implements TaskService {

    private final TaskRepository taskRepository;
    private final EmployeeRepository employeeRepository;

    @Override
    @Transactional
    public Task createTask(Task task) {
        log.info("Creating new task: {} for creator ID: {}", task.getTitle(), task.getCreatedBy().getId());
        if (task.getDueDate() != null && task.getDueDate().isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("Due date cannot be in the past");
        }
        if (task.getStatus() == null) {
            task.setStatus(TaskStatus.TODO);
        }
        Task savedTask = taskRepository.save(task);
        log.info("Task created with ID: {}", savedTask.getId());
        return savedTask;
    }

    @Override
    @Transactional
    public Task assignTask(Long taskId, Long employeeId) {
        log.info("Assigning task ID: {} to employee ID: {}", taskId, employeeId);
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
        log.info("Updating status for task ID: {} to {}", taskId, status);
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new IllegalArgumentException("Task not found"));
        task.setStatus(status);
        return taskRepository.save(task);
    }

    @Override
    @Transactional
    public void deleteTask(Long taskId, Long requesterId) {
        log.info("Attempting to delete task ID: {} by requester ID: {}", taskId, requesterId);
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new IllegalArgumentException("Task not found"));
        
        if (!task.getCreatedBy().getId().equals(requesterId)) {
            log.warn("Unauthorized delete attempt for task ID: {} by requester ID: {}", taskId, requesterId);
            throw new IllegalStateException("Only the creator can delete this task");
        }
        
        taskRepository.delete(task);
        log.info("Task ID: {} deleted successfully", taskId);
    }

    @Override
    public List<Task> getTasksByEmployee(Long employeeId) {
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new IllegalArgumentException("Employee not found"));
        return taskRepository.findByAssignedTo(employee);
    }
}
