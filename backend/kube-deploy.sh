#!/bin/sh
set -e
echo "*** Setting up Kubernetes access based on service account token ***"

# Accept the key file path as the first argument
GCP_KEYFILE_PATH="$1"

# Add debug inside the script to see what $GCP_KEYFILE_PATH is
echo "DEBUG (in kube-deploy.sh): GCP_KEYFILE_PATH is: ${GCP_KEYFILE_PATH}"
test -f "${GCP_KEYFILE_PATH}" && echo "DEBUG (in kube-deploy.sh): Key file exists and is a regular file." || echo "DEBUG (in kube-deploy.sh): Key file does NOT exist or is not a regular file."
test -r "${GCP_KEYFILE_PATH}" && echo "DEBUG (in kube-deploy.sh): Key file is readable." || echo "DEBUG (in kube-deploy.sh): Key file is NOT readable."

# CORRECTED LINE: Removed "${GCP_ACCOUNTNAME}" from the gcloud auth command
gcloud auth activate-service-account --key-file="${GCP_KEYFILE_PATH}" --project="${PROJECT_ID}"
gcloud container clusters get-credentials "${CLUSTER_NAME}" --zone="${LOCATION}"

echo "*** Creating deployment YAML files using envsubst ***"
mkdir -p "./output"
envsubst < "k8s/backend_deployment.tmpl" > "./output/backend-deployment.yaml"
env IMAGE_URL="${IMAGE_URL}" \
envsubst < "k8s/backend_service.tmpl" > "./output/backend-service.yaml"
env IMAGE_URL="${IMAGE_URL}" \
# env IMAGE_URL="${IMAGE_URL}" PROJECT_ID="${PROJECT_ID}" CLUSTER_NAME="${CLUSTER_NAME}" LOCATION="${LOCATION}" \
# envsubst < "../k8s/Backend/backend-deployment.tmpl" > "./output/backend-deployment.yaml"
# env IMAGE_URL="${IMAGE_URL}" \
# envsubst < "../k8s/Backend/backend-service.tmpl" > "./output/backend-service.yaml"
# env IMAGE_URL="${IMAGE_URL}" \
# envsubst < "../k8s/Backend/ingress.tmpl" > "./output/ingress.yaml"

echo "*** Deploying Docker container and setting up the service and ingress ***"
kubectl apply -f "./output/backend_deployment.yaml" --validate=false --insecure-skip-tls-verify=true
kubectl apply -f "./output/backend_service.yaml" --validate=false --insecure-skip-tls-verify=true
# kubectl apply -f "./output/ingress.yaml" --validate=false --insecure-skip-tls-verify=true

echo "Deployment commands executed successfully."
