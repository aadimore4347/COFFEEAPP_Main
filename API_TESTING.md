# 🧪 **Coffee Machine Monitoring - API Testing Guide**

## **Comprehensive API Testing & Integration Examples**

This guide provides complete examples for testing all API endpoints and MQTT integration functionality.

---

## 🏃‍♂️ **Quick Start Testing**

### **Start Application**
```bash
# Development mode with sample data
mvn spring-boot:run -Dspring-boot.run.profiles=dev

# Application will be available at:
# - API: http://localhost:8080/api
# - Swagger UI: http://localhost:8080/swagger-ui.html
# - H2 Console: http://localhost:8080/h2-console
```

### **Sample Data Available**
After starting in dev mode, you'll have:
- **2 Facilities**: "Main Office" and "Warehouse"
- **4 Coffee Machines**: 2 per facility
- **Sample Users**: admin/admin123, facility/facility123
- **Usage History**: Pre-populated usage data
- **Sample Alerts**: Low supply alerts

---

## 🔐 **Authentication Testing**

### **Login (Currently in Demo Mode)**
```bash
# Login endpoint (returns demo token)
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "admin",
    "password": "admin123"
  }'
```

**Expected Response:**
```json
{
  "token": "demo-token-admin-1642248000000",
  "refreshToken": "demo-refresh-admin-1642248000000",
  "tokenType": "Bearer",
  "expiresIn": 86400
}
```

### **Get User Profile**
```bash
curl -X GET http://localhost:8080/api/auth/profile \
  -H "Authorization: Bearer demo-token-admin-1642248000000"
```

---

## 🏢 **Facility Management Testing**

### **1. List All Facilities (Admin)**
```bash
curl -X GET http://localhost:8080/api/admin/facilities
```

**Expected Response:**
```json
{
  "status": "success",
  "data": [
    {
      "id": 1,
      "name": "Main Office",
      "location": "123 Business Ave, City Center",
      "isActive": true,
      "createdAt": "2024-01-15T08:00:00Z",
      "machineCount": 2
    },
    {
      "id": 2,
      "name": "Warehouse",
      "location": "456 Industrial Blvd, Warehouse District",
      "isActive": true,
      "createdAt": "2024-01-15T08:00:00Z",
      "machineCount": 2
    }
  ],
  "count": 2
}
```

### **2. Create New Facility**
```bash
curl -X POST http://localhost:8080/api/admin/facility \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Downtown Branch",
    "location": "789 Downtown Plaza, Suite 100"
  }'
```

**Expected Response:**
```json
{
  "status": "success",
  "message": "Facility created successfully",
  "data": {
    "id": 3,
    "name": "Downtown Branch",
    "location": "789 Downtown Plaza, Suite 100",
    "isActive": true,
    "createdAt": "2024-01-15T10:30:00Z",
    "machineCount": 0
  }
}
```

### **3. Get Facility Details**
```bash
curl -X GET http://localhost:8080/api/facility/1
```

### **4. Update Facility**
```bash
curl -X PUT http://localhost:8080/api/admin/facility/1 \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Main Office - Updated",
    "location": "123 Business Ave, Floor 2, City Center"
  }'
```

---

## ☕ **Coffee Machine Testing**

### **1. List Facility Machines**
```bash
curl -X GET http://localhost:8080/api/facility/1/machines
```

**Expected Response:**
```json
{
  "status": "success",
  "data": [
    {
      "id": 1,
      "facilityId": 1,
      "facilityName": "Main Office",
      "status": "ON",
      "temperature": 92.5,
      "waterLevel": 45,
      "milkLevel": 30,
      "beansLevel": 75,
      "isActive": true,
      "lastUpdate": "2024-01-15T09:45:00Z"
    },
    {
      "id": 2,
      "facilityId": 1,
      "facilityName": "Main Office",
      "status": "OFF",
      "temperature": 23.1,
      "waterLevel": 80,
      "milkLevel": 60,
      "beansLevel": 40,
      "isActive": true,
      "lastUpdate": "2024-01-15T09:30:00Z"
    }
  ],
  "count": 2
}
```

### **2. Add Machine to Facility**
```bash
curl -X POST http://localhost:8080/api/facility/1/machine \
  -H "Content-Type: application/json" \
  -d '{
    "status": "ON",
    "temperature": 94.0,
    "waterLevel": 100,
    "milkLevel": 100,
    "beansLevel": 100
  }'
```

**Expected Response:**
```json
{
  "status": "success",
  "message": "Machine added to facility successfully",
  "data": {
    "id": 5,
    "facilityId": 1,
    "facilityName": "Main Office",
    "status": "ON",
    "temperature": 94.0,
    "waterLevel": 100,
    "milkLevel": 100,
    "beansLevel": 100,
    "isActive": true,
    "createdAt": "2024-01-15T10:30:00Z"
  }
}
```

### **3. Get Machine Status**
```bash
curl -X GET http://localhost:8080/api/machine/1/status
```

### **4. Update Machine Details**
```bash
curl -X PUT http://localhost:8080/api/machine/1 \
  -H "Content-Type: application/json" \
  -d '{
    "status": "ON",
    "temperature": 93.5,
    "waterLevel": 40,
    "milkLevel": 25,
    "beansLevel": 70
  }'
```

### **5. Send Brew Command**
```bash
curl -X POST http://localhost:8080/api/machine/1/brew \
  -H "Content-Type: application/json" \
  -d '{
    "brewType": "ESPRESSO",
    "volumeMl": 30
  }'
```

**Expected Response:**
```json
{
  "status": "success",
  "message": "Brew command sent successfully: ESPRESSO (30ml) by user demo-user",
  "data": {
    "brewType": "ESPRESSO",
    "volumeMl": 30,
    "machineId": 1,
    "timestamp": "2024-01-15T10:30:00Z",
    "commandSent": true
  }
}
```

### **6. Get Machine Usage History**
```bash
# Last 24 hours
curl -X GET "http://localhost:8080/api/machine/1/history?hours=24"

# Last 7 days  
curl -X GET "http://localhost:8080/api/machine/1/history?hours=168"

# Custom date range
curl -X GET "http://localhost:8080/api/machine/1/history?startDate=2024-01-01&endDate=2024-01-15"
```

**Expected Response:**
```json
{
  "status": "success",
  "data": [
    {
      "id": 1,
      "machineId": 1,
      "brewType": "ESPRESSO",
      "volumeMl": 30,
      "temperatureAtBrew": 92.5,
      "timestamp": "2024-01-15T10:15:00Z"
    },
    {
      "id": 2,
      "machineId": 1,
      "brewType": "CAPPUCCINO",
      "volumeMl": 150,
      "temperatureAtBrew": 93.0,
      "timestamp": "2024-01-15T10:00:00Z"
    }
  ],
  "count": 2,
  "timeRange": {
    "start": "2024-01-14T10:30:00Z",
    "end": "2024-01-15T10:30:00Z",
    "hours": 24
  }
}
```

---

## 🚨 **Alert Management Testing**

### **1. Get Machine Alerts**
```bash
curl -X GET http://localhost:8080/api/machine/1/alerts
```

**Expected Response:**
```json
{
  "status": "success",
  "data": [
    {
      "id": 1,
      "machineId": 1,
      "type": "LOW_WATER",
      "severity": "WARNING",
      "message": "Water level is low (15%). Please refill soon.",
      "threshold": 20.0,
      "currentValue": 15.0,
      "resolved": false,
      "createdAt": "2024-01-15T09:45:00Z"
    }
  ],
  "count": 1
}
```

### **2. Get Facility Alerts**
```bash
curl -X GET http://localhost:8080/api/facility/1/alerts
```

### **3. Get All System Alerts (Admin)**
```bash
curl -X GET http://localhost:8080/api/admin/alerts
```

### **4. Resolve Alert**
```bash
curl -X POST http://localhost:8080/api/alert/1/resolve \
  -H "Content-Type: application/json" \
  -d '{
    "resolution": "Water tank refilled by maintenance team"
  }'
```

---

## 👥 **User Management Testing**

### **1. Create New User (Admin)**
```bash
curl -X POST http://localhost:8080/api/admin/user \
  -H "Content-Type: application/json" \
  -d '{
    "username": "facility_manager",
    "password": "secure123",
    "role": "FACILITY",
    "facilityId": 1
  }'
```

### **2. List Users (Admin)**
```bash
curl -X GET http://localhost:8080/api/admin/users
```

### **3. Update User Profile**
```bash
curl -X PUT http://localhost:8080/api/user/profile \
  -H "Content-Type: application/json" \
  -d '{
    "username": "facility_manager_updated"
  }'
```

### **4. Change Password**
```bash
curl -X POST http://localhost:8080/api/user/change-password \
  -H "Content-Type: application/json" \
  -d '{
    "currentPassword": "secure123",
    "newPassword": "newsecure456"
  }'
```

---

## 📊 **Analytics & Statistics Testing**

### **1. Facility Statistics**
```bash
curl -X GET http://localhost:8080/api/facility/1/statistics
```

**Expected Response:**
```json
{
  "status": "success",
  "data": {
    "facilityId": 1,
    "facilityName": "Main Office",
    "totalMachines": 2,
    "activeMachines": 1,
    "totalBrews": 45,
    "averageTemperature": 92.8,
    "averageWaterLevel": 62.5,
    "averageMilkLevel": 45.0,
    "averageBeansLevel": 57.5,
    "activeAlerts": 1,
    "timeRange": "24h",
    "lastUpdated": "2024-01-15T10:30:00Z"
  }
}
```

### **2. System Usage Statistics (Admin)**
```bash
curl -X GET "http://localhost:8080/api/admin/usage?days=7"
```

### **3. Alert Statistics (Admin)**
```bash
curl -X GET "http://localhost:8080/api/admin/alert-statistics?days=30"
```

---

## 📡 **MQTT Integration Testing**

### **Prerequisites**
```bash
# Install MQTT client tools
sudo apt install mosquitto-clients

# For development (local Mosquitto)
mosquitto_sub -h localhost -t "coffeeMachine/+/+"

# For production (HiveMQ Cloud)
mosquitto_sub -h your-cluster.s1.eu.hivemq.cloud -p 8883 \
  -u your_username -P your_password \
  --cafile /etc/ssl/certs/ca-certificates.crt \
  -t "coffeeMachine/+/+"
```

### **1. Temperature Update**
```bash
# Publish temperature data
mosquitto_pub -h localhost -t "coffeeMachine/1/temperature" \
  -m '{"temperature": 94.5, "timestamp": "2024-01-15T10:30:00Z"}'

# Or with plain value
mosquitto_pub -h localhost -t "coffeeMachine/1/temperature" -m "94.5"
```

### **2. Water Level Update**
```bash
mosquitto_pub -h localhost -t "coffeeMachine/1/waterLevel" \
  -m '{"level": 25, "timestamp": "2024-01-15T10:30:00Z"}'
```

### **3. Status Update**
```bash
mosquitto_pub -h localhost -t "coffeeMachine/1/status" \
  -m '{"status": "ERROR", "timestamp": "2024-01-15T10:30:00Z"}'
```

### **4. Usage Event**
```bash
mosquitto_pub -h localhost -t "coffeeMachine/1/usage" \
  -m '{
    "brewType": "CAPPUCCINO",
    "volume": 150,
    "timestamp": "2024-01-15T10:30:00Z"
  }'
```

### **5. Bulk Test Data**
```bash
# Send multiple updates to trigger alerts
mosquitto_pub -h localhost -t "coffeeMachine/1/waterLevel" -m "15"
mosquitto_pub -h localhost -t "coffeeMachine/1/milkLevel" -m "10"
mosquitto_pub -h localhost -t "coffeeMachine/1/beansLevel" -m "5"
```

### **6. Monitor MQTT Processing**
```bash
# Watch application logs for MQTT processing
tail -f logs/spring.log | grep -i mqtt
```

---

## 🔄 **End-to-End Testing Scenario**

### **Complete Workflow Test**
```bash
#!/bin/bash
# End-to-end API testing script

BASE_URL="http://localhost:8080/api"

echo "🧪 Starting Coffee Machine Monitoring E2E Test"

# 1. Create new facility
echo "1. Creating new facility..."
FACILITY_RESPONSE=$(curl -s -X POST $BASE_URL/admin/facility \
  -H "Content-Type: application/json" \
  -d '{"name": "Test Facility", "location": "Test Location"}')

FACILITY_ID=$(echo $FACILITY_RESPONSE | jq -r '.data.id')
echo "   Created facility ID: $FACILITY_ID"

# 2. Add machine to facility
echo "2. Adding machine to facility..."
MACHINE_RESPONSE=$(curl -s -X POST $BASE_URL/facility/$FACILITY_ID/machine \
  -H "Content-Type: application/json" \
  -d '{"status": "ON", "waterLevel": 100, "milkLevel": 100, "beansLevel": 100}')

MACHINE_ID=$(echo $MACHINE_RESPONSE | jq -r '.data.id')
echo "   Created machine ID: $MACHINE_ID"

# 3. Send MQTT updates (simulate low water)
echo "3. Simulating low water alert..."
mosquitto_pub -h localhost -t "coffeeMachine/$MACHINE_ID/waterLevel" -m "15"
sleep 2

# 4. Check for alert
echo "4. Checking for generated alert..."
ALERTS_RESPONSE=$(curl -s -X GET $BASE_URL/machine/$MACHINE_ID/alerts)
ALERT_COUNT=$(echo $ALERTS_RESPONSE | jq -r '.count')
echo "   Generated alerts: $ALERT_COUNT"

# 5. Send brew command
echo "5. Sending brew command..."
BREW_RESPONSE=$(curl -s -X POST $BASE_URL/machine/$MACHINE_ID/brew \
  -H "Content-Type: application/json" \
  -d '{"brewType": "ESPRESSO", "volumeMl": 30}')

echo "   Brew command result: $(echo $BREW_RESPONSE | jq -r '.status')"

# 6. Check usage history
echo "6. Checking usage history..."
HISTORY_RESPONSE=$(curl -s -X GET "$BASE_URL/machine/$MACHINE_ID/history?hours=1")
USAGE_COUNT=$(echo $HISTORY_RESPONSE | jq -r '.count')
echo "   Usage records: $USAGE_COUNT"

# 7. Get facility statistics
echo "7. Getting facility statistics..."
STATS_RESPONSE=$(curl -s -X GET $BASE_URL/facility/$FACILITY_ID/statistics)
TOTAL_MACHINES=$(echo $STATS_RESPONSE | jq -r '.data.totalMachines')
echo "   Total machines in facility: $TOTAL_MACHINES"

echo "✅ E2E test completed successfully!"
```

Make the script executable and run:
```bash
chmod +x e2e_test.sh
./e2e_test.sh
```

---

## 🧪 **Load Testing**

### **Apache Bench Testing**
```bash
# Install Apache Bench
sudo apt install apache2-utils

# Test facility listing endpoint
ab -n 1000 -c 10 http://localhost:8080/api/admin/facilities

# Test machine status endpoint
ab -n 500 -c 5 http://localhost:8080/api/machine/1/status

# Test with POST requests
ab -n 100 -c 5 -p facility_payload.json -T application/json \
  http://localhost:8080/api/admin/facility
```

### **MQTT Load Testing**
```bash
#!/bin/bash
# MQTT load test - simulate multiple machines

for i in {1..10}; do
  # Simulate temperature readings every 5 seconds
  while true; do
    TEMP=$(shuf -i 90-96 -n 1)
    mosquitto_pub -h localhost -t "coffeeMachine/$i/temperature" -m "$TEMP"
    sleep 5
  done &
done
```

---

## 📊 **Performance Monitoring**

### **Application Metrics**
```bash
# JVM metrics
curl http://localhost:8080/api/actuator/metrics/jvm.memory.used

# HTTP request metrics
curl http://localhost:8080/api/actuator/metrics/http.server.requests

# Database connection pool metrics
curl http://localhost:8080/api/actuator/metrics/hikaricp.connections.active
```

### **Database Performance**
```sql
-- Monitor slow queries
SHOW FULL PROCESSLIST;

-- Check table performance
SELECT table_name, table_rows, 
       ROUND(((data_length + index_length) / 1024 / 1024), 2) AS 'Size MB'
FROM information_schema.tables 
WHERE table_schema = 'coffee_machine_db';

-- Recent activity
SELECT COUNT(*) as recent_brews FROM usage_history 
WHERE created_at >= DATE_SUB(NOW(), INTERVAL 1 HOUR);
```

---

## 🔍 **Debugging & Troubleshooting**

### **Common API Issues**

#### **404 Not Found**
```bash
# Check if service is running
curl http://localhost:8080/api/actuator/health

# Verify endpoint URL
curl -v http://localhost:8080/api/facility/1
```

#### **500 Internal Server Error**
```bash
# Check application logs
tail -f logs/spring.log

# Check database connectivity
curl http://localhost:8080/api/actuator/health/db
```

#### **MQTT Messages Not Processing**
```bash
# Check MQTT broker connection
mosquitto_sub -h localhost -t "coffeeMachine/+/+"

# Verify message format
mosquitto_pub -h localhost -t "coffeeMachine/1/temperature" \
  -m '{"temperature": 92.5, "timestamp": "2024-01-15T10:30:00Z"}'

# Check application logs for MQTT errors
grep -i mqtt logs/spring.log
```

### **Test Data Reset**
```bash
# Clear test data (development mode)
curl -X DELETE http://localhost:8080/api/admin/test-data/clear

# Reload sample data
curl -X POST http://localhost:8080/api/admin/test-data/reload
```

---

## 📋 **Test Coverage Checklist**

### **API Endpoints**
- [ ] Authentication (login, profile, refresh)
- [ ] Facility management (CRUD operations)
- [ ] Machine management (CRUD, status updates)
- [ ] User management (CRUD, password changes)
- [ ] Alert management (view, resolve)
- [ ] Usage tracking (history, statistics)
- [ ] Admin operations (system stats, user management)

### **MQTT Integration**
- [ ] Temperature updates
- [ ] Level updates (water, milk, beans)
- [ ] Status changes
- [ ] Usage events
- [ ] Alert generation
- [ ] Message validation

### **Error Handling**
- [ ] Invalid JSON payloads
- [ ] Missing required fields
- [ ] Invalid IDs (404 responses)
- [ ] Database connection failures
- [ ] MQTT broker disconnection
- [ ] Authentication failures

### **Performance**
- [ ] Response times < 500ms for GET requests
- [ ] Response times < 1000ms for POST requests
- [ ] Concurrent user handling
- [ ] MQTT message throughput
- [ ] Database query optimization
- [ ] Memory usage monitoring

---

## 🎯 **Production Testing**

### **Pre-Production Checklist**
- [ ] Health checks responding correctly
- [ ] Database migrations applied
- [ ] MQTT connectivity verified
- [ ] SSL certificates valid
- [ ] Environment variables configured
- [ ] Log rotation working
- [ ] Backup procedures tested

### **Smoke Tests**
```bash
# Basic connectivity
curl http://production-domain.com/api/actuator/health

# Authentication flow
curl -X POST http://production-domain.com/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username": "testuser", "password": "testpass"}'

# Core functionality
curl http://production-domain.com/api/admin/facilities
```

---

## 🔧 **Testing Tools & Scripts**

### **Postman Collection**
Import the provided Postman collection for interactive testing:
- [Coffee Machine API.postman_collection.json](./postman/Coffee_Machine_API.postman_collection.json)

### **Automated Test Scripts**
- **e2e_test.sh**: Complete end-to-end workflow
- **mqtt_simulator.sh**: MQTT message simulation
- **load_test.sh**: Performance testing
- **health_check.sh**: Production monitoring

---

**🧪 Happy Testing!**

The Coffee Machine Monitoring System is ready for comprehensive testing. Use this guide to verify all functionality before production deployment.