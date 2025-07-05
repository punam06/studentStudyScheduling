# Configuration Guide

## Database Configuration

### MySQL Setup (Required)
1. **Install MySQL** (if not already installed):
   ```bash
   # macOS
   brew install mysql
   brew services start mysql
   
   # Ubuntu/Debian
   sudo apt update
   sudo apt install mysql-server
   sudo systemctl start mysql
   
   # Windows
   # Download MySQL installer from https://dev.mysql.com/downloads/mysql/
   ```

2. **Create Database**:
   ```bash
   # Run the automated setup script
   ./setup_database.sh
   
   # OR manually:
   mysql -u root -p
   CREATE DATABASE student_scheduling;
   USE student_scheduling;
   SOURCE database_setup.sql;
   EXIT;
   ```

3. **Update Database Password**:
   Edit `src/main/java/org/example/database/DatabaseConfig.java`:
   ```java
   private static final String DB_PASSWORD = "YOUR_MYSQL_ROOT_PASSWORD";
   ```

## Email Configuration (MANDATORY)

### Gmail Setup (Required for OTP)
Since OTP verification is now mandatory for all logins, proper email configuration is essential.

1. **Enable 2-Factor Authentication** on your Gmail account
2. **Generate App Password**:
   - Go to Google Account → Security → 2-Step Verification
   - Click "App passwords"
   - Select "Mail" and generate password
   - Copy the 16-character password

3. **Update Email Settings**:
   Edit `src/main/java/org/example/util/EmailService.java`:
   ```java
   private static final String EMAIL_USERNAME = "your-email@gmail.com";
   private static final String EMAIL_PASSWORD = "your-16-character-app-password";
   ```

### Important Email Notes
- **OTP Mandatory**: System requires working email for OTP delivery
- **No Bypass**: Email configuration cannot be skipped
- **Testing**: Use default accounts to test OTP functionality
- **Spam Folder**: Check spam if OTP emails don't arrive

## Security Configuration

### Enhanced Authentication System
The system now uses mandatory OTP verification for all login attempts.

#### OTP Settings
- **Expiration Time**: 10 minutes (configurable in `OTPService.java`)
- **OTP Length**: 6 digits (configurable)
- **Resend Limit**: 30 seconds between resend attempts

#### Configuration Options
Edit `src/main/java/org/example/auth/OTPService.java`:
```java
private static final int OTP_LENGTH = 6;              // OTP digits
private static final int OTP_EXPIRY_MINUTES = 10;     // Expiration time
```

### User Role Configuration
The system now supports only two roles:
- **STUDENT**: Basic scheduling and group participation
- **ADMIN**: Full system access and user management

#### Default Accounts
Default test accounts (also require OTP):
```java
// In AuthenticationService.java
admin@example.com (Admin role)
student@example.com (Student role)
```

## Scheduling Configuration

### Conflict Detection System
The system now prevents all scheduling conflicts automatically.

#### Conflict Settings
Edit `src/main/java/org/example/model/Schedule.java`:
```java
// Configure conflict detection behavior
private boolean strictConflictChecking = true;    // Prevent all overlaps
private boolean allowAdminOverride = true;        // Allow admin force scheduling
```

#### Scheduling Algorithm
- **Method**: ArrayList-based conflict detection
- **Complexity**: O(n) for conflict checking
- **Precision**: Exact time slot overlap detection
- **Integrity**: Maintains schedule consistency

### Time Slot Configuration
```java
// In TimeSlot.java
private static final DateTimeFormatter FORMATTER = 
    DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
```

## Application Configuration

### System Settings
Edit `src/main/java/org/example/Main.java`:
```java
// Database connection timeout
private static final int DB_TIMEOUT = 30;

// Email service timeout
private static final int EMAIL_TIMEOUT = 10;

// OTP cleanup interval (minutes)
private static final int OTP_CLEANUP_INTERVAL = 60;
```

### UI Configuration
```java
// In MainFrame.java
private static final int WINDOW_WIDTH = 1200;
private static final int WINDOW_HEIGHT = 800;
private static final String APP_TITLE = "Study Squad Synchronizer";
```

## Security Best Practices

### Password Security
- **Minimum Length**: 6 characters (configurable)
- **Hashing**: BCrypt with salt
- **Storage**: Never store plain text passwords

### OTP Security
- **Delivery**: Only via registered email
- **Expiration**: Automatic cleanup of expired OTPs
- **Rate Limiting**: Prevent OTP spam

### Session Management
- **Timeout**: Automatic session cleanup
- **Validation**: Verify user sessions
- **Logout**: Secure session termination

## Performance Configuration

### Database Optimization
```java
// In DatabaseConfig.java
private static final int MAX_POOL_SIZE = 20;
private static final int MIN_POOL_SIZE = 5;
private static final int CONNECTION_TIMEOUT = 30000;
```

### Memory Settings
```bash
# For large datasets, increase JVM memory
java -Xmx2g -Xms1g -jar student_scheduling.jar
```

## Development Configuration

### Debug Mode
Enable debug logging in `logback.xml`:
```xml
<logger name="org.example" level="DEBUG" />
```

### Testing Configuration
```java
// In test configurations
private static final boolean SKIP_EMAIL_IN_TESTS = true;
private static final boolean USE_IN_MEMORY_DB = true;
```

## Deployment Configuration

### Production Settings
1. **Database**: Use dedicated MySQL server
2. **Email**: Use dedicated Gmail account for OTP
3. **Security**: Enable all security features
4. **Monitoring**: Set up logging and monitoring

### Environment Variables
```bash
# Set environment variables for production
export DB_URL="jdbc:mysql://localhost:3306/student_scheduling"
export DB_USER="scheduling_user"
export DB_PASSWORD="secure_password"
export EMAIL_USERNAME="noreply@yourdomain.com"
export EMAIL_PASSWORD="app_password"
```

## Troubleshooting Configuration

### Common Issues

#### Email Not Working
- Check Gmail App Password
- Verify 2FA is enabled
- Test with default accounts
- Check spam folder

#### Database Connection Issues
- Verify MySQL is running
- Check connection parameters
- Ensure database exists
- Verify user permissions

#### OTP Issues
- Check email configuration
- Verify OTP expiration settings
- Test with different email providers
- Check system time synchronization

### Log Configuration
```java
// Enable detailed logging
Logger.getLogger("org.example.auth").setLevel(Level.DEBUG);
Logger.getLogger("org.example.model").setLevel(Level.DEBUG);
```

## Migration from Previous Version

### Upgrading to v2.0
1. **Backup Database**: Create backup before upgrade
2. **Update Schema**: Run migration scripts
3. **Configure Email**: Set up OTP email system
4. **Update User Roles**: Convert existing users to Student/Admin
5. **Test OTP**: Verify OTP functionality

### Migration Script
```sql
-- Update user roles (remove USER role)
UPDATE users SET role = 'STUDENT' WHERE role = 'USER';

-- Ensure all users have email addresses
UPDATE users SET email = CONCAT(username, '@example.com') 
WHERE email IS NULL OR email = '';

-- Add email verification status
ALTER TABLE users ADD COLUMN email_verified BOOLEAN DEFAULT FALSE;
```

## Performance Monitoring

### Key Metrics
- **Login Success Rate**: Monitor OTP delivery success
- **Conflict Detection**: Track scheduling conflict frequency
- **Database Performance**: Monitor query execution times
- **Email Delivery**: Track OTP delivery times

### Monitoring Tools
```java
// Add performance monitoring
private static final MetricRegistry metrics = new MetricRegistry();
private static final Timer loginTimer = metrics.timer("login.time");
private static final Counter conflictCounter = metrics.counter("conflicts.detected");
```

---

**Configuration Last Updated**: July 2025
**Version**: 2.0 with Enhanced Security and Conflict-Free Scheduling
