package com.example.itsupport.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

/**
 * General application configuration beans.
 */
@Configuration
public class AppConfig {

    /**
     * Declares RestTemplate as a Spring-managed bean so it can be injected,
     * configured centrally (e.g. timeouts), and mocked in unit tests.
     */
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}

