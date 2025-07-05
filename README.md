# Student Scheduling System

A comprehensive Java Swing application for managing study group schedules with MySQL database integration and OTP-based authentication.

## 🌟 Features

### Authentication & Security
- **Two-Factor Authentication**: OTP-based login via email
- **Role-Based Access Control**: Admin, Student, and Regular User roles
- **Secure Password Storage**: BCrypt hashing with salt
- **Email Verification**: Required for new user registration

### Database Integration
- **MySQL Database**: Persistent data storage
- **Connection Pooling**: HikariCP for efficient database connections
- **Auto Schema Creation**: Database tables created automatically
- **Data Integrity**: Foreign key constraints and indexes

### Study Group Management
- **Member Management**: Add, edit, remove group members
- **Schedule Coordination**: Find common available time slots
- **Meeting Scheduling**: Schedule meetings with email notifications
- **Force Scheduling**: Admin-only emergency meeting scheduling

### Communication
- **Email Integration**: JavaMail API for sending notifications
- **Meeting Invitations**: HTML email templates
- **OTP Delivery**: Secure code delivery for authentication

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

4. **Configure Database Connection**
   Edit `src/main/java/org/example/database/DatabaseConfig.java`:
   ```java
   private static final String DB_PASSWORD = "your-mysql-password";
   ```

5. **Build and Run**
   ```bash
   ./gradlew build
   ./gradlew run
   ```

## 📧 Email Configuration

### Gmail Setup
1. Enable 2-Factor Authentication on your Gmail account
2. Generate an App Password:
   - Go to Google Account settings
   - Security → 2-Step Verification → App passwords
   - Generate password for "Mail"
3. Use this App Password in the EMAIL_PASSWORD field

### Alternative Email Providers
Update SMTP settings in `EmailService.java`:
- **Outlook**: smtp-mail.outlook.com:587
- **Yahoo**: smtp.mail.yahoo.com:587
- **Custom SMTP**: Update host and port accordingly

## 🗄️ Database Setup

### Automatic Setup
The application automatically creates required tables on first run:
- `users` - User accounts and authentication
- `otp_tokens` - OTP verification codes
- `study_groups` - Study group information
- `schedules` - Meeting schedules
- `members` - Group membership

### Manual Setup
Run the provided SQL script:
```bash
mysql -u root -p
source database_setup.sql
```

## 👥 User Roles

### Administrator
- Full access to all features
- Can force schedule meetings
- User management capabilities
- System configuration access

### Student
- Create and join study groups
- Schedule meetings (with availability)
- View and respond to invitations
- Limited administrative access

### Regular User
- Standard scheduling features
- Group participation
- Meeting coordination
- Email notifications

## 🔐 Authentication Flow

### Registration
1. User provides username, email, and password
2. System validates input and checks for duplicates
3. OTP sent to email address
4. User enters OTP to verify email
5. Account activated upon successful verification

### Login
1. User enters username and password
2. System validates credentials
3. OTP sent to registered email
4. User enters OTP to complete login
5. Access granted based on user role

## 🛠️ Development

### Project Structure
```
src/main/java/org/example/
├── Main.java                    # Application entry point
├── MainFrame.java              # Main application window
├── SplashScreen.java           # Loading screen
├── auth/                       # Authentication components
│   ├── AuthenticationService.java
│   ├── OTPService.java
│   └── UserAccount.java
├── database/                   # Database layer
│   ├── DatabaseConfig.java
│   └── UserDAO.java
├── model/                      # Data models
│   ├── Member.java
│   ├── Schedule.java
│   ├── StudyGroup.java
│   └── TimeSlot.java
├── util/                       # Utilities
│   └── EmailService.java
└── view/                       # UI components
    ├── CalendarGrid.java
    ├── ForceScheduleDialog.java
    ├── LoginView.java
    └── OTPVerificationDialog.java
```

### Dependencies
- **MySQL Connector**: Database connectivity
- **JavaMail API**: Email functionality
- **BCrypt**: Password hashing
- **HikariCP**: Connection pooling
- **JSON Simple**: Configuration management

## 🧪 Testing

### Manual Testing
1. **Authentication Test**
   ```
   Username: admin
   Password: admin123
   Email: Check console for OTP (if email not configured)
   ```

2. **Student Account Test**
   ```
   Username: student
   Password: student123
   ```

3. **Feature Testing**
   - Create study groups
   - Add members
   - Schedule meetings
   - Test role-based access

### Database Verification
```sql
-- Check user accounts
SELECT username, email, role, is_email_verified FROM users;

-- Check OTP tokens
SELECT user_id, otp_code, expires_at, is_used FROM otp_tokens;
```

## 🐛 Troubleshooting

### Common Issues

#### Database Connection Failed
- Verify MySQL server is running
- Check database credentials in `DatabaseConfig.java`
- Ensure database `student_scheduling` exists

#### Email Sending Failed
- Verify email credentials in `EmailService.java`
- Check Gmail App Password setup
- Ensure network connectivity

#### OTP Not Received
- Check email spam folder
- Verify email address in user account
- Check console output for email errors

#### Build Failures
- Ensure Java 17+ is installed
- Check Gradle wrapper permissions: `chmod +x gradlew`
- Clear build cache: `./gradlew clean`

### Debug Mode
Enable detailed logging by setting:
```java
System.setProperty("java.util.logging.level", "ALL");
```

## 📝 Default Accounts

The system creates default accounts on first run:

| Username | Password | Role | Email |
|----------|----------|------|-------|
| admin | admin123 | ADMIN | admin@example.com |
| student | student123 | STUDENT | student@example.com |

**Note**: Change default passwords in production!

## 🔒 Security Considerations

- **Password Policy**: Minimum 6 characters (consider strengthening)
- **OTP Expiry**: 10-minute timeout for security
- **Session Management**: Automatic logout on application close
- **SQL Injection**: Protected via PreparedStatements
- **Email Privacy**: Email addresses masked in UI

## 📈 Performance

- **Connection Pooling**: Maximum 10 concurrent connections
- **Database Indexes**: Optimized for common queries
- **Memory Management**: Efficient Swing component handling
- **Background Processing**: Non-blocking email operations

## 🤝 Contributing

1. Fork the repository
2. Create feature branch: `git checkout -b feature/new-feature`
3. Commit changes: `git commit -am 'Add new feature'`
4. Push to branch: `git push origin feature/new-feature`
5. Submit Pull Request

## 📄 License

This project is developed for educational purposes at Bangladesh University of Professionals.

## 📞 Support

For issues and questions:
- Check troubleshooting section above
- Review application logs
- Create issue in repository

---

**Version**: 1.0.0  
**Last Updated**: December 2024  
**Java Version**: 17+  
**Database**: MySQL 8.0+
