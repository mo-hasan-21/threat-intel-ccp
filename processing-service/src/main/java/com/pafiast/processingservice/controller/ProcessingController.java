package com.pafiast.processingservice.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/processing")
@CrossOrigin(origins = "*")
public class ProcessingController {

    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> healthCheck() {
        Map<String, String> health = new HashMap<>();
        health.put("service", "processing-service");
        health.put("status", "UP");
        return ResponseEntity.ok(health);
    }

    @GetMapping("/status")
    public ResponseEntity<Map<String, Object>> getStatus() {
        Map<String, Object> status = new HashMap<>();
        status.put("service", "processing-service");
        status.put("consumer_group", "processing-group");
        status.put("input_topic", "raw-iocs");
        status.put("output_topic", "validated-iocs");
        status.put("running", true);
        return ResponseEntity.ok(status);
    }
}