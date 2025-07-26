package com.lopez.userhandler.util;

import java.util.regex.Pattern;

public class ValidationUtil {

    private static final Pattern EMAIL_PATTERN = Pattern.compile(
            "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");

    private static final Pattern ID_NUMBER_PATTERN = Pattern.compile("^[0-9]{5,12}$");
    private static final Pattern UUID_PATTERN = Pattern.compile(
            "^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$");

    public static boolean isValidEmail(String email) {
        return email != null && EMAIL_PATTERN.matcher(email).matches();
    }

    public static boolean isValidIdNumber(String idNumber) {
        return idNumber != null && ID_NUMBER_PATTERN.matcher(idNumber).matches();
    }

    public static boolean isValidUUID(String uuid) {
        return uuid != null && UUID_PATTERN.matcher(uuid).matches();
    }

    public static boolean isValidString(String str, int maxLength) {
        return str != null && !str.trim().isEmpty() && str.length() <= maxLength;
    }

    public static boolean isValidRiskLevel(String riskLevel) {
        if (riskLevel == null)
            return false;
        try {
            float level = Float.parseFloat(riskLevel);
            return level >= 1.0 && level <= 10.0;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}