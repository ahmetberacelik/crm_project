# Stage 1: Build the application using Maven
FROM maven:3.9.6-eclipse-temurin-17-focal AS builder

WORKDIR /app

# Parent POM
COPY pom.xml .
# Module POM
COPY ticket-service/pom.xml ./ticket-service/
COPY user-service/pom.xml ./user-service/

# Download dependencies (cached)
RUN mvn -f ticket-service/pom.xml dependency:go-offline

# Copy source code
COPY ./ticket-service/src ./ticket-service/src

# Package module JAR
RUN mvn -pl ticket-service -am package -DskipTests

# Stage 2: Runtime image
FROM eclipse-temurin:17-jre-focal
WORKDIR /app

# Copy built jar
COPY --from=builder /app/ticket-service/target/ticket-service-*.jar app.jar

# Expose ticket-service default port (override via env var if needed)
EXPOSE 8081

ENTRYPOINT ["java", "-jar", "app.jar"] 