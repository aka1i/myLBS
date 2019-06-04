package com.example.map.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.avos.avoscloud.AVUser;
import com.baidu.mapapi.model.LatLng;
import com.example.map.R;
import com.example.map.SPStr;
import com.example.map.adapter.GridImageAdapter;
import com.example.map.bean.NoteBean;
import com.example.map.bean.NoteLab;
import com.example.map.utils.BitmapUtil;
import com.example.map.utils.OnlineUtils;
import com.example.map.utils.OpenAlbumUtil;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class EditNoteActivity extends AppCompatActivity implements View.OnClickListener {
    private final static String NOTE = "note";
    private final static String NOTEID = "noteid";
    private final static String TITLE = "title";
    private final static String SUMMARY = "summary";
    private final static String EMOJIID = "emojiid";
    private final static String LONGITUDE = "longitude";
    private final static String LATITUDE = "latitude";
    private final static String IMGURLS = "imgurls";
    private final static String HASPOSITION = "hasPosition";
    private final static String TIME = "time";
    private final static String AVUSER = "avuser";
    private final static String TYPE = "type";
    private List<String> selectList = new ArrayList<>();
    private HashMap<String,Boolean> hashMap = new HashMap<>();
    private RecyclerView recyclerView;
    private GridImageAdapter adapter;
    private EditText mTitleText;
    private EditText mSummaryText;
    private ImageView emojiImg;
    private Button mApplyButton;
    private ImageView deleteImg;
    private PopupWindow albumPop;
    private PopupWindow emojiPop;
    private PopupWindow selectPop;
    private int type;
    private double longitude;
    private double latitude;
    private ProgressDialog progressDialog;
    private int emojiId = R.drawable.emoji_1;
    private static final int PHOTO_FROM_GALLERY = 1;
    private static final int PHOTO_FROM_CAMERA = 2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_note);
        init();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private void init(){
        Intent intent = getIntent();
        type = intent.getIntExtra(TYPE,0);
        latitude = intent.getDoubleExtra(LATITUDE,0);
        longitude = intent.getDoubleExtra(LONGITUDE,0);
        Toolbar toolbar = findViewById(R.id.setting_title);
        toolbar.setNavigationIcon(R.drawable.back_white);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        mTitleText = findViewById(R.id.edit_note_tile);
        mSummaryText = findViewById(R.id.edit_note_summary);
        emojiImg = findViewById(R.id.emoji_img);
        mApplyButton = findViewById(R.id.apply_button);
        deleteImg = findViewById(R.id.delete_img);
        RecyclerView recyclerView = findViewById(R.id.edit_note_pics);
        recyclerView.setLayoutManager(new GridLayoutManager(this,3));
        adapter = new GridImageAdapter(this,onAddPicClickListener);
        adapter.setList(selectList);
        adapter.setHasCover(false);
        adapter.setSelectMax(9);
        recyclerView.setAdapter(adapter);
        emojiImg.setBackground(getDrawable(R.drawable.emoji_1));
        emojiImg.setOnClickListener(this);
        mApplyButton.setOnClickListener(this);
        deleteImg.setOnClickListener(this);
        if (type == 1){
            deleteImg.setVisibility(View.VISIBLE);
            mTitleText.setText(intent.getStringExtra(TITLE));
            mSummaryText.setText(intent.getStringExtra(SUMMARY));
            emojiImg.setBackground(getDrawable(intent.getIntExtra(EMOJIID,0)));
            selectList = intent.getStringArrayListExtra(IMGURLS);
            for (String s : selectList){
                hashMap.put(s,true);
            }
            if (selectList.size() != 0)
                adapter.setAddedCover(true);
            adapter.setList(selectList);
            adapter.notifyDataSetChanged();
        }
    }
    private GridImageAdapter.onAddPicClickListener onAddPicClickListener =
            new GridImageAdapter.onAddPicClickListener() {

                @Override
                public void onAddPicClick() {
                    if(!adapter.isAddedCover()&&selectList.size()>0){//未添加封面先提示添加封面
                        Toast.makeText(getApplicationContext(),"请先添加封面图片",Toast.LENGTH_SHORT).show();
                    }
                    else
                        showPop();
                }
    };

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.emoji_img:
                showSelectEmojiPop();
                break;
            case R.id.delete_img:
                showSelectPop();
                break;
            case R.id.apply_button:
                progressDialog = new ProgressDialog(this);
                progressDialog.setCancelable(false);
                progressDialog.setMessage("正在刻入您的记忆...");
                progressDialog.show();
                if (type == 0){
                    NoteBean noteBean = new NoteBean(
                            "",
                            mTitleText.getText().toString(),
                            mSummaryText.getText().toString(),
                            longitude,
                            latitude,
                            new Date().getTime(),
                            emojiId,
                            true,
                            selectList,
                            AVUser.getCurrentUser()
                    );
                    NoteLab.get(this).addNote(noteBean,handler);
                }else {
                    NoteBean noteBean = NoteLab.get(this).getById(getIntent().getStringExtra(NOTEID));
                    Log.d("notelab", "onClick: " + noteBean.getId());
                    noteBean.setTitle(mTitleText.getText().toString());
                    noteBean.setDetail(mSummaryText.getText().toString());
                    noteBean.setLongitude(longitude);
                    noteBean.setLatitude(latitude);
                    noteBean.setTime(new Date().getTime());
                    noteBean.setEmojiId(emojiId);
                    noteBean.setImgUrl(selectList);
                    NoteLab.get(this).updateEvent(noteBean,handler,hashMap);
                }

                break;
        }
    }

    private void showSelectPop(){
        View popView = View.inflate(EditNoteActivity.this,R.layout.layout_select_pop,null);
        RelativeLayout cancle = popView.findViewById(R.id.cancle_rl);
        RelativeLayout apply = popView.findViewById(R.id.apply_rl);
        View.OnClickListener listener= new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                switch (v.getId()){
                    case R.id.cancle_rl:
                        break;
                    case R.id.apply_rl:
                        progressDialog = new ProgressDialog(EditNoteActivity.this);
                        progressDialog.setCancelable(false);
                        progressDialog.setMessage("正在抹去您的记忆...");
                        progressDialog.show();
                        NoteLab.get(EditNoteActivity.this).deleteEvent(NoteLab.get(EditNoteActivity.this).getById(getIntent().getStringExtra(NOTEID)),handler2);
                        break;
                }
                selectPop.dismiss();
            }
        };
        cancle.setOnClickListener(listener);
        apply.setOnClickListener(listener);

        selectPop = new PopupWindow(popView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        selectPop.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        selectPop.setOutsideTouchable(true);
        selectPop.setFocusable(true);
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.alpha = 0.5f;
        getWindow().setAttributes(lp);

        selectPop.setOnDismissListener(new PopupWindow.OnDismissListener() {

            @Override
            public void onDismiss() {
                WindowManager.LayoutParams lp = getWindow().getAttributes();
                lp.alpha = 1f;
                getWindow().setAttributes(lp);
                emojiPop = null;
            }
        });
        selectPop.setAnimationStyle(R.style.left_to_right_anim);
//        choosePop.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        selectPop.showAtLocation(getWindow().getDecorView(), Gravity.CENTER, 0, 0);

    }

    private void showSelectEmojiPop(){
        View popView = View.inflate(EditNoteActivity.this,R.layout.layout_emoji_pop,null);
        final List<String> urls = new ArrayList<>();
        final int[] emojiIds =  new int[]{R.drawable.emoji_1,R.drawable.emoji_2,R.drawable.emoji_3,
                R.drawable.emoji_4,R.drawable.emoji_5,R.drawable.emoji_6,R.drawable.emoji_7,R.drawable.emoji_8
                 ,R.drawable.emoji_9,R.drawable.emoji_10};
        for (int i : emojiIds){
            urls.add(Uri.parse("android.resource://" + getPackageName() + "/" + i).toString());
        }


        RecyclerView recyclerView = popView.findViewById(R.id.emoji_list);
        final GridImageAdapter adapter = new GridImageAdapter(this,null);
        adapter.setCanAdd(false);
        adapter.setHasDelet(false);
        adapter.setList(urls);
        adapter.setOnItemClickListener(new GridImageAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position, View v) {
                emojiId = emojiIds[position];
                emojiImg.setBackground(getDrawable(emojiIds[position]));
                emojiPop.dismiss();
            }
        });
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new GridLayoutManager(this,3));


        emojiPop = new PopupWindow(popView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        emojiPop.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        emojiPop.setOutsideTouchable(true);
        emojiPop.setFocusable(true);
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.alpha = 0.5f;
        getWindow().setAttributes(lp);

        emojiPop.setOnDismissListener(new PopupWindow.OnDismissListener() {

            @Override
            public void onDismiss() {
                WindowManager.LayoutParams lp = getWindow().getAttributes();
                lp.alpha = 1f;
                getWindow().setAttributes(lp);
                emojiPop = null;
            }
        });
        emojiPop.setAnimationStyle(R.style.left_to_right_anim);
//        choosePop.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        emojiPop.showAtLocation(getWindow().getDecorView(), Gravity.CENTER, 0, 0);
    }

    private void showPop() {
        View bottomView = View.inflate(EditNoteActivity.this, R.layout.bottom_dialog, null);
        TextView mAlbum=bottomView.findViewById(R.id.tv_album);
    //    TextView mCamera=bottomView.findViewById(R.id.tv_camera);
        TextView mCancel=bottomView.findViewById(R.id.tv_cancel) ;

        albumPop = new PopupWindow(bottomView, -1, -2);
        albumPop.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        albumPop.setOutsideTouchable(true);
        albumPop.setFocusable(true);
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.alpha = 0.5f;
        getWindow().setAttributes(lp);
        albumPop.setOnDismissListener(new PopupWindow.OnDismissListener() {

            @Override
            public void onDismiss() {
                WindowManager.LayoutParams lp = getWindow().getAttributes();
                lp.alpha = 1f;
                getWindow().setAttributes(lp);
                albumPop = null;
            }
        });
        albumPop.setAnimationStyle(R.style.main_menu_photo_anim);
        albumPop.showAtLocation(getWindow().getDecorView(), Gravity.BOTTOM, 0, 0);

        View.OnClickListener clickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                switch (view.getId()) {
                    case R.id.tv_album:
                        if(ContextCompat.checkSelfPermission(EditNoteActivity.this,
                                Manifest.permission.WRITE_EXTERNAL_STORAGE)!=
                                PackageManager.PERMISSION_GRANTED){
                            ActivityCompat.requestPermissions(EditNoteActivity.this,
                                    new String[]{ Manifest.permission.WRITE_EXTERNAL_STORAGE},1);
                        }
                        else {
                            OpenAlbumUtil.openAlbum(EditNoteActivity.this);
                        }
                        break;
//                    case R.id.tv_camera:
//                        //拍照
//                        if (ContextCompat.checkSelfPermission(InitiateNewActivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
//                            ActivityCompat.requestPermissions(InitiateNewActivity.this,new String[]
//                                    {Manifest.permission.CAMERA,Manifest.permission.WRITE_EXTERNAL_STORAGE},2);
//                        }
//                        else
//                            getPhotoByCamera();
//                        break;
                    case R.id.tv_cancel:
                        //取消
                        //closePopupWindow();
                        break;
                }
                if (albumPop != null && albumPop.isShowing()) {
                    albumPop.dismiss();
                    albumPop = null;
                }
            }
        };

        mAlbum.setOnClickListener(clickListener);
   //     mCamera.setOnClickListener(clickListener);
        mCancel.setOnClickListener(clickListener);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){
            case PHOTO_FROM_CAMERA:
                if(resultCode==RESULT_OK){
                    try {
                        //Bitmap bitmap= BitmapFactory.decodeStream(getContentResolver().openInputStream(cameraUri));
                        String compressPath= BitmapUtil.compressImage(getExternalCacheDir().getPath()+"/output.jpg",
                                getExternalCacheDir().getPath());
                        if(adapter.isAddedCover())
                            selectList.add(compressPath);
                        else{
                            if(selectList.size()==0)
                                selectList.add(compressPath);
                            else
                                selectList.set(0,compressPath);
                            adapter.setAddedCover(true);
                        }
                        adapter.setList(selectList);
                        adapter.notifyDataSetChanged();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    // 图片选择结果回调
                }
                break;
            case PHOTO_FROM_GALLERY:
                if(resultCode==RESULT_OK){
                    String path;
                    if(Build.VERSION.SDK_INT>=19){
                        path= OpenAlbumUtil.handleImageOnKitKat(this,data);
                    }
                    else{
                        path= OpenAlbumUtil.handleImageBeforeKitKat(this,data);
                    }

                    if(!"".equals(path)){
                        String compressPath=BitmapUtil.compressImage(path,
                                getExternalCacheDir().getPath());
                        if(adapter.isAddedCover())
                            selectList.add(compressPath);
                        else{
                            if(selectList.size()==0)
                                selectList.add(compressPath);
                            else
                                selectList.set(0,compressPath);
                            adapter.setAddedCover(true);
                        }
                        adapter.setList(selectList);
                        adapter.notifyDataSetChanged();
                    }
                }
                break;
        }
    }
    //新建
    public static Intent newIntent(Context context, LatLng latLng){
        Intent intent = new Intent(context,EditNoteActivity.class);
        intent.putExtra(LONGITUDE,latLng.longitude);
        intent.putExtra(LATITUDE,latLng.latitude);
        intent.putExtra(TYPE,0);
        return intent;
    }
    //修改
    public static Intent newIntent(Context context, NoteBean note){
        Intent intent = new Intent(context,EditNoteActivity.class);
        intent.putExtra(NOTEID,note.getId());
        intent.putExtra(TITLE,note.getTitle());
        intent.putExtra(SUMMARY,note.getDetail());
        intent.putExtra(EMOJIID,note.getEmojiId());
        intent.putExtra(LONGITUDE,note.getLongitude());
        intent.putExtra(LATITUDE,note.getLatitude());
        intent.putStringArrayListExtra(IMGURLS,(ArrayList<String>) note.getImgUrl());
        intent.putExtra(HASPOSITION,note.isHasPosition());
        intent.putExtra(TIME,note.getTime());
        intent.putExtra(AVUSER,note.getOwner());
        intent.putExtra(TYPE,1);
        return intent;
    }

    @SuppressLint("HandlerLeak")
    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (progressDialog!=null)
            progressDialog.cancel();
            switch (msg.what){
                case 0:
                    finish();
                    SharedPreferences.Editor editor = getSharedPreferences(SPStr.USER_INFO,MODE_PRIVATE).edit();
                    editor.putInt(SPStr.NOTE_COUNT,NoteLab.get(getApplicationContext()).getmNotes().size());
                    editor.apply();
                    OnlineUtils.saveData(getApplicationContext());
                    Toast.makeText(EditNoteActivity.this,"刻入成功",Toast.LENGTH_SHORT).show();
                    break;
                case -999:
                    Toast.makeText(EditNoteActivity.this,"网络异常，请稍后",Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };
    @SuppressLint("HandlerLeak")
    Handler handler2 = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (progressDialog!=null)
                progressDialog.cancel();
            switch (msg.what){
                case 0:
                    finish();
                    SharedPreferences.Editor editor = getSharedPreferences(SPStr.USER_INFO,MODE_PRIVATE).edit();
                    editor.putInt(SPStr.NOTE_COUNT,NoteLab.get(getApplicationContext()).getmNotes().size());
                    editor.apply();
                    OnlineUtils.saveData(getApplicationContext());
                    Toast.makeText(EditNoteActivity.this,"抹除成功",Toast.LENGTH_SHORT).show();
                    break;
                case -999:
                    Toast.makeText(EditNoteActivity.this,"网络异常，请稍后",Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };
}
