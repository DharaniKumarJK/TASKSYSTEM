package com.example.tasksystem.controller;

import com.example.tasksystem.dto.TaskRequestDTO;
import com.example.tasksystem.dto.TaskResponseDTO;
import com.example.tasksystem.model.Employee;
import com.example.tasksystem.model.Task;
import com.example.tasksystem.model.TaskStatus;
import com.example.tasksystem.repository.EmployeeRepository;
import com.example.tasksystem.service.TaskService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/tasks")
@RequiredArgsConstructor
public class TaskController {

    private final TaskService taskService;
    private final EmployeeRepository employeeRepository; // Used for simple lookups in controller mapping

    @PostMapping
    public ResponseEntity<TaskResponseDTO> createTask(@RequestBody TaskRequestDTO requestDTO) {
        Employee creator = employeeRepository.findById(requestDTO.getCreatedById())
                .orElseThrow(() -> new IllegalArgumentException("Creator employee not found"));
        
        Employee assignee = null;
        if (requestDTO.getAssignedToId() != null) {
            assignee = employeeRepository.findById(requestDTO.getAssignedToId())
                    .orElseThrow(() -> new IllegalArgumentException("Assignee employee not found"));
        }

        Task task = Task.builder()
                .title(requestDTO.getTitle())
                .description(requestDTO.getDescription())
                .status(requestDTO.getStatus() != null ? requestDTO.getStatus() : TaskStatus.TODO)
                .createdBy(creator)
                .assignedTo(assignee)
                .dueDate(requestDTO.getDueDate())
                .build();

        return ResponseEntity.ok(mapToDTO(taskService.createTask(task)));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<TaskResponseDTO> updateStatus(@PathVariable Long id, @RequestParam TaskStatus status) {
        return ResponseEntity.ok(mapToDTO(taskService.updateTaskStatus(id, status)));
    }

    @PutMapping("/{id}/assign/{employeeId}")
    public ResponseEntity<TaskResponseDTO> assignTask(@PathVariable Long id, @PathVariable Long employeeId) {
        return ResponseEntity.ok(mapToDTO(taskService.assignTask(id, employeeId)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTask(@PathVariable Long id, @RequestHeader("X-Requester-Id") Long requesterId) {
        taskService.deleteTask(id, requesterId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/employee/{employeeId}")
    public ResponseEntity<List<TaskResponseDTO>> getTasksByEmployee(@PathVariable Long employeeId) {
        List<Task> tasks = taskService.getTasksByEmployee(employeeId);
        return ResponseEntity.ok(tasks.stream().map(this::mapToDTO).collect(Collectors.toList()));
    }

    private TaskResponseDTO mapToDTO(Task task) {
        return TaskResponseDTO.builder()
                .id(task.getId())
                .title(task.getTitle())
                .description(task.getDescription())
                .status(task.getStatus())
                .createdByEmployee(task.getCreatedBy().getName())
                .assignedToEmployee(task.getAssignedTo() != null ? task.getAssignedTo().getName() : "Unassigned")
                .dueDate(task.getDueDate())
                .build();
    }
}
