package com.lopez.userhandler;

import jakarta.ws.rs.ApplicationPath;
import jakarta.ws.rs.core.Application;

@ApplicationPath("/api/v1")
public class UserApp extends Application {

  // This class can be used to define application-wide configurations if needed.
  // Currently, it serves as a marker for JAX-RS to recognize the base path for
  // REST endpoints.

}
