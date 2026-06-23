package com.example.demo.dto;

import com.example.demo.entity.Todo;

import java.time.Instant;

/**
 * Response payload representing a Todo returned to clients.
 */
public class TodoResponse {

    private Long id;
    private String title;
    private boolean completed;
    private Instant createdAt;
    private Instant updatedAt;

    public TodoResponse() {
    }

    public TodoResponse(Long id, String title, boolean completed, Instant createdAt, Instant updatedAt) {
        this.id = id;
        this.title = title;
        this.completed = completed;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    /**
     * Maps a {@link Todo} entity to a response DTO.
     */
    public static TodoResponse fromEntity(Todo todo) {
        return new TodoResponse(
                todo.getId(),
                todo.getTitle(),
                todo.isCompleted(),
                todo.getCreatedAt(),
                todo.getUpdatedAt());
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }
}
