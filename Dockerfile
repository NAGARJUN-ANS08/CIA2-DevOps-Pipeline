# Minimal Production Runtime
FROM eclipse-temurin:25-jre

WORKDIR /app

# Copy the pre-built jar from the workspace target directory
COPY target/student-management-1.0.0.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]