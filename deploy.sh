#!/bin/bash

set -e

# Configuration
STACK_NAME="user-handler-stack"
ENVIRONMENT="dev"
S3_BUCKET="user-handler-lambda-20250726-133012"
AWS_REGION="us-east-1"

echo "Building Lambda function..."

# Clean and build the native executable
mvn install -Dnative -DskipTests -Dquarkus.native.container-build=true

# Check if native executable exists
if [ ! -f "target/user-handler-1.0.0-SNAPSHOT-runner" ]; then
    echo "Error: Native executable not found"
    exit 1
fi

# Create function.zip with bootstrap
cp target/user-handler-1.0.0-SNAPSHOT-runner bootstrap
zip -j target/function.zip bootstrap
rm bootstrap

echo "Uploading to S3..."

# Upload to S3
aws s3 cp target/function.zip s3://${S3_BUCKET}/user-handler/function.zip

echo "Deploying CloudFormation stack..."

# Deploy CloudFormation stack
aws cloudformation deploy \
    --template-file cloudformation-template.yaml \
    --stack-name ${STACK_NAME} \
    --parameter-overrides \
        Environment=${ENVIRONMENT} \
        S3Bucket=${S3_BUCKET} \
        S3Key=user-handler/function.zip \
    --capabilities CAPABILITY_IAM \
    --region ${AWS_REGION}

echo "Getting stack outputs..."

echo "User Handler Lambda deployed successfully!"
echo "Function ARN: $(aws cloudformation describe-stacks --stack-name ${STACK_NAME} --query 'Stacks[0].Outputs[?OutputKey==`UserHandlerFunctionArn`].OutputValue' --output text --region ${AWS_REGION})"
echo "Use the shared API Gateway to access endpoints."