package com.example.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;


@Configuration
@ComponentScan("com.example")
public class AppConfig {
    // public AppConfig(){
    //     System.out.println("uo??");
    // }
    // @Bean
    // @Scope("prototype")
    // public Desktop desktop(){
    //     System.out.println("yo?");
    //     return new Desktop();
    // }
}
