# Student Scheduling System - User Guide

## Table of Contents
1. [Getting Started](#getting-started)
2. [System Requirements](#system-requirements)
3. [Installation Guide](#installation-guide)
4. [User Authentication](#user-authentication)
5. [Main Interface](#main-interface)
6. [Study Group Management](#study-group-management)
7. [Scheduling Features](#scheduling-features)
8. [Email Notifications](#email-notifications)
9. [Role-Based Features](#role-based-features)
10. [Troubleshooting](#troubleshooting)
11. [FAQ](#faq)

---

## Getting Started

### Welcome to Student Scheduling System
This application helps study groups coordinate meeting times efficiently. The system supports multiple user roles, secure authentication, and automatic email notifications.

### Key Benefits
- **Secure Access**: Two-factor authentication with email verification
- **Smart Scheduling**: Automatically find common available time slots
- **Role Management**: Different access levels for admins, students, and regular users
- **Email Integration**: Automatic meeting invitations and reminders
- **Persistent Storage**: MySQL database ensures data is never lost

---

## System Requirements

### Minimum Requirements
- **Operating System**: Windows 10, macOS 10.14, or Linux Ubuntu 18.04+
- **Java Runtime**: Java 17 or higher
- **Memory**: 512 MB RAM minimum, 1 GB recommended
- **Storage**: 100 MB free disk space
- **Database**: MySQL 8.0 or MariaDB 10.5+
- **Internet**: Required for email functionality

### Recommended Setup
- **Java**: OpenJDK 17 or Oracle JDK 17+
- **Database**: MySQL 8.0 with InnoDB engine
- **Email**: Gmail account with App Password enabled
- **Screen Resolution**: 1024x768 or higher

---

## Installation Guide

### Step 1: Database Setup

1. **Install MySQL Server**
   ```bash
   # Ubuntu/Debian
   sudo apt update
   sudo apt install mysql-server
   
   # macOS (using Homebrew)
   brew install mysql
   
   # Windows: Download MySQL installer from mysql.com
   ```

2. **Create Database**
   ```bash
   mysql -u root -p
   ```
   ```sql
   CREATE DATABASE student_scheduling;
   exit;
   ```

3. **Run Setup Script**
   ```bash
   mysql -u root -p student_scheduling < database_setup.sql
   ```

### Step 2: Email Configuration

1. **Gmail Setup** (Recommended)
   - Enable 2-Factor Authentication on your Gmail account
   - Go to Google Account → Security → 2-Step Verification → App passwords
   - Generate a new App Password for "Mail"
   - Save this password for configuration

2. **Update Email Settings**
   Edit `src/main/java/org/example/util/EmailService.java`:
   ```java
   private static final String EMAIL_USERNAME = "your-email@gmail.com";
   private static final String EMAIL_PASSWORD = "your-16-character-app-password";
   private static final String FROM_EMAIL = "your-email@gmail.com";
   ```

### Step 3: Database Configuration

Edit `src/main/java/org/example/database/DatabaseConfig.java`:
```java
private static final String DB_URL = "jdbc:mysql://localhost:3306/student_scheduling";
private static final String DB_USERNAME = "root";
private static final String DB_PASSWORD = "your-mysql-password";
```

### Step 4: Build and Run

```bash
# Make gradlew executable (Linux/macOS)
chmod +x gradlew

# Build the application
./gradlew build

# Run the application
./gradlew run
```

---

## User Authentication

### First Time Setup

When you first run the application, default accounts are automatically created:

| Username | Password | Role | Description |
|----------|----------|------|-------------|
| admin | admin123 | ADMIN | Full system access |
| student | student123 | STUDENT | Limited access |

**⚠️ Important**: Change these default passwords immediately in production!

### Registration Process

1. **Click "Register" on login screen**
2. **Fill out registration form**:
   - Username: Choose a unique username
   - Email: Valid email address for OTP delivery
   - Password: Minimum 6 characters
   - Confirm Password: Must match
   - Role: Select Student or Regular User

3. **Email Verification**:
   - Check your email for OTP code
   - Enter 6-digit code in verification dialog
   - Code expires in 10 minutes

4. **Account Activation**:
   - Successfully verified accounts can now login
   - Login requires OTP verification each time

### Login Process

1. **Enter Credentials**:
   - Username and password
   - Click "Login" or press Enter

2. **OTP Verification**:
   - Check email for 6-digit OTP code
   - Enter code in verification dialog
   - Use "Resend OTP" if needed
   - 10-minute expiration timer

3. **Access Granted**:
   - Main application window opens
   - Features available based on user role

### Security Features

- **Password Hashing**: BCrypt with salt for secure storage
- **OTP Expiry**: 10-minute timeout prevents replay attacks
- **Email Masking**: Email addresses partially hidden in UI
- **Session Management**: Automatic logout on application close
- **Role-Based Access**: Features restricted by user permissions

---

## Main Interface

### Application Layout

The main window consists of several key areas:

1. **Menu Bar**: File, Edit, Schedule, Settings, Account, Help
2. **Calendar Panel**: Interactive time grid showing availability
3. **Member Panel**: List of study group members
4. **Toolbar**: Quick access to common functions
5. **Status Bar**: Shows current operation status

### Menu System

#### File Menu
- **New Schedule**: Clear current schedule and start fresh
- **Open Schedule**: Load saved schedule (future feature)
- **Save Schedule**: Save current schedule (future feature)
- **Exit**: Close application

#### Edit Menu
- **Add Member**: Add new member to study group
- **Remove Member**: Remove selected member
- **Find Common Slots**: Highlight available time slots

#### Schedule Menu
- **Schedule Meeting**: Create new meeting
- **Force Schedule Meeting**: Admin-only emergency scheduling
- **Send Invitations**: Email meeting invitations

#### Settings Menu
- **Preferences**: Configure time ranges and minimum members

#### Account Menu
- **View Profile**: Display user information and access level
- **Logout**: Sign out and return to login screen

### Calendar Grid

The calendar displays time slots in a grid format:

- **Time Axis**: Vertical axis shows hours (8 AM - 10 PM by default)
- **Member Columns**: Each study group member has a column
- **Color Coding**:
  - Green: Available time slot
  - Red: Unavailable/busy
  - Blue: Common available slot (all members free)
  - Yellow: Scheduled meeting

### Navigation
- **Previous Day**: ◀ button or keyboard shortcut
- **Today**: Quick return to current date
- **Next Day**: ▶ button
- **Date Selection**: Click to jump to specific date

---

## Study Group Management

### Adding Members

1. **Click "Add" button** in member panel
2. **Enter member details**:
   - Name: Full name of member
   - Email: Email address for notifications
3. **Click OK** to save
4. **Member appears** in list and calendar grid

### Editing Members

1. **Select member** from list
2. **Click "Edit" button**
3. **Modify details** as needed
4. **Click OK** to save changes

### Removing Members

1. **Select member** from list
2. **Click "Remove" button**
3. **Confirm removal** in dialog
4. **Member removed** from group and calendar

### Member Availability

Members can indicate their availability by:
- Clicking on time slots in their column
- Green = Available, Red = Busy
- Availability persists across sessions

---

## Scheduling Features

### Finding Common Time Slots

1. **Add all group members** first
2. **Set member availability** by clicking time slots
3. **Click "Find Common Slots"** button
4. **Blue highlighting** shows times when all members are free

### Scheduling Regular Meetings

1. **Menu: Schedule → Schedule Meeting**
2. **Select time details**:
   - Hour: 0-23 (24-hour format)
   - Minute: 0, 5, 10, ... 55
   - Duration: 15-240 minutes
3. **Enter meeting details**:
   - Subject: Meeting topic
   - Message: Additional information
4. **Click OK** to schedule
5. **Choose to send invitations** immediately

### Force Scheduling (Admin Only)

For emergency or high-priority meetings:

1. **Menu: Schedule → Force Schedule Meeting**
2. **Select priority level**:
   - High: Important meeting
   - Critical: Emergency session
   - Urgent: Must attend
3. **Override availability conflicts**
4. **Automatic notification** to all members

### Meeting Preferences

Configure default settings via **Settings → Preferences**:

- **Start Time**: Default earliest meeting time
- **End Time**: Default latest meeting time
- **Minimum Members**: Required attendees (0 = all required)

---

## Email Notifications

### Email Templates

The system provides professional email templates:

1. **Meeting Invitation**: Standard meeting request
2. **Schedule Update**: Changes to existing meetings
3. **Reminder**: Upcoming meeting notification
4. **OTP Verification**: Authentication codes

### Invitation Process

1. **Schedule or select meeting**
2. **Menu: Schedule → Send Invitations**
3. **Configure details**:
   - Date and time
   - Duration
   - Subject and message
4. **Preview email** before sending
5. **Confirm to send** to all members

### Email Content

Each invitation includes:
- Meeting subject and description
- Date and time details
- Location or meeting link (if specified)
- RSVP instructions
- Professional formatting

### Troubleshooting Email Issues

If emails aren't sending:
1. **Check email configuration** in EmailService.java
2. **Verify Gmail App Password** is correct
3. **Test internet connection**
4. **Check spam folder** for delivered emails
5. **Review console output** for error messages

---

## Role-Based Features

### Administrator Features

Administrators have full access to:
- **Force Schedule Meetings**: Override member availability
- **System Configuration**: Modify application settings
- **User Management**: View all user accounts
- **Emergency Scheduling**: High-priority meeting creation
- **Advanced Preferences**: System-wide configuration

### Student Features

Students have access to:
- **Group Participation**: Join and participate in study groups
- **Meeting Scheduling**: Create meetings (with availability)
- **Email Notifications**: Receive and send invitations
- **Basic Preferences**: Personal settings
- **Availability Management**: Set personal schedule

### Regular User Features

Regular users can:
- **Standard Scheduling**: Full scheduling capabilities
- **Group Coordination**: Organize study sessions
- **Email Integration**: Send and receive notifications
- **Member Management**: Add and manage group members

### Feature Restrictions

| Feature | Admin | Student | Regular User |
|---------|-------|---------|--------------|
| Force Schedule | ✅ | ❌ | ❌ |
| Regular Schedule | ✅ | ✅ | ✅ |
| Add Members | ✅ | ✅ | ✅ |
| Email Invitations | ✅ | ✅ | ✅ |
| System Preferences | ✅ | ❌ | ❌ |
| Emergency Meetings | ✅ | ❌ | ❌ |

---

## Troubleshooting

### Common Issues

#### Application Won't Start

**Problem**: Application fails to launch
**Solutions**:
1. **Check Java version**: `java -version` (need 17+)
2. **Verify database connection**: Ensure MySQL is running
3. **Check dependencies**: Run `./gradlew build` first
4. **Review logs**: Check console output for errors

#### Database Connection Failed

**Problem**: "Cannot connect to database" error
**Solutions**:
1. **Start MySQL service**:
   ```bash
   # Linux
   sudo systemctl start mysql
   
   # macOS
   brew services start mysql
   
   # Windows
   net start mysql
   ```
2. **Check credentials** in DatabaseConfig.java
3. **Verify database exists**: `SHOW DATABASES;` in MySQL
4. **Test connection** with mysql command line

#### Email Not Working

**Problem**: OTP or invitations not sending
**Solutions**:
1. **Verify Gmail App Password**:
   - Must be 16-character app-specific password
   - Not your regular Gmail password
2. **Check email configuration**:
   - Correct username and password in EmailService.java
   - SMTP settings for your email provider
3. **Network connectivity**: Test internet connection
4. **Firewall settings**: Ensure port 587 is open

#### OTP Not Received

**Problem**: Verification code doesn't arrive
**Solutions**:
1. **Check spam folder**: Gmail may filter automated emails
2. **Wait a few minutes**: Email delivery can be delayed
3. **Verify email address**: Ensure correct email in account
4. **Use "Resend OTP"**: Get a new code
5. **Check console**: Look for email sending errors

#### Login Issues

**Problem**: Cannot log in with correct credentials
**Solutions**:
1. **Check username/password**: Verify exact spelling
2. **Account verification**: Ensure email is verified
3. **Database check**: Verify user exists in users table
4. **Clear expired OTPs**: Run cleanup in database
5. **Reset password**: Contact administrator for reset

### Error Messages

#### "User not found"
- Username doesn't exist in database
- Check spelling or register new account

#### "Invalid password"
- Password doesn't match stored hash
- Verify password or use reset function

#### "OTP expired"
- Verification code older than 10 minutes
- Request new OTP code

#### "Email already exists"
- Email address already registered
- Use different email or recover existing account

#### "Database connection timeout"
- MySQL server not responding
- Check server status and network connection

### Performance Issues

#### Slow Application Startup
1. **Database optimization**: Add indexes if missing
2. **Connection pool**: Verify HikariCP configuration
3. **Memory allocation**: Increase JVM heap size
4. **Network latency**: Check database server response time

#### UI Responsiveness
1. **Background operations**: Email sending in separate thread
2. **Large member lists**: Consider pagination for 100+ members
3. **Calendar rendering**: Optimize time slot calculations
4. **Memory usage**: Monitor with Java profiling tools

---

## FAQ

### General Questions

**Q: Can I use this offline?**
A: Partial functionality works offline, but email features and initial database setup require internet connection.

**Q: How many members can I add to a study group?**
A: There's no hard limit, but performance may decrease with 50+ members.

**Q: Can I change my username after registration?**
A: Currently not supported through UI. Contact administrator for database changes.

**Q: What happens if I forget my password?**
A: Password reset feature is not implemented. Contact administrator for manual reset.

### Technical Questions

**Q: Can I use a different database instead of MySQL?**
A: Code uses MySQL-specific features. PostgreSQL adaptation would require code changes.

**Q: Can I use Outlook instead of Gmail for emails?**
A: Yes, update SMTP settings in EmailService.java:
```java
private static final String SMTP_HOST = "smtp-mail.outlook.com";
private static final String SMTP_PORT = "587";
```

**Q: How do I backup my data?**
A: Use MySQL dump:
```bash
mysqldump -u root -p student_scheduling > backup.sql
```

**Q: Can I run this on a server for multiple users?**
A: Yes, but you'll need to configure proper database permissions and network access.

### Security Questions

**Q: How secure are the passwords?**
A: Passwords are hashed using BCrypt with salt, industry-standard security.

**Q: Can others see my email address?**
A: Email addresses are partially masked in the UI for privacy.

**Q: What data is stored in the database?**
A: Usernames, hashed passwords, email addresses, group memberships, and meeting schedules.

**Q: Can I delete my account?**
A: Account deletion is not implemented in UI. Database records can be manually removed.

### Usage Questions

**Q: Can I be in multiple study groups?**
A: Current version supports one active group per session. Multiple groups require separate application instances.

**Q: How do I export my schedule?**
A: Export functionality is planned for future versions. Currently, use database queries.

**Q: Can I set recurring meetings?**
A: Not currently supported. Each meeting must be scheduled individually.

**Q: What's the maximum meeting duration?**
A: UI allows up to 240 minutes (4 hours). Database has no limit.

---

## Support and Contact

### Getting Help

1. **Check this user guide** for common solutions
2. **Review troubleshooting section** for specific issues
3. **Check application logs** for error messages
4. **Consult README.md** for technical details

### Reporting Issues

When reporting problems, include:
- **Error messages**: Exact text of any error dialogs
- **Steps to reproduce**: What you were doing when problem occurred
- **System information**: OS, Java version, MySQL version
- **Log output**: Console messages and stack traces

### Feature Requests

Future enhancements may include:
- **Calendar export**: iCal/Google Calendar integration
- **Mobile app**: Android/iOS companion
- **Advanced scheduling**: Recurring meetings, time zones
- **Enhanced notifications**: SMS, push notifications
- **Multi-group support**: Participate in multiple groups

---

**Version**: 1.0.0  
**Last Updated**: December 2024  
**Support**: Educational use at Bangladesh University of Professionals
