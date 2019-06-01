package com.example.map.utils;

import android.content.Context;

import com.example.map.R;

import java.util.ArrayList;
import java.util.List;

import cc.shinichi.library.ImagePreview;
import cc.shinichi.library.bean.ImageInfo;

public class ImgDetailUtil {
    public static  void startImgDetail(Context context,List<String> imgUrlList, int position){
        if(imgUrlList.size()>0){
            ImageInfo imageInfo;
            List<ImageInfo> imageInfoList = new ArrayList<>();
            for(String url:imgUrlList){
                imageInfo = new ImageInfo();
                // 原图地址（必填）
                imageInfo.setOriginUrl(url);
                // 缩略图地址（必填）
                // __如果没有缩略图url，可以将两项设置为一样。（注意：此处作为演示用，加了-1200，你们不要这么做）__
                imageInfo.setThumbnailUrl(url);
                imageInfoList.add(imageInfo);
                imageInfo = null;
            }
            ImagePreview
                    .getInstance()
                    .setContext(context)// 上下文
                    .setIndex(position)// 从第一张图片开始，索引从0开始哦
                    .setImageInfoList(imageInfoList)// 图片源
                    .setLoadStrategy(ImagePreview.LoadStrategy.AlwaysThumb)// 加载策略，见下面介绍
                    .setFolderName(context.getExternalCacheDir().getAbsolutePath())// 保存的文件夹名称，SD卡根目录
                    .setScaleLevel(1, 3, 8)// 设置三级缩放级别
                    .setZoomTransitionDuration(300)// 缩放动画时长

                    .setEnableClickClose(true)// 是否启用点击图片关闭。默认启用
                    .setEnableDragClose(true)// 是否启用上拉/下拉关闭。默认不启用

                    .setShowCloseButton(true)// 是否显示关闭页面按钮，在页面左下角。默认显示
                    .setCloseIconResId(R.drawable.ic_action_close)// 设置关闭按钮图片资源

                    .setShowDownButton(false)// 是否显示下载按钮，在页面右下角。默认显示
                    .setDownIconResId(R.drawable.icon_download_new)// 设置下载按钮图片资源

                    .setShowIndicator(true)// 设置是否显示顶部的指示器（1/9）。默认显示
                    .start();
        }
    }
}
