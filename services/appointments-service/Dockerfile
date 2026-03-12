# ---- Build stage (JDK 17) ----
FROM eclipse-temurin:17-jdk AS build
WORKDIR /app

# Copy only build files first for better caching
COPY gradlew /app/gradlew
COPY gradle /app/gradle
COPY build.gradle /app/build.gradle
COPY settings.gradle /app/settings.gradle

# Make gradlew executable
RUN chmod +x /app/gradlew

# Warm up dependencies (optional but helps caching)
RUN ./gradlew --no-daemon dependencies || true

# Copy source
COPY src /app/src

# Build jar
RUN ./gradlew --no-daemon clean bootJar

# ---- Runtime stage (JRE 17) ----
FROM eclipse-temurin:17-jre
WORKDIR /app

# Copy built jar
COPY --from=build /app/build/libs/*.jar /app/app.jar

EXPOSE 8080
ENTRYPOINT ["java","-jar","/app/app.jar"]
