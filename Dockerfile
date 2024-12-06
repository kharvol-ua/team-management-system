FROM maven:3.9.8-eclipse-temurin-17 AS build

WORKDIR /tms-app

COPY pom.xml ./
COPY src ./src

RUN mvn clean package -DskipTests

FROM eclipse-temurin:17-jre

ENV JAR_NAME=team-management-system.jar

WORKDIR /tms-app

COPY --from=build /tms-app/target/*.jar ./$JAR_NAME

CMD java -jar ./$JAR_NAME
