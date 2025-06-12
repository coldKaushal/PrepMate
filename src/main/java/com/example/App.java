package com.example;

import com.example.service.WhatsAppService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

@SpringBootApplication
public class App {
    public static void main(String[] args) {
        ApplicationContext context = SpringApplication.run(App.class, args);
        
        // Get WhatsApp service bean
        WhatsAppService whatsAppService = context.getBean(WhatsAppService.class);
        
        // Send an initial hello world message
        // whatsAppService.sendMessage("919821106093", "Server is up and ready to receive messages!");
    }
}
