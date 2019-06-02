package com.example.map.activity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
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
import android.widget.TextView;
import android.widget.Toast;

import com.avos.avoscloud.AVUser;
import com.baidu.mapapi.model.LatLng;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.example.map.R;
import com.example.map.SPStr;
import com.example.map.adapter.GridImageAdapter;
import com.example.map.utils.BitmapUtil;
import com.example.map.utils.OpenAlbumUtil;

import java.util.ArrayList;
import java.util.List;

import cc.shinichi.sherlockutillibrary.utility.image.ImageUtil;

public class EditNoteActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String LONGITUDE = "longitude";
    private static final String LATITUDE = "latitude";
    private List<String> selectList = new ArrayList<>();
    private RecyclerView recyclerView;
    private GridImageAdapter adapter;
    private EditText mTitleText;
    private EditText mSummaryText;
    private ImageView emojiImg;
    private PopupWindow albumPop;
    private PopupWindow emojiPop;
    private static final int PHOTO_FROM_GALLERY = 1;
    private static final int PHOTO_FROM_CAMERA = 2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_note);
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
        mTitleText = findViewById(R.id.edit_note_tile);
        mSummaryText = findViewById(R.id.edit_note_summary);
        emojiImg = findViewById(R.id.emoji_img);
        RecyclerView recyclerView = findViewById(R.id.edit_note_pics);
        recyclerView.setLayoutManager(new GridLayoutManager(this,3));
        adapter = new GridImageAdapter(this,onAddPicClickListener);
        adapter.setList(selectList);
        adapter.setHasCover(false);
        adapter.setSelectMax(6);
        recyclerView.setAdapter(adapter);
        emojiImg.setBackground(getDrawable(R.drawable.emoji_1));
        emojiImg.setOnClickListener(this);
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
        }
    }

    private void showSelectEmojiPop(){
        View popView = View.inflate(EditNoteActivity.this,R.layout.layout_emoji_pop,null);
        final List<String> urls = new ArrayList<>();
        final int[] emojiIds =  new int[]{R.drawable.emoji_1,R.drawable.emoji_2,R.drawable.emoji_3,
                R.drawable.emoji_4,R.drawable.emoji_5,R.drawable.emoji_6,R.drawable.emoji_7,R.drawable.emoji_8
                 ,R.drawable.emoji_9,R.drawable.emoji_10};
        for (int i = 0;i < emojiIds.length; i++){
            urls.add(Uri.parse("android.resource://" + getPackageName() + "/" + emojiIds[i]).toString());
        }


        RecyclerView recyclerView = popView.findViewById(R.id.emoji_list);
        final GridImageAdapter adapter = new GridImageAdapter(this,null);
        adapter.setCanAdd(false);
        adapter.setHasDelet(false);
        adapter.setList(urls);
        adapter.setOnItemClickListener(new GridImageAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position, View v) {
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
    public static Intent newIntent(Context context, LatLng latLng){
        Intent intent = new Intent(context,EditNoteActivity.class);
        intent.putExtra(LONGITUDE,latLng.longitude);
        intent.putExtra(LATITUDE,latLng.latitude);
        return intent;
    }
}
