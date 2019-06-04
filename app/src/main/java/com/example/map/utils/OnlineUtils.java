package com.example.map.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.widget.Toast;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.SaveCallback;
import com.example.map.SPStr;
import com.example.map.activity.MainActivity;

public class OnlineUtils {
    public static void saveData(Context context){
        AVUser avUser = AVUser.getCurrentUser();
        SharedPreferences sp = context.getSharedPreferences(SPStr.USER_INFO,context.MODE_PRIVATE);

        avUser.put(SPStr.NOTE_COUNT, sp.getInt(SPStr.NOTE_COUNT,0));
        avUser.put(SPStr.STUDY_COUNT, sp.getInt(SPStr.STUDY_COUNT,0));
        avUser.put(SPStr.EAT_COUNT, sp.getInt(SPStr.EAT_COUNT,0));

        avUser.saveInBackground(new SaveCallback() {
            @Override
            public void done(AVException e) {
                if (e == null) {


                } else {

                }
              }
        });
    }
}
