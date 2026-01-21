# Multi-stage build for Spring Boot backend

# Stage 1: Build
FROM maven:3.9-amazoncorretto-17 AS build
WORKDIR /app

# Copy pom.xml and download dependencies
COPY notes-backend/pom.xml .
RUN mvn dependency:go-offline -B

# Copy source code and build
COPY notes-backend/src ./src
RUN mvn clean package -DskipTests

# Stage 2: Runtime
FROM amazoncorretto:17-alpine
WORKDIR /app

# Copy the JAR from build stage
COPY --from=build /app/target/*.jar app.jar

# Create directory for data
RUN mkdir -p /app/data

# Expose port (will be overridden by Render's PORT env variable)
EXPOSE ${PORT:-8080}

# Run the application with production profile
ENTRYPOINT ["sh", "-c", "java -Dserver.port=${PORT:-8080} -Dspring.profiles.active=${SPRING_PROFILES_ACTIVE:-prod} -jar app.jar"]
