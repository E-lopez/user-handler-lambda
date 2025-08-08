#!/bin/bash

# Set environment (default to dev)
ENVIRONMENT=${1:-dev}
DEPLOYMENT_BUCKET=${2:-user-handler-deployment-bucket}

echo "Deploying user-handler to environment: $ENVIRONMENT"

# Build the application
echo "Building the application..."
mvn clean package -Dnative -Dquarkus.native.container-build=true -Dquarkus.profile=$ENVIRONMENT

# Check if build was successful
if [ $? -ne 0 ]; then
    echo "Build failed!"
    exit 1
fi

# Create deployment bucket if it doesn't exist
echo "Creating deployment bucket if needed..."
if ! aws s3 ls s3://$DEPLOYMENT_BUCKET 2>/dev/null; then
    echo "Creating bucket: $DEPLOYMENT_BUCKET"
    aws s3 mb s3://$DEPLOYMENT_BUCKET
    if [ $? -ne 0 ]; then
        echo "Failed to create deployment bucket. Please check your AWS permissions."
        exit 1
    fi
else
    echo "Bucket $DEPLOYMENT_BUCKET already exists"
fi

# Upload function package to S3
echo "Uploading function package..."
if [ ! -f target/function.zip ]; then
    echo "Error: target/function.zip not found. Build may have failed."
    exit 1
fi

aws s3 cp target/function.zip s3://$DEPLOYMENT_BUCKET/user-handler/function.zip
if [ $? -ne 0 ]; then
    echo "Failed to upload function package. Please check your AWS permissions for bucket: $DEPLOYMENT_BUCKET"
    echo "Required permissions: s3:PutObject, s3:PutObjectAcl"
    exit 1
fi

# Deploy using CloudFormation
echo "Deploying to AWS..."
aws cloudformation deploy \
    --template-file cloudformation-template.yaml \
    --stack-name user-handler-stack-$ENVIRONMENT \
    --capabilities CAPABILITY_IAM \
    --parameter-overrides \
        Environment=$ENVIRONMENT \
        S3Bucket=$DEPLOYMENT_BUCKET \
        S3Key=user-handler/function.zip

# Get API Gateway URL from CloudFormation outputs
API_URL=$(aws cloudformation describe-stacks \
    --stack-name user-handler-stack-$ENVIRONMENT \
    --query 'Stacks[0].Outputs[?OutputKey==`ApiUrl`].OutputValue' \
    --output text)

echo "Deployment complete!"
echo "Environment: $ENVIRONMENT"
echo "Deployment Bucket: $DEPLOYMENT_BUCKET"
echo ""
echo "API Endpoints:"
echo "  Health Check:     GET  ${API_URL}health"
echo "  Get All Users:    GET  ${API_URL}users"
echo "  Get User by ID:   GET  ${API_URL}users/id/{id}"
echo "  Get User by Name: GET  ${API_URL}users/name/{name}"
echo "  Create User:      POST ${API_URL}users"
echo "  Update Risk:      POST ${API_URL}users/risk"