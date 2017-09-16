package com.tianpingpai.utils;

public class NumberUtils {
    public static double parseDouble(String number){
        double value = 0;
        try {
            value = Double.parseDouble(number);
        }catch (Exception e){

        }
        return value;
    }

    public static boolean equals(double arg1,double arg2){
        return Math.abs(arg1 - arg2) < 0.0001;
    }

    public static boolean equals(String arg1,String arg2){
        return equals(parseDouble(arg1),parseDouble(arg2));
    }
}
