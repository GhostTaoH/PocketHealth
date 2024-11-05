package com.example.pockethealth.security;

import org.mindrot.jbcrypt.BCrypt;

public class PasswordEncryptor {

    //对密码进行加密操作
    public static String hashPassword(String password){
        return BCrypt.hashpw(password,BCrypt.gensalt());
    }

    //对密码进行验证操作
    public static boolean checkPassword(String plainPassword, String hashedPassword){
        return BCrypt.checkpw(plainPassword,hashedPassword);
    }
}
