# Database Setup for Educator Assistant Chat

This directory contains the MySQL database schema and setup instructions for the Educator Assistant Chat application.

## Prerequisites

1. **MySQL Server** (version 8.0 or higher)
2. **MySQL Client** (for running SQL commands)
3. **MySQL user with CREATE DATABASE privileges**

## Database Setup Instructions

### 1. Start MySQL Server
Make sure your MySQL server is running on localhost:3306.

### 2. Create Database and Tables
Run the following command to execute the schema script:

```bash
# Option 1: Using mysql command line client
mysql -u root -p < schema.sql

# Option 2: Using MySQL Workbench or phpMyAdmin
# Copy and paste the contents of schema.sql into your MySQL client
```

### 3. Verify Database Setup
After running the schema script, you should have:

- Database: `educator_chat_db`
- Tables:
  - `chat_sessions` - Stores chat session information
  - `chat_messages` - Stores individual chat messages
  - `action_parameters` - Stores default parameters for different actions
  - `suggested_prompts` - Stores suggested prompts for users
  - `user_preferences` - Stores user-specific preferences

### 4. Verify Application Configuration
Make sure your `application.properties` file has the correct database configuration:

```properties
# Database Configuration (MySQL)
spring.datasource.url=jdbc:mysql://localhost:3306/educator_chat_db?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true
spring.datasource.username=root
spring.datasource.password=root
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver

# JPA/Hibernate Configuration
spring.jpa.hibernate.ddl-auto=update
spring.jpa.database-platform=org.hibernate.dialect.MySQL8Dialect
```

## Database Schema Overview

### Chat Sessions Table
- Stores information about chat sessions
- Each session has a unique session ID
- Tracks user ID, creation time, and session status

### Chat Messages Table
- Stores individual messages within sessions
- Supports different message types (USER, ASSISTANT, SYSTEM)
- Tracks action types and parameters for each message
- Includes performance metrics (response time, tokens used)

### Action Parameters Table
- Stores default parameters for different action types
- Supports dynamic parameter configuration
- Parameters can be of different types (STRING, INTEGER, BOOLEAN, JSON)

### Suggested Prompts Table
- Stores suggested prompts for different action types
- Includes categorization and usage tracking
- Supports active/inactive status

### User Preferences Table
- Stores user-specific preferences
- Includes language preferences and UI settings
- Supports JSON storage for flexible preferences

## Troubleshooting

### Common Issues

1. **"Unknown database 'educator_chat_db'"**
   - Solution: Run the schema.sql script to create the database

2. **"Access denied for user"**
   - Solution: Check your MySQL username and password in application.properties
   - Ensure the user has proper permissions

3. **"Connection refused"**
   - Solution: Make sure MySQL server is running on localhost:3306

4. **"Table doesn't exist"**
   - Solution: Run the schema.sql script to create all tables

### Database Connection Test
You can test the database connection using:

```bash
mysql -u root -p -h localhost -P 3306 educator_chat_db
```

## Backup and Restore

### Backup Database
```bash
mysqldump -u root -p educator_chat_db > backup_$(date +%Y%m%d_%H%M%S).sql
```

### Restore Database
```bash
mysql -u root -p educator_chat_db < backup_file.sql
```

## Performance Considerations

- The schema includes appropriate indexes for common query patterns
- JSON columns are used for flexible data storage
- Foreign key constraints ensure data integrity
- Timestamps are automatically managed for audit trails 