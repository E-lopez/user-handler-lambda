#!/bin/bash

set -e

# Configuration
BUCKET_NAME="user-handler-lambda-$(date +%Y%m%d-%H%M%S)"
AWS_REGION="us-east-1"

echo "Creating S3 bucket for Lambda deployment..."

# Create S3 bucket (handle region-specific creation)
if [ "${AWS_REGION}" = "us-east-1" ]; then
    aws s3 mb s3://${BUCKET_NAME}
else
    aws s3 mb s3://${BUCKET_NAME} --region ${AWS_REGION}
fi

# Enable versioning
aws s3api put-bucket-versioning \
    --bucket ${BUCKET_NAME} \
    --versioning-configuration Status=Enabled

echo "S3 bucket created: ${BUCKET_NAME}"
echo ""
echo "Update your deploy.sh script with:"
echo "S3_BUCKET=\"${BUCKET_NAME}\""