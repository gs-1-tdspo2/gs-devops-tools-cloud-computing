FROM maven:3.9.9-eclipse-temurin-17 AS build

WORKDIR /app

COPY .mvn/ .mvn/
COPY mvnw pom.xml ./
COPY src/ src/

RUN chmod +x mvnw && ./mvnw clean package -DskipTests


FROM eclipse-temurin:17-jre

WORKDIR /app

RUN groupadd --system amanaje \
    && useradd --system \
        --gid amanaje \
        --home-dir /app \
        --shell /usr/sbin/nologin \
        amanaje

COPY --from=build --chown=amanaje:amanaje /app/target/amanaje-api-0.0.1-SNAPSHOT.jar app.jar

USER amanaje

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]