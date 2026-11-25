# Stage 1: Build the Spring Boot application
FROM maven:3.9-eclipse-temurin-21-alpine AS build
WORKDIR /app

# Configure Google Maven Central mirror before any Maven command runs
RUN mkdir -p /root/.m2 && \
    printf '<settings><mirrors><mirror><id>google-maven-central</id><name>Google Maven Central Mirror</name><url>https://maven-central.storage-download.googleapis.com/maven2/</url><mirrorOf>central</mirrorOf></mirror></mirrors></settings>' > /root/.m2/settings.xml

# Copy pom.xml and pre-download dependencies into the Docker layer cache
COPY pom.xml .
RUN set -e; \
    ok=0; \
    for i in 1 2 3 4 5; do \
      if mvn dependency:go-offline -B; then \
        ok=1; \
        break; \
      fi; \
      echo "Maven dependency download attempt $i failed, retrying in 15s..."; \
      sleep 15; \
    done; \
    [ "$ok" -eq 1 ]

# Copy source code and build the executable JAR
COPY src ./src
RUN mvn clean package -DskipTests -B

# Stage 2: Run the application
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

RUN addgroup -S appgroup && adduser -S appuser -G appgroup

# Copy the built JAR from the build stage
COPY --from=build /app/target/*.jar app.jar
RUN chown appuser:appgroup /app/app.jar

USER appuser

# Expose port (Render automatically assigns $PORT at runtime)
EXPOSE 8080

# Run the Spring Boot app
ENTRYPOINT ["java", "-jar", "app.jar"]