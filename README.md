# Student Scheduling System

A comprehensive Java Swing application for managing study group schedules with MySQL database integration and mandatory OTP-based authentication.

## 🌟 Features

### Authentication & Security
- **Mandatory Two-Factor Authentication**: OTP required for every login via email
- **Role-Based Access Control**: Admin and Student roles (Regular User removed)
- **Secure Password Storage**: BCrypt hashing with salt
- **Email Verification**: Required for all user accounts
- **Separate Registration**: Distinct registration paths for Students and Admins

### Conflict-Free Scheduling
- **Automatic Conflict Detection**: Uses ArrayList-based conflict checking
- **Overlap Prevention**: Prevents double-booking of time slots
- **Conflict Reporting**: Detailed information about scheduling conflicts
- **Smart Scheduling**: Ensures no overlapping meetings or study sessions

### Database Integration
- **MySQL Database**: Persistent data storage
- **Connection Pooling**: HikariCP for efficient database connections
- **Auto Schema Creation**: Database tables created automatically
- **Data Integrity**: Foreign key constraints and indexes

### Study Group Management
- **Member Management**: Add, edit, remove group members
- **Schedule Coordination**: Find common available time slots with conflict prevention
- **Meeting Scheduling**: Schedule meetings with automatic conflict detection
- **Force Scheduling**: Admin-only emergency meeting scheduling

### Communication
- **Email Integration**: JavaMail API for sending notifications
- **Meeting Invitations**: HTML email templates
- **OTP Delivery**: Secure code delivery for authentication
- **Mandatory Email Verification**: OTP sent to registered email for every login

## 🚀 Quick Start

### Prerequisites
- Java 17 or higher
- MySQL Server 8.0+
- Gmail account with App Password (for email functionality)

### Installation

1. **Clone the repository**
   ```bash
   git clone <repository-url>
   cd student_scheduling
   ```

2. **Set up MySQL Database**
   ```bash
   mysql -u root -p < database_setup.sql
   ```

3. **Configure Email Settings**
   Edit `src/main/java/org/example/util/EmailService.java`:
   ```java
   private static final String EMAIL_USERNAME = "your-email@gmail.com";
   private static final String EMAIL_PASSWORD = "your-app-password";
   ```

4. **Build and Run**
   ```bash
   # Using Gradle wrapper
   ./gradlew build
   ./gradlew run
   
   # Or using quick start script
   ./quick_start.sh
   ```

## 🔐 Authentication System

### Account Types
- **Student Account**: Access to personal scheduling and study group participation
- **Admin Account**: Full system access including user management and force scheduling

### Login Process
1. Enter username and password
2. System validates credentials
3. OTP is automatically sent to registered email address
4. Enter 6-digit OTP code within 10 minutes
5. Access granted upon successful verification

**Note**: OTP verification is mandatory for every login - no bypass options available.

### Password Recovery
1. Click "Forgot Password" on the login screen
2. Enter your registered email address in the large, visible email field
3. Create a new password (no character limitations)
4. Confirm your new password
5. System verifies your email and updates your password
6. Login with your new credentials

**Enhanced Password Recovery Features**:
- **Large, Visible Email Field**: 25-character wide field with clear borders
- **No Password Limitations**: Set any length password you prefer
- **Professional Interface**: Clean, user-friendly dialog design
- **Auto-Focus**: Email field automatically selected for quick entry
- **Clear Instructions**: Helpful guidance throughout the process

### Registration Process
1. Choose account type (Student or Admin)
2. Enter username, email, and password
3. OTP sent to email for verification
4. Complete registration with OTP verification

## 📅 Scheduling Features

### Conflict-Free Scheduling
- **Automatic Detection**: System prevents overlapping time slots
- **Real-time Validation**: Immediate feedback on scheduling conflicts
- **Conflict Resolution**: Detailed information about conflicting appointments
- **Smart Suggestions**: Alternative time slots when conflicts occur

### Time Slot Management
- Add new time slots with automatic conflict checking
- Remove existing time slots
- View all scheduled items with conflict indicators
- Get detailed conflict reports

## 🛠️ Technical Implementation

### Security Enhancements
- **Mandatory OTP**: Every login requires email verification
- **Email Validation**: Ensures valid email addresses for all accounts
- **Enhanced Password Security**: BCrypt with salt for password storage
- **Session Management**: Secure user session handling

### Scheduling Algorithm
- **ArrayList-based Conflict Detection**: Efficient O(n) conflict checking
- **Overlap Detection**: Precise time slot overlap calculation
- **Conflict Reporting**: Comprehensive conflict information
- **Schedule Integrity**: Maintains schedule consistency

### Database Schema
- Users table with role-based permissions
- Time slots with conflict prevention
- OTP verification tracking
- Session management

## 📋 Default Accounts

For testing purposes, default accounts are available:

- **Admin Account**: 
  - Username: `admin`
  - Password: `admin123`
  - Email: `admin@example.com`

- **Student Account**:
  - Username: `student` 
  - Password: `student123`
  - Email: `student@example.com`

**Important**: These accounts also require OTP verification for login.

## 🔧 Configuration

### Email Configuration
The system requires a valid Gmail account with an App Password for OTP delivery:

1. Enable 2-factor authentication on your Gmail account
2. Generate an App Password for the application
3. Update the email credentials in `EmailService.java`

### Database Configuration
MySQL connection settings can be configured in `DatabaseConfig.java`:

```java
private static final String URL = "jdbc:mysql://localhost:3306/student_scheduling";
private static final String USERNAME = "root";
private static final String PASSWORD = "your-password";
```

## 🚨 Important Security Notes

1. **OTP is Mandatory**: Every login requires OTP verification via email
2. **Email Required**: All accounts must have valid email addresses
3. **No Bypass Options**: Security features cannot be disabled
4. **Session Security**: User sessions are properly managed and secured

## 📖 User Guide

For detailed usage instructions, see [USER_GUIDE.md](USER_GUIDE.md).

## ⚙️ Configuration

For system configuration details, see [CONFIGURATION.md](CONFIGURATION.md).

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Test thoroughly (especially security features)
5. Submit a pull request

## 📄 License

This project is licensed under the MIT License - see the LICENSE file for details.

## 🆘 Support

For issues or questions:
1. Check the [USER_GUIDE.md](USER_GUIDE.md) for usage instructions
2. Review the [CONFIGURATION.md](CONFIGURATION.md) for setup help
3. Create an issue in the GitHub repository

## 🏗️ Architecture

- **Frontend**: Java Swing GUI with custom components
- **Backend**: Java with MySQL database integration
- **Security**: BCrypt password hashing + OTP verification
- **Communication**: JavaMail API for email notifications
- **Scheduling**: Conflict-free algorithm with ArrayList-based detection
