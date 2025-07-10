package com.educatorassistantchat.fullstackproject.config;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.ai.openai.api.OpenAiApi;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration class for AI Chat Assistant using Spring AI
 */
@Configuration
public class ChatAssistantConfig {

    @Value("${OPENAI_API_KEY}")
    private String openAiApiKey;

    @Value("${spring.ai.openai.chat.options.model:gpt-3.5-turbo}")
    private String modelName;

    @Value("${spring.ai.openai.chat.options.max-tokens:1000}")
    private Integer maxTokens;

    @Value("${spring.ai.openai.chat.options.temperature:0.7}")
    private Double temperature;

    /**
     * Configure OpenAI API client
     */
    @Bean
    public OpenAiApi openAiApi() {
        return new OpenAiApi(openAiApiKey);
    }

    /**
     * Configure OpenAI Chat Model for general conversations
     */
    @Bean("generalChatModel")
    public OpenAiChatModel generalChatModel(OpenAiApi openAiApi) {
        return new OpenAiChatModel(openAiApi,
            OpenAiChatOptions.builder()
                .model(modelName)
                .maxTokens(maxTokens)
                .temperature(temperature.floatValue())
                .build());
    }

    /**
     * Configure OpenAI Chat Model for educational tasks
     */
    @Bean("educationalChatModel")
    public OpenAiChatModel educationalChatModel(OpenAiApi openAiApi) {
        return new OpenAiChatModel(openAiApi,
            OpenAiChatOptions.builder()
                .model("gpt-4")
                .maxTokens(2000)
                .temperature(0.3f)
                .build());
    }

    /**
     * Configure ChatClient for general chat functionality
     */
    @Bean("generalChatClient")
    public ChatClient generalChatClient(ChatModel generalChatModel) {
        return ChatClient.builder(generalChatModel)
                .defaultSystem("You are a helpful AI assistant designed to help educators and students. " +
                             "Provide clear, educational, and engaging responses.")
                .build();
    }

    /**
     * Configure ChatClient for educational functionality
     */
    @Bean("educationalChatClient")
    public ChatClient educationalChatClient(ChatModel educationalChatModel) {
        return ChatClient.builder(educationalChatModel)
                .defaultSystem("You are an expert educational assistant with deep knowledge in pedagogy, " +
                             "curriculum design, and teaching methodologies. Provide professional, " +
                             "research-backed educational guidance.")
                .build();
    }

    /**
     * Configure ChatClient for translation tasks
     */
    @Bean("translationChatClient")
    public ChatClient translationChatClient(ChatModel generalChatModel) {
        return ChatClient.builder(generalChatModel)
                .defaultSystem("You are a professional translator. Provide accurate, contextually " +
                             "appropriate translations while maintaining the original meaning and tone.")
                .build();
    }

    /**
     * Configure ChatClient for summarization tasks
     */
    @Bean("summarizationChatClient")
    public ChatClient summarizationChatClient(ChatModel generalChatModel) {
        return ChatClient.builder(generalChatModel)
                .defaultSystem("You are an expert at creating concise, informative summaries. " +
                             "Extract key points and present them clearly.")
                .build();
    }

    /**
     * Configure ChatClient for rewriting tasks
     */
    @Bean("rewritingChatClient")
    public ChatClient rewritingChatClient(ChatModel generalChatModel) {
        return ChatClient.builder(generalChatModel)
                .defaultSystem("You are an expert writer and editor. Rewrite content to improve " +
                             "clarity, tone, and effectiveness while maintaining the original meaning.")
                .build();
    }

    /**
     * Configure ChatClient for question generation
     */
    @Bean("questionGenerationChatClient")
    public ChatClient questionGenerationChatClient(ChatModel educationalChatModel) {
        return ChatClient.builder(educationalChatModel)
                .defaultSystem("You are an expert in educational assessment and question design. " +
                             "Create well-structured, thought-provoking questions that assess " +
                             "understanding at various cognitive levels.")
                .build();
    }
}
