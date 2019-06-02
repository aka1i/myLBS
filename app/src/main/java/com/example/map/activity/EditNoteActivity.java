package com.example.map.activity;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.baidu.mapapi.model.LatLng;
import com.example.map.R;

public class EditNoteActivity extends AppCompatActivity {
    private static final String LONGITUDE = "longitude";
    private static final String LATITUDE = "latitude";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_note);
    }

    public static Intent newIntent(Context context, LatLng latLng){
        Intent intent = new Intent(context,EditNoteActivity.class);
        intent.putExtra(LONGITUDE,latLng.longitude);
        intent.putExtra(LATITUDE,latLng.latitude);
        return intent;
    }
}
