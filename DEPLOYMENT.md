# 🚀 **Coffee Machine Monitoring - Deployment Guide**

## **Production Deployment Guide**

This guide covers deploying the Coffee Machine Monitoring System to production environments with HiveMQ Cloud integration.

---

## 🏗️ **Architecture Overview**

```
┌─────────────────┐    ┌──────────────────┐    ┌─────────────────┐
│   Coffee        │    │   HiveMQ Cloud   │    │   Spring Boot   │
│   Machines      │───▶│   MQTT Broker    │───▶│   Application   │
│   (IoT Devices) │    │                  │    │                 │
└─────────────────┘    └──────────────────┘    └─────────────────┘
                                                         │
                                                         ▼
                                               ┌─────────────────┐
                                               │   MySQL         │
                                               │   Database      │
                                               └─────────────────┘
```

---

## 📋 **Prerequisites**

### **System Requirements**
- **OS**: Ubuntu 20.04+ / CentOS 8+ / Amazon Linux 2
- **Java**: OpenJDK 17 or higher
- **Memory**: 2GB RAM minimum, 4GB recommended
- **Storage**: 20GB available disk space
- **Network**: Outbound HTTPS (443) and MQTT (8883) access

### **External Services**
- **MySQL 8.0** database server
- **HiveMQ Cloud** account and cluster
- **DNS** configuration for custom domains (optional)
- **SSL Certificate** for HTTPS (recommended)

---

## 🛠️ **Step 1: Environment Setup**

### **Install Java 17**
```bash
# Ubuntu/Debian
sudo apt update
sudo apt install openjdk-17-jdk

# CentOS/RHEL
sudo yum install java-17-openjdk-devel

# Verify installation
java -version
```

### **Install Maven (if building from source)**
```bash
# Ubuntu/Debian
sudo apt install maven

# CentOS/RHEL  
sudo yum install maven

# Verify installation
mvn -version
```

### **Create Application User**
```bash
sudo useradd -r -m -U -d /opt/coffeemachine -s /bin/false coffeemachine
sudo mkdir -p /opt/coffeemachine/logs
sudo chown -R coffeemachine:coffeemachine /opt/coffeemachine
```

---

## 🗄️ **Step 2: Database Setup**

### **MySQL Installation**
```bash
# Ubuntu/Debian
sudo apt install mysql-server-8.0

# CentOS/RHEL
sudo yum install mysql-server

# Start and enable MySQL
sudo systemctl start mysql
sudo systemctl enable mysql

# Secure MySQL installation
sudo mysql_secure_installation
```

### **Database Configuration**
```sql
-- Connect to MySQL as root
mysql -u root -p

-- Create database and user
CREATE DATABASE coffee_machine_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE USER 'coffee_user'@'localhost' IDENTIFIED BY 'your_secure_password_here';
GRANT ALL PRIVILEGES ON coffee_machine_db.* TO 'coffee_user'@'localhost';
FLUSH PRIVILEGES;

-- Verify connection
SHOW DATABASES;
EXIT;

-- Test connection with new user
mysql -u coffee_user -p coffee_machine_db
```

### **Database Tuning (Optional)**
```sql
-- /etc/mysql/mysql.conf.d/mysqld.cnf
[mysqld]
innodb_buffer_pool_size = 1G
innodb_log_file_size = 256M
max_connections = 200
query_cache_size = 32M
```

---

## 📡 **Step 3: HiveMQ Cloud Setup**

### **Create HiveMQ Cloud Cluster**
1. **Sign up** at [HiveMQ Cloud](https://www.hivemq.com/mqtt-cloud-broker/)
2. **Create a new cluster**:
   - Choose region closest to your deployment
   - Select appropriate tier (Serverless for development, Dedicated for production)
   - Note the cluster URL: `your-cluster.s1.eu.hivemq.cloud`

### **Configure Access Credentials**
1. **Create credentials** in HiveMQ Cloud Console
2. **Set permissions** for topic patterns:
   ```
   coffeeMachine/+/temperature    # Publish/Subscribe
   coffeeMachine/+/waterLevel     # Publish/Subscribe  
   coffeeMachine/+/milkLevel      # Publish/Subscribe
   coffeeMachine/+/beansLevel     # Publish/Subscribe
   coffeeMachine/+/status         # Publish/Subscribe
   coffeeMachine/+/usage          # Publish/Subscribe
   coffeeMachine/+/commands       # Publish/Subscribe
   ```

### **Test MQTT Connection**
```bash
# Install mosquitto clients for testing
sudo apt install mosquitto-clients

# Test connection (replace with your credentials)
mosquitto_sub -h your-cluster.s1.eu.hivemq.cloud -p 8883 \
  -u your_username -P your_password \
  --cafile /etc/ssl/certs/ca-certificates.crt \
  -t "coffeeMachine/+/status"
```

---

## ⚙️ **Step 4: Application Deployment**

### **Download/Build Application**

#### **Option A: Build from Source**
```bash
# Clone repository
git clone <your-repository-url>
cd coffee-machine-monitoring

# Build application
mvn clean package -DskipTests

# Copy JAR to deployment directory
sudo cp target/coffee-machine-monitoring-1.0.0-SNAPSHOT.jar /opt/coffeemachine/app.jar
```

#### **Option B: Download Release**
```bash
# Download pre-built JAR
wget https://github.com/your-org/coffee-machine-monitoring/releases/download/v1.0.0/coffee-machine-monitoring-1.0.0.jar

# Copy to deployment directory
sudo cp coffee-machine-monitoring-1.0.0.jar /opt/coffeemachine/app.jar
```

### **Set File Permissions**
```bash
sudo chown coffeemachine:coffeemachine /opt/coffeemachine/app.jar
sudo chmod 750 /opt/coffeemachine/app.jar
```

---

## 🔧 **Step 5: Configuration**

### **Create Environment File**
```bash
sudo vi /opt/coffeemachine/application.env
```

```bash
# /opt/coffeemachine/application.env

# Database Configuration
DB_USERNAME=coffee_user
DB_PASSWORD=your_secure_password_here

# HiveMQ Cloud Configuration
MQTT_BROKER_URL=ssl://your-cluster.s1.eu.hivemq.cloud:8883
MQTT_CLIENT_ID=coffee-machine-prod
MQTT_USERNAME=your_hivemq_username
MQTT_PASSWORD=your_hivemq_password

# Security Configuration
JWT_SECRET=your_super_secure_64_character_minimum_secret_key_here_12345

# Application Configuration
SPRING_PROFILES_ACTIVE=prod
SERVER_PORT=8080

# JVM Configuration
JAVA_OPTS=-Xms1g -Xmx2g -XX:+UseG1GC
```

### **Secure Environment File**
```bash
sudo chown coffeemachine:coffeemachine /opt/coffeemachine/application.env
sudo chmod 600 /opt/coffeemachine/application.env
```

### **Create Application Properties Override**
```bash
sudo vi /opt/coffeemachine/application-prod.yml
```

```yaml
# /opt/coffeemachine/application-prod.yml

server:
  port: ${SERVER_PORT:8080}
  servlet:
    context-path: /api

spring:
  datasource:
    url: jdbc:mysql://localhost:3306/coffee_machine_db?useSSL=true&serverTimezone=UTC
    username: ${DB_USERNAME}
    password: ${DB_PASSWORD}
    hikari:
      maximum-pool-size: 20
      minimum-idle: 5
      connection-timeout: 20000
      idle-timeout: 300000
      max-lifetime: 1200000

logging:
  level:
    com.example.coffeemachine: INFO
    org.springframework.security: WARN
  file:
    name: /opt/coffeemachine/logs/application.log
  pattern:
    file: "%d{yyyy-MM-dd HH:mm:ss.SSS} [%thread] %-5level %logger{36} - %msg%n"

management:
  endpoints:
    web:
      exposure:
        include: health,metrics,info
  endpoint:
    health:
      show-details: when-authorized
```

---

## 🎯 **Step 6: Systemd Service**

### **Create Service File**
```bash
sudo vi /etc/systemd/system/coffeemachine.service
```

```ini
[Unit]
Description=Coffee Machine Monitoring System
After=network.target mysql.service
Wants=mysql.service

[Service]
Type=simple
User=coffeemachine
Group=coffeemachine
WorkingDirectory=/opt/coffeemachine
EnvironmentFile=/opt/coffeemachine/application.env
ExecStart=/usr/bin/java $JAVA_OPTS -jar /opt/coffeemachine/app.jar --spring.config.additional-location=file:/opt/coffeemachine/
ExecStop=/bin/kill -TERM $MAINPID
Restart=always
RestartSec=10
StandardOutput=journal
StandardError=journal

# Security Settings
NoNewPrivileges=true
PrivateTmp=true
ProtectSystem=strict
ReadWritePaths=/opt/coffeemachine/logs
ProtectHome=true

[Install]
WantedBy=multi-user.target
```

### **Enable and Start Service**
```bash
# Reload systemd configuration
sudo systemctl daemon-reload

# Enable service to start at boot
sudo systemctl enable coffeemachine

# Start the service
sudo systemctl start coffeemachine

# Check service status
sudo systemctl status coffeemachine
```

### **Service Management Commands**
```bash
# Start service
sudo systemctl start coffeemachine

# Stop service
sudo systemctl stop coffeemachine

# Restart service
sudo systemctl restart coffeemachine

# View logs
sudo journalctl -u coffeemachine -f

# Check status
sudo systemctl status coffeemachine
```

---

## 🔍 **Step 7: Verification & Testing**

### **Health Checks**
```bash
# Application health check
curl http://localhost:8080/api/actuator/health

# Expected response:
# {"status":"UP","groups":["liveness","readiness"]}

# Database connectivity
curl http://localhost:8080/api/actuator/health/db

# MQTT connectivity (if implemented)
curl http://localhost:8080/api/actuator/health/mqtt
```

### **API Testing**
```bash
# Test facility creation
curl -X POST http://localhost:8080/api/admin/facility \
  -H "Content-Type: application/json" \
  -d '{"name": "Production Facility", "location": "Main Building"}'

# Test machine addition
curl -X POST http://localhost:8080/api/facility/1/machine \
  -H "Content-Type: application/json" \
  -d '{"status": "ON", "waterLevel": 100, "milkLevel": 100, "beansLevel": 100}'

# Test machine status
curl http://localhost:8080/api/machine/1/status
```

### **MQTT Testing**
```bash
# Simulate temperature update
mosquitto_pub -h your-cluster.s1.eu.hivemq.cloud -p 8883 \
  -u your_username -P your_password \
  --cafile /etc/ssl/certs/ca-certificates.crt \
  -t "coffeeMachine/1/temperature" \
  -m '{"temperature": 92.5, "timestamp": "2024-01-15T10:30:00Z"}'

# Verify in application logs
sudo journalctl -u coffeemachine -f | grep temperature
```

---

## 🛡️ **Step 8: Security Hardening**

### **Firewall Configuration**
```bash
# Allow SSH (if needed)
sudo ufw allow ssh

# Allow application port
sudo ufw allow 8080/tcp

# Allow outbound HTTPS and MQTT
sudo ufw allow out 443/tcp
sudo ufw allow out 8883/tcp

# Enable firewall
sudo ufw enable

# Check status
sudo ufw status
```

### **SSL/TLS Configuration (Optional)**
```bash
# Install Nginx as reverse proxy
sudo apt install nginx

# Configure Nginx for SSL termination
sudo vi /etc/nginx/sites-available/coffeemachine
```

```nginx
server {
    listen 80;
    server_name your-domain.com;
    return 301 https://$server_name$request_uri;
}

server {
    listen 443 ssl http2;
    server_name your-domain.com;

    ssl_certificate /path/to/ssl/certificate.crt;
    ssl_certificate_key /path/to/ssl/private.key;
    
    # SSL Security Headers
    add_header Strict-Transport-Security "max-age=31536000; includeSubDomains" always;
    add_header X-Content-Type-Options nosniff always;
    add_header X-Frame-Options DENY always;

    location /api/ {
        proxy_pass http://localhost:8080/api/;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;
    }
}
```

### **Log Rotation**
```bash
sudo vi /etc/logrotate.d/coffeemachine
```

```
/opt/coffeemachine/logs/*.log {
    daily
    missingok
    rotate 30
    compress
    delaycompress
    notifempty
    create 644 coffeemachine coffeemachine
    postrotate
        systemctl reload coffeemachine
    endscript
}
```

---

## 📊 **Step 9: Monitoring & Alerting**

### **Application Monitoring**
```bash
# Install monitoring tools (optional)
sudo apt install htop iotop nethogs

# Monitor application performance
htop -p $(pgrep -f coffeemachine)

# Monitor disk I/O
sudo iotop -p $(pgrep -f coffeemachine)

# Monitor network usage
sudo nethogs
```

### **Log Monitoring**
```bash
# Real-time log monitoring
sudo tail -f /opt/coffeemachine/logs/application.log

# Error monitoring
sudo grep -i error /opt/coffeemachine/logs/application.log

# MQTT message monitoring
sudo grep -i mqtt /opt/coffeemachine/logs/application.log
```

### **Database Monitoring**
```sql
-- Monitor active connections
SHOW PROCESSLIST;

-- Monitor table sizes
SELECT 
    table_name,
    ROUND(((data_length + index_length) / 1024 / 1024), 2) AS 'DB Size in MB'
FROM information_schema.tables 
WHERE table_schema = 'coffee_machine_db';

-- Monitor recent activities
SELECT * FROM usage_history ORDER BY created_at DESC LIMIT 10;
SELECT * FROM alert WHERE created_at >= DATE_SUB(NOW(), INTERVAL 1 HOUR);
```

---

## 🔄 **Step 10: Backup & Recovery**

### **Database Backup**
```bash
# Create backup script
sudo vi /opt/coffeemachine/backup.sh
```

```bash
#!/bin/bash
# /opt/coffeemachine/backup.sh

BACKUP_DIR="/opt/coffeemachine/backups"
DATE=$(date +%Y%m%d_%H%M%S)
DB_NAME="coffee_machine_db"
DB_USER="coffee_user"
DB_PASS="your_secure_password_here"

# Create backup directory
mkdir -p $BACKUP_DIR

# Create database backup
mysqldump -u $DB_USER -p$DB_PASS $DB_NAME > $BACKUP_DIR/coffeemachine_backup_$DATE.sql

# Compress backup
gzip $BACKUP_DIR/coffeemachine_backup_$DATE.sql

# Remove backups older than 30 days
find $BACKUP_DIR -name "*.sql.gz" -mtime +30 -delete

echo "Backup completed: coffeemachine_backup_$DATE.sql.gz"
```

```bash
# Make script executable
sudo chmod +x /opt/coffeemachine/backup.sh

# Set up daily backup cron job
sudo crontab -e

# Add this line to run backup daily at 2 AM
0 2 * * * /opt/coffeemachine/backup.sh
```

### **Application Backup**
```bash
# Backup configuration and logs
sudo tar -czf /opt/coffeemachine/backups/app_config_$(date +%Y%m%d).tar.gz \
  /opt/coffeemachine/application.env \
  /opt/coffeemachine/application-prod.yml \
  /opt/coffeemachine/logs/
```

---

## 🚨 **Troubleshooting**

### **Common Issues**

#### **Service Won't Start**
```bash
# Check service status
sudo systemctl status coffeemachine

# Check logs
sudo journalctl -u coffeemachine -n 50

# Check Java version
java -version

# Check file permissions
ls -la /opt/coffeemachine/
```

#### **Database Connection Issues**
```bash
# Test database connection
mysql -u coffee_user -p coffee_machine_db

# Check MySQL service
sudo systemctl status mysql

# Check database configuration
sudo grep -A 10 "datasource:" /opt/coffeemachine/application-prod.yml
```

#### **MQTT Connection Issues**
```bash
# Test MQTT connection manually
mosquitto_pub -h your-cluster.s1.eu.hivemq.cloud -p 8883 \
  -u your_username -P your_password \
  --cafile /etc/ssl/certs/ca-certificates.crt \
  -t "test/topic" -m "test message"

# Check MQTT configuration
sudo grep -A 10 "mqtt:" /opt/coffeemachine/application-prod.yml

# Check network connectivity
telnet your-cluster.s1.eu.hivemq.cloud 8883
```

#### **High Memory Usage**
```bash
# Check JVM memory settings
ps aux | grep coffeemachine

# Monitor memory usage
sudo systemctl status coffeemachine

# Adjust JVM settings in /opt/coffeemachine/application.env
JAVA_OPTS=-Xms512m -Xmx1g -XX:+UseG1GC
```

### **Recovery Procedures**

#### **Database Recovery**
```bash
# Stop application
sudo systemctl stop coffeemachine

# Restore from backup
gunzip < /opt/coffeemachine/backups/coffeemachine_backup_YYYYMMDD_HHMMSS.sql.gz | \
  mysql -u coffee_user -p coffee_machine_db

# Start application
sudo systemctl start coffeemachine
```

#### **Application Recovery**
```bash
# Restart service
sudo systemctl restart coffeemachine

# Check logs for errors
sudo journalctl -u coffeemachine -f

# Verify health
curl http://localhost:8080/api/actuator/health
```

---

## 📈 **Performance Optimization**

### **JVM Tuning**
```bash
# Production JVM settings in application.env
JAVA_OPTS=-Xms2g -Xmx4g -XX:+UseG1GC -XX:MaxGCPauseMillis=200 -XX:+UnlockExperimentalVMOptions -XX:+UseCGroupMemoryLimitForHeap
```

### **Database Optimization**
```sql
-- Index optimization
ANALYZE TABLE facility, coffee_machine, usage_history, alert;

-- Query performance
SHOW SLOW QUERIES;
```

### **Connection Pool Tuning**
```yaml
spring:
  datasource:
    hikari:
      maximum-pool-size: 30
      minimum-idle: 10
      connection-timeout: 20000
      idle-timeout: 600000
      max-lifetime: 1800000
```

---

## 🎯 **Production Checklist**

### **Pre-Deployment**
- [ ] Java 17 installed and configured
- [ ] MySQL database created and secured
- [ ] HiveMQ Cloud cluster configured
- [ ] SSL certificates obtained (if using HTTPS)
- [ ] Firewall rules configured
- [ ] Application user created

### **Deployment**
- [ ] Application JAR deployed
- [ ] Environment variables configured
- [ ] Systemd service created and enabled
- [ ] Database migrations executed
- [ ] Health checks passing
- [ ] MQTT connectivity verified

### **Post-Deployment**
- [ ] Monitoring configured
- [ ] Log rotation set up
- [ ] Backup procedures implemented
- [ ] Security hardening applied
- [ ] Performance monitoring enabled
- [ ] Documentation updated

---

## 🆘 **Support**

For deployment issues or questions:
- 📧 **Email**: ops@coffeemachine-monitoring.com
- 💬 **Issues**: GitHub Issues
- 📖 **Documentation**: [Deployment Wiki](../../wiki/deployment)

---

**🚀 Ready for production deployment!**