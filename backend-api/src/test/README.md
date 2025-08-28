# Testing Guide for Coffee Machine Monitoring System

## Overview
This directory contains comprehensive tests for the Coffee Machine Monitoring System backend API.

## Test Structure
```
src/test/
├── java/com/example/coffeemachine/
│   ├── CoffeeMachineMonitoringApplicationTests.java  # Main application tests
│   ├── web/                                          # Controller tests
│   │   ├── AuthControllerTest.java                   # Authentication tests
│   │   ├── AdminControllerTest.java                  # Admin operations tests
│   │   └── ...                                       # Other controller tests
│   ├── security/                                     # Security tests
│   │   └── JwtTokenProviderTest.java                 # JWT token tests
│   ├── mqtt/                                         # MQTT integration tests
│   │   └── MqttIntegrationTest.java                  # MQTT functionality tests
│   └── config/                                       # Test configurations
│       └── MqttTestConfig.java                       # MQTT test config
└── resources/
    └── application-test.properties                   # Test-specific properties
```

## Running Tests

### Prerequisites
- Java 17+
- Maven 3.6+
- H2 Database (for in-memory testing)

### Commands

#### Run All Tests
```bash
mvn test
```

#### Run Specific Test Class
```bash
mvn test -Dtest=AuthControllerTest
```

#### Run Tests with Coverage
```bash
mvn test jacoco:report
```

#### Run Integration Tests
```bash
mvn test -Dspring.profiles.active=test
```

## Test Categories

### 1. Unit Tests
- **Controller Tests**: Test REST endpoints and request/response handling
- **Service Tests**: Test business logic and service layer
- **Security Tests**: Test JWT token generation and validation
- **Repository Tests**: Test data access layer (if applicable)

### 2. Integration Tests
- **MQTT Integration**: Test MQTT message handling and sensor data processing
- **Database Integration**: Test database operations and data persistence
- **Security Integration**: Test authentication and authorization flows

### 3. Configuration Tests
- **Profile Configuration**: Test different Spring profiles (dev, test, prod)
- **MQTT Configuration**: Test MQTT broker connection and topic configuration

## Test Data

### Sample Data
The `sample-data.sql` file contains comprehensive test data including:
- 5 facilities with different characteristics
- 13 coffee machines with various states
- User accounts (admin and facility roles)
- Usage history and maintenance records
- Sample alerts and sensor data

### Test Credentials
- **Admin Users**: `admin`, `superadmin` (password: `password`)
- **Facility Users**: `downtown_manager`, `tech_supervisor`, etc. (password: `password`)

## MQTT Testing

### Test Configuration
- Test profile uses localhost MQTT broker (port 1883)
- H2 in-memory database for testing
- Mock MQTT services for unit tests

### Sensor Data Testing
- Temperature updates (92.5°C, 91.0°C, etc.)
- Level monitoring (water, milk, beans)
- Status updates (ON, OFF, ERROR)
- Usage event tracking

## Database Testing

### Test Database
- **Type**: H2 in-memory database
- **Profile**: `test`
- **DDL**: `create-drop` (fresh schema for each test)
- **SQL Logging**: Disabled for performance

### Test Data Management
- Tests use `@Transactional` for data isolation
- Each test method runs in its own transaction
- Data is automatically cleaned up after each test

## Security Testing

### Authentication Tests
- Valid login credentials
- Invalid login attempts
- Token refresh functionality
- User profile retrieval

### Authorization Tests
- Role-based access control
- Admin vs. Facility user permissions
- Protected endpoint access

## Best Practices

### Test Naming
- Use descriptive test method names
- Follow pattern: `methodName_Scenario_ExpectedResult`
- Example: `login_ValidCredentials_ReturnsSuccess`

### Test Isolation
- Each test should be independent
- Use `@BeforeEach` for setup
- Avoid test dependencies

### Mock Usage
- Mock external services (MQTT, database)
- Use `@MockBean` for Spring context tests
- Verify mock interactions when relevant

## Troubleshooting

### Common Issues

1. **Test Context Loading Failures**
   - Check `@ActiveProfiles("test")` annotation
   - Verify test properties configuration
   - Ensure required beans are available

2. **Database Connection Issues**
   - Verify H2 dependency in pom.xml
   - Check test profile configuration
   - Ensure no conflicting database configurations

3. **MQTT Connection Failures**
   - Check MQTT broker availability
   - Verify test MQTT configuration
   - Use mock MQTT services for unit tests

### Debug Mode
Enable debug logging for tests:
```properties
logging.level.com.example.coffeemachine=DEBUG
logging.level.org.springframework.test=DEBUG
```

## Coverage Goals
- **Line Coverage**: > 80%
- **Branch Coverage**: > 70%
- **Method Coverage**: > 85%

## Continuous Integration
Tests are automatically run on:
- Pull request creation
- Code push to main branch
- Scheduled nightly builds

## Additional Resources
- [Spring Boot Testing Guide](https://spring.io/guides/gs/testing-web/)
- [JUnit 5 Documentation](https://junit.org/junit5/docs/current/user-guide/)
- [Mockito Documentation](https://javadoc.io/doc/org.mockito/mockito-core/latest/org/mockito/Mockito.html)