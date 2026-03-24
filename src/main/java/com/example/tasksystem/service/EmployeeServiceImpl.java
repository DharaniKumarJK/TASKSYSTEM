package com.example.tasksystem.service;

import com.example.tasksystem.dto.EmployeeDTO;
import com.example.tasksystem.dto.JwtResponseDTO;
import com.example.tasksystem.dto.LoginRequestDTO;
import com.example.tasksystem.dto.RegisterRequestDTO;
import com.example.tasksystem.model.Employee;
import com.example.tasksystem.repository.EmployeeRepository;
import com.example.tasksystem.security.JwtUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmployeeServiceImpl implements EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;
    private final UserDetailsService userDetailsService;

    @Override
    @Transactional
    public EmployeeDTO createEmployee(EmployeeDTO employeeDTO) {
        log.info("Creating new employee: {}", employeeDTO.getEmail());
        Employee employee = Employee.builder()
                .name(employeeDTO.getName())
                .email(employeeDTO.getEmail())
                .password(passwordEncoder.encode("default123"))
                .build();
        employee = employeeRepository.save(employee);
        log.info("Employee created with ID: {}", employee.getId());
        return mapToDTO(employee);
    }

    @Override
    @Transactional
    public EmployeeDTO registerEmployee(RegisterRequestDTO registerRequest) {
        log.info("Registering new employee: {}", registerRequest.getEmail());
        if (employeeRepository.findByEmail(registerRequest.getEmail()).isPresent()) {
            throw new IllegalArgumentException("Email already exists");
        }

        Employee employee = Employee.builder()
                .name(registerRequest.getName())
                .email(registerRequest.getEmail())
                .password(passwordEncoder.encode(registerRequest.getPassword()))
                .build();

        Employee savedEmployee = employeeRepository.save(employee);
        log.info("Employee registered with ID: {}", savedEmployee.getId());
        return mapToDTO(savedEmployee);
    }

    @Override
    public JwtResponseDTO loginEmployee(LoginRequestDTO loginRequest) {
        log.info("Login attempt for email: {}", loginRequest.getEmail());
        
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword())
        );

        Employee employee = employeeRepository.findByEmail(loginRequest.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("Invalid email or password"));

        UserDetails userDetails = userDetailsService.loadUserByUsername(employee.getEmail());
        String jwt = jwtUtils.generateToken(userDetails);

        log.info("Login successful for email: {}", loginRequest.getEmail());
        return JwtResponseDTO.builder()
                .token(jwt)
                .id(employee.getId())
                .name(employee.getName())
                .email(employee.getEmail())
                .build();
    }

    @Override
    public List<EmployeeDTO> getAllEmployees() {
        return employeeRepository.findAll().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public EmployeeDTO getEmployeeById(Long id) {
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Employee not found"));
        return mapToDTO(employee);
    }

    private EmployeeDTO mapToDTO(Employee employee) {
        return EmployeeDTO.builder()
                .id(employee.getId())
                .name(employee.getName())
                .email(employee.getEmail())
                .build();
    }
}
