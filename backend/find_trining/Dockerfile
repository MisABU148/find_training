
# Этап сборки Java-приложения
FROM maven:3.9-eclipse-temurin-25 AS build

WORKDIR /app

COPY pom.xml .
RUN mvn -B dependency:go-offline

COPY src ./src
RUN mvn -B -DskipTests package \
    && cp target/find_trining-0.0.1-SNAPSHOT.jar /app/app.jar

# Этап запуска
FROM eclipse-temurin:25-jre

WORKDIR /app

COPY --from=build /app/app.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]