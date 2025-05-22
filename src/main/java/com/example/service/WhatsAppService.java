package com.example.service;

import com.example.config.WhatsAppConfig;
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
} 