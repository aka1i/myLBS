package com.example.map.activity;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.example.map.R;
import com.example.map.bean.NoteBean;

public class NoteDetailActivity extends AppCompatActivity {
    private final static String NOTE = "note";
    private final static String TITLE = "note";
    private final static String SUMMARY = "note";
    private final static String EMOJIID = "note";
    private final static String LONGITUDE = "longitude";
    private final static String LATITUDE = "latitude";
    private final static String HASPOSITION = "hasPosition";
    private final static String TIME = "time";
    private final static String AVUSER = "avuser";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_detail);
    }
    public static Intent newIntent(Context context, NoteBean note){
        Intent intent = new Intent(context,NoteDetailActivity.class);
        intent.putExtra(TITLE,note.getTitle());
        intent.putExtra(SUMMARY,note.getDetail());
        intent.putExtra(EMOJIID,note.getEmojiId());
        intent.putExtra(LONGITUDE,note.getLongitude());
        intent.putExtra(LATITUDE,note.getLatitude());
        intent.putExtra(HASPOSITION,note.isHasPosition());
        intent.putExtra(TIME,note.getTime());
        intent.putExtra(AVUSER,note.getOwner());
        return intent;
    }
}
