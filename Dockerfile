# Stage 1: Build the Spring Boot application
FROM openjdk:17-jdk-slim as builder

WORKDIR /app

# Copy the Gradle wrapper and other necessary files
COPY gradlew /app/gradlew
COPY gradle /app/gradle
COPY build.gradle /app/
COPY settings.gradle /app/

# Copy the source code
COPY src /app/src

# Make gradlew executable
RUN chmod +x /app/gradlew

# Set Gradle user home
ENV GRADLE_USER_HOME /app/.gradle

# Verify Java installation
RUN java -version
RUN javac -version

# Log directory structure before build
RUN echo "Directory structure before build:" && find /app

# Build the application
RUN ./gradlew --no-daemon bootJar

# Log directory structure after build
RUN echo "Directory structure after build:" && find /app

# Stage 2: Set up Nginx and copy the Spring Boot application
FROM openjdk:17-jdk-slim

# Install Nginx
RUN apt-get update && apt-get install -y nginx && rm -rf /var/lib/apt/lists/*

# Copy the Spring Boot application from the builder stage
COPY --from=builder /app/build/libs/*.jar /app/app.jar

# Copy Nginx configuration
COPY nginx.conf /etc/nginx/nginx.conf

# Copy static files
COPY src/frontend/build /usr/share/nginx/html

# Copy SSL certificates
COPY secrets /etc/letsencrypt/live/writeshite.com
COPY secrets/archive /etc/letsencrypt/archive/writeshite.com

# Add the start script
COPY start.sh /app/start.sh
RUN chmod +x /app/start.sh

# Expose ports for Nginx and the Spring Boot application
EXPOSE 80 8080 443

# Start Nginx and the Spring Boot application
CMD ["/app/start.sh"]
