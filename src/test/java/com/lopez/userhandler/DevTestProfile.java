package com.lopez.userhandler;

import io.quarkus.test.junit.QuarkusTestProfile;
import java.util.Map;

public class DevTestProfile implements QuarkusTestProfile {

    @Override
    public Map<String, String> getConfigOverrides() {
        return Map.of(
            "quarkus.profile", "test"
        );
    }

    @Override
    public boolean disableGlobalTestResources() {
        return false;
    }
}