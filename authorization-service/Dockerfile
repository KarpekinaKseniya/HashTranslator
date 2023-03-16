FROM adoptopenjdk/openjdk11 AS builder
WORKDIR /app
COPY gradle/ gradle/
COPY gradlew build.gradle settings.gradle ./
RUN ./gradlew --no-daemon dependencies
COPY src/ src/
RUN ./gradlew --no-daemon build

FROM adoptopenjdk/openjdk11
WORKDIR /app
EXPOSE 8080
CMD ["java", "-jar", "/app/authorization.jar"]
COPY --from=builder /app/build/libs/authorization-*.jar authorization.jar