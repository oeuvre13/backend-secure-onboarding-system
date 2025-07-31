# Multi-stage build untuk Secure Onboarding
# Stage 1: Build stage
FROM maven:3.9.8-eclipse-temurin-21 AS build
WORKDIR /app

# Copy pom.xml dan download dependencies
COPY pom.xml .
COPY mvnw .
COPY .mvn .mvn
RUN ./mvnw dependency:go-offline

# Copy source code
COPY src ./src

# Copy environment files dengan default fallback
COPY .env* ./
COPY src/main/resources/model-parsec-465503-p3-firebase-adminsdk-fbsvc-1e9901efad.json* ./src/main/resources/

# Build aplikasi
RUN ./mvnw clean package -DskipTests

# Stage 2: Runtime stage
FROM eclipse-temurin:21-jre
WORKDIR /app

# Copy JAR dari build stage
COPY --from=build /app/target/*.jar app.jar

# Copy .env file ke runtime (jika ada)
COPY --from=build /app/.env* ./

# Environment variables (match dengan application.properties)
ENV DB_URL=jdbc:postgresql://postgres-db:5432/customer_registration
ENV DB_USERNAME=postgres
ENV DB_PASSWORD=postgres123
ENV JWT_SECRET=a1b2c3d4e5f6789012345678901234567890abcdef1234567890abcdef123456789012345678901234567890abcdef1234567890abcdef1234567890abcdef
ENV JWT_EXPIRATION=86400000
ENV SERVER_PORT=8080
ENV FIREBASE_CONFIG_PATH=model-parsec-465503-p3-firebase-adminsdk-fbsvc-1e9901efad.json
ENV DUKCAPIL_SERVICE_URL=http://dukcapil-dummy:8081
ENV DUKCAPIL_API_KEY=dukcapil-secret-key-123
ENV APP_CORS_ALLOWED_ORIGINS=http://localhost:3000,http://localhost:5173,http://wondrdesktop.andrc1613.my.id

# Expose port
EXPOSE 8080

# Run aplikasi dengan nama JAR yang fixed
CMD ["java", "-jar", "app.jar"]
