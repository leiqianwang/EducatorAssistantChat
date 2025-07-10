package com.educatorassistantchat.fullstackproject.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * Service for generating contextual suggested prompts for Educators
 */
@Service
@Slf4j
public class SuggestedPromptsService {

    private final Map<String, List<String>> educatorPrompts = new HashMap<>();
    private final Map<String, List<String>> actionPrompts = new HashMap<>();

    public SuggestedPromptsService() {
        initializePrompts();
    }

    /**
     * Generate contextual prompts based on subject and action type
     */
    public List<String> generateContextualPrompts(String subject, String actionType) {
        List<String> prompts = new ArrayList<>();

        // Add general educator prompts
        prompts.addAll(educatorPrompts.getOrDefault("general", new ArrayList<>()));

        // Add subject-specific prompts if subject is provided
        if (subject != null && !subject.isEmpty()) {
            prompts.addAll(educatorPrompts.getOrDefault(subject.toLowerCase(), new ArrayList<>()));
        }

        // Add action-specific prompts if action type is provided
        if (actionType != null && !actionType.isEmpty()) {
            prompts.addAll(getActionSpecificPrompts(actionType));
        }

        // Shuffle and return top 5 prompts
        Collections.shuffle(prompts);
        return prompts.stream().limit(5).toList();
    }

    /**
     * Get action-specific prompts
     */
    private List<String> getActionSpecificPrompts(String actionType) {
        return actionPrompts.getOrDefault(actionType.toLowerCase(), new ArrayList<>());
    }

    /**
     * Initialize all prompt categories for educators
     */
    private void initializePrompts() {
        initializeEducatorPrompts();
        initializeActionPrompts();
    }

    /**
     * Initialize educator-specific prompts
     */
    private void initializeEducatorPrompts() {
        // General educator prompts
        educatorPrompts.put("general", Arrays.asList(
            "Create a lesson plan for today's topic",
            "Generate discussion questions for class engagement",
            "Design an assessment rubric",
            "Suggest classroom management strategies",
            "Create homework assignments that reinforce learning",
            "Develop differentiated instruction approaches",
            "Generate parent communication templates",
            "Design interactive learning activities",
            "Create a quiz for this lesson",
            "Suggest ways to make this topic more engaging",
            "Help me plan a group project",
            "Generate ice-breaker activities for class"
        ));

        // Math-specific prompts
        educatorPrompts.put("math", Arrays.asList(
            "Create word problems for algebra practice",
            "Generate step-by-step problem solutions",
            "Design math games for concept reinforcement",
            "Create visual aids for geometric concepts",
            "Develop real-world math applications",
            "Make practice worksheets for this concept",
            "Create math center activities"
        ));

        // English/Language Arts prompts
        educatorPrompts.put("english", Arrays.asList(
            "Create creative writing prompts",
            "Generate reading comprehension questions",
            "Design vocabulary building exercises",
            "Create grammar practice activities",
            "Develop literature analysis guides",
            "Plan a book club discussion",
            "Create writing rubrics"
        ));

        // Science prompts
        educatorPrompts.put("science", Arrays.asList(
            "Design hands-on science experiments",
            "Create lab safety protocols",
            "Generate hypothesis testing activities",
            "Develop science fair project ideas",
            "Create concept mapping exercises",
            "Plan field trip connections",
            "Design STEM challenges"
        ));

        // Social Studies prompts
        educatorPrompts.put("social studies", Arrays.asList(
            "Create timeline activities",
            "Design role-playing scenarios",
            "Generate current events discussions",
            "Create map-based activities",
            "Develop cultural comparison projects"
        ));
    }

    /**
     * Initialize action-specific prompts
     */
    private void initializeActionPrompts() {
        actionPrompts.put("translate", Arrays.asList(
            "Translate this lesson content to Spanish for ELL students",
            "Convert technical terms to simple language",
            "Translate parent communication letters",
            "Create multilingual classroom resources",
            "Make vocabulary cards in multiple languages"
        ));

        actionPrompts.put("summarize", Arrays.asList(
            "Summarize this chapter in bullet points",
            "Create a one-paragraph summary for students",
            "Extract key concepts from this text",
            "Generate executive summary for administrators",
            "Make student-friendly chapter highlights"
        ));

        actionPrompts.put("rewrite", Arrays.asList(
            "Rewrite this for elementary students",
            "Make this more engaging and interactive",
            "Convert to formal academic language",
            "Simplify this explanation for struggling learners",
            "Adapt this content for different grade levels"
        ));

        actionPrompts.put("question_generation", Arrays.asList(
            "Create quiz questions from this content",
            "Generate discussion starters",
            "Make multiple choice questions",
            "Create open-ended reflection questions",
            "Design critical thinking questions"
        ));
    }

    /**
     * Add custom prompt for educator
     */
    public void addCustomPrompt(String category, String prompt) {
        educatorPrompts.computeIfAbsent(category.toLowerCase(), k -> new ArrayList<>()).add(prompt);
        log.info("Added custom educator prompt in category {}", category);
    }

    /**
     * Get prompts by category
     */
    public List<String> getPromptsByCategory(String category) {
        return new ArrayList<>(educatorPrompts.getOrDefault(category.toLowerCase(), new ArrayList<>()));
    }
}
