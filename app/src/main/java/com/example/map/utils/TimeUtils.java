package com.example.map.utils;

import java.util.Calendar;

public class TimeUtils {
    public static boolean isSameDay(long t1,long t2){
        Calendar cal1 = Calendar.getInstance();
        cal1.setTimeInMillis(t1);
        Calendar cal2 = Calendar.getInstance();
        cal1.setTimeInMillis(t2);
        return isSameDay(cal1, cal2);
    }
    public static boolean isSameDay(Calendar cal1, Calendar cal2) {
        if(cal1 != null && cal2 != null) {
            return cal1.get(0) == cal2.get(0) && cal1.get(1) == cal2.get(1) && cal1.get(6) == cal2.get(6);
        } else {
            throw new IllegalArgumentException("The date must not be null");
        }
    }
}
