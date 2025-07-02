#!/bin/sh
set -e
echo "*** Setting up Kubernetes access based on service account token ***"
gcloud auth activate-service-account "${GCP_ACCOUNTNAME}" --key-file="${GCP_KEYFILE_PATH}" --project="${PROJECT_ID}"
gcloud container clusters get-credentials "${CLUSTER_NAME}" --zone="${LOCATION}"
echo "*** Creating deployment YAML files using envsubst ***"
mkdir -p "./output"
env IMAGE_URL="${IMAGE_URL}" PROJECT_ID="${PROJECT_ID}" CLUSTER_NAME="${CLUSTER_NAME}" LOCATION="${LOCATION}" \
envsubst < "../k8s/Backend/backend-deployment.tmpl" > "./output/backend-deployment.yaml"
env IMAGE_URL="${IMAGE_URL}" \
envsubst < "../k8s/Backend/backend-service.tmpl" > "./output/backend-service.yaml"
env IMAGE_URL="${IMAGE_URL}" \
envsubst < "../k8s/Backend/ingress.tmpl" > "./output/ingress.yaml"
echo "*** Deploying Docker container and setting up the service and ingress ***"
kubectl apply -f "./output/backend-deployment.yaml" --validate=false --insecure-skip-tls-verify=true
kubectl apply -f "./output/backend-service.yaml" --validate=false --insecure-skip-tls-verify=true
kubectl apply -f "./output/ingress.yaml" --validate=false --insecure-skip-tls-verify=true
echo "Deployment commands executed successfully."