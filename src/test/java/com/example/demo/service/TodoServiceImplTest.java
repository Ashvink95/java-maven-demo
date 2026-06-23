package com.example.demo.service;

import com.example.demo.dto.TodoRequest;
import com.example.demo.dto.TodoResponse;
import com.example.demo.entity.Todo;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.repository.TodoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TodoServiceImplTest {

    @Mock
    private TodoRepository todoRepository;

    @InjectMocks
    private TodoServiceImpl todoService;

    private Todo sampleTodo;

    @BeforeEach
    void setUp() {
        sampleTodo = new Todo("Sample", false);
        sampleTodo.setId(1L);
    }

    @Test
    void getAllTodos_returnsMappedList() {
        when(todoRepository.findAll()).thenReturn(Arrays.asList(sampleTodo));

        List<TodoResponse> result = todoService.getAllTodos();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getTitle()).isEqualTo("Sample");
    }

    @Test
    void getTodoById_whenExists_returnsTodo() {
        when(todoRepository.findById(1L)).thenReturn(Optional.of(sampleTodo));

        TodoResponse result = todoService.getTodoById(1L);

        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getTitle()).isEqualTo("Sample");
    }

    @Test
    void getTodoById_whenMissing_throwsNotFound() {
        when(todoRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> todoService.getTodoById(99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("99");
    }

    @Test
    void createTodo_savesAndReturnsResponse() {
        TodoRequest request = new TodoRequest("New task", false);
        when(todoRepository.save(any(Todo.class))).thenReturn(sampleTodo);

        TodoResponse result = todoService.createTodo(request);

        assertThat(result.getTitle()).isEqualTo("Sample");
        verify(todoRepository).save(any(Todo.class));
    }

    @Test
    void updateTodo_whenExists_updatesFields() {
        TodoRequest request = new TodoRequest("Updated", true);
        when(todoRepository.findById(1L)).thenReturn(Optional.of(sampleTodo));
        when(todoRepository.save(any(Todo.class))).thenReturn(sampleTodo);

        TodoResponse result = todoService.updateTodo(1L, request);

        assertThat(sampleTodo.getTitle()).isEqualTo("Updated");
        assertThat(sampleTodo.isCompleted()).isTrue();
        assertThat(result).isNotNull();
    }

    @Test
    void updateTodo_whenMissing_throwsNotFound() {
        TodoRequest request = new TodoRequest("Updated", true);
        when(todoRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> todoService.updateTodo(99L, request))
                .isInstanceOf(ResourceNotFoundException.class);
        verify(todoRepository, never()).save(any(Todo.class));
    }

    @Test
    void deleteTodo_whenExists_deletes() {
        when(todoRepository.findById(1L)).thenReturn(Optional.of(sampleTodo));

        todoService.deleteTodo(1L);

        verify(todoRepository).delete(sampleTodo);
    }

    @Test
    void deleteTodo_whenMissing_throwsNotFound() {
        when(todoRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> todoService.deleteTodo(99L))
                .isInstanceOf(ResourceNotFoundException.class);
        verify(todoRepository, never()).delete(any(Todo.class));
    }
}
