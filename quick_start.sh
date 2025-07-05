#!/bin/bash

# Student Scheduling System - Quick Start Script
# This script helps you get the application running quickly

clear
echo "🎓 Student Scheduling System - Quick Start"
echo "=========================================="
echo

# Function to check if command exists
command_exists() {
    command -v "$1" >/dev/null 2>&1
}

# Function to check if MySQL is running
mysql_running() {
    pgrep mysqld >/dev/null 2>&1
}

# Step 1: Check Prerequisites
echo "📋 Checking Prerequisites..."
echo

# Check Java
if command_exists java; then
    JAVA_VERSION=$(java -version 2>&1 | head -n1 | cut -d'"' -f2)
    echo "✅ Java found: $JAVA_VERSION"
else
    echo "❌ Java not found. Please install Java 17 or higher."
    exit 1
fi

# Check MySQL
if command_exists mysql; then
    echo "✅ MySQL found"
    if mysql_running; then
        echo "✅ MySQL service is running"
    else
        echo "⚠️  MySQL service not running. Starting..."
        if command_exists brew; then
            brew services start mysql
        elif command_exists systemctl; then
            sudo systemctl start mysql
        else
            echo "❌ Please start MySQL manually: sudo systemctl start mysql"
            exit 1
        fi
    fi
else
    echo "❌ MySQL not found. Please install MySQL first."
    echo "   macOS: brew install mysql"
    echo "   Ubuntu: sudo apt install mysql-server"
    exit 1
fi

echo

# Step 2: Database Setup
echo "🗄️  Database Setup..."
echo

# Check if database exists
DB_EXISTS=$(mysql -u root -p --execute="SHOW DATABASES LIKE 'student_scheduling';" 2>/dev/null | wc -l)

if [ $DB_EXISTS -eq 0 ]; then
    echo "Database 'student_scheduling' not found. Setting up..."
    echo "Please enter your MySQL root password:"

    if [ -f "./setup_database.sh" ]; then
        ./setup_database.sh
    else
        echo "❌ Database setup script not found. Please run manually:"
        echo "   mysql -u root -p < database_setup.sql"
        exit 1
    fi
else
    echo "✅ Database 'student_scheduling' already exists"
fi

echo

# Step 3: Configuration Check
echo "⚙️  Configuration Check..."
echo

CONFIG_FILE="src/main/java/org/example/util/EmailService.java"
if grep -q "your-email@gmail.com" "$CONFIG_FILE" 2>/dev/null; then
    echo "⚠️  Email not configured yet. Please update:"
    echo "   $CONFIG_FILE"
    echo "   Replace 'your-email@gmail.com' with your actual email"
    echo "   Replace 'your-app-password' with your Gmail App Password"
    echo
    echo "📧 Gmail App Password Setup:"
    echo "   1. Enable 2FA on your Gmail account"
    echo "   2. Go to Google Account → Security → App passwords"
    echo "   3. Generate password for 'Mail'"
    echo "   4. Use this 16-character password in the config"
    echo
else
    echo "✅ Email configuration appears to be set"
fi

DB_CONFIG_FILE="src/main/java/org/example/database/DatabaseConfig.java"
if grep -q 'DB_PASSWORD = ""' "$DB_CONFIG_FILE" 2>/dev/null; then
    echo "⚠️  Database password not set. Please update:"
    echo "   $DB_CONFIG_FILE"
    echo "   Set DB_PASSWORD to your MySQL root password"
    echo
else
    echo "✅ Database configuration appears to be set"
fi

echo

# Step 4: Build and Run
echo "🚀 Starting Application..."
echo

# Build first
echo "Building application..."
./gradlew build --quiet

if [ $? -eq 0 ]; then
    echo "✅ Build successful"
    echo
    echo "🎯 Launching Student Scheduling System..."
    echo
    echo "Default login accounts:"
    echo "  Admin: username='admin', password='admin123'"
    echo "  Student: username='student', password='student123'"
    echo
    echo "⚠️  Note: Change these passwords after first login!"
    echo

    # Run the application
    ./gradlew run
else
    echo "❌ Build failed. Please check the error messages above."
    exit 1
fi
