FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app
COPY pom.xml .
RUN mvn dependency:go-offline
COPY src ./src
RUN mvn clean package -DskipTests

FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
COPY --from=build /app/target/user-service-*.jar app.jar

COPY src/main/resources/hibernate.properties ./hibernate.properties

ENTRYPOINT ["java", "-jar", "app.jar"]