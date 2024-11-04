# Pre-fetch dependencies
FROM maven:3.9.4-eclipse-temurin-21 AS dependencies

# Copy the pom.xml files
WORKDIR /opt/app

COPY application/pom.xml application/pom.xml
COPY cli-input-adapter/pom.xml cli-input-adapter/pom.xml
COPY common/pom.xml common/pom.xml
COPY domain/pom.xml domain/pom.xml
COPY maria-output-adapter/pom.xml maria-output-adapter/pom.xml
COPY mongo-output-adapter/pom.xml mongo-output-adapter/pom.xml
COPY rest-input-adapter/pom.xml rest-input-adapter/pom.xml
COPY pom.xml .

RUN mvn -B -e org.apache.maven.plugins:maven-dependency-plugin:3.1.2:go-offline -DexcludeArtifactIds=domain

# Build the jar
FROM maven:3.9.4-eclipse-temurin-21 AS build

WORKDIR /opt/app

COPY --from=dependencies /root/.m2 /root/.m2
COPY --from=dependencies /opt/app/ /opt/app/

COPY application/src /opt/app/application/src
COPY cli-input-adapter/src /opt/app/app/cli-input-adapter/src
COPY common/src /opt/app/common/src
COPY domain/src /opt/app/domain/src
COPY maria-output-adapter/src /opt/app/maria-output-adapter/src
COPY mongo-output-adapter/src /opt/app/mongo-output-adapter/src
COPY rest-input-adapter/src /opt/app/rest-input-adapter/src

RUN mvn -B -e clean install -DskipTests

# Prepare runtime env
FROM eclipse-temurin:21-jdk-jammy

WORKDIR /opt/app

# COPY --from=build /opt/app/rest-input-adapter/target/*.jar /rest/app.jar
COPY --from=build /opt/app/cli-input-adapter/target/*.jar /cli/app.jar
COPY --from=build /opt/app/rest-input-adapter/target/*.jar /rest/app.jar

ENTRYPOINT ["sh", "-c", "java -jar ${APP_TYPE:-/cli/app.jar}"]
