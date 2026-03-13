# Multi-stage build for Spring Boot application

# Stage 1: Build stage - compile and package the application
FROM maven:3.9-eclipse-temurin-17 AS build

# Set working directory
WORKDIR /app

# Copy pom.xml first (for layer caching - dependencies don't change often)
COPY pom.xml .

# Download dependencies (this layer will be cached if pom.xml doesn't change)
RUN mvn dependency:go-offline -B

# Copy source code
COPY src ./src

# Build the application (skip tests for faster builds)
RUN mvn clean package -DskipTests

# Stage 2: Runtime stage - run the application
FROM eclipse-temurin:17-jre-alpine

# Set working directory
WORKDIR /app

# Copy the JAR from build stage
COPY --from=build /app/target/*.jar app.jar

# Create a non-root user for security
RUN addgroup -S spring && adduser -S spring -G spring

# Create data directory and give ownership to spring user
RUN mkdir -p /app/data && chown -R spring:spring /app/data

USER spring:spring

# Expose port (will be mapped via docker-compose)
EXPOSE 8080

# Environment variables (will be overridden by docker-compose)
ENV SPRING_PROFILES_ACTIVE=master
ENV SERVER_PORT=8080

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]
