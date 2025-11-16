FROM eclipse-temurin:21-jre-jammy

WORKDIR /app

# Copy the backend jar built by Maven
COPY target/maavooripachadi-backend-*.jar app.jar

EXPOSE 8081

ENTRYPOINT ["java","-jar","/app/app.jar"]
