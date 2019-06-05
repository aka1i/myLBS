package com.example.map.activity;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.TextView;

import com.example.map.R;
import com.example.map.adapter.EasyImgAdapter;
import com.example.map.adapter.IndoorPicAdapter;
import com.example.map.bean.PositionData;
import com.leochuan.CenterSnapHelper;
import com.leochuan.ScaleLayoutManager;

import java.util.ArrayList;
import java.util.List;

public class IndoorActivity extends AppCompatActivity {
    RecyclerView recyclerView;
    RecyclerView.Adapter adapter;
    TextView mTextView;
    private final static String POSITION = "position";
    int[] ids;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_indoor);
        init();
    }
    private void init(){
        mTextView = findViewById(R.id.position_text);

        String position = getIntent().getStringExtra(POSITION);
        mTextView.setText(position);
        if (position.equals("西3"))
            ids = new int[]{R.drawable.west_3_1,R.drawable.west_3_2,R.drawable.west_3_3,R.drawable.west_3_4,R.drawable.west_3_5};
        else if (position.equals("西2"))
            ids = new int[]{R.drawable.west_2_1,R.drawable.west_2_2,R.drawable.west_2_3,R.drawable.west_2_4,R.drawable.west_2_5};
        else if (position.equals("西1"))
            ids = new int[]{R.drawable.west_1_1,R.drawable.west_1_2,R.drawable.west_1_3,R.drawable.west_1_4,R.drawable.west_1_5};
        else if (position.equals("中楼"))
            ids = new int[]{R.drawable.mid_1,R.drawable.mid_2,R.drawable.mid_3,R.drawable.mid_4,R.drawable.mid_5};
        else if (position.equals("东1"))
            ids = new int[]{R.drawable.east_1_1,R.drawable.east_1_2,R.drawable.east_1_3,R.drawable.east_1_4,R.drawable.east_1_5};
        else if (position.equals("东2"))
            ids = new int[]{R.drawable.east_2_1,R.drawable.east_2_2,R.drawable.east_2_3,R.drawable.east_2_4,R.drawable.east_2_5};
        else if (position.equals("东3"))
            ids = new int[]{R.drawable.east_3_1,R.drawable.east_3_2,R.drawable.east_3_3,R.drawable.east_3_4,R.drawable.east_3_5};
        else if (position.equals("图书馆"))
            ids = new int[]{R.drawable.east_3_1,R.drawable.east_3_2,R.drawable.east_3_3,R.drawable.east_3_4,R.drawable.east_3_5};
        recyclerView = findViewById(R.id.pic_list);
        if (PositionData.outdoors.get(position) != null){
            List<String> strings = new ArrayList<>();
            for (int id : ids){
                Uri uri = Uri.parse("android.resource://" + getPackageName() + "/" + id);
                strings.add(uri.toString());
            }
            adapter = new EasyImgAdapter(this,strings);
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
        }else{
            adapter = new IndoorPicAdapter(this,ids);
            ScaleLayoutManager layoutManager = new ScaleLayoutManager.Builder(this,5)
                    .setMaxVisibleItemCount(5)
                    .setMoveSpeed((float)0.5)
                    .build();
            recyclerView.setLayoutManager(layoutManager);
            new CenterSnapHelper().attachToRecyclerView(recyclerView);
        }
        recyclerView.setAdapter(adapter);
    }

    public static Intent newIntent(Context context,String position){
        Intent intent = new Intent(context,IndoorActivity.class);
        intent.putExtra(POSITION,position);
        return intent;
    }
}
