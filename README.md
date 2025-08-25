# ☕ Coffee Machine Monitoring System

A comprehensive, enterprise-grade **real-time coffee machine monitoring and management system** built with **Spring Boot**, **MQTT**, and **HiveMQ Cloud**.

## 🌟 **Key Features**

### 🔄 **Real-Time Monitoring**
- **MQTT Integration** with HiveMQ Cloud for instant machine data
- **Live tracking** of temperature, water/milk/beans levels
- **Status monitoring** (ON/OFF/ERROR states)
- **Usage analytics** with brewing history

### 🚨 **Intelligent Alert System**  
- **Threshold-based alerts** for low supplies
- **Malfunction detection** and notifications
- **Alert debouncing** to prevent spam
- **Multi-severity levels** (INFO/WARNING/CRITICAL)

### 👥 **Role-Based Access Control**
- **ADMIN** users: Full system management
- **FACILITY** users: Manage assigned facilities only
- **JWT authentication** (configurable)
- **Secure API endpoints**

### 📊 **Analytics & Reporting**
- **Usage statistics** and trends
- **Facility performance** metrics
- **Alert history** and resolution tracking
- **Machine health** monitoring

### 🏗️ **Enterprise Architecture**
- **Microservice-ready** Spring Boot application
- **Soft delete** for data integrity
- **Audit trails** (created/updated timestamps)
- **Database migrations** with Flyway
- **Comprehensive API** documentation with Swagger

---

## 🚀 **Quick Start**

### **Prerequisites**
- **Java 17** or higher
- **Maven 3.9+**
- **MySQL 8.0** (for production) or **H2** (for development)
- **HiveMQ Cloud** account (or local Mosquitto for development)

### **1. Clone & Build**
```bash
git clone <repository-url>
cd coffee-machine-monitoring
mvn clean install
```

### **2. Development Setup (H2 Database)**
```bash
# Run with development profile (uses H2 in-memory database)
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```

The application will start on `http://localhost:8080/api`

**Development Features:**
- ✅ H2 in-memory database with sample data
- ✅ Local MQTT broker configuration
- ✅ Swagger UI available at `/swagger-ui.html`
- ✅ H2 Console at `/h2-console` (username: `sa`, password: empty)

### **3. Production Setup**

#### **Environment Variables**
```bash
# Database Configuration
export DB_USERNAME=your_db_user
export DB_PASSWORD=your_db_password

# HiveMQ Cloud Configuration  
export MQTT_BROKER_URL=ssl://your-cluster.s1.eu.hivemq.cloud:8883
export MQTT_CLIENT_ID=coffee-machine-prod
export MQTT_USERNAME=your_hivemq_username
export MQTT_PASSWORD=your_hivemq_password

# Security Configuration
export JWT_SECRET=your-super-secure-secret-key-min-64-chars-long
```

#### **Run Production**
```bash
mvn spring-boot:run -Dspring-boot.run.profiles=prod
```

---

## 📡 **MQTT Integration**

### **Topic Structure**
```
coffeeMachine/{machineId}/temperature    # Temperature readings (°C)
coffeeMachine/{machineId}/waterLevel     # Water level (0-100%)
coffeeMachine/{machineId}/milkLevel      # Milk level (0-100%)
coffeeMachine/{machineId}/beansLevel     # Beans level (0-100%)
coffeeMachine/{machineId}/status         # Machine status (ON/OFF/ERROR)
coffeeMachine/{machineId}/usage          # Brewing events
coffeeMachine/{machineId}/commands       # Remote commands
```

### **Message Formats**

#### **Status Update**
```json
{
  "status": "ON",
  "timestamp": "2024-01-15T10:30:00Z"
}
```

#### **Level Update**
```json
{
  "level": 75,
  "timestamp": "2024-01-15T10:30:00Z"
}
```

#### **Usage Event**
```json
{
  "brewType": "ESPRESSO",
  "volume": 30,
  "timestamp": "2024-01-15T10:30:00Z"
}
```

### **HiveMQ Cloud Setup**
1. Create account at [HiveMQ Cloud](https://www.hivemq.com/mqtt-cloud-broker/)
2. Create a new cluster
3. Configure credentials and access control
4. Update environment variables with connection details

---

## 🔌 **REST API**

### **Base URL**: `http://localhost:8080/api`

### **Authentication Endpoints**
```http
POST /auth/login          # User login
POST /auth/refresh        # Refresh JWT token  
GET  /auth/profile        # Current user profile
```

### **Facility Endpoints**
```http
GET  /facility/{id}                    # Get facility details
GET  /facility/{id}/machines           # List facility machines
POST /facility/{id}/machine            # Add machine to facility
GET  /facility/{id}/statistics         # Facility statistics
GET  /facility/{id}/alerts             # Facility alerts
```

### **Machine Endpoints**
```http
GET  /machine/{id}/status              # Machine status & levels
GET  /machine/{id}/history?hours=24    # Usage history
POST /machine/{id}/brew                # Send brew command
GET  /machine/{id}/alerts              # Machine alerts
```

### **Admin Endpoints**
```http
POST /admin/facility                   # Create facility
GET  /admin/facilities                 # List all facilities
POST /admin/user                       # Create user
GET  /admin/usage                      # System statistics
GET  /admin/alerts                     # All system alerts
```

### **API Documentation**
- **Swagger UI**: `http://localhost:8080/swagger-ui.html`
- **OpenAPI JSON**: `http://localhost:8080/v3/api-docs`

---

## 🏗️ **Architecture**

### **Technology Stack**
- **Backend**: Spring Boot 3.2, Java 17
- **Database**: MySQL 8.0 (prod) / H2 (dev)
- **MQTT**: Spring Integration MQTT + HiveMQ Cloud
- **Security**: Spring Security + JWT
- **Documentation**: SpringDoc OpenAPI 3
- **Build**: Maven 3.9
- **Testing**: JUnit 5 + Testcontainers

### **Package Structure**
```
com.example.coffeemachine/
├── config/           # Configuration classes
├── domain/           # JPA entities
├── repository/       # Spring Data repositories  
├── service/          # Business logic
├── service/dto/      # Data Transfer Objects
├── service/mapper/   # MapStruct mappers
├── web/             # REST controllers
├── security/        # JWT & authentication
├── mqtt/            # MQTT message handling
├── alert/           # Alert evaluation system
└── util/            # Utility classes
```

### **Database Schema**
- **facility**: Facilities (buildings, locations)
- **coffee_machine**: Individual coffee machines
- **user**: System users (ADMIN/FACILITY roles)
- **alert**: System alerts and notifications
- **usage_history**: Machine usage tracking

---

## 🚨 **Alert System**

### **Alert Types**
- **LOW_WATER**: Water level < 20%
- **LOW_MILK**: Milk level < 20%  
- **LOW_BEANS**: Beans level < 20%
- **MALFUNCTION**: Machine errors or temperature issues

### **Alert Severities**
- **INFO**: Informational messages
- **WARNING**: Requires attention
- **CRITICAL**: Immediate action required

### **Alert Workflow**
1. **MQTT message** received with machine data
2. **Threshold evaluation** in AlertEvaluatorService
3. **Alert creation** if threshold exceeded
4. **Notification** sent (currently logging, extensible)
5. **Debouncing** prevents duplicate alerts

---

## 🔧 **Configuration**

### **Application Profiles**

#### **Development (`dev`)**
- H2 in-memory database
- Sample data auto-loaded
- Local Mosquitto broker
- Debug logging enabled
- Swagger UI enabled

#### **Production (`prod`)**
- MySQL database
- HiveMQ Cloud MQTT
- Optimized logging  
- External configuration via environment variables

### **Key Configuration Properties**

#### **Database**
```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/coffee_machine_db
    username: ${DB_USERNAME:coffee_user}
    password: ${DB_PASSWORD:coffee_pass}
```

#### **MQTT**
```yaml
mqtt:
  broker:
    url: ${MQTT_BROKER_URL:ssl://your-cluster.s1.eu.hivemq.cloud:8883}
    client-id: ${MQTT_CLIENT_ID:coffee-machine-prod}
    username: ${MQTT_USERNAME:your-username}
    password: ${MQTT_PASSWORD:your-password}
```

#### **Security**
```yaml
app:
  jwt:
    secret: ${JWT_SECRET:defaultDevSecretKey123456789012345678901234567890}
    expiration-ms: 86400000      # 24 hours
    refresh-expiration-ms: 604800000  # 7 days
```

---

## 🧪 **Testing**

### **Run Tests**
```bash
# Unit tests
mvn test

# Integration tests  
mvn integration-test

# All tests with coverage
mvn clean verify
```

### **Manual API Testing**

#### **1. Create a Facility (Admin)**
```bash
curl -X POST http://localhost:8080/api/admin/facility \
  -H "Content-Type: application/json" \
  -d '{"name": "Downtown Office", "location": "123 Main St"}'
```

#### **2. Add Machine to Facility**
```bash
curl -X POST http://localhost:8080/api/facility/1/machine \
  -H "Content-Type: application/json" \
  -d '{"status": "ON", "waterLevel": 80, "milkLevel": 60, "beansLevel": 90}'
```

#### **3. Send Brew Command**
```bash
curl -X POST http://localhost:8080/api/machine/1/brew \
  -H "Content-Type: application/json" \
  -d '{"brewType": "ESPRESSO", "volumeMl": 30}'
```

#### **4. Check Machine Status**
```bash
curl http://localhost:8080/api/machine/1/status
```

---

## 🚀 **Deployment**

### **Production Checklist**

#### **✅ Database Setup**
```sql
CREATE DATABASE coffee_machine_db;
CREATE USER 'coffee_user'@'%' IDENTIFIED BY 'secure_password';
GRANT ALL PRIVILEGES ON coffee_machine_db.* TO 'coffee_user'@'%';
FLUSH PRIVILEGES;
```

#### **✅ HiveMQ Cloud Setup**
1. Create HiveMQ Cloud cluster
2. Configure access credentials
3. Set up topic permissions
4. Test MQTT connection

#### **✅ Environment Variables**
```bash
# Required for production
export DB_USERNAME=coffee_user
export DB_PASSWORD=secure_password
export MQTT_BROKER_URL=ssl://your-cluster.s1.eu.hivemq.cloud:8883
export MQTT_USERNAME=your_mqtt_user
export MQTT_PASSWORD=your_mqtt_password
export JWT_SECRET=your_64_char_minimum_secret_key
```

#### **✅ Security Hardening**
- Change default JWT secret
- Use strong database passwords
- Enable HTTPS in production
- Configure proper CORS settings
- Set up firewall rules

#### **✅ Monitoring**
- **Application metrics**: `/actuator/metrics`
- **Health checks**: `/actuator/health`
- **Database monitoring**: Connection pool metrics
- **MQTT monitoring**: Message throughput
- **Alert monitoring**: Notification delivery

---

## 📊 **Monitoring & Operations**

### **Health Checks**
```bash
# Application health
curl http://localhost:8080/actuator/health

# Database connectivity  
curl http://localhost:8080/actuator/health/db

# MQTT connectivity
curl http://localhost:8080/actuator/health/mqtt
```

### **Metrics**
```bash
# Application metrics
curl http://localhost:8080/actuator/metrics

# JVM metrics
curl http://localhost:8080/actuator/metrics/jvm.memory.used

# Database metrics
curl http://localhost:8080/actuator/metrics/hikaricp.connections.active
```

### **Logging**
- **Application logs**: Structured JSON logging
- **MQTT logs**: Message processing details  
- **Security logs**: Authentication/authorization events
- **Alert logs**: Alert generation and resolution

---

## 🔧 **Troubleshooting**

### **Common Issues**

#### **MQTT Connection Failed**
```bash
# Check HiveMQ Cloud credentials
curl -v ssl://your-cluster.s1.eu.hivemq.cloud:8883

# Verify environment variables
echo $MQTT_BROKER_URL
echo $MQTT_USERNAME
```

#### **Database Connection Issues**
```bash
# Test MySQL connection
mysql -h localhost -u coffee_user -p coffee_machine_db

# Check environment variables
echo $DB_USERNAME
echo $DB_PASSWORD
```

#### **JWT Token Issues**
*Note: JWT is currently in demo mode. For production deployment:*
1. Implement proper JWT library compatibility
2. Enable JWT authentication in SecurityConfig
3. Test token generation and validation

---

## 🔮 **Future Enhancements**

### **Phase 6+ Roadmap**
- **Frontend Dashboard**: React-based real-time dashboards
- **Mobile App**: Native iOS/Android applications
- **Advanced Analytics**: Machine learning predictions
- **Multi-tenant Support**: SaaS deployment model
- **IoT Integration**: Direct hardware integration
- **Notification Channels**: Email, SMS, Slack webhooks
- **Performance Optimization**: Caching, database sharding

---

## 👥 **Contributing**

### **Development Setup**
1. Fork the repository
2. Create feature branch: `git checkout -b feature/amazing-feature`
3. Make changes and test thoroughly
4. Commit: `git commit -m 'Add amazing feature'`
5. Push: `git push origin feature/amazing-feature`
6. Create Pull Request

### **Code Standards**
- Follow Spring Boot best practices
- Include comprehensive tests
- Update documentation
- Use consistent code formatting

---

## 📄 **License**

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

---

## 🆘 **Support**

For technical support or questions:
- 📧 Email: support@coffeemachine-monitoring.com
- 💬 Issues: GitHub Issues
- 📖 Documentation: [Wiki](../../wiki)

---

**Built with ❤️ for coffee lovers and IoT enthusiasts**