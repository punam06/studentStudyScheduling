# Study Squad Synchronizer - User Guide

## Table of Contents
1. [Getting Started](#getting-started)
2. [System Requirements](#system-requirements)
3. [Installation Guide](#installation-guide)
4. [Authentication System](#authentication-system)
5. [User Registration](#user-registration)
6. [OTP Verification](#otp-verification)
7. [Main Application Interface](#main-application-interface)
8. [Schedule Management](#schedule-management)
9. [Study Group Features](#study-group-features)
10. [Email Notifications](#email-notifications)
11. [Data Persistence](#data-persistence)
12. [Role-Based Features](#role-based-features)
13. [Troubleshooting](#troubleshooting)
14. [FAQ](#faq)

---

## Getting Started

### Welcome to Study Squad Synchronizer
This application helps study groups coordinate meeting times efficiently with enhanced security, persistent data storage, and email-based authentication. The system features automatic data saving, cross-session continuity, and mandatory OTP verification for all users.

### Key Benefits
- **Enhanced Security**: Email-based OTP verification for every login attempt
- **Persistent Data**: All schedules, members, and user accounts save automatically between sessions
- **No Re-registration**: Register once and your account is saved permanently
- **Guaranteed OTP**: Verification dialog always appears when logging in
- **Auto-Save**: All changes saved automatically - no manual saving required
- **Cross-Platform**: Works on Windows, macOS, and Linux without database setup

---

## System Requirements

### Minimum Requirements
- **Operating System**: Windows 10, macOS 10.14, or Linux Ubuntu 18.04+
- **Java Runtime**: Java 17 or higher (required)
- **Memory**: 512 MB RAM minimum, 1 GB recommended
- **Storage**: 50 MB free disk space for application and data files
- **Internet**: Required for email-based OTP delivery
- **Email**: Gmail account with App Password (for OTP functionality)

### Recommended Setup
- **Java**: OpenJDK 17 or Oracle JDK 17+
- **Email**: Gmail account with 2FA enabled and App Password configured
- **Screen Resolution**: 1024x768 or higher for optimal interface display
- **Network**: Stable internet connection for reliable OTP delivery

---

## Installation Guide

### Step 1: Download and Setup
1. Clone the repository or download the project files
2. Ensure Java 17+ is installed on your system
3. No database setup required - the application uses file-based storage

### Step 2: Email Configuration (Required)
Edit the email settings in `src/main/java/org/example/util/EmailService.java`:
```java
private static final String EMAIL_USERNAME = "your-email@gmail.com";
private static final String EMAIL_PASSWORD = "your-app-password";
```

**Important**: Use your Gmail App Password, not your regular password.

### Step 3: Run the Application
```bash
# Build and run with Gradle
./gradlew build
./gradlew run

# Or use the launch script
./launch.sh
```

---

## Authentication System

### Enhanced Security Features
The system now features **mandatory email-based OTP verification** for every login attempt. This ensures maximum security for all user accounts.

### Default Test Accounts
The application comes with pre-configured accounts for testing:

- **Admin Account**: 
  - Email: `admin@example.com`
  - Password: `admin123`
  - Features: Full system access, emergency scheduling, user management

- **Student Account**:
  - Email: `student@example.com`
  - Password: `student123`
  - Features: Standard scheduling, group participation

### Login Process (Step-by-Step)
1. **Start Application**: Launch the Study Squad Synchronizer
2. **Enter Credentials**: 
   - Email: Enter your registered email address
   - Password: Enter your password
3. **Click Login**: The system validates your credentials
4. **OTP Generation**: A 6-digit code is automatically sent to your email
5. **OTP Dialog Appears**: Verification window will always open after valid credentials
6. **Enter OTP**: Type the 6-digit code from your email (or check console for testing)
7. **Verification**: Click "Verify" to complete login
8. **Access Granted**: Main application opens upon successful verification

**Important Notes**:
- The OTP verification dialog is guaranteed to appear with valid credentials
- OTP codes expire after 10 minutes
- For testing, OTP codes are also displayed in the console output
- You can use the Enter key to verify the OTP instead of clicking the button

---

## User Registration

### Persistent Registration System
One of the major improvements is that **user registrations now persist between sessions**. Once you register, you never need to register again.

### Registration Process
1. **Click "Register"** on the login screen
2. **Fill Registration Form**:
   - Username: Choose a unique username
   - Email: Enter a valid email address
   - Password: Create a secure password
   - Confirm Password: Re-enter your password
3. **Submit Registration**: Click "OK" to create your account
4. **Account Created**: Your account is immediately saved to the system
5. **Login Ready**: You can now log in with your new credentials

### Registration Features
- **Automatic Saving**: Registered accounts persist between application restarts
- **Email Validation**: System ensures valid email format
- **Unique Checking**: Prevents duplicate usernames and emails
- **Role Assignment**: New users automatically get Student role
- **No Re-registration**: Register once and your account is permanent

---

## OTP Verification

### Enhanced OTP System
The OTP (One-Time Password) system has been significantly improved to ensure reliable operation.

### OTP Dialog Features
- **Always Appears**: Verification dialog guaranteed to open with valid credentials
- **Large Dialog**: 450x300 pixel window for better visibility
- **Input Validation**: Only accepts 6-digit numeric codes
- **Auto-Focus**: OTP field automatically selected for immediate typing
- **Enter Key Support**: Press Enter to verify instead of clicking button
- **Clear Instructions**: Helpful text guides you through the process
- **Error Handling**: Clear error messages if OTP is incorrect

### OTP Code Details
- **6-Digit Format**: All codes are exactly 6 numeric digits
- **10-Minute Expiration**: Codes valid for 10 minutes after generation
- **Single Use**: Each code can only be used once
- **Email Delivery**: Codes sent to your registered email address
- **Console Display**: For testing, codes also appear in application console

### Troubleshooting OTP
If you don't receive the OTP email:
1. Check your spam/junk folder
2. Verify email configuration in EmailService.java
3. Check console output for the code (testing purposes)
4. Ensure internet connection is stable
5. Try logging in again to generate a new code

---

## Main Application Interface

### Application Layout
After successful login, the main interface provides:

- **Calendar Panel**: Central scheduling area with time slots
- **Member Panel**: Right sidebar showing group members
- **Toolbar**: Quick access buttons for common actions
- **Menu Bar**: Full feature access via menus
- **Status Bar**: Real-time information about operations

### Key Interface Features
- **Auto-Save Indicators**: Status messages confirm when data is saved
- **Role-Based Access**: Different features available based on your role
- **Persistent State**: Interface remembers your last session
- **Debug Output**: Console shows detailed operation information

---

## Schedule Management

### Persistent Schedule System
All schedule data now **automatically saves and persists between sessions**. You can close the application and resume exactly where you left off.

### Finding Common Slots
1. **Add Members**: Use the "Add" button to include group members
2. **Find Common Slots**: Click "Find Common Slots" in toolbar or menu
3. **View Results**: Common time slots are highlighted on the calendar
4. **Automatic Saving**: Results are immediately saved to disk
5. **Persistent Storage**: Slots remain available after restarting application

### Schedule Features
- **Conflict Detection**: Automatic prevention of overlapping appointments
- **Visual Indicators**: Clear marking of available and busy times
- **Multiple Attempts**: Can run "Find Common Slots" multiple times without losing data
- **Group Filtering**: Find slots for specific groups or all members
- **Time Flexibility**: 30-minute intervals with customizable duration

### Adding Schedules
1. **Menu Access**: Go to Schedule > Add Schedule
2. **Enter Details**:
   - Date: YYYY-MM-DD format
   - Time: HH:MM format (24-hour)
   - Duration: Minutes (e.g., 60 for one hour)
3. **Automatic Saving**: Schedule immediately saved to persistent storage
4. **Conflict Checking**: System prevents overlapping appointments

---

## Study Group Features

### Member Management
The member system now features **complete persistence** - added members remain in the system between sessions.

### Adding Members
1. **Click "Add" Button**: In the Members panel
2. **Fill Member Information**:
   - Name: Member's full name
   - Email: Contact email address
   - Group: Optional group assignment
3. **Automatic Saving**: Member immediately added to persistent storage
4. **Cross-Session Access**: Members available after application restart

### Group Organization
- **Group Assignment**: Organize members into study groups
- **Group Scheduling**: Find common slots for specific groups
- **Member Editing**: Update member information with persistent saving
- **Group Statistics**: View group membership and availability

### Member Features
- **Edit Members**: Modify member information anytime
- **Remove Members**: Delete members with confirmation
- **Group Management**: Assign and reassign group memberships
- **Email Integration**: Use member emails for notifications

---

## Email Notifications

### Email System Features
- **OTP Delivery**: Secure codes sent for every login
- **Meeting Invitations**: Professional HTML email templates
- **Gmail Integration**: Works with Gmail App Passwords
- **Fallback Mode**: Graceful operation when email unavailable

### Email Configuration
To enable email functionality:
1. **Gmail Setup**: Use a Gmail account with 2FA enabled
2. **App Password**: Generate an App Password (not regular password)
3. **Configuration**: Update credentials in EmailService.java
4. **Testing**: Test with first login OTP

---

## Data Persistence

### Automatic Data Saving
One of the major improvements is **comprehensive data persistence**:

- **User Accounts**: All registrations saved permanently
- **Schedule Data**: All scheduling information persists between sessions
- **Member Information**: Added members remain available
- **Application State**: Resume exactly where you left off

### File Storage System
- **JSON Format**: Human-readable data files
- **Automatic Creation**: Data files created automatically
- **Multiple Locations**: System checks multiple file paths for reliability
- **Backup Strategy**: Robust file handling with error recovery

### Data Files
- **users.json**: Contains all registered user accounts
- **data/members.json**: Stores group member information
- **data/schedules.json**: Contains all scheduling data
- **data/study_group.json**: Study group configuration

---

## Role-Based Features

### Admin Features
- **Full Access**: All scheduling and management features
- **Emergency Scheduling**: Force schedule meetings during conflicts
- **User Management**: Access to user administration features
- **Force Schedule Dialog**: Override scheduling conflicts when necessary

### Student Features
- **Standard Scheduling**: Add schedules and find common slots
- **Group Participation**: Join and participate in study groups
- **Meeting Scheduling**: Schedule meetings during available times
- **Member Management**: Add and manage group members

### Feature Restrictions
- **Force Scheduling**: Only available to Admin users
- **User Administration**: Admin-only features disabled for students
- **Emergency Override**: Students cannot force schedule during conflicts

---

## Troubleshooting

### Common Issues and Solutions

#### Login Problems
**Issue**: "No pending verification found" error
- **Solution**: Updated system now handles this automatically
- **Note**: OTP dialog will always appear with valid credentials

**Issue**: OTP dialog doesn't appear
- **Solution**: System now guarantees OTP dialog opens
- **Check**: Verify credentials are correct

#### Registration Issues
**Issue**: "User already exists" error
- **Solution**: Check if you've already registered (accounts persist)
- **Action**: Try logging in instead of registering

#### Data Not Saving
**Issue**: Members or schedules disappear
- **Solution**: System now auto-saves all changes
- **Check**: Look for save confirmation messages in status bar

#### Email Problems
**Issue**: Not receiving OTP emails
- **Solutions**:
  - Check spam/junk folder
  - Verify EmailService.java configuration
  - Check console output for testing codes
  - Ensure stable internet connection

### Debug Information
- **Console Output**: Detailed logging for troubleshooting
- **Status Messages**: Real-time feedback on operations
- **File Locations**: Check users.json and data/ folder for saved information

---

## FAQ

### General Questions

**Q: Do I need to register every time I use the application?**
A: No! User registration now persists between sessions. Register once and your account is saved permanently.

**Q: Will my schedules and members be saved between sessions?**
A: Yes! All data is automatically saved and will be exactly as you left it when you restart the application.

**Q: Do I need a database to run this application?**
A: No! The application uses file-based storage and doesn't require any database setup.

### Authentication Questions

**Q: Is OTP verification required for every login?**
A: Yes, email-based OTP verification is mandatory for all login attempts for enhanced security.

**Q: What if I don't receive the OTP email?**
A: Check your spam folder, verify email configuration, and look at the console output where OTP codes are displayed for testing purposes.

**Q: Can I bypass the OTP verification?**
A: No, OTP verification is mandatory and cannot be bypassed for security reasons.

### Technical Questions

**Q: What happens if I don't have internet during login?**
A: Internet is required for OTP delivery. The application will show appropriate error messages if email delivery fails.

**Q: How long are OTP codes valid?**
A: OTP codes expire after 10 minutes and can only be used once.

**Q: Where is my data stored?**
A: Data is stored in JSON files in the application directory (users.json and data/ folder).

### Feature Questions

**Q: What's the difference between Admin and Student accounts?**
A: Admins have full access including emergency scheduling and user management, while Students have standard scheduling features.

**Q: Can I edit member information after adding them?**
A: Yes, select a member and click "Edit" to modify their information. Changes are automatically saved.

**Q: How do I find common meeting times?**
A: Add your group members, then click "Find Common Slots" in the toolbar or menu. Results are automatically saved.

---

## Support

For additional support or questions:
- Check the console output for detailed debug information
- Review the CONFIGURATION.md file for setup details
- Ensure all prerequisites are properly installed
- Verify email configuration for OTP functionality

**Version**: 2.0  
**Last Updated**: July 2025  
**Repository**: https://github.com/punam06/studentStudyScheduling
