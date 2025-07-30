# Stage 1: Build the application using Maven
# We use a specific version of Maven with Eclipse Temurin 17 to match our project's Java version.
FROM maven:3.9.6-eclipse-temurin-17-focal AS builder

# Set the working directory inside the container
WORKDIR /app

# Copy the parent pom.xml from the project root
# This requires the Docker build context to be the project's root directory.
COPY pom.xml .

# Copy the user-service module's pom.xml
COPY user-service/pom.xml ./user-service/


# Download all dependencies into the local Maven repository.
# This step is cached by Docker, so it only runs again if the pom files change.
RUN mvn -f user-service/pom.xml dependency:go-offline

# Copy the source code of the user-service module
COPY ./user-service/src ./user-service/src

# Package the application into a JAR file, skipping the tests.
# The "-pl" flag selects the user-service project, and "-am" builds its dependencies too.
RUN mvn -pl user-service -am package -DskipTests

# Stage 2: Create the final, lightweight runtime image
# We use a JRE (Java Runtime Environment) image, which is smaller than a full JDK.
FROM eclipse-temurin:17-jre-focal

# Set the working directory for the runtime image
WORKDIR /app

# Copy the executable JAR file created in the 'builder' stage
# The name of the JAR is determined by the <artifactId> and <version> in the user-service/pom.xml
COPY --from=builder /app/user-service/target/user-service-*.jar app.jar

# Expose the port that the Spring Boot application will run on.
# Bu port .env dosyasındaki USER_SERVER_PORT değişkeni ile eşleşmelidir.
ARG USER_SERVER_PORT
EXPOSE ${USER_SERVER_PORT}

# Define the command to execute when the container starts.
ENTRYPOINT ["java", "-jar", "app.jar"] 