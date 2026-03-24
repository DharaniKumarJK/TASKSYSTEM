package com.example.tasksystem.service;

import com.example.tasksystem.dto.EmployeeDTO;
import com.example.tasksystem.dto.LoginRequestDTO;
import com.example.tasksystem.dto.RegisterRequestDTO;

import java.util.List;

public interface EmployeeService {
    EmployeeDTO createEmployee(EmployeeDTO employeeDTO); // Legacy/Internal
    EmployeeDTO registerEmployee(RegisterRequestDTO registerRequest);
    EmployeeDTO loginEmployee(LoginRequestDTO loginRequest);
    List<EmployeeDTO> getAllEmployees();
    EmployeeDTO getEmployeeById(Long id);
}
