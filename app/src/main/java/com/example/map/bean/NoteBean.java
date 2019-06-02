package com.example.map.bean;

import android.os.Parcelable;

import com.avos.avoscloud.AVUser;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

public class NoteBean {
    private String id;
    private String title;
    private String detail;
    private double longitude;
    private double latitude;
    private long time;
    private int emojiId;
    private boolean hasPosition;
    private List<String> imgUrl;
    private AVUser owner;

    public NoteBean(String id, String title, String detail, double longitude, double latitude, long time, int emojiId, boolean hasPosition, List<String> imgUrl, AVUser owner) {
        this.id = id;
        this.title = title;
        this.detail = detail;
        this.longitude = longitude;
        this.latitude = latitude;
        this.time = time;
        this.emojiId = emojiId;
        this.hasPosition = hasPosition;
        this.imgUrl = imgUrl;
        this.owner = owner;
    }



    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public int getEmojiId() {
        return emojiId;
    }

    public void setEmojiId(int emojiId) {
        this.emojiId = emojiId;
    }

    public boolean isHasPosition() {
        return hasPosition;
    }

    public void setHasPosition(boolean hasPosition) {
        this.hasPosition = hasPosition;
    }

    public List<String> getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(List<String> imgUrl) {
        this.imgUrl = imgUrl;
    }

    public AVUser getOwner() {
        return owner;
    }

    public void setOwner(AVUser owner) {
        this.owner = owner;
    }
}
