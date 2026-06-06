ARG MAVEN_IMAGE=dockerproxy.net/library/maven:3.8.6-openjdk-8-slim
ARG JRE_IMAGE=dockerproxy.net/library/eclipse-temurin:8-jre-jammy

FROM ${MAVEN_IMAGE} AS builder
WORKDIR /app
COPY pom.xml .
RUN mvn dependency:go-offline -B
COPY src ./src
RUN mvn package -DskipTests

FROM ${JRE_IMAGE}
WORKDIR /app
COPY --from=builder /app/target/haircut-care-server-1.0.0.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
