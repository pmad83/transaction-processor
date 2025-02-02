FROM eclipse-temurin:21-jdk-alpine

WORKDIR /app

COPY build/libs/transaction-app-1.0.jar /app/app.jar

ENTRYPOINT ["java", "-jar", "app.jar"]
