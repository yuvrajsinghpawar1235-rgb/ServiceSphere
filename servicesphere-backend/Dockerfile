# Stage 1: Build the Spring Boot application
FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /app

# Copy Maven wrapper and pom files
COPY pom.xml .
COPY src ./src

# Build the jar file, skipping tests for fast deployment
RUN for i in 1 2 3 4 5; do \
      mvn clean package -DskipTests -B && break; \
      echo "Maven build attempt $i failed, retrying in 15s..."; \
      sleep 15; \
    done

# Stage 2: Run the application
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# Copy the built jar from the build stage
COPY --from=build /app/target/*.jar app.jar

# Expose port (Render automatically assigns $PORT at runtime)
EXPOSE 8080

# Run the Spring Boot app
ENTRYPOINT ["java", "-jar", "app.jar"]