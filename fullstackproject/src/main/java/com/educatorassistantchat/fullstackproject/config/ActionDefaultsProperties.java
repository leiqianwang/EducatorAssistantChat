package com.educatorassistantchat.fullstackproject.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Configuration properties for action type default parameters
 */
@Component
@ConfigurationProperties(prefix = "educator.chat.actions")
@Data
public class ActionDefaultsProperties {

    private TranslateDefaults translate = new TranslateDefaults();
    private SummarizeDefaults summarize = new SummarizeDefaults();
    private RewriteDefaults rewrite = new RewriteDefaults();
    private QuestionGenerationDefaults questionGeneration = new QuestionGenerationDefaults();

    @Data
    public static class TranslateDefaults {
        private String targetLanguage = "Spanish";
        private String originalLanguage = "auto-detect";
        private String tone = "neutral";

        public Map<String, Object> toMap() {
            Map<String, Object> map = new HashMap<>();
            map.put("targetLanguage", targetLanguage);
            map.put("originalLanguage", originalLanguage);
            map.put("tone", tone);
            return map;
        }
    }

    @Data
    public static class SummarizeDefaults {
        private String summaryType = "PARAGRAPH";
        private Integer maxLength = 200;
        private String focusArea = "main_ideas";

        public Map<String, Object> toMap() {
            Map<String, Object> map = new HashMap<>();
            map.put("summaryType", summaryType);
            map.put("maxLength", maxLength);
            map.put("focusArea", focusArea);
            return map;
        }
    }

    @Data
    public static class RewriteDefaults {
        private String targetAudience = "general";
        private String tone = "professional";
        private String purpose = "educational";

        public Map<String, Object> toMap() {
            Map<String, Object> map = new HashMap<>();
            map.put("targetAudience", targetAudience);
            map.put("tone", tone);
            map.put("purpose", purpose);
            return map;
        }
    }

    @Data
    public static class QuestionGenerationDefaults {
        private Integer questionCount = 5;
        private String difficultyLevel = "intermediate";
        private List<String> questionTypes = List.of("multiple_choice", "short_answer");
        private List<String> cognitiveLevel = List.of("knowledge", "comprehension", "application");

        public Map<String, Object> toMap() {
            Map<String, Object> map = new HashMap<>();
            map.put("questionCount", questionCount);
            map.put("difficultyLevel", difficultyLevel);
            map.put("questionTypes", questionTypes);
            map.put("cognitiveLevel", cognitiveLevel);
            return map;
        }
    }
}
