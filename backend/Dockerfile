# Use the confirmed OpenJDK 24 slim image as the base
FROM openjdk:24-jdk-slim-bullseye

# Install necessary tools: curl for downloading, and clean up apt lists to reduce image size
RUN apt-get update && apt-get install -y curl && rm -rf /var/lib/apt/lists/*

# Install Maven on top of the OpenJDK image
# CHANGED: Using Maven 3.9.5 as 3.9.6 seems to have a temporary 404 issue on the CDN
ARG MAVEN_VERSION=3.9.5
ARG USER_HOME_DIR="/root"
ARG BASE_URL=https://dlcdn.apache.org/maven/maven-3/${MAVEN_VERSION}/binaries

# Use the recommended "key=value" format for ENV instructions (addresses warnings)
ENV MAVEN_HOME=/usr/share/maven
ENV MAVEN_CONFIG="${USER_HOME_DIR}/.m2"
# Add Maven to the PATH
ENV PATH="${MAVEN_HOME}/bin:${PATH}"

RUN set -eux; \
    curl -fsSL ${BASE_URL}/apache-maven-${MAVEN_VERSION}-bin.tar.gz | tar -xz -C /usr/share/; \
    mv /usr/share/apache-maven-${MAVEN_VERSION} /usr/share/maven; \
    rm -f /tmp/apache-maven-${MAVEN_VERSION}-bin.tar.gz

# Set the working directory for your application
WORKDIR /app

# Copy the Maven project file (pom.xml) first.
COPY pom.xml .

# Copy the source code.
COPY src ./src

# Build your Spring Boot application
RUN mvn clean install -DskipTests

# Expose the port your Spring Boot application listens on
EXPOSE 8080

# Command to run your application when the container starts
CMD ["java", "-jar", "target/creative-space-finder-0.0.1-SNAPSHOT.jar"]
