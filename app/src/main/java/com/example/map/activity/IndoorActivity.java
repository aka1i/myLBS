package com.example.map.activity;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.example.map.R;
import com.example.map.adapter.IndoorPicAdapter;
import com.leochuan.CenterSnapHelper;
import com.leochuan.ScaleLayoutManager;

public class IndoorActivity extends AppCompatActivity {
    RecyclerView recyclerView;
    IndoorPicAdapter adapter;
    int[] ids;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_indoor);
        init();
    }
    private void init(){
        int type = getIntent().getIntExtra("type",0);
        if (type == 0)
            ids = new int[]{R.drawable.west_3_1,R.drawable.west_3_2,R.drawable.west_3_3,R.drawable.west_3_4,R.drawable.west_3_5};
        else if (type == 1)
            ids = new int[]{R.drawable.west_2_1,R.drawable.west_2_2,R.drawable.west_2_3,R.drawable.west_2_4,R.drawable.west_2_5};
        else if (type == 2)
            ids = new int[]{R.drawable.west_1_1,R.drawable.west_1_2,R.drawable.west_1_3,R.drawable.west_1_4,R.drawable.west_1_5};
        else if (type == 3)
            ids = new int[]{R.drawable.mid_1,R.drawable.mid_2,R.drawable.mid_3,R.drawable.mid_4,R.drawable.mid_5};
        else if (type == 4)
            ids = new int[]{R.drawable.east_1_1,R.drawable.east_1_2,R.drawable.east_1_3,R.drawable.east_1_4,R.drawable.east_1_5};
        else if (type == 5)
            ids = new int[]{R.drawable.east_2_1,R.drawable.east_2_2,R.drawable.east_2_3,R.drawable.east_2_4,R.drawable.east_2_5};
        else if (type == 6)
            ids = new int[]{R.drawable.east_3_1,R.drawable.east_3_2,R.drawable.east_3_3,R.drawable.east_3_4,R.drawable.east_3_5};
        recyclerView = findViewById(R.id.pic_list);
        adapter = new IndoorPicAdapter(this,ids);
        recyclerView.setAdapter(adapter);
        ScaleLayoutManager layoutManager = new ScaleLayoutManager.Builder(this,5)
                .setMaxVisibleItemCount(5)
                .setMoveSpeed((float)0.5)
                .build();
        recyclerView.setLayoutManager(layoutManager);
        new CenterSnapHelper().attachToRecyclerView(recyclerView);
    }

    public static Intent newIntent(Context context,int type){
        Intent intent = new Intent(context,IndoorActivity.class);
        intent.putExtra("type",type);
        return intent;
    }
}
