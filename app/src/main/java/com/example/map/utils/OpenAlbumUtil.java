package com.example.map.utils;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.ContentUris;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.util.Log;

public class OpenAlbumUtil {
    private static String TAG = "OpenAlbumUtil";
    private static final int PHOTO_FROM_GALLERY = 1;
    private static final int PHOTO_FROM_CAMERA = 2;

    public static void openAlbum(Activity context){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        context.startActivityForResult(intent, PHOTO_FROM_GALLERY);
    }

    public static void openAlbum(Fragment context){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        context.startActivityForResult(intent, PHOTO_FROM_GALLERY);
    }


    @TargetApi(19)
    public static String handleImageOnKitKat(Activity activity,Intent data) {
        String imagePath = null;
        Uri uri = data.getData();
        Log.d(TAG, "uri: " + uri);
        Log.d(TAG, "isDocumentUri: " + DocumentsContract.isDocumentUri(activity, uri));
        if (DocumentsContract.isDocumentUri(activity, uri)) {
            String docId = DocumentsContract.getDocumentId(uri);
            Log.d(TAG, "docId: " + docId);
            if ("com.android.providers.media.documents".equals(uri.getAuthority())) {
                String id = docId.split(":")[1];
                String selection = MediaStore.Images.Media._ID + "=" +id;
                imagePath = getImagePath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, selection,activity);
            } else if ("com.android.providers.downloads.documents".equals(uri.getAuthority())) {
                Uri contentUri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"), Long.valueOf(docId));
                imagePath = getImagePath(contentUri, null,activity);
            }
        } else if ("content".equalsIgnoreCase(uri.getScheme())) {
            imagePath = getImagePath(uri, null,activity);
        } else if ("file".equalsIgnoreCase(uri.getScheme())) {
            imagePath = uri.getPath();
        }
        return imagePath;
    }


    public static String handleImageBeforeKitKat(Activity activity,Intent data){
        Uri uri=data.getData();
        return getImagePath(uri,null,activity);
    }

    public static String getImagePath(Uri uri,String selection,Activity activity){
        String path=null;
        Cursor cursor=activity.getContentResolver().query(uri,null,selection,null,null);
        if(cursor!=null){
            if(cursor.moveToFirst()){
                path=cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
            }
            cursor.close();
        }
        return path;
    }

}
