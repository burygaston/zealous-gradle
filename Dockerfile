# Multi-stage Dockerfile for Task Manager application

# ------------------------------------------------------------------------------
# Stage 1: Build the application
# ------------------------------------------------------------------------------
FROM gradle:8.5-jdk17 AS build

WORKDIR /app

# 1. Copy root Gradle setup files
COPY build.gradle settings.gradle gradlew ./
COPY gradle ./gradle

# 2. Copy the MODULE specific build files
#    (Required because your project is split into 'app' and 'common')
COPY app/build.gradle ./app/
COPY common/build.gradle ./common/

# 3. Download dependencies (This layer is cached if build.gradle files don't change)
RUN ./gradlew dependencies --no-daemon || true

# 4. Copy source code for both modules
COPY app/src ./app/src
COPY common/src ./common/src

# 5. Build the application
#    We skip tests (-x test) here to speed up the build,
#    assuming tests were run in the previous CI pipeline step.
RUN ./gradlew :app:build -x test --no-daemon

# ------------------------------------------------------------------------------
# Stage 2: Runtime image
# ------------------------------------------------------------------------------
FROM eclipse-temurin:17-jre-alpine

WORKDIR /app

# Create a non-root user for security
RUN addgroup -S spring && adduser -S spring -G spring

# 6. COPY FIX:
#    The JAR is generated inside the 'app' module's build directory.
#    Path: /app (container root) /app (module name) /build/libs
COPY --from=build /app/app/build/libs/*.jar app.jar

# Change ownership
RUN chown -R spring:spring /app

# Switch to non-root user
USER spring:spring

# Expose port
EXPOSE 8080

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]

# Default profile
CMD ["--spring.profiles.active=h2"]