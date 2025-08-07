# Manual Deployment Steps

Since there are AWS credential issues, here are the manual steps to deploy:

## 1. Build the Native Application
```bash
mvn clean package -Dnative -DskipTests -Dquarkus.native.container-build=true
```

## 2. Upload function.zip to Lambda
1. Go to AWS Lambda Console
2. Find the function `user-handler-dev`
3. Go to "Code" tab
4. Click "Upload from" → ".zip file"
5. Upload `target/function.zip`

## 3. Update Lambda Configuration
**Runtime**: `provided.al2`
**Handler**: `not.used.in.provided.runtime`

**Environment variables**:
- `USER_TABLE_NAME`: `Users-dev`
- `QUARKUS_DYNAMODB_AWS_REGION`: `us-east-1`
- `QUARKUS_DYNAMODB_AWS_CREDENTIALS_TYPE`: `default`
- `DISABLE_SIGNAL_HANDLERS`: `true`

## 4. Test Endpoints
- Health: `https://tz0r439zzd.execute-api.us-east-1.amazonaws.com/Prod/health`
- Test: `https://tz0r439zzd.execute-api.us-east-1.amazonaws.com/Prod/users/test`
- Users: `https://tz0r439zzd.execute-api.us-east-1.amazonaws.com/Prod/users`

## 5. Check CloudWatch Logs
If there are still errors, check CloudWatch logs for the Lambda function to see detailed error messages.