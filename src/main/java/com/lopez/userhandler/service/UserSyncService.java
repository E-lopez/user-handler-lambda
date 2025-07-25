package com.lopez.userhandler.service;

import java.util.List;

import org.jboss.logging.Logger;

import com.lopez.userhandler.dto.ApiResponse;
import com.lopez.userhandler.dto.UpdateUserRiskDto;
import com.lopez.userhandler.dto.User;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbIndex;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryConditional;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryEnhancedRequest;
import software.amazon.awssdk.services.dynamodb.model.ResourceNotFoundException;

@ApplicationScoped
public class UserSyncService extends AbstractService {

  private DynamoDbTable<User> userTable;

  @Inject
  public UserSyncService(DynamoDbEnhancedClient dynamoEnhancedClient) {
    userTable = dynamoEnhancedClient.table(USER_TABLE_NAME,
        TableSchema.fromClass(User.class));
    try {
      userTable.describeTable();
      logger.infof("Describe able: %s", userTable.describeTable());
    } catch (ResourceNotFoundException e) {
      try {
        userTable.createTable();
        logger.infof("Created DynamoDB table: %s", USER_TABLE_NAME);
      } catch (Exception ce) {
        logger.errorf("Error creating DynamoDB table: %s", ce.getMessage());
      }
    } catch (Exception e) {
      try {
        userTable.deleteTable();
        logger.infof("Dropped DynamoDB table: %s", USER_TABLE_NAME);
      } catch (ResourceNotFoundException e1) {
        try {
          userTable.createTable();
          logger.infof("Created DynamoDB table: %s", USER_TABLE_NAME);
        } catch (Exception ce) {
          logger.errorf("Error creating DynamoDB table: %s", ce.getMessage());
        }
      }
    }
  }

  public ApiResponse<List<User>> getAll() {
    try {
      List<User> users = userTable.scan().items().stream().toList();
      return ApiResponse.ok(users);
    } catch (Exception e) {
      logger.errorf("Error getting all users");
      return ApiResponse.internalServerError("Failed to retrieve users");
    }
  }

  public ApiResponse<User> add(User user) {
    try {
      if (existsByIdNumber(userTable, user.getIdNumber())) {
        return ApiResponse.conflict("User with ID Number already exists");
      }
      if (existsByEmail(userTable, user.getEmail())) {
        return ApiResponse.conflict("User with Email already exists");
      }
      User newUser = new User();
      newUser.setIdNumber(user.getIdNumber());
      newUser.setUserName(user.getUserName());
      newUser.setOccupation(user.getOccupation());
      newUser.setEmail(user.getEmail());
      newUser.setDateOfBirth(user.getDateOfBirth());
      newUser.setGender(user.getGender());
      newUser.setRiskLevel(user.getRiskLevel());
      newUser.setId(java.util.UUID.randomUUID().toString());
      userTable.putItem(newUser);
      return ApiResponse.ok(newUser, "User created successfully");
    } catch (Exception e) {
      logger.errorf("Error adding user");
      return ApiResponse.internalServerError("Failed to create user");
    }
  }

  public ApiResponse<User> getUserById(String id) {
    try {
      Key partitionKey = Key.builder().partitionValue(id).build();
      User user = userTable.getItem(partitionKey);
      if (user == null) {
        return ApiResponse.notFound("User not found");
      }
      return ApiResponse.ok(user);
    } catch (Exception e) {
      logger.errorf("Error getting user by ID");
      return ApiResponse.internalServerError("Failed to retrieve user");
    }
  }

  public ApiResponse<User> getUserByName(String userName) {
    try {
      DynamoDbIndex<User> userNameIndexTable = userTable.index(USER_NAME_INDEX);
      QueryConditional condition = QueryConditional.keyEqualTo(Key.builder()
          .partitionValue(userName)
          .build());
      QueryEnhancedRequest request = QueryEnhancedRequest.builder()
          .queryConditional(condition)
          .scanIndexForward(false)
          .build();
      List<User> results = userNameIndexTable.query(request).stream().flatMap(page -> page.items().stream()).toList();
      if (results.isEmpty()) {
        return ApiResponse.notFound("User not found");
      }
      return ApiResponse.ok(results.get(0));
    } catch (Exception e) {
      logger.errorf("Error getting user by NAME");
      return ApiResponse.internalServerError("Failed to retrieve user");
    }
  }

  public ApiResponse<User> updateRiskLevel(UpdateUserRiskDto updateUserRiskDto) {
    try {
      Key partitionKey = Key.builder().partitionValue(updateUserRiskDto.getUserId()).build();
      User user = userTable.getItem(partitionKey);
      if (user == null) {
        return ApiResponse.notFound("User not found");
      }
      user.setRiskLevel(updateUserRiskDto.getRiskLevel());
      userTable.putItem(user);
      return ApiResponse.ok(user, "Risk level updated successfully");
    } catch (Exception e) {
      logger.errorf("Error updating risk level");
      return ApiResponse.internalServerError("Failed to update risk level");
    }
  }

}
