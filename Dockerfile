
# Stage 1: Build the Java application
FROM maven:3.9.16-eclipse-temurin-25 AS build

# Set the working directory
WORKDIR /app

# Copy Maven configuration
COPY pom.xml .

# Download dependencies
RUN mvn dependency:go-offline

# Copy application source code
COPY src ./src

# Compile, test and package the application
RUN mvn clean package

# Stage 2: Create the production runtime image
FROM eclipse-temurin:25-jre

# Set the working directory
WORKDIR /app

# Copy the executable JAR from the build stage
COPY --from=build /app/target/student-management-1.0.0.jar app.jar

# Document the application port
EXPOSE 8080

# Start the Java application
ENTRYPOINT ["java", "-jar", "app.jar"]
