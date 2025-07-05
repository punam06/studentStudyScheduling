#!/bin/bash

# Student Scheduling System - Database Setup Script
# This script automates the database creation process

echo "=== Student Scheduling System Database Setup ==="
echo

# Check if MySQL is installed and running
if ! command -v mysql &> /dev/null; then
    echo "❌ MySQL is not installed. Please install MySQL first:"
    echo "   macOS: brew install mysql"
    echo "   Ubuntu: sudo apt install mysql-server"
    echo "   Windows: Download from https://dev.mysql.com/downloads/mysql/"
    exit 1
fi

# Check if MySQL service is running
if ! pgrep mysqld > /dev/null; then
    echo "⚠️  MySQL service is not running. Starting MySQL..."
    if command -v brew &> /dev/null; then
        brew services start mysql
    elif command -v systemctl &> /dev/null; then
        sudo systemctl start mysql
    else
        echo "Please start MySQL service manually"
        exit 1
    fi
fi

echo "✅ MySQL is running"
echo

# Prompt for MySQL root password
echo "Please enter your MySQL root password:"
read -s MYSQL_ROOT_PASSWORD

# Test connection
echo "Testing MySQL connection..."
mysql -u root -p"$MYSQL_ROOT_PASSWORD" -e "SELECT 1;" &> /dev/null

if [ $? -ne 0 ]; then
    echo "❌ Failed to connect to MySQL. Please check your password."
    exit 1
fi

echo "✅ MySQL connection successful"
echo

# Create database
echo "Creating database 'student_scheduling'..."
mysql -u root -p"$MYSQL_ROOT_PASSWORD" -e "CREATE DATABASE IF NOT EXISTS student_scheduling;" 2>/dev/null

if [ $? -eq 0 ]; then
    echo "✅ Database 'student_scheduling' created successfully"
else
    echo "❌ Failed to create database"
    exit 1
fi

# Run setup script
echo "Setting up database tables..."
mysql -u root -p"$MYSQL_ROOT_PASSWORD" student_scheduling < database_setup.sql

if [ $? -eq 0 ]; then
    echo "✅ Database tables created successfully"
else
    echo "❌ Failed to create database tables"
    exit 1
fi

# Verify setup
echo "Verifying database setup..."
TABLE_COUNT=$(mysql -u root -p"$MYSQL_ROOT_PASSWORD" student_scheduling -e "SHOW TABLES;" 2>/dev/null | wc -l)

if [ $TABLE_COUNT -gt 1 ]; then
    echo "✅ Database setup completed successfully!"
    echo
    echo "Tables created:"
    mysql -u root -p"$MYSQL_ROOT_PASSWORD" student_scheduling -e "SHOW TABLES;"
    echo
    echo "🚀 You can now run the application with: ./gradlew run"
else
    echo "❌ Database setup verification failed"
    exit 1
fi

echo
echo "=== Setup Complete ==="
echo "Default accounts will be created on first application run:"
echo "  Admin: username='admin', password='admin123'"
echo "  Student: username='student', password='student123'"
echo
echo "⚠️  Remember to:"
echo "  1. Configure email settings in src/main/java/org/example/util/EmailService.java"
echo "  2. Update database password in src/main/java/org/example/database/DatabaseConfig.java"
echo "  3. Change default passwords after first login"
