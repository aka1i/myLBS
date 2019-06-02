package com.example.map.bean;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVFile;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.DeleteCallback;
import com.avos.avoscloud.FindCallback;
import com.avos.avoscloud.GetCallback;
import com.avos.avoscloud.SaveCallback;
import com.example.map.activity.MainActivity;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class NoteLab {
    private static NoteLab sNoteLab;
    private static List<NoteBean> mNotes = new ArrayList<>();
    private static List<NoteBean> mOthersEvents = new ArrayList<>();
    private static Context mContext;
    private static String TAG = "NoteLab";
    public NoteLab(Context context){
        mContext = context.getApplicationContext();
    }
    public static NoteLab get(Context context){
        if (sNoteLab == null){
            sNoteLab = new NoteLab(context);
        }
        return sNoteLab;
    }


    public static List<NoteBean> getmNotes() {
        return mNotes;
    }


    public void getMymNotesFormNet(final Handler handler) {
        mNotes.clear();
        AVQuery<AVObject> query = new AVQuery<>("Note");
        query.whereEqualTo("owner", AVUser.getCurrentUser());
        query.findInBackground(new FindCallback<AVObject>() {
            @Override
            public void done(List<AVObject> list, AVException e) {
                if (e == null) {
                    for (int i = 0; i < list.size(); i++) {
                        AVObject avObject = list.get(i);
                        List<AVFile> imgs = avObject.getList("imgUrl");
                        List<String> imgUrl = new ArrayList<>();
                        for (AVFile img : imgs){
                            imgUrl.add(img.getUrl());
                        }
                        NoteBean noteBean = new NoteBean(avObject.getObjectId(),
                                avObject.getString("title"),
                                avObject.getString("detail"),
                                avObject.getDouble("longitude"),
                                avObject.getDouble("latitude"),
                                avObject.getLong("time"),
                                avObject.getInt("emojiId"),
                                avObject.getBoolean("hasPosition"),
                                imgUrl,
                                AVUser.getCurrentUser());
                        mNotes.add(noteBean);
                    }
                    Collections.sort(mNotes, new Comparator<NoteBean>() {
                        @Override
                        public int compare(NoteBean o1, NoteBean o2) {
                            return (int) (o2.getTime() - o1.getTime());
                        }
                    });
                    handler.sendEmptyMessage(0);
                }
            }

        });

    }

    public static void addEvent(final NoteBean note, final Handler handler){
        final ProgressDialog progressDialog = new ProgressDialog(mContext);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("请稍后...");
        progressDialog.show();
        AVObject noteSave = new AVObject("Note");// 构建对象
        noteSave.put("title", note.getTitle());
        noteSave.put("detail", note.getDetail());
        noteSave.put("longitude", note.getLongitude());
        noteSave.put("latitude",note.getLatitude());
        noteSave.put("time",note.getTime());
        noteSave.put("emojiId",note.getEmojiId());
        noteSave.put("hasPosition", note.isHasPosition());
        noteSave.put("owner",note.getOwner());
        List<AVFile> files = new ArrayList<>();
        for (String url : note.getImgUrl()){
            AVFile file = null;
            try {
                file = AVFile.withAbsoluteLocalPath("LeanCloud.png", url);
                files.add(file);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                Toast.makeText(mContext,"找不到图片",Toast.LENGTH_SHORT).show();
            }
        }
        noteSave.put("imgUrl",files);

        noteSave.saveInBackground(new SaveCallback() {// 保存到服务端
            @Override
            public void done(AVException e) {
                if (e == null) {
                    mNotes.add(note);
                    handler.sendEmptyMessage(0);
                    Toast.makeText(mContext,"发表成功",Toast.LENGTH_SHORT).show();
                    // 存储成功
                    // 保存成功之后，objectId 会自动从服务端加载到本地
                } else {
                    Toast.makeText(mContext,"网络异常，请稍后",Toast.LENGTH_SHORT).show();
                    // 失败的话，请检查网络环境以及 SDK 配置是否正确
                }
                progressDialog.cancel();
            }
        });
    }
    public static void deleteEvent(final NoteBean note, final Handler handler){
        final ProgressDialog progressDialog = new ProgressDialog(mContext);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("请稍后...");
        progressDialog.show();
        final AVObject noteDelet = AVObject.createWithoutData("Note", note.getId());
        noteDelet.deleteInBackground(new DeleteCallback() {
            @Override
            public void done( AVException e) {
                if (e == null){
                    for (int i = 0; i < mNotes.size();i++){
                        if (mNotes.get(i).getId().equals(note.getId()))
                        {
                            mNotes.remove(i);
                            break;
                        }
                    }
                    handler.sendEmptyMessage(0);
                    Toast.makeText(mContext,"删除成功",Toast.LENGTH_SHORT).show();
                }else {
                    Toast.makeText(mContext,"网络异常，请稍后",Toast.LENGTH_SHORT).show();
                }
                progressDialog.cancel();
            }
        });
    }

    public static void updateEvent(final NoteBean note, final Handler handler,final String path){
        final ProgressDialog progressDialog = new ProgressDialog(mContext);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("请稍后...");
        progressDialog.show();
        AVQuery<AVObject> avQuery = new AVQuery<>("Note");
        avQuery.getInBackground(note.getId(), new GetCallback<AVObject>() {
            @Override
            public void done(AVObject noteSave, AVException e) {
                if (e == null){
                    noteSave.put("title", note.getTitle());// 设置名称
                    noteSave.put("detail", note.getDetail());// 设置优先级
                    noteSave.put("longitude", note.getLongitude());// 设置优先级
                    noteSave.put("latitude",note.getLatitude());
                    noteSave.put("time",note.getTime());
                    noteSave.put("emojiId",note.getEmojiId());
                    noteSave.put("hasPosition", note.isHasPosition());// 设置优先级
                    noteSave.put("owner",note.getOwner());
                    List<AVFile> files = new ArrayList<>();
                    for (String url : note.getImgUrl()){
                        AVFile file = null;
                        try {
                            file = AVFile.withAbsoluteLocalPath("LeanCloud.png", url);
                            files.add(file);
                        } catch (FileNotFoundException f) {
                            f.printStackTrace();
                            Toast.makeText(mContext,"找不到图片",Toast.LENGTH_SHORT).show();
                        }
                    }
                    noteSave.put("imgUrl",files);

                    noteSave.saveInBackground(new SaveCallback() {// 保存到服务端
                        @Override
                        public void done(AVException e) {
                            if (e == null) {
                                handler.sendEmptyMessage(0);
                                Toast.makeText(mContext,"修改成功",Toast.LENGTH_SHORT).show();
                                // 存储成功
                                // 保存成功之后，objectId 会自动从服务端加载到本地
                            } else {
                                Log.d(TAG, "done: " + e.getMessage());
                                Toast.makeText(mContext,"网络异常，请稍后",Toast.LENGTH_SHORT).show();
                                // 失败的话，请检查网络环境以及 SDK 配置是否正确
                            }
                            progressDialog.cancel();
                        }
                    });
                }else {
                    Toast.makeText(mContext,"网络异常，请稍后",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
