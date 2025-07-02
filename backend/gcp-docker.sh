# #!/bin/sh
# set -e
# echo "*** Docker Build and Push to Google Artifact Registry ***"
# FULL_IMAGE_NAME="${AR_REGION}-docker.pkg.dev/${AR_PROJECT}/${AR_REPO_NAME}/backend:${IMAGE_TAG}"
# echo "Building Docker image: ${FULL_IMAGE_NAME}"
# docker build -t "${FULL_IMAGE_NAME}" .
# echo "Pushing Docker image: ${FULL_IMAGE_NAME}"
# docker push "${FULL_IMAGE_NAME}"
# echo "Docker operations completed successfully."
# #!/bin/sh
# set -e
# echo "*** Docker Build and Push to Google Artifact Registry ***"
# FULL_IMAGE_NAME="${AR_REGION}-docker.pkg.dev/${AR_PROJECT}/${AR_REPO_NAME}/backend:${IMAGE_TAG}"
# echo "Building Docker image: ${FULL_IMAGE_NAME}"

# # Change this line: Add the path to the Dockerfile
# docker build -t "${FULL_IMAGE_NAME}" -f backend/Dockerfile backend/

# echo "Pushing Docker image: ${FULL_IMAGE_NAME}"
# docker push "${FULL_IMAGE_NAME}"
# echo "Docker operations completed successfully."


# Use the confirmed OpenJDK 24 slim image as the base
FROM openjdk:24-jdk-slim-bullseye

# Install Maven on top of the OpenJDK image
# We'll use a specific Maven version (e.g., 3.9.6)
ARG MAVEN_VERSION=3.9.6
ARG USER_HOME_DIR="/root" # Maven will install here by default as root
ARG BASE_URL=https://apache.osuosl.org/maven/maven-3/${MAVEN_VERSION}/binaries
ENV MAVEN_HOME /usr/share/maven
ENV MAVEN_CONFIG "${USER_HOME_DIR}/.m2"
# Add Maven to the PATH - Corrected comment placement
ENV PATH="${MAVEN_HOME}/bin:${PATH}" 

RUN set -eux; \
    # Download and extract Maven
    curl -fsSL ${BASE_URL}/apache-maven-${MAVEN_VERSION}-bin.tar.gz | tar -xz -C /usr/share/; \
    # Rename the extracted folder
    mv /usr/share/apache-maven-${MAVEN_VERSION} /usr/share/maven; \
    # Clean up downloaded archive to reduce image size (optional, but good practice)
    rm -f /tmp/apache-maven-${MAVEN_VERSION}-bin.tar.gz

# Set the working directory for your application
WORKDIR /app

# Copy the Maven project file (pom.xml) first.
# This allows Docker to cache this layer if only the source code changes.
COPY pom.xml .

# Copy the source code.
COPY src ./src

# Build your Spring Boot application
# This command will now run in an environment with Java 24 and Maven 3.9.6
RUN mvn clean install -DskipTests

# Expose the port your Spring Boot application listens on
EXPOSE 8080

# Command to run your application when the container starts
CMD ["java", "-jar", "target/creative-space-finder-0.0.1-SNAPSHOT.jar"]
