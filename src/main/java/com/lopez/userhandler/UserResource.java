package com.lopez.userhandler;

import java.util.List;

import org.jboss.logging.Logger;

import com.lopez.userhandler.dto.ApiResponse;
import com.lopez.userhandler.dto.UpdateUserRiskDto;
import com.lopez.userhandler.dto.User;
import com.lopez.userhandler.service.UserSyncService;
import com.lopez.userhandler.util.ValidationUtil;

import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/users")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class UserResource {

    Logger logger = Logger.getLogger(UserResource.class);

    @Inject
    UserSyncService userService;

    @GET
    public ApiResponse<List<User>> getAll() {
        return userService.getAll();
    }

    @GET
    @Path("/id/{id}")
    public ApiResponse<User> getSingle(@PathParam("id") String id) {
        if (!ValidationUtil.isValidUUID(id)) {
            return ApiResponse.badRequest("Invalid user ID format");
        }
        logger.infof("Fetching user by ID");
        return userService.getUserById(id);
    }

    @GET
    @Path("/name/{name}")
    public ApiResponse<User> getSingleByName(@PathParam("name") String name) {
        if (!ValidationUtil.isValidString(name, 50)) {
            return ApiResponse.badRequest("Invalid username format");
        }
        logger.infof("Fetching user by name");
        return userService.getUserByName(name);
    }

    @POST
    public ApiResponse<User> add(User user) {
        if (user == null) {
            return ApiResponse.badRequest("User data is required");
        }
        if (!ValidationUtil.isValidIdNumber(user.getIdNumber())) {
            return ApiResponse.badRequest("Invalid ID number format");
        }
        if (!ValidationUtil.isValidEmail(user.getEmail())) {
            return ApiResponse.badRequest("Invalid email format");
        }
        if (!ValidationUtil.isValidString(user.getUserName(), 50)) {
            return ApiResponse.badRequest("Invalid username");
        }
        if (user.getRiskLevel() != null && !ValidationUtil.isValidRiskLevel(user.getRiskLevel())) {
            return ApiResponse.badRequest("Risk level must be between 1-10");
        }
        logger.infof("Creating new user");
        return userService.add(user);
    }

    @POST
    @Path("/risk")
    public ApiResponse<User> updateRiskLevel(UpdateUserRiskDto updateUserRiskDto) {
        if (updateUserRiskDto == null) {
            return ApiResponse.badRequest("Update data is required");
        }
        if (!ValidationUtil.isValidUUID(updateUserRiskDto.getUserId())) {
            return ApiResponse.badRequest("Invalid user ID format");
        }
        if (!ValidationUtil.isValidRiskLevel(updateUserRiskDto.getRiskLevel())) {
            return ApiResponse.badRequest("Risk level must be between 1-10");
        }
        logger.infof("Updating user risk level");
        return userService.updateRiskLevel(updateUserRiskDto);
    }

    @GET
    @Path("/check-id/{idNumber}")
    public ApiResponse<Boolean> checkIdNumberExists(@PathParam("idNumber") String idNumber) {
        if (!ValidationUtil.isValidIdNumber(idNumber)) {
            return ApiResponse.badRequest("Invalid ID number format. Must be 5-12 digits");
        }
        logger.infof("Checking if ID number exists");
        return userService.checkIdNumberExists(idNumber);
    }

    @GET
    @Path("/check-email/{email}")
    public ApiResponse<Boolean> checkEmailExists(@PathParam("email") String email) {
        if (!ValidationUtil.isValidEmail(email)) {
            return ApiResponse.badRequest("Invalid email format");
        }
        logger.infof("Checking if email exists");
        return userService.checkEmailExists(email);
    }
}
