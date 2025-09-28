package com.careerLink.user_service.utils;

import org.mindrot.jbcrypt.BCrypt;

public class PasswordUtil {

    // Hash a Password for the first time
    public static String hashPassword(String plainTextPassword){
        return BCrypt.hashpw(plainTextPassword,BCrypt.gensalt());
    }

    //check that a plain text password matches a previously hashed one

    public static boolean checkPassword(String plainTextString, String hashedPassword){
        return BCrypt.checkpw(plainTextString,hashedPassword);
    }

}
