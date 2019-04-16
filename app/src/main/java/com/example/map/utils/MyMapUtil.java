package com.example.map.utils;


import android.graphics.Color;
import android.graphics.Typeface;
import android.util.Log;
import android.view.Gravity;
import android.widget.TextView;

import com.baidu.mapapi.map.InfoWindow;
import com.baidu.mapapi.model.CoordUtil;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.model.inner.Point;
import com.example.map.bean.PositionData;

public class MyMapUtil {
    public double getDistance(LatLng var0, LatLng var1) {
        if (var0 != null && var1 != null) {
            Point var2 = CoordUtil.ll2point(var0);
            Point var3 = CoordUtil.ll2point(var1);
            return var2 != null && var3 != null ? CoordUtil.getDistance(var2, var3) : -1.0D;
        } else {
            return -1.0D;
        }
    }

    public static LatLng changeTextToLatng(String str,double currentX,double currentY){
        if (str.equals("我的位置")){
            return new LatLng(currentX,currentY);
        }else if (PositionData.teachingBuilding.keySet().contains(str)){
            return PositionData.teachingBuilding.get(str);
        }else if (PositionData.canteen.keySet().contains(str)){
            return PositionData.canteen.get(str);
        }else if (PositionData.scenicSpot.keySet().contains(str)){
            return PositionData.scenicSpot.get(str);
        }else if (PositionData.others.keySet().contains(str)){
            return PositionData.others.get(str);
        }else
            return null;

    }

}
