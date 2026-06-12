package com.example.demo;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class Greeter {

    @GetMapping("/")
    public String greet() {
        return "Hello from Java Maven on NevTan Cloud!";
    }

    @GetMapping("/health")
    public String health() {
        return "OK";
    }
}
