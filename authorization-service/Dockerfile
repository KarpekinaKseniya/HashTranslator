FROM adoptopenjdk/openjdk11
WORKDIR /app
EXPOSE 8080
CMD ["java", "-jar", "/app/authorization.jar"]
COPY /build/libs/authorization-*.jar authorization.jar