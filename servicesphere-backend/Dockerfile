# Stage 1: Build the Java application using Maven with Java 21
FROM maven:3.9-eclipse-temurin-21-alpine AS build
WORKDIR /app

# Cache dependencies before copying source code
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copy source and build the JAR
COPY src ./src
RUN mvn clean package -DskipTests -B

# Stage 2: Run the built JAR file using a lightweight JRE 21 runtime
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# Run as a non-root user
RUN addgroup -S appgroup && adduser -S appuser -G appgroup

# Copy only the packaged artifact (avoid matching *.jar.original if present)
COPY --from=build /app/target/*-SNAPSHOT.jar app.jar
RUN chown appuser:appgroup app.jar

USER appuser
EXPOSE 8080
ENTRYPOINT ["java", "-XX:MaxRAMPercentage=75.0", "-jar", "app.jar"]