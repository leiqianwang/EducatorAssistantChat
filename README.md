# Educator Assistant Chat - Service Architecture Refactoring

## Overview
This document outlines the modular service architecture for the Educator Assistant Chat system, breaking down the monolithic ChatService into specialized, focused components.

## Current Issues
- **ChatService.java**: 600+ lines of code (violation of Single Responsibility Principle)
- **Mixed concerns**: Session management, AI processing, validation, and data persistence in one class
- **Hard to maintain**: Changes in one feature affect the entire service
- **Poor testability**: Difficult to unit test individual features

## Proposed Modular Architecture

### 1. Core Service Layer Components

#### 1.1 ChatOrchestrationService
**Responsibility**: Main coordinator for chat requests
- Validates incoming requests
- Coordinates between different specialized services
- Builds final responses
- Handles error scenarios

**Lines of Code**: ~100-150 lines

#### 1.2 SessionManagementService
**Responsibility**: Chat session lifecycle management
- Create new sessions
- Retrieve existing sessions
- Update session activity
- Manage session context and metadata

**Lines of Code**: ~80-120 lines

#### 1.3 MessagePersistenceService
**Responsibility**: Message storage and retrieval
- Save user messages
- Save AI responses
- Retrieve chat history
- Message search and filtering

**Lines of Code**: ~60-100 lines

#### 1.4 AIResponseService
**Responsibility**: AI model interaction and response generation
- Route requests to appropriate AI models
- Handle different action types
- Manage AI client configurations
- Token usage tracking

**Lines of Code**: ~120-180 lines

### 2. Specialized Action Services

#### 2.1 TranslationService
**Responsibility**: Handle translation operations
- Validate translation parameters
- Build translation prompts
- Execute translation via AI model
- Process and format translation results

**API Endpoint**: `POST /api/chat/actions/translate`

#### 2.2 SummarizationService
**Responsibility**: Handle content summarization
- Validate summarization parameters
- Build summarization prompts
- Execute summarization via AI model
- Format summary results

**API Endpoint**: `POST /api/chat/actions/summarize`

#### 2.3 RewritingService
**Responsibility**: Handle content rewriting
- Validate rewriting parameters
- Build rewriting prompts
- Execute rewriting via AI model
- Format rewritten content

**API Endpoint**: `POST /api/chat/actions/rewrite`

#### 2.4 QuestionGenerationService
**Responsibility**: Handle educational question generation
- Validate question generation parameters
- Build question prompts with educational context
- Execute question generation via AI model
- Format and structure generated questions

**API Endpoint**: `POST /api/chat/actions/generate-questions`

### 3. Support Services

#### 3.1 ContextBuilderService
**Responsibility**: Build contextual information for AI prompts
- Aggregate session history
- Include educational context
- Build user-specific context
- Format context for AI consumption

#### 3.2 PromptEngineeringService
**Responsibility**: Manage and optimize AI prompts
- Template management
- Dynamic prompt generation
- A/B testing for prompt effectiveness
- Prompt versioning

#### 3.3 ResponseEnhancementService
**Responsibility**: Enhance AI responses with additional data
- Add suggested prompts
- Include metadata
- Format responses for frontend
- Add educational recommendations

## Implementation Plan

### Phase 1: Extract Core Services (Week 1)
1. Create SessionManagementService
2. Create MessagePersistenceService
3. Create ContextBuilderService
4. Update ChatOrchestrationService to use extracted services

### Phase 2: Extract Action Services (Week 2)
1. Create TranslationService with dedicated controller endpoint
2. Create SummarizationService with dedicated controller endpoint
3. Create RewritingService with dedicated controller endpoint
4. Create QuestionGenerationService with dedicated controller endpoint

### Phase 3: Advanced Services (Week 3)
1. Create PromptEngineeringService
2. Create ResponseEnhancementService
3. Implement real-time adaptation features
4. Add advanced analytics and monitoring

### Phase 4: AI Model Integration Enhancement (Week 4)
1. Implement context-aware model selection
2. Add response quality scoring
3. Implement adaptive prompt optimization
4. Add real-time learning capabilities

## API Design

### General Chat Endpoint
```
POST /api/chat/message
Content-Type: application/json

{
  "prompt": "Help me create a lesson plan",
  "sessionId": "session_abc123",
  "userId": "educator_001",
  "educationalContext": {
    "subject": "Mathematics",
    "gradeLevel": "6-8"
  }
}
```

### Action-Specific Endpoints

#### Translation
```
POST /api/chat/actions/translate
Content-Type: application/json

{
  "prompt": "Hello students, welcome to class",
  "actionParams": {
    "targetLanguage": "Spanish",
    "tone": "formal"
  },
  "educationalContext": {
    "subject": "Spanish Language",
    "gradeLevel": "6-8"
  }
}
```

#### Summarization
```
POST /api/chat/actions/summarize
Content-Type: application/json

{
  "prompt": "Long educational content...",
  "actionParams": {
    "summaryType": "BULLET_POINTS",
    "maxLength": 200
  },
  "educationalContext": {
    "subject": "Science",
    "gradeLevel": "9-12"
  }
}
```

#### Question Generation
```
POST /api/chat/actions/generate-questions
Content-Type: application/json

{
  "prompt": "Photosynthesis process content...",
  "actionParams": {
    "questionCount": 5,
    "difficultyLevel": "intermediate",
    "questionTypes": ["multiple_choice", "short_answer"]
  },
  "educationalContext": {
    "subject": "Biology",
    "gradeLevel": "9-12",
    "lessonTopic": "Photosynthesis"
  }
}
```

## Benefits of This Architecture

### 1. Single Responsibility Principle
- Each service has one clear purpose
- Easier to understand and maintain
- Better separation of concerns

### 2. Improved Testability
- Each service can be unit tested independently
- Mock dependencies easily
- Better test coverage

### 3. Scalability
- Individual services can be scaled independently
- Microservice-ready architecture
- Easy to add new action types

### 4. Maintainability
- Changes in one service don't affect others
- Easier to debug issues
- Clear code organization

### 5. Reusability
- Services can be reused across different endpoints
- Common functionality extracted to shared services
- Better code reuse

## Implementation Guidelines

### Service Design Principles
1. **Single Responsibility**: Each service should have one clear purpose
2. **Dependency Injection**: Use Spring's DI for loose coupling
3. **Interface Segregation**: Define clear interfaces for each service
4. **Error Handling**: Consistent error handling across all services
5. **Logging**: Comprehensive logging for debugging and monitoring

### Testing Strategy
1. **Unit Tests**: Test each service in isolation
2. **Integration Tests**: Test service interactions
3. **API Tests**: Test controller endpoints
4. **Performance Tests**: Ensure response times are acceptable

### Monitoring and Analytics
1. **Response Times**: Track AI model response times
2. **Usage Patterns**: Monitor which actions are most used
3. **Error Rates**: Track and alert on error rates
4. **User Satisfaction**: Implement feedback mechanisms

## Next Steps
1. Review and approve this architecture plan
2. Begin Phase 1 implementation
3. Set up testing infrastructure
4. Implement monitoring and logging
5. Plan for gradual migration from monolithic service
