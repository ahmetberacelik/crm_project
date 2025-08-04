# ---- Build Stage ----
# Use a specific Maven and JDK version consistent with other services
FROM maven:3.9.6-eclipse-temurin-17-focal AS builder

# Set the working directory
WORKDIR /app

# Copy all necessary POM files for the multi-module build context
# This requires the Docker build context to be the project's root directory.
COPY pom.xml .
COPY user-service/pom.xml ./user-service/
COPY department-service/pom.xml ./department-service/
COPY ticket-service/pom.xml ./ticket-service/

# Download all dependencies first to leverage Docker's layer caching
RUN mvn -f ticket-service/pom.xml dependency:go-offline

# Copy the source code for the ticket-service
COPY ./ticket-service/src ./ticket-service/src

# Package the application, building only the ticket-service module and its dependencies
RUN mvn -pl ticket-service -am package -DskipTests

# ---- Runtime Stage ----
# Use a lightweight JRE image for the final container
FROM eclipse-temurin:17-jre-focal

# Set the working directory
WORKDIR /app

# Argument for the server port, to be passed from docker-compose
ARG TICKET_SERVER_PORT

# Copy the built JAR from the builder stage
COPY --from=builder /app/ticket-service/target/ticket-service-*.jar app.jar

# Expose the port the application will run on
EXPOSE ${TICKET_SERVER_PORT}

# Define the command to run the application
ENTRYPOINT ["java","-