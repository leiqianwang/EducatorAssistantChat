@echo off
echo ========================================
echo Educator Assistant Chat Database Setup
echo ========================================
echo.

echo This script will create the educator_chat_db database and all required tables.
echo Make sure MySQL server is running and you have the correct credentials.
echo.

set /p MYSQL_USER=Enter MySQL username (default: root): 
if "%MYSQL_USER%"=="" set MYSQL_USER=root

set /p MYSQL_PASSWORD=Enter MySQL password: 

echo.
echo Creating database and tables...
mysql -u %MYSQL_USER% -p%MYSQL_PASSWORD% < schema.sql

if %ERRORLEVEL% EQU 0 (
    echo.
    echo ========================================
    echo Database setup completed successfully!
    echo ========================================
    echo.
    echo The following database and tables have been created:
    echo - Database: educator_chat_db
    echo - Tables: chat_sessions, chat_messages, action_parameters, suggested_prompts, user_preferences
    echo.
    echo You can now start your Spring Boot application.
) else (
    echo.
    echo ========================================
    echo Database setup failed!
    echo ========================================
    echo.
    echo Please check:
    echo 1. MySQL server is running
    echo 2. Username and password are correct
    echo 3. User has CREATE DATABASE privileges
    echo.
    echo You can also run the schema.sql file manually using:
    echo mysql -u %MYSQL_USER% -p < schema.sql
)

pause 