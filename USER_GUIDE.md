# Study Squad Synchronizer
## User Guide

![Study Squad Synchronizer Logo](https://via.placeholder.com/150x150.png?text=SSS)

*Version 1.0 - June 2025*

---

## Table of Contents
1. [Introduction](#1-introduction)
2. [Getting Started](#2-getting-started)
   - [System Requirements](#21-system-requirements)
   - [Installation](#22-installation)
   - [First Launch](#23-first-launch)
3. [Account Management](#3-account-management)
   - [Default Accounts](#31-default-accounts)
   - [Logging In](#32-logging-in)
   - [Creating New Accounts](#33-creating-new-accounts)
   - [Account Types and Permissions](#34-account-types-and-permissions)
   - [Viewing Your Profile](#35-viewing-your-profile)
   - [Logging Out](#36-logging-out)
4. [Managing Members](#4-managing-members)
   - [Adding Members](#41-adding-members)
   - [Editing Members](#42-editing-members)
   - [Removing Members](#43-removing-members)
5. [Calendar and Scheduling](#5-calendar-and-scheduling)
   - [Navigating the Calendar](#51-navigating-the-calendar)
   - [Managing Time Slots](#52-managing-time-slots)
   - [Finding Common Availability](#53-finding-common-availability)
   - [Scheduling Meetings](#54-scheduling-meetings)
   - [Emergency Scheduling](#55-emergency-scheduling)
6. [Email Notifications](#6-email-notifications)
   - [Sending Invitations](#61-sending-invitations)
   - [Email Templates](#62-email-templates)
   - [Previewing Emails](#63-previewing-emails)
7. [Settings and Preferences](#7-settings-and-preferences)
   - [Time Range Settings](#71-time-range-settings)
   - [Minimum Member Requirements](#72-minimum-member-requirements)
8. [Troubleshooting](#8-troubleshooting)
   - [Common Issues](#81-common-issues)
   - [Technical Support](#82-technical-support)
9. [FAQ](#9-faq)

---

## 1. Introduction

Study Squad Synchronizer is a comprehensive tool designed to help study groups coordinate their schedules and find common meeting times. The application provides an intuitive interface for managing group members, visualizing availability on a calendar grid, finding overlapping free time slots, and scheduling meetings with automatic notifications.

**Key Features:**
- User account management with different permission levels
- Member management system
- Visual calendar grid for time slot selection
- Automated common time slot detection
- Meeting scheduling with email notifications
- Emergency scheduling options for urgent sessions

This guide will walk you through all aspects of using the Study Squad Synchronizer to help your study group collaborate more effectively.

---

## 2. Getting Started

### 2.1 System Requirements

To run Study Squad Synchronizer, your system must meet the following requirements:

- **Operating System**: Windows 10/11, macOS 10.15+, or Linux
- **Java**: Java Runtime Environment (JRE) 11 or newer
- **Memory**: Minimum 4GB RAM
- **Disk Space**: At least 100MB free space
- **Screen Resolution**: 1024x768 or higher

### 2.2 Installation

1. **Download** the Study Squad Synchronizer application package from the provided source.
2. **Extract** the downloaded file to your preferred location.
3. **Run** the application:
   - **Windows**: Double-click the `student_scheduling.bat` file
   - **macOS/Linux**: Open Terminal, navigate to the extracted directory, and run `./student_scheduling`

### 2.3 First Launch

1. Upon first launch, you will see a splash screen displaying the application name and version.
2. After a few seconds, the login screen will appear.
3. Use one of the default accounts to log in (see [Default Accounts](#31-default-accounts)).
4. After successful login, the main application window will open.

---

## 3. Account Management

### 3.1 Default Accounts

The application comes with two pre-configured accounts:

- **Admin Account**:
  - Username: `admin`
  - Password: `admin123`
  - Full access to all features

- **Student Account**:
  - Username: `student`
  - Password: `student123`
  - Limited access (cannot use emergency scheduling)

### 3.2 Logging In

1. Enter your username and password in the corresponding fields.
2. Click the "Login" button or press Enter.
3. If successful, the main application window will open.
4. If unsuccessful, an error message will display. Check your credentials and try again.

### 3.3 Creating New Accounts

1. From the login screen, click the "Register" button.
2. Fill in the required information:
   - Username (required)
   - Password (required)
   - Confirm password (must match password)
   - Role: Select either "Student" or "Regular User"
3. Click "OK" to create the account.
4. A confirmation message will appear if registration was successful.
5. You can now log in with your new account.

### 3.4 Account Types and Permissions

The application supports three user roles:

- **Administrator**:
  - Full access to all features
  - Can use emergency scheduling
  - Can access all administrative functions

- **Student**:
  - Basic scheduling functionality
  - Cannot use emergency scheduling
  - Limited administrative access

- **Regular User**:
  - Similar to Student role
  - Standard access to scheduling features
  - No access to administrative functions

### 3.5 Viewing Your Profile

1. After logging in, click on the "Account" menu.
2. Select "View Profile" from the dropdown menu.
3. A dialog will appear displaying your:
   - Username
   - Role
   - Access level

### 3.6 Logging Out

1. Click on the "Account" menu.
2. Select "Logout" from the dropdown menu.
3. Confirm that you want to log out.
4. You will be returned to the login screen.

---

## 4. Managing Members

### 4.1 Adding Members

1. Navigate to the "Members" panel on the right side of the application.
2. Click the "Add" button at the bottom of the panel.
3. Enter the required information:
   - Name (required)
   - Email address (optional but recommended for notifications)
4. Click "OK" to add the member.
5. The new member will appear in the members list.

Alternatively:
1. Click the "Edit" menu in the top menu bar.
2. Select "Add Member" from the dropdown menu.
3. Follow steps 3-5 above.

### 4.2 Editing Members

1. In the "Members" panel, select the member you wish to edit.
2. Click the "Edit" button at the bottom of the panel.
3. Update the member's information as needed.
4. Click "OK" to save your changes.

### 4.3 Removing Members

1. In the "Members" panel, select the member you wish to remove.
2. Click the "Remove" button at the bottom of the panel.
3. Confirm that you want to remove this member.
4. The member will be removed from the list and their time slots cleared.

---

## 5. Calendar and Scheduling

### 5.1 Navigating the Calendar

The calendar grid shows days on the horizontal axis and members on the vertical axis:

- Use the "◀ Previous Day" and "Next Day ▶" buttons to navigate between days.
- Click the "Today" button to return to the current date.
- The current date is displayed at the top of the calendar.

### 5.2 Managing Time Slots

To mark time slots as available or unavailable:

1. **Left-click** on a cell to toggle between available (green) and unavailable (white).
2. **Click and drag** to select multiple cells at once.
3. **Right-click** on a cell to open a context menu with additional options.

### 5.3 Finding Common Availability

To find times when all members are available:

1. Ensure all members have marked their availability on the calendar.
2. Click the "Find Common Slots" button in the toolbar.
3. Common time slots will be highlighted in bright green on the calendar grid.

### 5.4 Scheduling Meetings

To schedule a regular meeting:

1. Click the "Schedule" menu.
2. Select "Schedule Meeting" from the dropdown menu.
3. Enter the meeting details:
   - Time (hour and minute)
   - Duration (in minutes)
   - Subject
   - Message (optional)
4. Click "OK" to proceed.
5. Review the meeting details in the confirmation dialog.
6. Click "Yes" to schedule the meeting.
7. Choose whether to send email invitations to members.

### 5.5 Emergency Scheduling

For administrators who need to schedule urgent meetings:

1. Click the "Schedule" menu.
2. Select "Force Schedule Meeting" from the dropdown menu.
   - Note: This option is only available to administrators.
3. In the dialog that appears:
   - Select the meeting time and duration
   - Choose a priority level (Low, Medium, High, Critical)
   - Enter a subject and message
4. Click "OK" to proceed.
5. Review the emergency meeting details and confirm.
6. Choose whether to send urgent email notifications to all members.

---

## 6. Email Notifications

### 6.1 Sending Invitations

To send email invitations for a meeting:

1. Click the "Schedule" menu.
2. Select "Send Invitations" from the dropdown menu.
3. Enter the meeting details:
   - Date (year, month, day)
   - Time (hour and minute)
   - Duration (in minutes)
   - Subject
   - Message (optional)
4. Click "OK" to proceed.
5. Preview the email before sending (see [Previewing Emails](#63-previewing-emails)).
6. Click "OK" to send the invitations to all members.

### 6.2 Email Templates

The application includes several email templates:

- **Meeting Invitation**: Standard invitation for new meetings
- **Schedule Update**: Notification about changes to an existing meeting
- **Reminder**: Reminder about an upcoming meeting

### 6.3 Previewing Emails

Before sending any email notification:

1. The system will display a preview of the email as it will appear to recipients.
2. Review the content for accuracy and completeness.
3. Click "OK" to send or "Cancel" to make changes.

---

## 7. Settings and Preferences

### 7.1 Time Range Settings

To change the default time range displayed in the calendar:

1. Click the "Settings" menu.
2. Select "Preferences" from the dropdown menu.
3. Adjust the start and end times (in hours).
4. Click "OK" to apply the changes.

### 7.2 Minimum Member Requirements

To set the minimum number of members required for scheduling:

1. Click the "Settings" menu.
2. Select "Preferences" from the dropdown menu.
3. Adjust the "Minimum Members" value:
   - 0: All members are required
   - 1 or higher: The specified number of members are required
4. Click "OK" to apply the changes.

---

## 8. Troubleshooting

### 8.1 Common Issues

**Login Issues**
- Ensure you're using the correct username and password.
- Check that caps lock is not enabled.
- If you've forgotten your password, contact your administrator.

**Calendar Not Displaying Correctly**
- Ensure the application window is properly sized.
- Try adjusting the time range in Preferences.
- Restart the application if the issue persists.

**Email Notifications Not Working**
- Check that all members have valid email addresses.
- Verify your internet connection.
- Note that this is a simulated email service for demonstration purposes.

### 8.2 Technical Support

For technical support:
- Contact your system administrator
- Submit issues to the support portal at [support@studysquad.example.com](mailto:support@studysquad.example.com)
- Check for updates at our website: [www.studysquad.example.com](http://www.studysquad.example.com)

---

## 9. FAQ

**Q: Can I use the application without creating an account?**
A: No, you must log in with a valid account. You can use the default accounts or register a new one.

**Q: How many members can I add to a study group?**
A: The application supports an unlimited number of members, but for practical purposes and better visualization, we recommend keeping the group size under 20 members.

**Q: Can I export the schedule to other calendar applications?**
A: This feature is planned for future versions but is not currently available.

**Q: Is my data saved when I close the application?**
A: Yes, user account data is saved between sessions. Schedule and member information is also persisted.

**Q: What's the difference between regular scheduling and force scheduling?**
A: Regular scheduling looks for time slots where all required members are available. Force scheduling allows administrators to schedule meetings even when some members have conflicts.

**Q: Can I customize the email templates?**
A: The current version uses fixed templates with customizable subject and message fields. Full template customization will be available in future versions.

---

*Thank you for using Study Squad Synchronizer!*

© 2025 Your University. All rights reserved.
