package com.example.tasksystem.controller;

import com.example.tasksystem.dto.TaskRequestDTO;
import com.example.tasksystem.dto.TaskResponseDTO;
import com.example.tasksystem.model.TaskStatus;
import com.example.tasksystem.service.TaskService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/tasks")
@RequiredArgsConstructor
public class TaskController {

    private final TaskService taskService;

    @GetMapping("/me")
    public ResponseEntity<List<TaskResponseDTO>> getMyTasks(Principal principal) {
        return ResponseEntity.ok(taskService.getMyTasks(principal.getName()));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    public ResponseEntity<TaskResponseDTO> createTask(
            @Valid @RequestBody TaskRequestDTO taskRequestDTO,
            @RequestHeader("X-Requester-Id") Long requesterId) {
        return ResponseEntity.ok(taskService.createTask(taskRequestDTO, requesterId));
    }

    @PostMapping("/{taskId}/assign/{employeeId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    public ResponseEntity<TaskResponseDTO> assignTask(
            @PathVariable Long taskId,
            @PathVariable Long employeeId) {
        return ResponseEntity.ok(taskService.assignTask(taskId, employeeId));
    }

    @PutMapping("/{taskId}/status")
    public ResponseEntity<TaskResponseDTO> updateTaskStatus(
            @PathVariable Long taskId,
            @RequestParam TaskStatus status) {
        return ResponseEntity.ok(taskService.updateTaskStatus(taskId, status));
    }

    @GetMapping("/employee/{employeeId}")
    public ResponseEntity<List<TaskResponseDTO>> getTasksByEmployee(@PathVariable Long employeeId) {
        return ResponseEntity.ok(taskService.getTasksByEmployee(employeeId));
    }

    @DeleteMapping("/{taskId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteTask(
            @PathVariable Long taskId,
            @RequestHeader("X-Requester-Id") Long requesterId) {
        taskService.deleteTask(taskId, requesterId);
        return ResponseEntity.noContent().build();
    }
}
