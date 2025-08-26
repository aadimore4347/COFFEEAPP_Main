# Coffee Machine Backend API

This is the core backend API for the Coffee Machine Monitoring application. It provides REST endpoints for managing facilities, coffee machines, users, and alerts.

## Features

- **REST API**: Complete REST endpoints for facility and admin operations
- **JWT Authentication**: Secure authentication and authorization
- **Database**: MySQL (production) and H2 (development) support
- **Migrations**: Flyway database migrations
- **Documentation**: OpenAPI/Swagger UI integration
- **Auditing**: Automatic audit trail for all entities

## Technology Stack

- Java 17
- Spring Boot 3.2.0
- Spring Security with JWT
- Spring Data JPA
- Flyway for migrations
- MySQL/H2 databases
- Lombok and MapStruct
- OpenAPI documentation

## Getting Started

### Prerequisites

- Java 17+
- Maven 3.6+
- MySQL (for production) or H2 (for development)

### Running the Application

1. **Development Profile (H2 in-memory database):**
   ```bash
   mvn spring-boot:run -Dspring-boot.run.profiles=dev
   ```

2. **Production Profile (MySQL):**
   ```bash
   mvn spring-boot:run -Dspring-boot.run.profiles=prod
   ```

3. **Test Profile:**
   ```bash
   mvn spring-boot:run -Dspring-boot.run.profiles=test
   ```

### API Documentation

Once running, access the Swagger UI at:
- http://localhost:8080/api/swagger-ui.html

### Database

- **Development**: H2 in-memory database with auto-created schema
- **Production**: MySQL with Flyway migrations
- **H2 Console**: Available at http://localhost:8080/api/h2-console (dev profile only)

## Architecture

The application is designed as a clean, layered architecture:

- **Controllers**: REST endpoints and request/response handling
- **Services**: Business logic and orchestration
- **Repositories**: Data access layer
- **Domain**: JPA entities with auditing
- **Security**: JWT-based authentication and role-based authorization

## MQTT Integration

This service does not handle MQTT directly. Instead, it receives updates from the separate `mqtt-worker` service via REST calls. The mqtt-worker subscribes to MQTT topics and forwards data to this API.

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
java -jar target/coffee-machine-backend-api-1.0.0.jar
```