package com.vw.isms.util;

import org.apache.commons.lang3.RandomStringUtils;

public class PasswordUtil {

    public static final char[] chars = "01234567890abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ!@#$%^&*()_-+=".toCharArray();
    public static final int normalUserLength = 10;
    public static final int adminUserLength = 18;

    public static boolean hasUpper(String s) {
        for (int i = 0; i < s.length(); i++) {
            if (isUpper(s.charAt(i))) {
                return true;
            }
        }
        return false;
    }

    public static boolean isUpper(char c) {
        return (c >= 'A') && (c <= 'Z');
    }

    public static boolean hasLower(String s) {
        for (int i = 0; i < s.length(); i++) {
            if (isLower(s.charAt(i))) {
                return true;
            }
        }
        return false;
    }

    public static boolean isLower(char c) {
        return (c >= 'a') && (c <= 'z');
    }

    public static boolean hasNumber(String s) {
        for (int i = 0; i < s.length(); i++) {
            if (isNumber(s.charAt(i))) {
                return true;
            }
        }
        return false;
    }

    public static boolean isNumber(char c) {
        return (c >= '0') && (c <= '9');
    }

    public static boolean hasNonAlphaNumeric(String s) {
        for (int i = 0; i < s.length(); i++) {
            if ((!isLower(s.charAt(i))) && (!isUpper(s.charAt(i))) && (!isNumber(s.charAt(i)))) {
                return true;
            }
        }
        return false;
    }

    public static boolean isCompliantPassword(int length,String name,String password) {
        if (password.length() < length) {
            return false;
        }
        if(name.equals(password)){
            return false;
        }
        int complianceCount = 0;
        if (hasUpper(password)) {
            complianceCount++;
        }
        if (hasLower(password)) {
            complianceCount++;
        }
        if (hasNumber(password)) {
            complianceCount++;
        }
        if (hasNonAlphaNumeric(password)) {
            complianceCount++;
        }
        return complianceCount >= 4;
    }

    public static String randomCompliantPassword(String name) {
        return randomCompliantPassword(normalUserLength,name);
    }

    public static String randomCompliantPassword(int length,String name){
        String password;
        do {
            password = RandomStringUtils.random(length,chars);
        } while (!isCompliantPassword(length,name,password));
        return password;
    }

    public static void main(String[] args) {
        String name = "test";
        System.out.println(randomCompliantPassword(adminUserLength,name));
    }
}
