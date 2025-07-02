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
#!/bin/sh
set -e
echo "*** Docker Build and Push to Google Artifact Registry ***"
FULL_IMAGE_NAME="${AR_REGION}-docker.pkg.dev/${AR_PROJECT}/${AR_REPO_NAME}/backend:${IMAGE_TAG}"
echo "Building Docker image: ${FULL_IMAGE_NAME}"

# This command uses your Dockerfile to build the image.
# The Dockerfile itself should be in backend/Dockerfile
docker build -t "${FULL_IMAGE_NAME}" -f backend/Dockerfile backend/

echo "Pushing Docker image: ${FULL_IMAGE_NAME}"
docker push "${FULL_IMAGE_NAME}"
echo "Docker operations completed successfully."
