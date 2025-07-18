package com.educatorassistantchat.fullstackproject.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Map;

/**
 * Configuration class for AI Chat Assistant using Ollama
 */
@Configuration
public class ChatAssistantConfig {

    @Value("${spring.ai.ollama.chat.options.model:llama3.1:8b}")
    private String modelName;

    @Value("${spring.ai.ollama.chat.options.max-tokens:1000}")
    private Integer maxTokens;

    @Value("${spring.ai.ollama.chat.options.temperature:0.7}")
    private Double temperature;

    @Value("${spring.ai.ollama.base-url:http://localhost:11434}")
    private String ollamaBaseUrl;

    /**
     * Configure WebClient for Ollama API
     */
    @Bean
    public WebClient ollamaWebClient() {
        return WebClient.builder()
                .baseUrl(ollamaBaseUrl)
                .build();
    }

    /**
     * Simple Ollama service for making API calls
     */
    @Bean
    public OllamaService ollamaService(WebClient ollamaWebClient) {
        return new OllamaService(ollamaWebClient, modelName, temperature, maxTokens);
    }

    /**
     * Simple Ollama service for making API calls
     */
    public static class OllamaService {
        private final WebClient webClient;
        private final String model;
        private final Double temperature;
        private final Integer maxTokens;

        public OllamaService(WebClient webClient, String model, Double temperature, Integer maxTokens) {
            this.webClient = webClient;
            this.model = model;
            this.temperature = temperature;
            this.maxTokens = maxTokens;
        }

        public Mono<String> generateResponse(String prompt) {
            return webClient.post()
                    .uri("/api/generate")
                    .bodyValue(Map.of(
                            "model", model,
                            "prompt", prompt,
                            "stream", false,
                            "options", Map.of(
                                    "temperature", temperature,
                                    "num_predict", maxTokens
                            )
                    ))
                    .retrieve()
                    .bodyToMono(Map.class)
                    .map(response -> (String) response.get("response"));
        }
    }
}
