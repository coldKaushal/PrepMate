package com.example.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@RestController
public class WebhookController {

    private static final String VERIFY_TOKEN = "my-secret-token-123";
    private final ObjectMapper objectMapper = new ObjectMapper();

    // Handle GET for verification
    @GetMapping("/webhook")
    public ResponseEntity<String> verifyWebhook(
        @RequestParam(name = "hub.mode", required = false) String mode,
        @RequestParam(name = "hub.verify_token", required = false) String token,
        @RequestParam(name = "hub.challenge", required = false) String challenge
    ) {
        if ("subscribe".equals(mode) && VERIFY_TOKEN.equals(token)) {
            return ResponseEntity.ok(challenge);
        } else {
            return ResponseEntity.status(403).body("Verification failed");
        }
    }

    // Handle POST for incoming messages
    @PostMapping("/webhook")
    public ResponseEntity<Void> receiveMessage(@RequestBody String payload) {
        try {
            JsonNode root = objectMapper.readTree(payload);
            JsonNode messagesNode = root.path("entry").get(0)
                                        .path("changes").get(0)
                                        .path("value").path("messages");

            if (messagesNode.isArray() && messagesNode.size() > 0) {
                JsonNode message = messagesNode.get(0);
                String from = message.path("from").asText(); // User's phone number
                String text = message.path("text").path("body").asText(); // Text content

                // output the received message
                System.out.println("Received message from: " + from);
                System.out.println("Message: " + text);
            }

        } catch (Exception e) {
            System.err.println("Failed to parse incoming message: " + e.getMessage());
        }

        return ResponseEntity.ok().build();
    }
}
