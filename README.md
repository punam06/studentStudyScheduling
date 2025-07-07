# Study Squad Synchronizer

A comprehensive Java Swing application for managing study group schedules with file-based authentication, email-based OTP verification, and persistent data storage.

## 🌟 Key Features

### Enhanced Authentication & Security
- **Email-Based OTP Verification**: Secure two-factor authentication for every login
- **Persistent User Registration**: User accounts save between sessions - no need to re-register
- **Role-Based Access Control**: Admin and Student roles with different permissions
- **Password Reset Functionality**: Easy password recovery with email verification
- **Secure Password Storage**: SHA-256 hashing with salt for enhanced security

### Intelligent Scheduling System
- **Persistent Schedule Storage**: Schedules and members save automatically between sessions
- **Find Common Slots**: Discover available meeting times for all group members
- **Conflict-Free Scheduling**: Automatic detection and prevention of overlapping appointments
- **Emergency Scheduling**: Admin-only force scheduling for urgent meetings
- **Smart Data Management**: Automatic saving during all scheduling operations

### Study Group Management
- **Member Management**: Add, edit, remove group members with persistent storage
- **Group Organization**: Organize members into different study groups
- **Schedule Coordination**: Find optimal meeting times for specific groups
- **Persistent Member Data**: Member information automatically saved and loaded

### Email Integration
- **OTP Delivery**: Secure 6-digit codes sent via email for authentication
- **Meeting Invitations**: Professional HTML email templates for meeting notifications
- **Automatic Notifications**: Email updates for schedule changes and meetings
- **Gmail Integration**: Works with Gmail accounts using App Passwords

### Data Persistence
- **File-Based Storage**: All data saved in JSON format for reliability
- **Automatic Backup**: Data automatically saved during operations
- **Cross-Session Continuity**: Resume work exactly where you left off
- **No Database Required**: Simple file-based system for easy deployment

## 🚀 Quick Start

### Prerequisites
- Java 17 or higher
- Gmail account with App Password (for email functionality)
- Internet connection (required for OTP delivery)

### Installation

1. **Clone the repository**
   ```bash
   git clone https://github.com/punam06/studentStudyScheduling.git
   cd studentStudyScheduling
   ```

2. **Configure Email Settings**
   Edit `src/main/java/org/example/util/EmailService.java`:
   ```java
   private static final String EMAIL_USERNAME = "your-email@gmail.com";
   private static final String EMAIL_PASSWORD = "your-app-password";
   ```

3. **Build and Run**
   ```bash
   # Using Gradle wrapper
   ./gradlew build
   ./gradlew run
   
   # Or using launch script
   ./launch.sh
   ```

## 🔐 Authentication System

### Default Accounts
The system comes with pre-configured test accounts:

- **Admin Account**: 
  - Email: `admin@example.com`
  - Password: `admin123`
  - Access: Full system administration

- **Student Account**:
  - Email: `student@example.com`
  - Password: `student123`
  - Access: Standard user features

### Login Process
1. **Enter Email and Password**: Use your registered email address
2. **Credential Validation**: System verifies your credentials
3. **OTP Generation**: 6-digit code automatically sent to your email
4. **OTP Verification**: Enter the code in the verification dialog (check console for testing)
5. **Access Granted**: Login to the main application

**Important**: The OTP verification dialog will always appear when you click login with valid credentials.

### User Registration
- **Persistent Registration**: Register once and your account is saved permanently
- **Email Verification**: All accounts require valid email addresses
- **Automatic Role Assignment**: New users get Student role by default
- **No Re-registration**: User data persists between application sessions

### Password Recovery
1. Click "Forgot Password" on the login screen
2. Enter your registered email address
3. Set a new password
4. Confirm your new password
5. Your password is updated immediately

## 📅 Scheduling Features

### Persistent Schedule Management
- **Automatic Saving**: All schedule changes saved immediately
- **Session Continuity**: Schedules persist between application restarts
- **Member Persistence**: Added members remain available after logout
- **Find Common Slots**: Discover optimal meeting times that persist

### Advanced Scheduling
- **Smart Conflict Detection**: Prevents overlapping appointments
- **Group-Based Scheduling**: Find common times for specific groups
- **Emergency Scheduling**: Admin override for urgent meetings
- **Schedule History**: All scheduling data maintained across sessions

## 🛠️ Technical Features

### Enhanced User Experience
- **Guaranteed OTP Dialog**: Verification window always appears during login
- **Robust Error Handling**: Comprehensive error messages and recovery
- **Auto-Save Functionality**: Data automatically saved during operations
- **Debug Logging**: Detailed console output for troubleshooting

### File-Based Architecture
- **JSON Storage**: Human-readable data format
- **Automatic File Management**: Creates and manages data files automatically
- **Cross-Platform**: Works on Windows, macOS, and Linux
- **No Database Setup**: Simple deployment without database configuration

### Email System
- **Gmail Integration**: Works with Gmail App Passwords
- **Fallback Mode**: Graceful degradation when email is unavailable
- **OTP Generation**: Secure 6-digit codes with 10-minute expiration
- **Professional Templates**: HTML email formatting for notifications

## 📊 System Architecture

### Authentication Flow
1. **File-Based User Storage**: Users stored in `users.json`
2. **OTP Service**: In-memory OTP management with email delivery
3. **Session Management**: Secure user session handling
4. **Role-Based Access**: Different features for Admin vs Student users

### Data Management
- **DataManager**: Handles all file I/O operations
- **Automatic Persistence**: Data saved during user actions
- **JSON Serialization**: Reliable data format with error handling
- **Backup Strategy**: Multiple file locations checked for reliability

## 🔧 Configuration

### Email Setup (Required for OTP)
1. Enable 2-factor authentication on your Gmail account
2. Generate an App Password for the application
3. Update credentials in `EmailService.java`
4. Test email functionality during first login

### Application Settings
- **Data Storage**: Files saved in project root directory
- **Debug Mode**: Console logging enabled by default
- **Time Zones**: Uses system local time
- **Session Timeout**: OTP codes expire after 10 minutes

## 🚨 Important Notes

### Security Features
- **Mandatory OTP**: Every login requires email verification
- **Persistent Users**: Registration data saved between sessions
- **Password Security**: Salted SHA-256 hashing
- **Session Security**: Proper logout and session management

### Data Persistence
- **Automatic Saving**: No manual save required
- **File Reliability**: Multiple backup strategies
- **Cross-Session Data**: All work preserved between sessions
- **Member Continuity**: Added members persist permanently

## 📖 Documentation

- **User Guide**: [USER_GUIDE.md](USER_GUIDE.md) - Detailed usage instructions
- **Configuration**: [CONFIGURATION.md](CONFIGURATION.md) - System setup details
- **Launch Scripts**: Use `launch.sh` for quick startup

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch
3. Test all authentication and persistence features
4. Ensure OTP verification works correctly
5. Submit a pull request

## 📄 License

This project is part of the Bangladesh University of Professionals coursework.

---

**Version**: 2.0  
**Last Updated**: July 2025  
**Repository**: https://github.com/punam06/studentStudyScheduling
