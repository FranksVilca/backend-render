#!/bin/bash
# Render build script for Spring Boot backend

echo "Starting build process..."

# Navigate to the notes-backend directory
cd notes-backend

# Build the application using Maven wrapper
echo "Building with Maven..."
./mvnw clean package -DskipTests

# Check if build was successful
if [ $? -eq 0 ]; then
    echo "Build successful!"
    echo "Generated JAR file:"
    ls -lh target/*.jar
else
    echo "Build failed!"
    exit 1
fi
