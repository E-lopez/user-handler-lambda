package com.lopez.userhandler;

import org.jboss.logging.Logger;

import com.lopez.userhandler.dto.User;
import com.lopez.userhandler.service.UserService;

import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

@Path("/users")
public class UserResource {
    private final UserService userService;
    private static final Logger log = Logger.getLogger(UserResource.class);

    @Inject
    public UserResource(UserService userService) {
        this.userService = userService;
    }

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String getUsers() {
        return userService.getUsers();
    }

    @GET
    @Path("/{userId}")
    @Produces(MediaType.TEXT_PLAIN)
    public String getUserById(@PathParam("userId") String userId) {
        return userService.getUserById(userId);
    }

    @GET
    @Path("/name/{userName}")
    @Produces(MediaType.TEXT_PLAIN)
    public String getUserByName(@PathParam("userName") String userName) {
        return userService.getUserByName(userName);
    }

    @POST
    @Path("/create")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public User createUser(User user) {
        log.info("Creating user: " + user.getUserName());
        return userService.createUser(user);
    }

    @DELETE
    @Path("/clear")
    @Produces(MediaType.TEXT_PLAIN)
    public String deleteUser() {
        log.info("Deleting all users");
        return userService.clearUsers();
    }
}
