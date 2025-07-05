c# Student Scheduling System - User Guide

## Table of Contents
1. [Getting Started](#getting-started)
2. [System Requirements](#system-requirements)
3. [Installation Guide](#installation-guide)
4. [User Authentication](#user-authentication)
5. [Account Registration](#account-registration)
6. [Main Interface](#main-interface)
7. [Conflict-Free Scheduling](#conflict-free-scheduling)
8. [Study Group Management](#study-group-management)
9. [Email Notifications](#email-notifications)
10. [Role-Based Features](#role-based-features)
11. [Troubleshooting](#troubleshooting)
12. [FAQ](#faq)

---

## Getting Started

### Welcome to Student Scheduling System
This application helps study groups coordinate meeting times efficiently with conflict-free scheduling. The system supports Admin and Student roles, mandatory OTP authentication, and automatic email notifications.

### Key Benefits
- **Enhanced Security**: Mandatory two-factor authentication with email verification for every login
- **Conflict-Free Scheduling**: Automatic detection and prevention of overlapping time slots
- **Role Management**: Separate registration and access for Students and Admins
- **Email Integration**: Automatic meeting invitations, reminders, and OTP delivery
- **Persistent Storage**: MySQL database ensures data is never lost

---

## System Requirements

### Minimum Requirements
- **Operating System**: Windows 10, macOS 10.14, or Linux Ubuntu 18.04+
- **Java Runtime**: Java 17 or higher
- **Memory**: 512 MB RAM minimum, 1 GB recommended
- **Storage**: 100 MB free disk space
- **Database**: MySQL 8.0 or MariaDB 10.5+
- **Internet**: Required for email functionality (mandatory for OTP)

### Recommended Setup
- **Java**: OpenJDK 17 or Oracle JDK 17+
- **Database**: MySQL 8.0 with InnoDB engine
- **Email**: Gmail account with App Password enabled
- **Screen Resolution**: 1024x768 or higher

---

## Installation Guide

### Step 1: Download and Setup
1. Clone or download the project files
2. Ensure Java 17+ is installed
3. Set up MySQL database using the provided `database_setup.sql` script
4. Configure email settings in `EmailService.java`

### Step 2: Database Setup
```bash
mysql -u root -p < database_setup.sql
```

### Step 3: Email Configuration
Edit the email settings in `src/main/java/org/example/util/EmailService.java`:
- Set your Gmail address
- Set your App Password (not your regular password)

### Step 4: Run the Application
```bash
./gradlew build
./gradlew run
```

---

## User Authentication

### New Security Features
The system now requires **mandatory OTP verification** for every login attempt. There are no bypass options - all users must verify their identity via email.

### Login Process
1. **Enter Credentials**: Provide username and password
2. **Credential Verification**: System validates your credentials
3. **OTP Delivery**: 6-digit OTP automatically sent to your registered email
4. **OTP Verification**: Enter the OTP within 10 minutes
5. **Access Granted**: System grants access after successful verification

### Password Recovery (Enhanced)
If you forget your password, you can easily reset it:

1. **Click "Forgot Password"** on the login screen
2. **Enter Email Address**: Use the large, visible email field (25 characters wide)
3. **Set New Password**: Create any password you want (no character limitations)
4. **Confirm Password**: Re-enter your new password to confirm
5. **Email Verification**: System sends OTP to verify your identity
6. **Complete Reset**: Your password is updated after verification

**Enhanced Features**:
- **Large Email Field**: 25-character wide field with clear borders and larger font
- **No Password Restrictions**: Set passwords of any length - no minimum requirements
- **Professional Interface**: Clean dialog with proper spacing and styling
- **Auto-Focus**: Email field automatically selected when dialog opens
- **Clear Guidance**: Helpful instructions throughout the process
- **Progress Indication**: Shows "Resetting..." during password update
- **Success Feedback**: Confirmation message and login form cleared after successful reset

### Important Notes
- **Email Required**: All accounts must have valid email addresses
- **OTP Mandatory**: Every login requires OTP verification
- **Time Limit**: OTP expires after 10 minutes
- **Resend Option**: You can request a new OTP if needed

### Default Test Accounts
For testing purposes (also require OTP):
- **Admin**: username `admin`, password `admin123`, email `admin@example.com`
- **Student**: username `student`, password `student123`, email `student@example.com`

---

## Account Registration

### New Registration System
The system now provides separate registration paths for Students and Admins, with no "Regular User" option.

### Registration Process
1. **Choose Account Type**: Select either Student or Admin
2. **Enter Information**: 
   - Username (must be unique)
   - Email address (must be valid)
   - Password (minimum 6 characters)
   - Confirm password
3. **Email Verification**: OTP sent to your email
4. **Complete Registration**: Enter OTP to activate account

### Account Types
- **Student Account**: 
  - Personal scheduling access
  - Study group participation
  - Basic scheduling features
  
- **Admin Account**:
  - Full system access
  - User management capabilities
  - Force scheduling permissions
  - Advanced administrative features

---

## Main Interface

### Dashboard Overview
After successful login, you'll see the main dashboard with:
- Current user information and role
- Quick access to scheduling features
- Recent activity summary
- Conflict notifications (if any)

### Navigation Menu
- **Schedule**: View and manage your time slots
- **Study Groups**: Create and join study groups
- **Members**: Manage group members (role-dependent)
- **Settings**: Account and system preferences
- **Logout**: Secure logout with session cleanup

---

## Conflict-Free Scheduling

### New Scheduling Features
The system now prevents all scheduling conflicts automatically using advanced conflict detection algorithms.

### How It Works
1. **Automatic Detection**: System checks for overlaps when adding new time slots
2. **Real-time Validation**: Immediate feedback on potential conflicts
3. **Conflict Prevention**: New appointments blocked if they overlap existing ones
4. **Detailed Reporting**: Shows exactly which appointments conflict

### Adding Time Slots
1. Click "Add Time Slot" or "Schedule Meeting"
2. Select date and time range
3. System automatically checks for conflicts
4. If conflicts exist:
   - View detailed conflict information
   - Choose different time slot
   - Or resolve conflicts first
5. If no conflicts, appointment is added successfully

### Conflict Resolution
When conflicts are detected:
- **View Conflicts**: See all overlapping appointments
- **Alternative Times**: System suggests conflict-free alternatives
- **Modify Existing**: Edit conflicting appointments if needed
- **Force Schedule**: Admin-only option for emergency meetings

### Benefits
- **No Double-booking**: Prevents scheduling multiple appointments at the same time
- **Clear Feedback**: Immediate notification of scheduling conflicts
- **Smart Suggestions**: Alternative time slots when conflicts occur
- **Data Integrity**: Maintains consistent and reliable schedules

---

## Study Group Management

### Creating Study Groups
1. Navigate to "Study Groups" section
2. Click "Create New Group"
3. Enter group name and description
4. Set initial schedule preferences
5. Add initial members (conflict-free scheduling applies)

### Managing Members
- **Add Members**: Invite new participants
- **Remove Members**: Remove inactive participants
- **View Schedules**: See all member availability
- **Conflict Checking**: Automatic conflict detection when adding members

### Finding Common Times
The system automatically finds time slots that work for all group members:
1. Analyzes all member schedules
2. Identifies conflict-free time slots
3. Suggests optimal meeting times
4. Prevents scheduling conflicts

---

## Email Notifications

### OTP Delivery
- **Login OTP**: Sent for every login attempt
- **Registration OTP**: Sent during account creation
- **Resend Option**: Available if OTP not received
- **Expiration**: OTP expires after 10 minutes

### Meeting Notifications
- **Meeting Invitations**: Sent to all participants
- **Schedule Changes**: Notifications for any updates
- **Conflict Alerts**: Warning emails for scheduling conflicts
- **Reminders**: Advance meeting reminders

### Email Requirements
- **Valid Email**: All accounts must have working email addresses
- **Gmail Recommended**: System optimized for Gmail SMTP
- **App Password**: Required for security (not regular password)

---

## Role-Based Features

### Student Role Features
- **Personal Scheduling**: Manage your own time slots
- **Study Group Participation**: Join and participate in groups
- **Conflict Prevention**: Automatic conflict detection
- **Basic Notifications**: Email updates for your activities

### Admin Role Features
- **All Student Features**: Full access to student functionality
- **User Management**: Create and manage user accounts
- **Force Scheduling**: Override conflicts for emergency meetings
- **System Configuration**: Access to system settings
- **Advanced Reporting**: Detailed system usage reports

### Permission System
- **Role-Based Access**: Different features based on user role
- **Secure Operations**: Sensitive operations require admin privileges
- **Audit Trail**: All administrative actions are logged

---

## Troubleshooting

### Common Issues

#### Login Problems
- **Problem**: Not receiving OTP
- **Solution**: Check spam folder, verify email address, try resend option
- **Alternative**: Contact admin if email issues persist

#### Scheduling Conflicts
- **Problem**: Cannot schedule desired time
- **Solution**: Check conflict report, choose alternative time, or resolve existing conflicts
- **Note**: System prevents double-booking for data integrity

#### Email Configuration
- **Problem**: OTP emails not sending
- **Solution**: Verify Gmail App Password, check internet connection, update email settings

#### Database Connection
- **Problem**: Cannot connect to database
- **Solution**: Verify MySQL is running, check connection settings, ensure database exists

### Getting Help
1. Check this user guide for solutions
2. Review the FAQ section
3. Contact system administrator
4. Check application logs for error details

---

## FAQ

### Authentication Questions

**Q: Why do I need OTP for every login?**
A: Enhanced security requires OTP verification for every login to protect user accounts and data.

**Q: Can I disable OTP verification?**
A: No, OTP verification is mandatory and cannot be disabled for security reasons.

**Q: What if I don't receive the OTP?**
A: Check your spam folder, verify your email address, and use the resend option if needed.

### Scheduling Questions

**Q: Why can't I schedule overlapping meetings?**
A: The system prevents conflicts to ensure data integrity and avoid double-booking.

**Q: How do I resolve scheduling conflicts?**
A: View the conflict report, choose alternative times, or modify existing appointments.

**Q: Can admins override scheduling conflicts?**
A: Yes, admins have force scheduling capabilities for emergency situations.

### Account Questions

**Q: What's the difference between Student and Admin accounts?**
A: Students have basic scheduling access, while Admins have full system management capabilities.

**Q: Can I change my account type?**
A: Contact an administrator to change your account type.

**Q: What happened to Regular User accounts?**
A: Regular User accounts have been removed. You now choose between Student and Admin accounts.

### Technical Questions

**Q: What email providers are supported?**
A: Gmail is recommended, but other SMTP providers can be configured.

**Q: Is my data secure?**
A: Yes, passwords are encrypted, OTP provides additional security, and all data is stored securely.

**Q: Can I use this offline?**
A: No, internet connection is required for OTP verification and email functionality.

---

## Support

For additional help:
- Review the [CONFIGURATION.md](CONFIGURATION.md) file
- Check the [README.md](README.md) for technical details
- Contact your system administrator
- Create an issue in the project repository

---

**Last Updated**: July 2025
**Version**: 2.0 with Enhanced Security and Conflict-Free Scheduling
