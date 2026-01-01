FROM gradle:8-jdk21 AS builder
WORKDIR /app
COPY . .
RUN ./gradlew :core:core-api:bootJar -x test --no-daemon

FROM eclipse-temurin:21-jdk-jammy
WORKDIR /app
COPY --from=builder /app/core/core-api/build/libs/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
