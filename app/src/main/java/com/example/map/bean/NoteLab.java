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

import org.json.JSONArray;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

public class NoteLab {
    private static NoteLab sNoteLab;
    private static List<NoteBean> mNotes = new ArrayList<>();
    private static List<NoteBean> mOthersEvents = new ArrayList<>();
    private static Context mContext;
    private static String TAG = "NoteLab";
    private boolean hasSYN = false; //是否同步成功过
    int time = 0;//已经图片上次数
    int count = 0;//本次上传的图片次数
    public NoteLab(Context context){
        mContext = context.getApplicationContext();
    }
    public static NoteLab get(Context context){
        if (sNoteLab == null){
            sNoteLab = new NoteLab(context);
        }
        return sNoteLab;
    }


    public List<NoteBean> getmNotes() {
        return mNotes;
    }

    public boolean isHasSYN() {
        return hasSYN;
    }

    public void setHasSYN(boolean hasSYN) {
        this.hasSYN = hasSYN;
    }

    public void clear(){
        mNotes.clear();
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

                        List<String> imgUrl = avObject.getList("imgUrl");
//                        for ( object : imgs){
//                            AVFile file = new AVFile(object);
//                            imgUrl.add(img.getUrl());
//                        }

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
                    hasSYN = true;
                }
            }

        });

    }

    public void addNote(final NoteBean note, final Handler handler){
        time = 0;
        final AVObject noteSave = new AVObject("Note");// 构建对象
        noteSave.put("title", note.getTitle());
        noteSave.put("detail", note.getDetail());
        noteSave.put("longitude", note.getLongitude());
        noteSave.put("latitude",note.getLatitude());
        noteSave.put("time",note.getTime());
        noteSave.put("emojiId",note.getEmojiId());
        noteSave.put("hasPosition", note.isHasPosition());
        noteSave.put("owner",note.getOwner());
        final List<String> uploadUrl = new ArrayList<>();
        final List<AVFile> files = new ArrayList<>();
        if (note.getImgUrl().size() == 0) {
            noteSave.put("imgUrl", uploadUrl);

            noteSave.saveInBackground(new SaveCallback() {// 保存到服务端
                @Override
                public void done(AVException e) {
                    if (e == null) {
                        mNotes.add(0, note);
                        handler.sendEmptyMessage(0);
                        // 存储成功
                        // 保存成功之后，objectId 会自动从服务端加载到本地
                    } else {
                        handler.sendEmptyMessage(-999);
                        // 失败的话，请检查网络环境以及 SDK 配置是否正确
                    }
                }
            });
            return;
        }
        try {//模拟单一请求
            for (String s : note.getImgUrl()){
                files.add(AVFile.withAbsoluteLocalPath("LeanCloud.png", s));
            }

            SaveCallback saveCallback = new SaveCallback() {
                @Override
                public void done(AVException e) {
                    if (e == null){
                        uploadUrl.add(files.get(time).getUrl());
                        time++;
                        if (time == note.getImgUrl().size()){
                            noteSave.put("imgUrl",uploadUrl);

                            noteSave.saveInBackground(new SaveCallback() {// 保存到服务端
                                @Override
                                public void done(AVException e) {
                                    if (e == null) {
                                        mNotes.add(0,note);
                                        handler.sendEmptyMessage(0);
                                        // 存储成功
                                        // 保存成功之后，objectId 会自动从服务端加载到本地
                                    } else {
                                        handler.sendEmptyMessage(-999);
                                        // 失败的话，请检查网络环境以及 SDK 配置是否正确
                                    }
                                }
                            });
                        }else {
                            files.get(time).saveInBackground(this);
                        }
                    }else {
                        Log.d(TAG, "done: " + e.getMessage());
                        handler.sendEmptyMessage(-999);
                        // 失败的话，请检查网络环境以及 SDK 配置是否正确
                    }
                }
            };
            files.get(0).saveInBackground(saveCallback);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            Toast.makeText(mContext,"找不到图片",Toast.LENGTH_SHORT).show();
        }

//        for (String url : note.getImgUrl()){
//            try {
//                final AVFile file = AVFile.withAbsoluteLocalPath("LeanCloud.png", url);
//                file.saveInBackground(new SaveCallback() {
//                    @Override
//                    public void done(AVException e) {
//                        if (e == null){
//                            uploadUrl.add(file.getUrl());
//                            time++;
//                            if (time == note.getImgUrl().size()){
//                                noteSave.put("imgUrl",uploadUrl);
//
//                                noteSave.saveInBackground(new SaveCallback() {// 保存到服务端
//                                    @Override
//                                    public void done(AVException e) {
//                                        if (e == null) {
//                                            mNotes.add(0,note);
//                                            handler.sendEmptyMessage(0);
//                                            // 存储成功
//                                            // 保存成功之后，objectId 会自动从服务端加载到本地
//                                        } else {
//                                            handler.sendEmptyMessage(-999);
//                                            // 失败的话，请检查网络环境以及 SDK 配置是否正确
//                                        }
//                                    }
//                                });
//                            }
//                        }else {
//                            Log.d(TAG, "done: " + e.getMessage());
//                            handler.sendEmptyMessage(-999);
//                            // 失败的话，请检查网络环境以及 SDK 配置是否正确
//                        }
//                    }
//                });
//            } catch (FileNotFoundException e) {
//                e.printStackTrace();
//                Toast.makeText(mContext,"找不到图片",Toast.LENGTH_SHORT).show();
//            }
//        }
    }
    public void deleteEvent(final NoteBean note, final Handler handler){
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

    public void updateEvent(final NoteBean note, final Handler handler,final HashMap<String,Boolean> map){
        time = 0;
        count = 0;
        AVQuery<AVObject> avQuery = new AVQuery<>("Note");
        avQuery.getInBackground(note.getId(), new GetCallback<AVObject>() {
            @Override
            public void done(final AVObject noteSave, AVException e) {
                if (e == null) {
                    noteSave.put("title", note.getTitle());// 设置名称
                    noteSave.put("detail", note.getDetail());// 设置优先级
                    noteSave.put("longitude", note.getLongitude());// 设置优先级
                    noteSave.put("latitude", note.getLatitude());
                    noteSave.put("time", note.getTime());
                    noteSave.put("emojiId", note.getEmojiId());
                    noteSave.put("hasPosition", note.isHasPosition());// 设置优先级
                    noteSave.put("owner", note.getOwner());
                    final List<String> uploadUrl = new ArrayList<>();
                    final List<AVFile> files = new ArrayList<>();
                    try {
                        for (String s : note.getImgUrl()) {
                            if (map.get(s) == null)
                                files.add(AVFile.withAbsoluteLocalPath("LeanCloud.png", s));
                            else {
                                uploadUrl.add(s);
                                time++;
                            }
                        }
                    }catch (FileNotFoundException d){
                        d.printStackTrace();
                    }
                    Log.d(TAG, "done: " + note.getImgUrl().size());
                    Log.d(TAG, "done: " +time);
                    if (note.getImgUrl().size() == time) {
                        noteSave.put("imgUrl", uploadUrl);

                        noteSave.saveInBackground(new SaveCallback() {// 保存到服务端
                            @Override
                            public void done(AVException e) {
                                if (e == null) {
                                    handler.sendEmptyMessage(0);
                                    // 存储成功
                                    // 保存成功之后，objectId 会自动从服务端加载到本地
                                } else {
                                    handler.sendEmptyMessage(-999);
                                    // 失败的话，请检查网络环境以及 SDK 配置是否正确
                                }
                            }
                        });
                        return;
                    }
                    SaveCallback saveCallback = new SaveCallback() {
                        @Override
                        public void done(AVException e) {
                            if (e == null) {
                                uploadUrl.add(files.get(count).getUrl());
                                count++;
                                time++;
                                if (time == note.getImgUrl().size()) {
                                    noteSave.put("imgUrl", uploadUrl);

                                    noteSave.saveInBackground(new SaveCallback() {// 保存到服务端
                                        @Override
                                        public void done(AVException e) {
                                            if (e == null) {
                                                handler.sendEmptyMessage(0);
                                                // 存储成功
                                                // 保存成功之后，objectId 会自动从服务端加载到本地
                                            } else {
                                                handler.sendEmptyMessage(-999);
                                                // 失败的话，请检查网络环境以及 SDK 配置是否正确
                                            }
                                        }
                                    });
                                } else {
                                    files.get(count).saveInBackground(this);
                                }
                            } else {
                                Log.d(TAG, "done: " + e.getMessage());
                                handler.sendEmptyMessage(-999);
                                // 失败的话，请检查网络环境以及 SDK 配置是否正确
                            }
                        }
                    };
                    files.get(0).saveInBackground(saveCallback);

                }
            }
        });
    }
    public NoteBean getById(String id){
        for (NoteBean noteBean : mNotes){
            if (noteBean.getId().equals(id))
                return noteBean;
        }
        return null;
    }
}
