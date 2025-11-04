package com.example.prm392_final_prj.utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class ResetPasswordUtils {

    public static String generatePassword() {
        String upper = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        String lower = "abcdefghijklmnopqrstuvwxyz";
        String digits = "0123456789";
        String special = "!@#$%^&*";
        String all = upper + lower + digits + special;
        Random rand = new Random();
        StringBuilder sb = new StringBuilder();
        sb.append(upper.charAt(rand.nextInt(upper.length())));
        sb.append(lower.charAt(rand.nextInt(lower.length())));
        sb.append(digits.charAt(rand.nextInt(digits.length())));
        sb.append(special.charAt(rand.nextInt(special.length())));
        for (int i = 4; i < 8; i++) {
            sb.append(all.charAt(rand.nextInt(all.length())));
        }
        List<Character> pwdChars = new ArrayList<>();
        for (char c : sb.toString().toCharArray()) pwdChars.add(c);
        Collections.shuffle(pwdChars);
        StringBuilder password = new StringBuilder();
        for (char c : pwdChars) password.append(c);
        return password.toString();
    }
}
