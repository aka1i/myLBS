package com.example.map.activity;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.avos.avoscloud.AVUser;
import com.example.map.R;
import com.example.map.adapter.NoteAdapter;
import com.example.map.bean.NoteBean;

import java.util.ArrayList;
import java.util.List;

public class NoteListActivity extends AppCompatActivity {

    RecyclerView mRecyclerView;
    List<NoteBean> notes = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_list);
        init();
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
        List<String> urls = new ArrayList<>();
        urls.add(Uri.parse("android.resource://" + getPackageName() + "/" + R.drawable.west_2_1).toString());
        urls.add(Uri.parse("android.resource://" + getPackageName() + "/" + R.drawable.west_2_1).toString());
        urls.add(Uri.parse("android.resource://" + getPackageName() + "/" + R.drawable.west_2_1).toString());
        urls.add(Uri.parse("android.resource://" + getPackageName() + "/" + R.drawable.west_2_1).toString());
        notes.add(new NoteBean("111","222","3333",0.1,0.1,(long)111111111,R.drawable.emoji_1,true,urls, AVUser.getCurrentUser()));
        notes.add(new NoteBean("111","222","3333",0.1,0.1,(long)111111111,R.drawable.emoji_1,true,urls, AVUser.getCurrentUser()));
        notes.add(new NoteBean("111","222","3333",0.1,0.1,(long)111111111,R.drawable.emoji_1,true,urls, AVUser.getCurrentUser()));
        notes.add(new NoteBean("111","222","3333",0.1,0.1,(long)111111111,R.drawable.emoji_1,true,urls, AVUser.getCurrentUser()));
        NoteAdapter adapter = new NoteAdapter(notes,this);
        mRecyclerView.setAdapter(adapter);
    }
    public static Intent newIntent(Context context){
        Intent intent = new Intent(context,NoteListActivity.class);
        return intent;
    }
}
