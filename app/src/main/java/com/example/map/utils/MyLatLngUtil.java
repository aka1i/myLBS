package com.example.map.utils;

import com.baidu.mapapi.model.LatLng;

public class MyLatLngUtil {
    public static boolean equal(LatLng l1,LatLng l2){
        if (l1.longitude == l2.longitude && l1.latitude == l2.latitude)
            return true;
        return false;
    }
}
