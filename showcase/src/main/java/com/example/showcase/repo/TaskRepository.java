package com.example.showcase.repo;

import com.example.showcase.domens.Task;
import org.springframework.http.ProblemDetail;

import java.util.List;
import java.util.Optional;
import java.util.UUID;


public interface TaskRepository {

    List<Task> findAll();


    void save(Task task);

    Optional<Task> findById(UUID id);
}
