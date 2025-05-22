package com.example.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class WebhookConfig {
    
    @Value("${whatsapp.webhook.verify_token}")
    private String verifyToken;

    public String getVerifyToken() {
        return verifyToken;
    }
} 