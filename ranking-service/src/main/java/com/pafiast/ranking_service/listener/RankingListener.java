package com.pafiast.ranking_service.listener;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Random;

@Component
public class RankingListener {

    private static final Logger logger = LoggerFactory.getLogger(RankingListener.class);
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;
    private final Random random;

    public RankingListener(KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
        this.objectMapper = new ObjectMapper();
        this.random = new Random();
    }

    @KafkaListener(topics = "validated-iocs", groupId = "ranking-group")
    public void rankIOC(ConsumerRecord<String, String> record) {
        try {
            String message = record.value();
            logger.info("Received validated IOC for ranking: {}", message);

            JsonNode jsonNode = objectMapper.readTree(message);
            String ioc = jsonNode.get("ioc").asText();
            String type = jsonNode.get("type").asText();

            // Simulate external ranking API call
            int severityScore = calculateSeverityScore(ioc, type);

            // Create ranked IOC
            ObjectNode rankedIOC = (ObjectNode) jsonNode;
            rankedIOC.put("severity_score", severityScore);
            rankedIOC.put("ranked_at", java.time.Instant.now().toString());
            rankedIOC.put("ranking_method", "simulated_api");

            String rankedJson = objectMapper.writeValueAsString(rankedIOC);
            kafkaTemplate.send("ranked-iocs", rankedJson);
            
            logger.info("🎯 Ranked {} with severity: {}", ioc, severityScore);

        } catch (Exception e) {
            logger.error("Error ranking IOC: {}", e.getMessage(), e);
            // Fallback: assign default severity
            try {
                JsonNode jsonNode = objectMapper.readTree(record.value());
                ((ObjectNode) jsonNode).put("severity_score", 50);
                ((ObjectNode) jsonNode).put("error", "ranking_failed");
                kafkaTemplate.send("ranked-iocs", objectMapper.writeValueAsString(jsonNode));
            } catch (Exception ex) {
                logger.error("Failed to send fallback ranking: {}", ex.getMessage());
            }
        }
    }

    private int calculateSeverityScore(String ioc, String type) {
        // Simulate ranking logic (replace with actual API call)
        // In production, this would call external ranking API
        
        int baseScore = random.nextInt(60) + 40; // 40-100
        
        // Adjust based on type
        if ("IP".equalsIgnoreCase(type)) {
            baseScore += 10;
        }
        
        // Ensure score is between 0-100
        return Math.min(100, Math.max(0, baseScore));
    }
}