package com.example.map.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Toast;

import com.avos.avoscloud.AVUser;
import com.example.map.R;
import com.example.map.SPStr;
import com.example.map.adapter.NoteAdapter;
import com.example.map.bean.NoteBean;
import com.example.map.bean.NoteLab;
import com.example.map.utils.OnlineUtils;

import java.util.ArrayList;
import java.util.List;

public class NoteListActivity extends AppCompatActivity {

    RecyclerView mRecyclerView;
    NoteAdapter adapter;
    ProgressDialog progressDialog;
    List<NoteBean> notes = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_list);
        init();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (adapter!=null)
            adapter.notifyDataSetChanged();
    }

    private void init(){
        Toolbar toolbar = findViewById(R.id.setting_title);
        toolbar.setNavigationIcon(R.drawable.back_white);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        mRecyclerView = findViewById(R.id.note_list);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
//        List<String> urls = new ArrayList<>();
//        urls.add(Uri.parse("android.resource://" + getPackageName() + "/" + R.drawable.west_2_1).toString());
//        urls.add(Uri.parse("android.resource://" + getPackageName() + "/" + R.drawable.west_2_1).toString());
//        urls.add(Uri.parse("android.resource://" + getPackageName() + "/" + R.drawable.west_2_1).toString());
//        urls.add(Uri.parse("android.resource://" + getPackageName() + "/" + R.drawable.west_2_1).toString());
//        notes.add(new NoteBean("111","222","3333",0.1,0.1,(long)111111111,R.drawable.emoji_1,true,urls, AVUser.getCurrentUser()));
//        notes.add(new NoteBean("111","222","3333",0.1,0.1,(long)111111111,R.drawable.emoji_1,true,urls, AVUser.getCurrentUser()));
//        notes.add(new NoteBean("111","222","3333",0.1,0.1,(long)111111111,R.drawable.emoji_1,true,urls, AVUser.getCurrentUser()));
//        notes.add(new NoteBean("111","222","3333",0.1,0.1,(long)111111111,R.drawable.emoji_1,true,urls, AVUser.getCurrentUser()));
        adapter = new NoteAdapter(notes,this);
        adapter.setListenr(new NoteAdapter.FollowMeListenr() {
            @Override
            public void onclick(double longitude,double latitude) {
                Intent data = new Intent();
                data.putExtra("longitude",longitude);
                data.putExtra("latitude",latitude);
                setResult(RESULT_OK,data);
                finish();
            }
        });
        mRecyclerView.setAdapter(adapter);
        if (!NoteLab.get(this).isHasSYN()){
            progressDialog = new ProgressDialog(this);
            progressDialog.setCancelable(false);
            progressDialog.setMessage("正在唤醒您的记忆...");
            progressDialog.show();
            NoteLab.get(this).getMymNotesFormNet(handler);
        }else {
            adapter.setNotes(NoteLab.get(this).getmNotes());
            adapter.notifyDataSetChanged();
        }
    }
    public static Intent newIntent(Context context){
        Intent intent = new Intent(context,NoteListActivity.class);
        return intent;
    }
    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case 0:
                    SharedPreferences.Editor editor = getSharedPreferences(SPStr.USER_INFO,MODE_PRIVATE).edit();
                    editor.putInt(SPStr.NOTE_COUNT,NoteLab.get(getApplicationContext()).getmNotes().size());
                    editor.apply();
                    adapter.setNotes(NoteLab.get(NoteListActivity.this).getmNotes());
                    adapter.notifyDataSetChanged();
                    OnlineUtils.saveData(getApplicationContext());
                    break;
                case -999:
                    Toast.makeText(NoteListActivity.this,"网络连接失败",Toast.LENGTH_SHORT).show();
                    break;
            }
            if (progressDialog != null)
            progressDialog.cancel();
        }
    };
}
