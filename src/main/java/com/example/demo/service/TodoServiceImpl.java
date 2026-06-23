package com.example.demo.service;

import com.example.demo.dto.TodoRequest;
import com.example.demo.dto.TodoResponse;
import com.example.demo.entity.Todo;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.repository.TodoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Default implementation of {@link TodoService}.
 */
@Service
@Transactional
public class TodoServiceImpl implements TodoService {

    private final TodoRepository todoRepository;

    public TodoServiceImpl(TodoRepository todoRepository) {
        this.todoRepository = todoRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<TodoResponse> getAllTodos() {
        return todoRepository.findAll().stream()
                .map(TodoResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public TodoResponse getTodoById(Long id) {
        Todo todo = findTodoOrThrow(id);
        return TodoResponse.fromEntity(todo);
    }

    @Override
    public TodoResponse createTodo(TodoRequest request) {
        Todo todo = new Todo(request.getTitle(), request.isCompleted());
        Todo saved = todoRepository.save(todo);
        return TodoResponse.fromEntity(saved);
    }

    @Override
    public TodoResponse updateTodo(Long id, TodoRequest request) {
        Todo todo = findTodoOrThrow(id);
        todo.setTitle(request.getTitle());
        todo.setCompleted(request.isCompleted());
        Todo saved = todoRepository.save(todo);
        return TodoResponse.fromEntity(saved);
    }

    @Override
    public void deleteTodo(Long id) {
        Todo todo = findTodoOrThrow(id);
        todoRepository.delete(todo);
    }

    private Todo findTodoOrThrow(Long id) {
        return todoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Todo not found with id: " + id));
    }
}
