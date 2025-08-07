package com.lopez.userhandler.service;

import org.jboss.logging.Logger;

import com.lopez.userhandler.dto.User;

import software.amazon.awssdk.enhanced.dynamodb.DynamoDbIndex;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryConditional;
import software.amazon.awssdk.services.dynamodb.model.ScanRequest;

public class AbstractService {

  public static final String USER_ID = "id";
  public static final String USER_ID_NUMBER = "idNumber";
  public static final String USER_NAME = "userName";
  public static final String USER_DATE_OF_BIRTH = "dateOfBirth";
  public static final String USER_GENDER = "gender";
  public static final String USER_OCCUPATION = "occupation";
  public static final String USER_RISK_LEVEL = "riskLevel";
  public static final String USER_EMAIL = "email";
  protected static final String USER_TABLE_NAME = "Users-dev";
  public static final String ID_NUMBER_INDEX = "IdNumberIndex";
  public static final String EMAIL_INDEX = "EmailIndex";
  public static final String USER_NAME_INDEX = "UserNameIndex";

  Logger logger = Logger.getLogger(AbstractService.class);

  protected ScanRequest scanRequest() {
    return ScanRequest.builder().tableName(USER_TABLE_NAME)
        .attributesToGet(USER_ID, USER_NAME).build();
  }

  protected boolean existsByIdNumber(DynamoDbTable<User> userTable, String idNumber) {
    try {
      DynamoDbIndex<User> idNumberIndex = userTable.index(ID_NUMBER_INDEX);
      QueryConditional condition = QueryConditional.keyEqualTo(Key.builder()
          .partitionValue(idNumber)
          .build());

      return idNumberIndex.query(condition).stream()
          .flatMap(page -> page.items().stream())
          .findFirst()
          .isPresent();
    } catch (Exception e) {
      logger.errorf("Error checking if idNumber exists");
      return false;
    }
  }

  protected boolean existsByEmail(DynamoDbTable<User> userTable, String email) {
    try {
      DynamoDbIndex<User> emailIndex = userTable.index(EMAIL_INDEX);
      QueryConditional condition = QueryConditional.keyEqualTo(Key.builder()
          .partitionValue(email)
          .build());

      return emailIndex.query(condition).stream()
          .flatMap(page -> page.items().stream())
          .findFirst()
          .isPresent();
    } catch (Exception e) {
      logger.errorf("Error checking if email exists");
      return false;
    }
  }
}
