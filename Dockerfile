# syntax=docker/dockerfile:1

FROM C:\Program Files\Java\jdk-11.0.16.1

WORKDIR /news

COPY .mvn/ .mvn
COPY mvnw pom.xml ./
RUN ./mvnw dependency:resolve

COPY src ./src

CMD ["./mvnw", "spring-boot:run"]