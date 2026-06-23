package com.example.demo.controller;

import com.example.demo.dto.TodoResponse;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.service.TodoService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TodoController.class)
class TodoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private TodoService todoService;

    private TodoResponse sample() {
        return new TodoResponse(1L, "Sample", false, Instant.now(), Instant.now());
    }

    @Test
    void getAllTodos_returnsOk() throws Exception {
        when(todoService.getAllTodos()).thenReturn(Arrays.asList(sample()));

        mockMvc.perform(get("/api/todos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value("Sample"));
    }

    @Test
    void getTodoById_returnsOk() throws Exception {
        when(todoService.getTodoById(1L)).thenReturn(sample());

        mockMvc.perform(get("/api/todos/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void getTodoById_whenMissing_returnsNotFound() throws Exception {
        when(todoService.getTodoById(eq(99L)))
                .thenThrow(new ResourceNotFoundException("Todo not found with id: 99"));

        mockMvc.perform(get("/api/todos/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404));
    }

    @Test
    void createTodo_returnsCreated() throws Exception {
        when(todoService.createTodo(any())).thenReturn(sample());
        Map<String, Object> body = new HashMap<>();
        body.put("title", "Sample");
        body.put("completed", false);

        mockMvc.perform(post("/api/todos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value("Sample"));
    }

    @Test
    void createTodo_whenTitleBlank_returnsBadRequest() throws Exception {
        Map<String, Object> body = new HashMap<>();
        body.put("title", "");
        body.put("completed", false);

        mockMvc.perform(post("/api/todos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));
    }

    @Test
    void deleteTodo_returnsNoContent() throws Exception {
        mockMvc.perform(delete("/api/todos/1"))
                .andExpect(status().isNoContent());
    }
}
