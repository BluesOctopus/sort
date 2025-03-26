package com.example.demo.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/sort")
@CrossOrigin(origins = "http://localhost:3000")
public class SortingController {

    private final SortingService sortingService;
    public SortingController(SortingService sortingService) {
        this.sortingService = sortingService;
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> startSorting(@RequestBody SortingRequest request) {
            Long sortingTime = sortingService.startSorting(request);
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Sorting request received and processed successfully.");
            response.put("sortingTime", sortingTime);

            return ResponseEntity.ok(response);

    }
} 