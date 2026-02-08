# Exercise 10 — Testing

## 0) What We’ll Build (Our Plan)

In this exercise, we introduce testing in a Spring Boot web application.
Until now, we focused on implementing features and understanding how Spring Boot works at runtime.
In this exercise, we focus on verifying correctness and preventing regressions by introducing automated tests.

Testing is a core part of professional software development.
A correct application is not only one that works now, but one that can be changed safely in the future.

In this exercise, we will implement three complementary layers of testing:

1. Adding dependencies for testing
- We will add all necessary libraries for unit, integration, and UI testing.
- We will explain why these dependencies are added with test scope.

2. Creating a dedicated test profile
- We will create a separate Spring profile called test.
- We will configure an in-memory database that is used only during tests.
- We will ensure that tests are isolated from development and production environments.

3. Writing Unit Tests (service-level testing)
- We will test business logic in isolation using JUnit and Mockito.
- We will mock repositories and other dependencies.
- We will test both valid behavior and all invalid input cases.

4. Writing Integration Tests (Spring Boot + MVC)
- We will start the full Spring context during tests.
- We will test controller endpoints using MockMvc.
- We will verify HTTP responses, model attributes, and rendered views.

5. Writing UI Scenario Tests (Selenium)
- We will simulate real user interaction using Selenium.
- We will run tests in a headless browser using HtmlUnit.
- We will organize UI tests using the Page Object pattern.

6. Final recap
- We will connect all testing layers.
- We will explain when each type of test is appropriate.

---

## 1) Adding dependencies for testing

### Why additional dependencies are needed

By default, a Spring Boot project does not include everything needed for advanced testing scenarios.
Different types of tests require different tools:

- Unit tests need a testing framework and mocking support.
- Integration tests need Spring test utilities.
- UI tests need a browser automation framework.

To support all of these, we add additional dependencies that are used only during testing.

### Adding dependencies in pom.xml

We extend the pom.xml file with the following dependencies:

```xml
<dependency>
    <groupId>org.seleniumhq.selenium</groupId>
    <artifactId>htmlunit-driver</artifactId>
    <version>4.8.3</version>
    <scope>test</scope>
</dependency>

<dependency>
    <groupId>junit</groupId>
    <artifactId>junit</artifactId>
    <scope>test</scope>
</dependency>

<dependency>
    <groupId>org.mockito</groupId>
    <artifactId>mockito-core</artifactId>
    <scope>test</scope>
</dependency>

<dependency>
    <groupId>org.seleniumhq.selenium</groupId>
    <artifactId>selenium-java</artifactId>
    <scope>test</scope>
</dependency>
````

### Explanation of the dependencies

JUnit is the core testing framework used to define and run tests.
It provides annotations, assertions, and exception checking.
JUnit is used in unit tests, integration tests, and UI tests.

Mockito is a mocking framework.
It allows us to replace real dependencies with fake ones and control their behavior.
Mockito is essential for unit testing, where we test one class without involving Spring or a database.

Selenium is a browser automation framework.
It allows us to simulate real user behavior such as opening pages, filling forms, clicking buttons, and navigating between views.

HtmlUnit is a headless browser.
It runs without a visible UI, executes fast, and is suitable for automated environments.

---

## 2) Creating a dedicated test profile

### Why we need a separate test profile

Tests must be isolated, repeatable, and independent of real data.
For this reason, we introduce a separate Spring profile named test.
This profile is used only when tests are executed.

### Adding application-test.properties

We create the following file:

src/main/resources/application-test.properties

```properties
server.port=9999

spring.datasource.url=jdbc:h2:mem:testdb
spring.datasource.username=sa
spring.datasource.password=

spring.jpa.hibernate.ddl-auto=create-drop
spring.jpa.show-sql=true
```

### Explanation of the configuration

The H2 in-memory database exists only during the test run.
All data is deleted after tests finish.

The create-drop option ensures that the schema is created at the start of tests and dropped at the end.
Each test run starts from a clean database state.

The fixed server port is required for Selenium tests and ensures predictable URLs.

### Activating the test profile

In Spring Boot tests, we explicitly activate the profile:

```java
@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
```

---

## 3) Writing Unit Tests with Mockito

### Purpose of unit tests

Unit tests focus on one class at a time.
They do not start Spring, do not use a database, and do not involve the web layer.
They test business logic only.

### Example: testing the register functionality

We create a RegisterTest class.

Key ideas:

Using the Mockito test runner enables Mockito annotations without loading Spring.

Repositories and encoders are mocked so their behavior can be fully controlled.

We test valid behavior where the user is registered successfully.

We test all invalid cases:

* null username
* empty username
* null password
* empty password
* passwords do not match
* username already exists

Each invalid case is verified using Assert.assertThrows to ensure correct error handling.

---

## 4) Writing Integration Tests with Spring Boot

### Purpose of integration tests

Integration tests verify that Spring components are wired correctly.
They test controllers, services, repositories, and configuration together.

### Using MockMvc

MockMvc allows us to simulate HTTP requests without a browser.
We can verify HTTP status codes, model attributes, and view names.
The Spring application context is started, but requests are handled internally.

---

## 5) Writing UI Scenario Tests with Selenium

### Why UI tests are needed

Some issues cannot be detected by unit or integration tests.
These include broken templates, incorrect form bindings, and navigation errors.

UI tests simulate real user interaction.

### Page Object pattern

We use the Page Object pattern to keep tests readable and maintainable.

AbstractPage contains shared navigation logic.
LoginPage contains login-related actions.
CategoriesPage contains assertions for the categories view.

### Selenium scenario

A typical scenario:

1. Open the login page
2. Enter username and password
3. Submit the form
4. Navigate to the categories page
5. Verify displayed data

This validates the full user flow from UI to database.

---

## 6) Final recap

In this exercise, we introduced a complete testing strategy:

* Unit tests validate business logic
* Integration tests validate Spring configuration and wiring
* UI tests validate real user interaction

All three layers are necessary to build robust and maintainable Spring Boot applications.
