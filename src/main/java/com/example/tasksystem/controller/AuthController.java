package com.example.tasksystem.controller;

import com.example.tasksystem.dto.EmployeeDTO;
import com.example.tasksystem.dto.LoginRequestDTO;
import com.example.tasksystem.dto.RegisterRequestDTO;
import com.example.tasksystem.service.EmployeeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final EmployeeService employeeService;

    @PostMapping("/register")
    public ResponseEntity<EmployeeDTO> register(@Valid @RequestBody RegisterRequestDTO registerRequest) {
        return ResponseEntity.ok(employeeService.registerEmployee(registerRequest));
    }

    @PostMapping("/login")
    public ResponseEntity<EmployeeDTO> login(@Valid @RequestBody LoginRequestDTO loginRequest) {
        return ResponseEntity.ok(employeeService.loginEmployee(loginRequest));
    }
}
