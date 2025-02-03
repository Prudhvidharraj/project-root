FROM maven:3.8.6-openjdk-11-slim AS build
WORKDIR /app
COPY pom.xml .
RUN mvn dependency:go-offline -B
COPY src ./src
RUN mvn clean package -DskipTests
FROM openjdk:11-jre-slim@sha256:90d7a6c2b785fd0e6e7a2d6d0a5d4d9b7c0e5d5c9f4a1a1a1a1a1a1a1a1a1a1a1
WORKDIR /app
RUN groupadd -r spring && useradd -r -g spring spring \
    && chown -R spring:spring /app
USER spring

COPY --from=build --chown=spring:spring /app/target/prudhvi-boot-*-jar-with-dependencies.jar app.jar

HEALTHCHECK --interval=30s --timeout=3s \
  CMD curl -f http://localhost:8080/health || exit 1
ENV TZ=America/Los_Angeles
ENV JAVA_OPTS="-XX:+UseContainerSupport -XX:MaxRAMPercentage=75.0"
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]