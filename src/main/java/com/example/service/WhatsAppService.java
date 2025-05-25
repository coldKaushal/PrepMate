package com.example.service;

import com.example.config.WhatsAppConfig;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Service
public class WhatsAppService {

    private final WhatsAppConfig whatsAppConfig;
    private final RestTemplate restTemplate;

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
        String responseMessage = "Hello, how can I help you today?";
        
        try {
            System.out.println(responseMessage);
            // Then send a text response back to the user
            sendMessage(phoneNumber, responseMessage);
            
            return responseMessage;
        } catch (Exception e) {
            responseMessage = "Failed to process the message";
            try {
                sendMessage(phoneNumber, responseMessage);
            } catch (Exception ex) {
                // Log the error but don't throw it since we already have a primary error
                System.err.println("Failed to send error message: " + ex.getMessage());
            }
            return responseMessage;
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
} 