FROM maven:3.6.3-openjdk-11 AS MAVEN_BUILD 
COPY pom.xml /build/
COPY src /build/src/
WORKDIR /build/
RUN mvn -DskipTests=true package

FROM openjdk:11-jdk

WORKDIR /app
COPY --from=MAVEN_BUILD /build/target/bankanwendung-0.0.1-SNAPSHOT.jar /app/bankanwendung.jar
EXPOSE 8080

ENTRYPOINT ["java", "-jar", "bankanwendung.jar"]
