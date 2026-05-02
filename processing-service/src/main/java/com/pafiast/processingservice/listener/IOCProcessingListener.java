package com.pafiast.processingservice.listener;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

@Component
public class IOCProcessingListener {

    private static final Logger logger = LoggerFactory.getLogger(IOCProcessingListener.class);
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    public IOCProcessingListener(KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
        this.objectMapper = new ObjectMapper();
    }

    @KafkaListener(topics = "raw-iocs", groupId = "processing-group")
    public void processIOC(ConsumerRecord<String, String> record) {
        try {
            String message = record.value();
            logger.info("Received raw IOC: {}", message);

            JsonNode jsonNode = objectMapper.readTree(message);
            String ioc = jsonNode.get("ioc").asText();
            String type = jsonNode.get("type").asText();
            String source = jsonNode.get("source").asText();

            // Validate IOC
            if (validateIOC(ioc, type)) {
                Map<String, Object> validatedIOC = new HashMap<>();
                validatedIOC.put("ioc", ioc);
                validatedIOC.put("type", type);
                validatedIOC.put("source", source);
                validatedIOC.put("validated", true);
                validatedIOC.put("processed_at", java.time.Instant.now().toString());

                String validatedJson = objectMapper.writeValueAsString(validatedIOC);
                kafkaTemplate.send("validated-iocs", validatedJson);
                logger.info("✅ Validated and forwarded: {}", ioc);
            } else {
                logger.warn("❌ Invalid IOC rejected: {}", ioc);
            }

        } catch (Exception e) {
            logger.error("Error processing IOC: {}", e.getMessage(), e);
        }
    }

    private boolean validateIOC(String ioc, String type) {
        if (ioc == null || ioc.trim().isEmpty()) {
            return false;
        }

        if ("IP".equalsIgnoreCase(type)) {
            return isValidIP(ioc);
        } else if ("DOMAIN".equalsIgnoreCase(type)) {
            return isValidDomain(ioc);
        }

        return false;
    }

    private boolean isValidIP(String ip) {
        String ipRegex = "^((25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)$";
        return ip.matches(ipRegex);
    }

    private boolean isValidDomain(String domain) {
        String domainRegex = "^[a-zA-Z0-9][a-zA-Z0-9-]{0,61}[a-zA-Z0-9]?(\\.[a-zA-Z]{2,})+$";
        return domain.matches(domainRegex);
    }
}