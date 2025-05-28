FROM maven AS builder

COPY src /usr/src/app/src
COPY pom.xml /usr/src/app

RUN mvn -f /usr/src/app/pom.xml clean package


FROM eclipse-temurin:21-jre-alpine
WORKDIR /tmp
COPY --from=builder /usr/src/app/target/*.jar app.jar
ENTRYPOINT ["java","-jar","app.jar"]