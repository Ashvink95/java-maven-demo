package com.example.demo.repository;

import com.example.demo.entity.Todo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository for {@link Todo} entities.
 */
@Repository
public interface TodoRepository extends JpaRepository<Todo, Long> {
}
