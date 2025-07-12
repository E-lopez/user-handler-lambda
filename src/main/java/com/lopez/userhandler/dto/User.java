package com.lopez.userhandler.dto;

public class User {
  public User() {
  }

  public User(String id, String idNumber, String userName, String dateOfBirth, String gender, String occupation,
      String riskLevel) {
    this.id = id;
    this.idNumber = idNumber;
    this.userName = userName;
    this.dateOfBirth = dateOfBirth;
    this.gender = gender;
    this.occupation = occupation;
    this.riskLevel = riskLevel;
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getIdNumber() {
    return idNumber;
  }

  public void setIdNumber(String idNumber) {
    this.idNumber = idNumber;
  }

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

  public String getOccupation() {
    return occupation;
  }

  public void setOccupation(String occupation) {
    this.occupation = occupation;
  }

  public String getRiskLevel() {
    return riskLevel;
  }

  public void setRiskLevel(String riskLevel) {
    this.riskLevel = riskLevel;
  }

  private String id;
  private String idNumber;
  private String userName;
  private String dateOfBirth;
  private String gender;
  private String occupation;
  private String riskLevel;
}
