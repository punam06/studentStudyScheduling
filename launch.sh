#!/bin/bash

# Student Scheduling System - Final Launch Script
# This script provides the quickest way to run the application

clear
echo "🎓 Student Scheduling System"
echo "============================"
echo

# Quick build and run
echo "🚀 Building and launching application..."
cd "/Users/punam/Desktop/javaProject"

# Build the project
./gradlew build --quiet

if [ $? -eq 0 ]; then
    echo "✅ Build successful!"
    echo
    echo "📱 Launching Student Scheduling System..."
    echo
    echo "📋 Login Information:"
    echo "   Admin:   email='admin@example.com'   password='admin123'"
    echo "   Student: email='student@example.com' password='student123'"
    echo
    echo "💡 The application will automatically:"
    echo "   • Try to connect to MySQL database"
    echo "   • Fall back to demo mode if database unavailable"
    echo "   • Skip OTP verification in demo mode"
    echo
    echo "🔧 For full functionality with email OTP:"
    echo "   1. Install and start MySQL"
    echo "   2. Configure email in EmailService.java"
    echo "   3. Run ./setup_database.sh"
    echo

    # Launch the application
    ./gradlew run
else
    echo "❌ Build failed. Please check the errors above."
    exit 1
fi
