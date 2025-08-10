# ---- Build stage (JDK 11) ----
FROM maven:3.9.6-eclipse-temurin-11 AS builder

WORKDIR /app

# Leverage cache for deps
COPY pom.xml ./
RUN mvn -B -q dependency:go-offline

# Build
COPY src ./src
RUN mvn -B clean package -DskipTests

# ---- Runtime stage (JRE 11) ----
FROM eclipse-temurin:11-jre-alpine

WORKDIR /app

# Copy the fat jar
COPY --from=builder /app/target/*.jar /app/app.jar

# App port
EXPOSE 8080

# Run as non-root (Alpine syntax)
RUN addgroup -S app && adduser -S -G app app
USER app

ENTRYPOINT ["java","-jar","/app/app.jar"]
