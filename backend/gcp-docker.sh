set -e
echo "*** Docker Build and Push to Google Artifact Registry ***"

# Define the base image name without any tag
BASE_IMAGE_NAME="${AR_REGION}-docker.pkg.dev/${AR_PROJECT}/${AR_REPO_NAME}/backend"

# Define the full image name with the unique build tag (e.g., main-29)
UNIQUE_TAGGED_IMAGE="${BASE_IMAGE_NAME}:${IMAGE_TAG}"

echo "Building Docker image: ${UNIQUE_TAGGED_IMAGE}"
# This command uses your Dockerfile to build the image.
# The Dockerfile itself should be in backend/Dockerfile
docker build -t "${UNIQUE_TAGGED_IMAGE}" -f backend/Dockerfile backend/

echo "Pushing Docker image with unique tag: ${UNIQUE_TAGGED_IMAGE}"
docker push "${UNIQUE_TAGGED_IMAGE}"

# Define the full image name with the 'latest' tag
LATEST_TAGGED_IMAGE="${BASE_IMAGE_NAME}:latest"

echo "Tagging image with :latest: ${LATEST_TAGGED_IMAGE}"
# Tag the uniquely built image with the 'latest' tag
docker tag "${UNIQUE_TAGGED_IMAGE}" "${LATEST_TAGGED_IMAGE}"

echo "Pushing Docker image with latest tag: ${LATEST_TAGGED_IMAGE}"
# Push the 'latest' tagged image to Artifact Registry
docker push "${LATEST_TAGGED_IMAGE}"

echo "Docker operations completed successfully."
