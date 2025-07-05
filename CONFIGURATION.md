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

## Email Configuration

### Gmail Setup (Recommended)
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
   private static final String EMAIL_PASSWORD = "your-16-char-app-password";
   private static final String FROM_EMAIL = "your-email@gmail.com";
   ```

### Alternative Email Providers
- **Outlook**: Change SMTP_HOST to "smtp-mail.outlook.com"
- **Yahoo**: Change SMTP_HOST to "smtp.mail.yahoo.com"
- **Custom**: Update SMTP_HOST and SMTP_PORT accordingly

## First Run

### Default Accounts
The system creates these accounts automatically:
- **Admin**: username=`admin`, password=`admin123`
- **Student**: username=`student`, password=`student123`

### Security Notes
- Change default passwords immediately
- Email verification required for new accounts
- OTP codes expire in 10 minutes

## Testing Checklist

### Database Test
- [ ] MySQL server running
- [ ] Database `student_scheduling` exists
- [ ] All tables created (users, otp_tokens, etc.)

### Email Test  
- [ ] Gmail App Password configured
- [ ] SMTP settings correct
- [ ] Test email sending

### Application Test
- [ ] Application starts without errors
- [ ] Login with default accounts works
- [ ] OTP verification functional
- [ ] Main interface loads properly

## Production Deployment

### Security Hardening
1. **Change default passwords**
2. **Use environment variables** for sensitive data
3. **Enable MySQL SSL** for production
4. **Configure firewall** rules
5. **Regular database backups**

### Performance Optimization
1. **Increase connection pool** size if needed
2. **Add database indexes** for large datasets
3. **Monitor memory usage**
4. **Configure JVM heap** size

## Troubleshooting

### Database Issues
- Check MySQL service status
- Verify database credentials
- Test connection manually

### Email Issues
- Verify Gmail App Password
- Check spam folder for OTPs
- Test SMTP connectivity

### Application Issues
- Check Java version (requires 17+)
- Verify all dependencies downloaded
- Review console output for errors
