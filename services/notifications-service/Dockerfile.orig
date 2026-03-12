FROM eclipse-temurin:21-jre
WORKDIR /app
COPY build/libs/*.jar app.jar
USER 65532:65532
EXPOSE 8081
ENTRYPOINT ["java","-jar","/app/app.jar"]
