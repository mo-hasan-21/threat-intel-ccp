package com.pafiast.ingestion_service.controller;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;

import java.time.Instant;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@RestController
@RequestMapping("/api/ingest")
@CrossOrigin(origins = "*")
public class IngestionController {

    private final KafkaTemplate<String, String> kafkaTemplate;

    private static final String IP_REGEX = "\\b(?:\\d{1,3}\\.){3}\\d{1,3}\\b";
    private static final String DOMAIN_REGEX = "\\b(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,}\\b";

    public IngestionController(KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    @PostMapping("/trigger")
    public ResponseEntity<Map<String, Object>> triggerIngestion() {
        Map<String, Object> response = new HashMap<>();

        try {
            // Simulated threat data (replace with real API calls)
            List<String> rawThreatData = Arrays.asList(
                "{\"ip\": \"192.168.1.100\", \"domain\": \"malware.test.com\", \"country\": \"US\", \"abuse_confidence\": 85}",
                "{\"ip\": \"10.0.0.50\", \"domain\": \"phishing.evil.net\", \"country\": \"CN\", \"abuse_confidence\": 92}",
                "{\"ip\": \"172.16.0.25\", \"domain\": \"botnet.bad.org\", \"country\": \"RU\", \"abuse_confidence\": 78}"
            );

            List<String> extractedIOCs = new ArrayList<>();

            for (String data : rawThreatData) {

                // Extract IPs
                Pattern ipPattern = Pattern.compile(IP_REGEX);
                Matcher ipMatcher = ipPattern.matcher(data);

                while (ipMatcher.find()) {
                    String ioc = createIOCJson(ipMatcher.group(), "IP", "abuseipdb");
                    kafkaTemplate.send("raw-iocs", ioc);
                    extractedIOCs.add(ioc);
                }

                // Extract Domains
                Pattern domainPattern = Pattern.compile(DOMAIN_REGEX);
                Matcher domainMatcher = domainPattern.matcher(data);

                while (domainMatcher.find()) {
                    String domain = domainMatcher.group();

                    // Avoid matching IPs as domains
                    if (!domain.matches(IP_REGEX)) {
                        String ioc = createIOCJson(domain, "DOMAIN", "alienvault");
                        kafkaTemplate.send("raw-iocs", ioc);
                        extractedIOCs.add(ioc);
                    }
                }
            }

            response.put("status", "success");
            response.put("message", "Successfully published IOCs to Kafka");
            response.put("count", extractedIOCs.size());
            response.put("topic", "raw-iocs");

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            response.put("status", "error");
            response.put("message", e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }

    private String createIOCJson(String value, String type, String source) {
        return String.format(
            "{\"ioc\":\"%s\",\"type\":\"%s\",\"source\":\"%s\",\"timestamp\":\"%s\"}",
            value, type, source, Instant.now().toString()
        );
    }

    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> healthCheck() {
        Map<String, String> health = new HashMap<>();
        health.put("service", "ingestion-service");
        health.put("status", "UP");
        health.put("timestamp", Instant.now().toString());
        return ResponseEntity.ok(health);
    }

    @GetMapping("/status")
    public ResponseEntity<Map<String, Object>> getStatus() {
        Map<String, Object> status = new HashMap<>();
        status.put("service", "ingestion-service");
        status.put("kafka_bootstrap", "localhost:9092");
        status.put("topic", "raw-iocs");
        status.put("running", true);
        return ResponseEntity.ok(status);
    }
}