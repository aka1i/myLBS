package com.example.map.bean;

import com.baidu.mapapi.model.LatLng;

import java.util.HashMap;
import java.util.LinkedHashMap;

public class PositionData {
    public static LinkedHashMap<String,LatLng> data = new LinkedHashMap();
    static {
        data.put("图书馆",new LatLng(26.064609,119.204299));
    }
}
