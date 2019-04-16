package com.example.map.bean;

import com.baidu.mapapi.model.LatLng;

import java.util.LinkedHashMap;

public class PositionData {
    public static LinkedHashMap<String,LatLng> teachingBuilding = new LinkedHashMap();
    public static LinkedHashMap<String,LatLng> canteen = new LinkedHashMap();
    public static LinkedHashMap<String,LatLng> scenicSpot = new LinkedHashMap();
    public static LinkedHashMap<String,LatLng> others = new LinkedHashMap();
    static {
        teachingBuilding.put("图书馆",new LatLng(26.06479,119.204322));
        teachingBuilding.put("西3",new LatLng(26.064384,119.202052));
        teachingBuilding.put("西2",new LatLng(26.064882,119.201983));
        teachingBuilding.put("西1",new LatLng(26.065286,119.202043));
        teachingBuilding.put("中楼",new LatLng(26.065985,119.201939));
        teachingBuilding.put("东1",new LatLng(26.066262,119.202591));
        teachingBuilding.put("东2",new LatLng(26.066953,119.203485));
        teachingBuilding.put("东3",new LatLng(26.066683,119.204275));
        teachingBuilding.put("文1",new LatLng(26.067906,119.20496));
        teachingBuilding.put("文2",new LatLng(26.067801,119.205535));


        canteen.put("紫荆园",new LatLng(26.058864,119.198528));
        canteen.put("玫瑰园",new LatLng(26.058803,119.199269));
        canteen.put("京元",new LatLng(26.062943,119.199216));
        canteen.put("丁香园",new LatLng(26.062804,119.198644));

        scenicSpot.put("福友阁",new LatLng(26.059701,119.206877));
        scenicSpot.put("阳光园",new LatLng(26.065868,119.205262));
        scenicSpot.put("生态文化园",new LatLng(26.06624,119.206416));

        others.put("校医院",new LatLng(26.060325,119.200791));
        others.put("风雨操场",new LatLng(26.057763,119.201115));
        others.put("第一田径场",new LatLng(26.06237,119.200871));
        others.put("第二田径场",new LatLng(26.056879,119.202193));
        others.put("素拓",new LatLng(26.062536,119.202416));
        others.put("山北行政楼",new LatLng(26.063713,119.207176));
        others.put("山南行政楼",new LatLng(26.059943,119.204567));
        others.put("科技园",new LatLng(26.05786,119.208832));
        others.put("文体综合馆",new LatLng(26.057383,119.203504));

    }
}
