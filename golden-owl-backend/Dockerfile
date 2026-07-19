FROM eclipse-temurin:17-jdk-jammy AS build
WORKDIR /workspace
COPY . .
RUN chmod +x ./mvnw && ./mvnw -DskipTests package

FROM eclipse-temurin:17-jre-jammy
WORKDIR /app
COPY --from=build /workspace/target/*.jar app.jar
COPY sql ./sql
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
