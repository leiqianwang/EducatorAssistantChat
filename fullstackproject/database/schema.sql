-- MySQL Database Schema for Educator Assistant Chat
-- Database: educator_chat_db

-- Create database if it doesn't exist
CREATE DATABASE IF NOT EXISTS educator_chat_db
CHARACTER SET utf8mb4
COLLATE utf8mb4_unicode_ci;

-- Use the database
USE educator_chat_db;

-- Chat Sessions Table
CREATE TABLE IF NOT EXISTS chat_sessions (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    session_id VARCHAR(255) NOT NULL UNIQUE,
    user_id VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    is_active BOOLEAN DEFAULT TRUE,
    session_data JSON,
    INDEX idx_session_id (session_id),
    INDEX idx_user_id (user_id),
    INDEX idx_created_at (created_at)
);

-- Chat Messages Table
CREATE TABLE IF NOT EXISTS chat_messages (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    session_id VARCHAR(255) NOT NULL,
    message_type ENUM('USER', 'ASSISTANT', 'SYSTEM') NOT NULL,
    content TEXT NOT NULL,
    action_type ENUM('TRANSLATE', 'SUMMARIZE', 'REWRITE', 'QUESTION_GENERATION', 'GENERAL') DEFAULT 'GENERAL',
    action_parameters JSON,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    sequence_number INT DEFAULT 0,
    is_processed BOOLEAN DEFAULT FALSE,
    response_time_ms INT,
    tokens_used INT,
    FOREIGN KEY (session_id) REFERENCES chat_sessions(session_id) ON DELETE CASCADE,
    INDEX idx_session_id (session_id),
    INDEX idx_message_type (message_type),
    INDEX idx_action_type (action_type),
    INDEX idx_created_at (created_at),
    INDEX idx_sequence_number (sequence_number)
);

-- Action Parameters Configuration Table
CREATE TABLE IF NOT EXISTS action_parameters (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    action_type ENUM('TRANSLATE', 'SUMMARIZE', 'REWRITE', 'QUESTION_GENERATION') NOT NULL,
    parameter_name VARCHAR(100) NOT NULL,
    parameter_value TEXT,
    parameter_type ENUM('STRING', 'INTEGER', 'BOOLEAN', 'JSON') DEFAULT 'STRING',
    is_default BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE KEY unique_action_param (action_type, parameter_name),
    INDEX idx_action_type (action_type),
    INDEX idx_is_default (is_default)
);

-- Suggested Prompts Table
CREATE TABLE IF NOT EXISTS suggested_prompts (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    prompt_text TEXT NOT NULL,
    action_type ENUM('TRANSLATE', 'SUMMARIZE', 'REWRITE', 'QUESTION_GENERATION', 'GENERAL') DEFAULT 'GENERAL',
    category VARCHAR(100),
    usage_count INT DEFAULT 0,
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_action_type (action_type),
    INDEX idx_category (category),
    INDEX idx_usage_count (usage_count),
    INDEX idx_is_active (is_active)
);

-- User Preferences Table
CREATE TABLE IF NOT EXISTS user_preferences (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id VARCHAR(255) NOT NULL UNIQUE,
    preferred_language VARCHAR(10) DEFAULT 'en',
    educational_context VARCHAR(100),
    preferred_action_types JSON,
    ui_preferences JSON,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_user_id (user_id)
);

-- Insert default action parameters
INSERT INTO action_parameters (action_type, parameter_name, parameter_value, parameter_type, is_default) VALUES
-- Translation parameters
('TRANSLATE', 'target_language', 'Spanish', 'STRING', TRUE),
('TRANSLATE', 'original_language', 'auto-detect', 'STRING', TRUE),
('TRANSLATE', 'tone', 'neutral', 'STRING', TRUE),

-- Summarization parameters
('SUMMARIZE', 'summary_type', 'PARAGRAPH', 'STRING', TRUE),
('SUMMARIZE', 'max_length', '200', 'INTEGER', TRUE),
('SUMMARIZE', 'focus_area', 'main_ideas', 'STRING', TRUE),

-- Rewriting parameters
('REWRITE', 'target_audience', 'general', 'STRING', TRUE),
('REWRITE', 'tone', 'professional', 'STRING', TRUE),
('REWRITE', 'purpose', 'educational', 'STRING', TRUE),

-- Question Generation parameters
('QUESTION_GENERATION', 'question_count', '5', 'INTEGER', TRUE),
('QUESTION_GENERATION', 'difficulty_level', 'intermediate', 'STRING', TRUE),
('QUESTION_GENERATION', 'question_types', '["multiple_choice", "short_answer"]', 'JSON', TRUE),
('QUESTION_GENERATION', 'cognitive_level', '["knowledge", "comprehension", "application"]', 'JSON', TRUE);

-- Insert sample suggested prompts
INSERT INTO suggested_prompts (prompt_text, action_type, category) VALUES
-- Translation prompts
('Translate this text to Spanish', 'TRANSLATE', 'basic'),
('Translate this educational content to French', 'TRANSLATE', 'educational'),
('Translate this technical document to German', 'TRANSLATE', 'technical'),

-- Summarization prompts
('Summarize this article in 3 sentences', 'SUMMARIZE', 'basic'),
('Create a bullet-point summary of this text', 'SUMMARIZE', 'structured'),
('Summarize the main ideas from this chapter', 'SUMMARIZE', 'educational'),

-- Rewriting prompts
('Rewrite this text in a more formal tone', 'REWRITE', 'tone'),
('Rewrite this for a high school audience', 'REWRITE', 'audience'),
('Rewrite this explanation in simpler terms', 'REWRITE', 'simplification'),

-- Question Generation prompts
('Generate 5 multiple choice questions from this text', 'QUESTION_GENERATION', 'assessment'),
('Create short answer questions about this topic', 'QUESTION_GENERATION', 'comprehension'),
('Generate questions to test understanding of this concept', 'QUESTION_GENERATION', 'evaluation'),

-- General prompts
('Explain this concept in detail', 'GENERAL', 'explanation'),
('What are the key points of this text?', 'GENERAL', 'analysis'),
('How can I improve this writing?', 'GENERAL', 'feedback');

-- Create indexes for better performance
CREATE INDEX idx_chat_messages_session_sequence ON chat_messages(session_id, sequence_number);
CREATE INDEX idx_chat_sessions_user_active ON chat_sessions(user_id, is_active);
CREATE INDEX idx_action_parameters_type_default ON action_parameters(action_type, is_default); 