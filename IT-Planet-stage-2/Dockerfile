FROM openjdk:21-jdk

WORKDIR /app

COPY pom.xml .
COPY mvnw .
COPY .mvn .mvn

RUN ./mvnw dependency:go-offline

COPY src src

RUN ./mvnw package -DskipTests

CMD ["java", "-jar", "target/IT-Planet-stage-2-0.0.1-SNAPSHOT.jar"]
