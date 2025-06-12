package com.example.service;

import com.example.config.WhatsAppConfig;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

@Service
public class WhatsAppService {
    private static final Logger logger = LoggerFactory.getLogger(WhatsAppService.class);
    private final WhatsAppConfig whatsAppConfig;
    private final RestTemplate restTemplate;
    private static final String AI_COMPANION_URL = "http://localhost:8000/ai-companion";

    public WhatsAppService(WhatsAppConfig whatsAppConfig) {
        this.whatsAppConfig = whatsAppConfig;
        this.restTemplate = new RestTemplate();
    }

    public void sendTemplateMessage(String phoneNumber, String templateName) {
        String url = String.format("%s/%s/messages",
                whatsAppConfig.getApiUrl(),
                whatsAppConfig.getPhoneNumberId());

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + whatsAppConfig.getAccessToken());

        Map<String, Object> messageData = new HashMap<>();
        messageData.put("messaging_product", "whatsapp");
        messageData.put("to", phoneNumber);
        messageData.put("type", "template");
        
        Map<String, Object> template = new HashMap<>();
        template.put("name", templateName);
        
        Map<String, String> language = new HashMap<>();
        language.put("code", "en_US");
        template.put("language", language);
        
        messageData.put("template", template);

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(messageData, headers);
        restTemplate.postForEntity(url, request, String.class);
    }

    public void sendMessage(String phoneNumber, String message) {
        System.out.println(message);
        String url = String.format("%s/%s/messages",
                whatsAppConfig.getApiUrl(),
                whatsAppConfig.getPhoneNumberId());

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + whatsAppConfig.getAccessToken());

        Map<String, Object> messageData = new HashMap<>();
        messageData.put("messaging_product", "whatsapp");
        messageData.put("recipient_type", "individual");
        messageData.put("to", phoneNumber);
        messageData.put("type", "text");
        
        Map<String, String> textData = new HashMap<>();
        textData.put("body", message);
        messageData.put("text", textData);

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(messageData, headers);
        restTemplate.postForEntity(url, request, String.class);
    }

    public String processMessage(JsonNode webhookPayload) {
        String phoneNumber = extractPhoneNumber(webhookPayload);
        String userMessage = extractMessage(webhookPayload);
        String userName = extractName(webhookPayload);
        
        try {
            // Prepare request for AI companion
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            Map<String, String> requestBody = new HashMap<>();
            requestBody.put("user_phone_number", phoneNumber);
            requestBody.put("user_name", userName); // You might want to get this from a user database
            requestBody.put("user_message", userMessage);

            HttpEntity<Map<String, String>> request = new HttpEntity<>(requestBody, headers);
            
            // Call AI companion API
            logger.info("Sending request to AI companion: {}", requestBody);
            ResponseEntity<JsonNode> responseEntity = restTemplate.postForEntity(AI_COMPANION_URL, request, JsonNode.class);
            
            String aiResponse = responseEntity.getBody().get("response").asText();
            logger.info("Received response from AI companion: {}", aiResponse);
            
            // Send the AI response back to the user
            sendMessage(phoneNumber, aiResponse);
            
            return aiResponse;
        } catch (Exception e) {
            String errorMessage = "Sorry, I'm having trouble processing your request right now.";
            logger.error("Error processing message: {}", e.getMessage(), e);
            try {
                sendMessage(phoneNumber, errorMessage);
            } catch (Exception ex) {
                logger.error("Failed to send error message: {}", ex.getMessage(), ex);
            }
            return errorMessage;
        }
    }

    private String extractPhoneNumber(JsonNode webhookPayload) {
        // Extract phone number from WhatsApp webhook payload
        try {
            return webhookPayload
                .get("entry").get(0)
                .get("changes").get(0)
                .get("value")
                .get("messages").get(0)
                .get("from").asText();
        } catch (Exception e) {
            throw new RuntimeException("Failed to extract phone number from webhook payload", e);
        }
    }

    private String extractMessage(JsonNode webhookPayload) {
        // Extract message text from WhatsApp webhook payload
        try {
            return webhookPayload
                .get("entry").get(0)
                .get("changes").get(0)
                .get("value")
                .get("messages").get(0)
                .get("text")
                .get("body").asText();
        } catch (Exception e) {
            throw new RuntimeException("Failed to extract message from webhook payload", e);
        }
    }

    private String extractName(JsonNode webhookPayload){
        try {
            return webhookPayload
                .get("entry").get(0)
                .get("changes").get(0)
                .get("value")
                .get("contacts").get(0)
                .get("profile")
                .get("name").asText();
        } catch (Exception e) {
            throw new RuntimeException("Failed to extract name from webhook payload", e);
        }
    }
} 