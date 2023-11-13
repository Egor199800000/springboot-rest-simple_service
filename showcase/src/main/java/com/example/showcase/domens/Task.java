package com.example.showcase.domens;

import java.util.UUID;

//идентификатор, строковое описание задачи, состояние завершения задачи
public record Task(UUID id,String details, boolean completed) {

    public Task(String details) {
        this(UUID.randomUUID(), details, false);
    }
}
