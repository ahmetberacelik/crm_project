# ---- Build Stage ----
FROM maven:3.9.6-eclipse-temurin-17-focal AS builder
WORKDIR /app
# Parent and module poms
COPY pom.xml .
COPY department-service/pom.xml ./department-service/
COPY user-service/pom.xml ./user-service/
COPY ticket-service/pom.xml ./ticket-service/
# download deps
RUN mvn -f department-service/pom.xml dependency:go-offline
# copy source
COPY ./department-service/src ./department-service/src
RUN mvn -pl department-service -am package -DskipTests

# ---- Runtime Stage ----
FROM eclipse-temurin:17-jre-focal
WORKDIR /app
ARG DEPARTMENT_SERVER_PORT
COPY --from=builder /app/department-service/target/department-service-*.jar app.jar
EXPOSE ${DEPARTMENT_SERVER_PORT}
ENTRYPOINT ["java","-jar","app.jar"]
