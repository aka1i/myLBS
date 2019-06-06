package com.example.map.activity;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
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
    TextView banquanText;
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
        banquanText = findViewById(R.id.banqun);
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
            ids = new int[]{R.drawable.tushuguan_1,R.drawable.tushuguan_2,R.drawable.tushuguan_3,R.drawable.tushuguan_4,R.drawable.tushuguan_5,R.drawable.tushuguan_6};
        else if (position.equals("福友阁"))
            ids = new int[]{R.drawable.fuyouge_1,R.drawable.fuyouge_2,R.drawable.fuyouge_3};
        else if (position.equals("游泳池"))
            ids = new int[]{R.drawable.youyongchi_1};
        else if (position.equals("山北行政楼"))
            ids = new int[]{R.drawable.shanbeixingzhenglou_1,R.drawable.shanbeixingzhenglou_2};
        else if (position.equals("山南行政楼"))
            ids = new int[]{R.drawable.shannanxingzhenglou_1,R.drawable.shannanxingzhenglou_2};
        else if (position.equals("南门"))
            ids = new int[]{R.drawable.nanmen_1};
        else if (position.equals("北门"))
            ids = new int[]{R.drawable.beimen_1};
        else if (position.equals("东门"))
            ids = new int[]{R.drawable.dongmen_1};
        else if (position.equals("西门"))
            ids = new int[]{R.drawable.ximen_1};
        else if (position.equals("素拓"))
            ids = new int[]{R.drawable.sutuo_1,R.drawable.sutuo_2,R.drawable.sutuo_3,R.drawable.sutuo_4};
        else if (position.equals("阳光园"))
            ids = new int[]{R.drawable.yangguangyuan_1};
        else if (position.equals("晋江园"))
            ids = new int[]{R.drawable.jinjiangyuan_1};
        else if (position.equals("火烈鸟"))
            ids = new int[]{R.drawable.huolieniao_1,R.drawable.huolieniao_2};
        else if (position.equals("生态文化园"))
            ids = new int[]{R.drawable.wenhuayuan};
        else if (position.equals("浦城丹桂园"))
            ids = new int[]{R.drawable.danguiyuan_1};
        else if (position.equals("火山地质园"))
            ids = new int[]{R.drawable.huoshangongyuan_1,R.drawable.huoshangongyuan_2};
        else if (position.equals("文体综合馆"))
            ids = new int[]{R.drawable.wentizongheguan_1,R.drawable.wentizongheguan_2};
        else if (position.equals("紫荆园"))
            ids = new int[]{R.drawable.zijing_1};
        else if (position.equals("玫瑰园"))
            ids = new int[]{R.drawable.meigui_1};
        else if (position.equals("京元"))
            ids = new int[]{R.drawable.jingyuan_1};
        else if (position.equals("丁香园"))
            ids = new int[]{R.drawable.dingxiang_1};
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
            banquanText.setVisibility(View.INVISIBLE);
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
