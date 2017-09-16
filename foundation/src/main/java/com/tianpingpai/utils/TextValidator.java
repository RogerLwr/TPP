package com.tianpingpai.utils;

import android.util.Patterns;

import java.util.regex.Pattern;

public class TextValidator {
    public static boolean isMobileNumber(CharSequence text){
        return Patterns.PHONE.matcher(text).matches();
    }

    static Pattern p = Pattern.compile("^(?![0-9]+$)(?![a-zA-Z]+$)[0-9A-Za-z]{6,16}$");

    public static boolean isPasswordValid(CharSequence text){
        return p.matcher(text).matches();
    }
}
