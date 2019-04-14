package com.example.map;


import android.graphics.Color;
import android.graphics.Typeface;
import android.view.Gravity;
import android.widget.TextView;

import com.baidu.mapapi.map.InfoWindow;
import com.baidu.mapapi.model.CoordUtil;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.model.inner.Point;

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




}
