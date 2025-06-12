package com.example.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.service.WhatsAppService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
public class WebhookController {
    private static final Logger logger = LoggerFactory.getLogger(WebhookController.class);
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Value("${whatsapp.webhook.verify_token}")
    private String verifyToken;

    @Autowired
    private WhatsAppService whatsAppService;

    // Handle GET for verification
    @GetMapping("/webhook")
    public ResponseEntity<String> verifyWebhook(
        @RequestParam(name = "hub.mode", required = false) String mode,
        @RequestParam(name = "hub.verify_token", required = false) String token,
        @RequestParam(name = "hub.challenge", required = false) String challenge
    ) {
        System.out.println("something happened1" + LocalDateTime.now());
        logger.info("Received webhook verification request - Mode: {}, Token: {}, Challenge: {}", mode, token, challenge);
        
        if ("subscribe".equals(mode) && verifyToken.equals(token)) {
            logger.info("Webhook verification successful");
            return ResponseEntity.ok(challenge);
        } else {
            logger.warn("Webhook verification failed - Mode: {}, Token: {}", mode, token);
            return ResponseEntity.status(403).body("Verification failed");
        }
    }

    // Handle POST for incoming messages
    @PostMapping("/webhook")
    public ResponseEntity<Void> receiveMessage(@RequestBody String payload) {
        logger.info("Received webhook payload: {}", payload);
        try {
            JsonNode root = objectMapper.readTree(payload);
            JsonNode messagesNode = root.path("entry").get(0)
                                        .path("changes").get(0)
                                        .path("value").path("messages");

            if (messagesNode.isArray() && messagesNode.size() > 0) {
                JsonNode message = messagesNode.get(0);
                String from = message.path("from").asText();
                String text = message.path("text").path("body").asText();

                logger.info("Processing message from: {}, content: {}", from, text);
                whatsAppService.processMessage(root);
            }

        } catch (Exception e) {
            logger.error("Failed to process webhook payload: {}", e.getMessage(), e);
        }

        return ResponseEntity.ok().build();
    }
}
