package com.pafiast.ranking_service.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/ranking")
@CrossOrigin(origins = "*")
public class RankingController {

    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> healthCheck() {
        Map<String, String> health = new HashMap<>();
        health.put("service", "ranking-service");
        health.put("status", "UP");
        return ResponseEntity.ok(health);
    }

    @GetMapping("/status")
    public ResponseEntity<Map<String, Object>> getStatus() {
        Map<String, Object> status = new HashMap<>();
        status.put("service", "ranking-service");
        status.put("consumer_group", "ranking-group");
        status.put("input_topic", "validated-iocs");
        status.put("output_topic", "ranked-iocs");
        status.put("running", true);
        return ResponseEntity.ok(status);
    }
}