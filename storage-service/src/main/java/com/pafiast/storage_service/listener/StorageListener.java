package com.pafiast.storage_service.listener;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pafiast.storage_service.entity.IOC;
import com.pafiast.storage_service.repository.IOCRepository;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
public class StorageListener {

    private static final Logger logger = LoggerFactory.getLogger(StorageListener.class);
    private final IOCRepository repository;
    private final ObjectMapper objectMapper;

    public StorageListener(IOCRepository repository) {
        this.repository = repository;
        this.objectMapper = new ObjectMapper();
    }

    @KafkaListener(topics = "ranked-iocs", groupId = "storage-group")
    public void storeIOC(ConsumerRecord<String, String> record) {
        try {
            String message = record.value();
            logger.info("Received ranked IOC for storage: {}", message);

            JsonNode jsonNode = objectMapper.readTree(message);
            
            String iocValue = jsonNode.get("ioc").asText();
            String iocType = jsonNode.get("type").asText();
            String source = jsonNode.get("source").asText();
            int severityScore = jsonNode.has("severity_score") ? 
                               jsonNode.get("severity_score").asInt() : 50;
            boolean validated = jsonNode.has("validated") ? 
                               jsonNode.get("validated").asBoolean() : true;

            // Check if IOC already exists
            repository.findByIocValueAndIocType(iocValue, iocType)
                .ifPresentOrElse(existingIOC -> {
                    // Update existing
                    existingIOC.setSeverityScore(severityScore);
                    existingIOC.setValidated(validated);
                    repository.save(existingIOC);
                    logger.info("🔄 Updated existing IOC: {}", iocValue);
                }, () -> {
                    // Save new
                    IOC ioc = new IOC(iocValue, iocType, source);
                    ioc.setSeverityScore(severityScore);
                    ioc.setValidated(validated);
                    repository.save(ioc);
                    logger.info("💾 Saved new IOC: {}", iocValue);
                });

        } catch (Exception e) {
            logger.error("Error storing IOC: {}", e.getMessage(), e);
        }
    }
}
