package com.lopez.userhandler.service;

import com.lopez.userhandler.dto.User;

import jakarta.enterprise.context.RequestScoped;

@RequestScoped
public class UserService {

  public String getUsers() {
    return "Users list";
  }

  public String getUserById(String userId) {
    return "User details for ID: " + userId;
  }

  public String getUserByName(String userName) {
    return "User details for Name: " + userName;
  }

  public User createUser(User user) {
    return user;
  }

  public String updateUserRisk(String userId, String riskLevel) {
    return "User ID: " + userId + " updated with risk level: " + riskLevel;
  }

  public String clearUsers() {
    return "All users cleared";
  }

}
