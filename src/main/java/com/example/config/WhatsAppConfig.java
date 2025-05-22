package com.example.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class WhatsAppConfig {
    
    @Value("${whatsapp.api.url:https://graph.facebook.com/v22.0}")
    private String apiUrl;
    
    @Value("${whatsapp.phone.number.id:587081517832628}")
    private String phoneNumberId;
    
    @Value("${whatsapp.access.token:EAAO89fpb46YBOZB4NY6oonCWoZC5Npr0klsOBFreD3FtIpW8BkR8fawmQZApE5VoSdfAPCz3ZCrAB5ekoL9Ri8ApcZAk6MhIKGxfFHt1SLZCSmaCqEl93lWuOgZBAOuN2oco6FGnuiGRVzrM64mX636OabQ7jUZAVBewvTqE5Dwt8cQWBD6Qq09IYFh1L7ZAtcNvgeDZCe2fVZB0CuCXZAMsbP86cR87ikdSm2HIKhMZD}")
    private String accessToken;

    public String getApiUrl() {
        return apiUrl;
    }

    public String getPhoneNumberId() {
        return phoneNumberId;
    }

    public String getAccessToken() {
        return accessToken;
    }
} 