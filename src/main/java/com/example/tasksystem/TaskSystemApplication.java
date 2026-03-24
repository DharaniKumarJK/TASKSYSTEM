package com.example.tasksystem;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class TaskSystemApplication {

    public static void main(String[] args) {
        SpringApplication.run(TaskSystemApplication.class, args);
    }

}
