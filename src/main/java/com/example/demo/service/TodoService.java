package com.example.demo.service;

import com.example.demo.dto.TodoRequest;
import com.example.demo.dto.TodoResponse;

import java.util.List;

/**
 * Business operations for managing Todo items.
 */
public interface TodoService {

    List<TodoResponse> getAllTodos();

    TodoResponse getTodoById(Long id);

    TodoResponse createTodo(TodoRequest request);

    TodoResponse updateTodo(Long id, TodoRequest request);

    void deleteTodo(Long id);
}
