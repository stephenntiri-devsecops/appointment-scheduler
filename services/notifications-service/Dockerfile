# ---- Build stage ----
FROM gradle:8.7-jdk21 AS build
WORKDIR /app

COPY build.gradle settings.gradle gradle.properties* /app/
COPY gradle /app/gradle
COPY gradlew /app/gradlew

RUN ./gradlew --no-daemon dependencies || true

COPY src /app/src
RUN ./gradlew --no-daemon clean bootJar

# ---- Runtime stage ----
FROM eclipse-temurin:21-jre
WORKDIR /app

RUN useradd -u 10001 appuser
USER 10001

COPY --from=build /app/build/libs/*.jar /app/app.jar

EXPOSE 8081
ENV JAVA_OPTS="-XX:MaxRAMPercentage=75.0"
ENTRYPOINT ["sh","-c","java $JAVA_OPTS -jar /app/app.jar --server.port=8081"]
