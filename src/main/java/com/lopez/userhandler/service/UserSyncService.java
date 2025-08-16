package com.lopez.userhandler.service;

import java.util.List;
import java.util.UUID;

import com.lopez.userhandler.dto.ApiResponse;
import com.lopez.userhandler.dto.UpdateUserRiskDto;
import com.lopez.userhandler.dto.User;
import com.lopez.userhandler.util.ValidationUtil;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbIndex;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryConditional;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryEnhancedRequest;
import software.amazon.awssdk.services.dynamodb.model.ConditionalCheckFailedException;
import software.amazon.awssdk.services.dynamodb.model.DynamoDbException;

@ApplicationScoped
public class UserSyncService extends AbstractService {

  private final DynamoDbTable<User> userTable;
  private static final String USER_NOT_FOUND = "User not found";

  @Inject
  public UserSyncService(DynamoDbEnhancedClient dynamoEnhancedClient) {
    logger.infof("Initializing UserSyncService with table: %s", USER_TABLE_NAME);
    this.userTable = dynamoEnhancedClient.table(USER_TABLE_NAME, TableSchema.fromClass(User.class));
    logger.infof("UserSyncService initialized successfully");
  }

  public ApiResponse<List<User>> getAll() {
    try {
      logger.infof("Scanning table: %s", USER_TABLE_NAME);
      List<User> users = userTable.scan().items().stream().toList();
      logger.infof("Retrieved %d users", users.size());
      return ApiResponse.ok(users);
    } catch (DynamoDbException e) {
      logger.errorf("DynamoDB error getting all users from table %s: %s", USER_TABLE_NAME, e.getMessage());
      return ApiResponse.internalServerError("Database error retrieving users");
    } catch (Exception e) {
      logger.errorf("Unexpected error getting all users from table %s: %s", USER_TABLE_NAME, e.getMessage());
      return ApiResponse.internalServerError("Failed to retrieve users");
    }
  }

  public ApiResponse<User> add(User user) {
    if (user == null) {
      return ApiResponse.badRequest("User cannot be null");
    }

    ApiResponse<String> validationResult = validateUser(user);
    if (validationResult != null) {
      return ApiResponse.badRequest(validationResult.getMessage());
    }

    try {
      if (existsByIdNumber(userTable, user.getIdNumber())) {
        return ApiResponse.conflict("User with ID Number already exists");
      }
      if (existsByEmail(userTable, user.getEmail())) {
        return ApiResponse.conflict("User with Email already exists");
      }

      User newUser = new User();
      newUser.setId(UUID.randomUUID().toString());
      newUser.setIdNumber(user.getIdNumber().trim());
      newUser.setUserName(user.getUserName().trim());
      newUser.setOccupation(user.getOccupation().trim());
      newUser.setEmail(user.getEmail().trim().toLowerCase());
      newUser.setDateOfBirth(user.getDateOfBirth().trim());
      newUser.setGender(user.getGender().trim());
      newUser.setRiskLevel(user.getRiskLevel().trim());

      userTable.putItem(newUser);
      logger.infof("User created successfully with ID: %s", newUser.getId());
      return ApiResponse.ok(newUser, "User created successfully");
    } catch (ConditionalCheckFailedException e) {
      logger.warnf("Conditional check failed when adding user: %s", e.getMessage());
      return ApiResponse.conflict("User already exists");
    } catch (DynamoDbException e) {
      logger.errorf("DynamoDB error adding user: %s", e.getMessage());
      return ApiResponse.internalServerError("Database error creating user");
    } catch (Exception e) {
      logger.errorf("Unexpected error adding user: %s", e.getMessage());
      return ApiResponse.internalServerError("Failed to create user");
    }
  }

  public ApiResponse<User> getUserById(String id) {
    if (!ValidationUtil.isValidString(id, 50) || !ValidationUtil.isValidUUID(id)) {
      return ApiResponse.badRequest("Invalid user ID format");
    }

    try {
      Key partitionKey = Key.builder().partitionValue(id).build();
      User user = userTable.getItem(partitionKey);
      if (user == null) {
        return ApiResponse.notFound(USER_NOT_FOUND);
      }
      return ApiResponse.ok(user);
    } catch (DynamoDbException e) {
      logger.errorf("DynamoDB error getting user by ID %s: %s", id, e.getMessage());
      return ApiResponse.internalServerError("Database error retrieving user");
    } catch (Exception e) {
      logger.errorf("Unexpected error getting user by ID %s: %s", id, e.getMessage());
      return ApiResponse.internalServerError("Failed to retrieve user");
    }
  }

  public ApiResponse<User> getUserByName(String userName) {
    if (!ValidationUtil.isValidString(userName, 100)) {
      return ApiResponse.badRequest("Invalid username format");
    }

    try {
      DynamoDbIndex<User> userNameIndexTable = userTable.index(USER_NAME_INDEX);
      QueryConditional condition = QueryConditional.keyEqualTo(Key.builder()
          .partitionValue(userName.trim())
          .build());
      QueryEnhancedRequest request = QueryEnhancedRequest.builder()
          .queryConditional(condition)
          .scanIndexForward(false)
          .limit(1)
          .build();
      List<User> results = userNameIndexTable.query(request).stream()
          .flatMap(page -> page.items().stream())
          .toList();
      if (results.isEmpty()) {
        return ApiResponse.notFound(USER_NOT_FOUND);
      }
      return ApiResponse.ok(results.get(0));
    } catch (DynamoDbException e) {
      logger.errorf("DynamoDB error getting user by name %s: %s", userName, e.getMessage());
      return ApiResponse.internalServerError("Database error retrieving user");
    } catch (Exception e) {
      logger.errorf("Unexpected error getting user by name %s: %s", userName, e.getMessage());
      return ApiResponse.internalServerError("Failed to retrieve user");
    }
  }

  public ApiResponse<User> updateRiskLevel(UpdateUserRiskDto updateUserRiskDto) {
    if (updateUserRiskDto == null) {
      return ApiResponse.badRequest("Update request cannot be null");
    }
    if (!ValidationUtil.isValidString(updateUserRiskDto.getUserId(), 50) ||
        !ValidationUtil.isValidUUID(updateUserRiskDto.getUserId())) {
      return ApiResponse.badRequest("Invalid user ID format");
    }
    if (!ValidationUtil.isValidRiskLevel(updateUserRiskDto.getRiskLevel())) {
      return ApiResponse.badRequest("Invalid risk level. Must be between 1.0 and 10.0");
    }

    try {
      Key partitionKey = Key.builder().partitionValue(updateUserRiskDto.getUserId()).build();
      User user = userTable.getItem(partitionKey);
      if (user == null) {
        return ApiResponse.notFound(USER_NOT_FOUND);
      }

      user.setRiskLevel(updateUserRiskDto.getRiskLevel().trim());
      userTable.putItem(user);
      logger.infof("Risk level updated for user ID: %s", updateUserRiskDto.getUserId());
      return ApiResponse.ok(user, "Risk level updated successfully");
    } catch (DynamoDbException e) {
      logger.errorf("DynamoDB error updating risk level for user %s: %s",
          updateUserRiskDto.getUserId(), e.getMessage());
      return ApiResponse.internalServerError("Database error updating risk level");
    } catch (Exception e) {
      logger.errorf("Unexpected error updating risk level for user %s: %s",
          updateUserRiskDto.getUserId(), e.getMessage());
      return ApiResponse.internalServerError("Failed to update risk level");
    }
  }

  public ApiResponse<Boolean> checkIdNumberExists(String idNumber) {
    if (!ValidationUtil.isValidIdNumber(idNumber)) {
      return ApiResponse.badRequest("Invalid ID number format. Must be 5-12 digits");
    }

    try {
      boolean exists = existsByIdNumber(userTable, idNumber.trim());
      return ApiResponse.ok(exists);
    } catch (DynamoDbException e) {
      logger.errorf("DynamoDB error checking ID number %s: %s", idNumber, e.getMessage());
      return ApiResponse.internalServerError("Database error checking ID number");
    } catch (Exception e) {
      logger.errorf("Unexpected error checking ID number %s: %s", idNumber, e.getMessage());
      return ApiResponse.internalServerError("Failed to check ID number");
    }
  }

  public ApiResponse<Boolean> checkEmailExists(String email) {
    if (!ValidationUtil.isValidEmail(email)) {
      return ApiResponse.badRequest("Invalid email format");
    }

    try {
      boolean exists = existsByEmail(userTable, email.trim());
      return ApiResponse.ok(exists);
    } catch (DynamoDbException e) {
      logger.errorf("DynamoDB error checking email %s: %s", email, e.getMessage());
      return ApiResponse.internalServerError("Database error checking email");
    } catch (Exception e) {
      logger.errorf("Unexpected error checking email %s: %s", email, e.getMessage());
      return ApiResponse.internalServerError("Failed to check email");
    }
  }

  private ApiResponse<String> validateUser(User user) {
    if (!ValidationUtil.isValidString(user.getIdNumber(), 12) ||
        !ValidationUtil.isValidIdNumber(user.getIdNumber())) {
      return ApiResponse.badRequest("Invalid ID number format");
    }
    if (!ValidationUtil.isValidString(user.getUserName(), 100)) {
      return ApiResponse.badRequest("Invalid username");
    }
    if (!ValidationUtil.isValidString(user.getEmail(), 255) ||
        !ValidationUtil.isValidEmail(user.getEmail())) {
      return ApiResponse.badRequest("Invalid email format");
    }
    if (!ValidationUtil.isValidString(user.getOccupation(), 100)) {
      return ApiResponse.badRequest("Invalid occupation");
    }
    if (!ValidationUtil.isValidString(user.getDateOfBirth(), 10)) {
      return ApiResponse.badRequest("Invalid date of birth");
    }
    if (!ValidationUtil.isValidString(user.getGender(), 10)) {
      return ApiResponse.badRequest("Invalid gender");
    }
    if (!ValidationUtil.isValidRiskLevel(user.getRiskLevel())) {
      return ApiResponse.badRequest("Invalid risk level. Must be between 1.0 and 10.0");
    }
    return null;
  }

}
