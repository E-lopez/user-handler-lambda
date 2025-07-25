package com.lopez.userhandler.dto;

import java.util.Objects;

import com.lopez.userhandler.service.AbstractService;

import io.quarkus.runtime.annotations.RegisterForReflection;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbAttribute;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbSecondaryPartitionKey;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbSecondarySortKey;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbSortKey;

@RegisterForReflection
@DynamoDbBean
public class User {
  private String id; // UUID partition key
  private String idNumber;
  private String userName;
  private String dateOfBirth;
  private String gender;
  private String occupation;
  private String email;
  // Risk level is a secondary sort key for the UserNameIndex
  private String riskLevel;

  public User() {
    // No need for initialization here
  }

  @DynamoDbPartitionKey
  @DynamoDbAttribute(AbstractService.USER_ID)
  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  @DynamoDbSecondaryPartitionKey(indexNames = "IdNumberIndex")
  @DynamoDbAttribute(AbstractService.USER_ID_NUMBER)
  public String getIdNumber() {
    return idNumber;
  }

  public void setIdNumber(String idNumber) {
    this.idNumber = idNumber;
  }

  @DynamoDbSecondaryPartitionKey(indexNames = "UserNameIndex")
  public String getUserName() {
    return userName;
  }

  public void setUserName(String userName) {
    this.userName = userName;
  }

  public String getDateOfBirth() {
    return dateOfBirth;
  }

  public void setDateOfBirth(String dateOfBirth) {
    this.dateOfBirth = dateOfBirth;
  }

  public String getGender() {
    return gender;
  }

  public void setGender(String gender) {
    this.gender = gender;
  }

  @DynamoDbSecondaryPartitionKey(indexNames = "EmailIndex")
  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public String getOccupation() {
    return occupation;
  }

  public void setOccupation(String occupation) {
    this.occupation = occupation;
  }

  @DynamoDbSecondaryPartitionKey(indexNames = "IdNumberRiskLevelIndex")
  public String getRiskLevel() {
    return riskLevel;
  }

  public void setRiskLevel(String riskLevel) {
    this.riskLevel = riskLevel;
  }

  @Override
  public boolean equals(Object o) {
    if (!(o instanceof User)) {
      return false;
    }

    User other = (User) o;

    return Objects.equals(other.idNumber, this.idNumber);
  }

  @Override
  public int hashCode() {
    return Objects.hash(this.idNumber);
  }

}
