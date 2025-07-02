# Use a stable Maven image with OpenJDK 17
# This image contains Maven and Java, suitable for building your Spring Boot application.
FROM maven:3-openjdk-17

# Set the working directory inside the container
# All subsequent commands will be executed relative to this directory.
WORKDIR /app

# Copy the Maven project file (pom.xml) first.
# This allows Docker to cache this layer if only the source code changes,
# speeding up subsequent builds.
COPY pom.xml .

# Copy the source code.
# The 'backend/' context in 'docker build' command ensures this path is correct.
COPY src ./src

# Build your Spring Boot application.
# 'mvn clean install -DskipTests' compiles the code, runs tests (which are skipped here),
# and packages the application into a JAR file in the 'target' directory.
RUN mvn clean install -DskipTests

# Expose the port your Spring Boot application listens on.
# Based on your backend_deployment.yaml, your application listens on port 8080.
EXPOSE 8080

# Define the command to run your application when the container starts.
# It executes the JAR file produced by the Maven build.
# The JAR name is derived from your pom.xml: <artifactId>-<version>.jar
CMD ["java", "-jar", "target/creative-space-finder-0.0.1-SNAPSHOT.jar"]
