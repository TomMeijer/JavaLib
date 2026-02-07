# JavaLib

JavaLib is a utility library for Java 21 applications, providing common components for HTTP communication, JWT-based security, and error logging.

## Features

- **Security**: JWT creation, validation, and Spring Security integration (Filter, AuthService).
- **HTTP Client**: A flexible `HttpClient` wrapper around Spring's `RestTemplate` with support for builder-pattern requests and interceptors.
- **Error Logging**: Pluggable error logging system with implementations for Console and Slack.
- **Spring Integration**: Designed to work seamlessly with Spring Web and Spring Security.

## Installation

Add the following dependency to your `pom.xml`:

[View on GitHub Packages](https://github.com/TomMeijer/JavaLib/packages/1947529)

```xml
<dependency>
    <groupId>com.tommeijer</groupId>
    <artifactId>javalib</artifactId>
    <version>2.0.0</version>
</dependency>
```

You may also need to configure your `settings.xml` to include the GitHub Packages repository:

```xml
<repository>
    <id>github</id>
    <url>https://maven.pkg.github.com/TomMeijer/JavaLib</url>
</repository>
```

## Modules

### 1. Security

The security module provides tools for handling JWT authentication.

- **JwtService**: Handles JWT creation and validation using `jjwt`.
- **AuthTokenFilter**: A Spring Security filter that extracts Bearer tokens and sets the authentication context.
- **AuthService**: Simplifies the authentication flow and token refreshing.

#### Example: Creating a JwtService
```java
TokenService tokenService = new JwtService("your-secret-key", 3600000L);
String token = tokenService.create("username");
```

### 2. HTTP Client

A simple wrapper around `RestTemplate` to perform HTTP requests using a builder pattern.

- **HttpClient**: Interface for executing requests.
- **HttpRequest**: Builder-based class for defining requests (URL, Method, Headers, Query Params, Body).
- **BearerAuthInterceptor**: Interceptor to automatically add Bearer tokens to requests.

#### Example: Performing a GET Request
```java
HttpClient client = new DefaultHttpClient(new RestTemplate());
HttpRequest request = HttpRequest.builder()
        .method(HttpRequest.Method.GET)
        .url("https://api.example.com/data")
        .queryParam("key", "value")
        .build();

MyResponse response = client.executeRequest(request, MyResponse.class);
```

### 3. Error Logging

A pluggable error logging system.

- **ErrorLogger**: Main interface for logging errors.
- **ConsoleErrorLogger**: Logs errors to the console.
- **SlackErrorLogger**: Uploads stack traces to a Slack channel as a file.
- **DelegatingErrorLogger**: Broadcasts errors to multiple loggers.

#### Example: Using DelegatingErrorLogger
```java
ErrorLogger logger = new DelegatingErrorLogger(List.of(
    new ConsoleErrorLogger(),
    new SlackErrorLogger("C12345", slackHttpClient)
));

logger.log("Something went wrong", throwable);
```

## Requirements

- Java 21 or higher
- Spring Framework 7.0.2+ (Web and Security)
- Jackson (for JSON mapping)

## License

This project is licensed under the terms provided in the [LICENSE](LICENSE) file.
