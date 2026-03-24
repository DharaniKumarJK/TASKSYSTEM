package com.example.tasksystem.service;

import com.example.tasksystem.dto.TaskRequestDTO;
import com.example.tasksystem.dto.TaskResponseDTO;
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
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class TaskServiceImpl implements TaskService {

    private final TaskRepository taskRepository;
    private final EmployeeRepository employeeRepository;

    @Override
    @Transactional
    public TaskResponseDTO createTask(TaskRequestDTO taskRequest, Long requesterId) {
        log.info("Creating new task: {} for creator ID: {}", taskRequest.getTitle(), requesterId);
        
        Employee creator = employeeRepository.findById(requesterId)
                .orElseThrow(() -> new IllegalArgumentException("Creator (Employee) not found"));

        if (taskRequest.getDueDate() != null && taskRequest.getDueDate().isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("Due date cannot be in the past");
        }

        Task task = Task.builder()
                .title(taskRequest.getTitle())
                .description(taskRequest.getDescription())
                .dueDate(taskRequest.getDueDate())
                .status(TaskStatus.TODO)
                .createdBy(creator)
                .build();

        Task savedTask = taskRepository.save(task);
        log.info("Task created with ID: {}", savedTask.getId());
        return mapToResponseDTO(savedTask);
    }

    @Override
    @Transactional
    public TaskResponseDTO assignTask(Long taskId, Long employeeId) {
        log.info("Assigning task ID: {} to employee ID: {}", taskId, employeeId);
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new IllegalArgumentException("Task not found"));
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new IllegalArgumentException("Employee not found"));
        
        task.setAssignedTo(employee);
        Task updatedTask = taskRepository.save(task);
        return mapToResponseDTO(updatedTask);
    }

    @Override
    @Transactional
    public TaskResponseDTO updateTaskStatus(Long taskId, TaskStatus status) {
        log.info("Updating status for task ID: {} to {}", taskId, status);
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new IllegalArgumentException("Task not found"));
        task.setStatus(status);
        Task updatedTask = taskRepository.save(task);
        return mapToResponseDTO(updatedTask);
    }

    @Override
    @Transactional
    public void deleteTask(Long taskId, Long requesterId) {
        log.info("Attempting to delete task ID: {} by requester ID: {}", taskId, requesterId);
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new IllegalArgumentException("Task not found"));
        
        // creator check is redundant if using @PreAuthorize on controller, 
        // but good for extra safety.
        if (!task.getCreatedBy().getId().equals(requesterId)) {
            log.warn("Unauthorized delete attempt for task ID: {} by requester ID: {}", taskId, requesterId);
            throw new IllegalStateException("Only the creator can delete this task");
        }
        
        taskRepository.delete(task);
        log.info("Task ID: {} deleted successfully", taskId);
    }

    @Override
    public List<TaskResponseDTO> getTasksByEmployee(Long employeeId) {
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new IllegalArgumentException("Employee not found"));
        return taskRepository.findByAssignedTo(employee).stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    private TaskResponseDTO mapToResponseDTO(Task task) {
        return TaskResponseDTO.builder()
                .id(task.getId())
                .title(task.getTitle())
                .description(task.getDescription())
                .dueDate(task.getDueDate())
                .status(task.getStatus())
                .assignedToName(task.getAssignedTo() != null ? task.getAssignedTo().getName() : "Unassigned")
                .createdByName(task.getCreatedBy().getName())
                .build();
    }
}
