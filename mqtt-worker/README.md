# Coffee Machine MQTT Worker

This service handles real-time MQTT message ingestion from coffee machines and forwards the data to the backend API via REST calls.

## Features

- **MQTT Client**: Subscribes to coffee machine topics
- **Message Processing**: Parses and validates MQTT payloads
- **REST Integration**: Forwards data to backend API
- **Real-time Processing**: Handles live machine updates
- **Error Handling**: Robust error handling and logging

## Technology Stack

- Java 17
- Spring Boot 3.2.0
- Spring Integration MQTT
- Eclipse Paho MQTT Client
- WebClient for REST calls
- Lombok for boilerplate reduction

## MQTT Topics

The service subscribes to the following topics:

- `coffeeMachine/{id}/temperature` - Machine temperature updates
- `coffeeMachine/{id}/waterLevel` - Water level updates
- `coffeeMachine/{id}/milkLevel` - Milk level updates
- `coffeeMachine/{id}/beansLevel` - Beans level updates
- `coffeeMachine/{id}/status` - Machine status updates (ON/OFF/ERROR)
- `coffeeMachine/{id}/usage` - Brewing event updates

## Architecture

The service follows a simple architecture:

1. **MQTT Listener**: Subscribes to topics and receives messages
2. **Message Handler**: Processes and validates incoming messages
3. **REST Client**: Forwards processed data to backend API
4. **Configuration**: Environment-specific MQTT and API settings

## Getting Started

### Prerequisites

- Java 17+
- Maven 3.6+
- MQTT Broker (Mosquitto, HiveMQ, etc.)
- Backend API running (for REST calls)

### Running the Application

1. **Development Profile:**
   ```bash
   mvn spring-boot:run -Dspring-boot.run.profiles=dev
   ```

2. **Production Profile:**
   ```bash
   mvn spring-boot:run -Dspring-boot.run.profiles=prod
   ```

### Configuration

The service can be configured via `application.yml` or environment variables:

- **MQTT Broker**: URL, credentials, client ID
- **Backend API**: Base URL for REST calls
- **Topics**: MQTT topic patterns to subscribe to

### Environment Variables

- `MQTT_BROKER_URL`: MQTT broker connection URL
- `MQTT_USERNAME`: MQTT broker username
- `MQTT_PASSWORD`: MQTT broker password
- `BACKEND_API_URL`: Backend API base URL

## Message Flow

1. **MQTT Message Received**: Service receives message on subscribed topic
2. **Payload Parsing**: Message payload is parsed and validated
3. **Data Transformation**: Raw data is converted to DTOs
4. **REST Call**: Data is sent to backend API via HTTP POST
5. **Response Handling**: Success/failure is logged

## Error Handling

- **MQTT Connection Issues**: Automatic reconnection with exponential backoff
- **Invalid Payloads**: Logged and skipped, no data loss
- **API Failures**: Retry logic and detailed error logging
- **Network Issues**: Graceful degradation and recovery

## Monitoring

The service provides comprehensive logging:

- MQTT connection status
- Message processing statistics
- API call success/failure rates
- Error details for troubleshooting

## Testing

Run tests with:
```bash
mvn test
```

## Building

Build the application with:
```bash
mvn clean package
```

The resulting JAR file can be run with:
```bash
java -jar target/coffee-machine-mqtt-worker-1.0.0.jar
```

## Integration with Backend API

This service is designed to work alongside the `backend-api` service:

1. **MQTT Worker**: Receives real-time machine data
2. **Backend API**: Processes and stores the data
3. **REST Communication**: HTTP calls between services
4. **Data Consistency**: Single source of truth in backend API

## Deployment

The service can be deployed independently:

- **Development**: Local MQTT broker (Mosquitto)
- **Production**: Cloud MQTT service (HiveMQ Cloud, AWS IoT, etc.)
- **Scaling**: Multiple instances can run for high availability