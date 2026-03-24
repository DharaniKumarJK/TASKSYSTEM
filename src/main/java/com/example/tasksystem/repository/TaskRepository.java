package com.example.tasksystem.repository;

import com.example.tasksystem.model.Employee;
import com.example.tasksystem.model.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {
    List<Task> findByAssignedTo(Employee employee);
}
