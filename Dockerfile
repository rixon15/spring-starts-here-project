# --- STAGE 1: BUILD STAGE (Compiling the JAR) ---
FROM eclipse-temurin:21-jdk-alpine AS builder

# Set the working directory inside the container
WORKDIR /app

# Copy the Maven/Gradle project files first (for efficient caching)
COPY pom.xml .
COPY src ./src

RUN ./mvnw clean package -DskipTests

# --- STAGE 2: RUNNING STAGE (Creating the Final Image) ---
# We switch to a JRE-only image. This image is much smaller and more secure than the full JDK image.
FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

EXPOSE 8080

ARG JAR_FILE=target/*.jar
COPY --from=builder /app/${JAR_FILE} app.jar

ENTRYPOINT ["java", "-jar", "app.jar"]