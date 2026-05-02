package com.pafiast.storage_service.controller;

import com.pafiast.storage_service.entity.IOC;
import com.pafiast.storage_service.repository.IOCRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/iocs")
@CrossOrigin(origins = "*")
public class IOCController {

    private final IOCRepository repository;

    public IOCController(IOCRepository repository) {
        this.repository = repository;
    }

    @GetMapping
    public ResponseEntity<List<IOC>> getAllIOCs() {
        return ResponseEntity.ok(repository.findAll());
    }

    @GetMapping("/{value}")
    public ResponseEntity<IOC> getIOCByValue(@PathVariable String value) {
        return repository.findByIocValue(value)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/type/{type}")
    public ResponseEntity<List<IOC>> getIOCsByType(@PathVariable String type) {
        return ResponseEntity.ok(repository.findByIocType(type));
    }

    @GetMapping("/severity/high")
    public ResponseEntity<List<IOC>> getHighSeverityIOCs(@RequestParam(defaultValue = "70") int threshold) {
        return ResponseEntity.ok(repository.findBySeverityScoreGreaterThan(threshold));
    }

    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getStatistics() {
        Map<String, Object> stats = new HashMap<>();
        
        long total = repository.count();
        long ipCount = repository.countByIocType("IP");
        long domainCount = repository.countByIocType("DOMAIN");
        Double avgSeverity = repository.getAverageSeverityScore();
        
        stats.put("total_iocs", total);
        stats.put("ip_count", ipCount);
        stats.put("domain_count", domainCount);
        stats.put("average_severity", avgSeverity != null ? avgSeverity : 0);
        stats.put("timestamp", new Date().toString());
        
        return ResponseEntity.ok(stats);
    }

    @GetMapping("/search")
    public ResponseEntity<List<IOC>> searchIOCs(@RequestParam String keyword) {
        return ResponseEntity.ok(repository.searchIOCs(keyword));
    }
}