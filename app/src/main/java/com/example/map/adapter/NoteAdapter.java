package com.example.map.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.map.R;
import com.example.map.SPStr;
import com.example.map.activity.NoteDetailActivity;
import com.example.map.bean.NoteBean;
import com.example.map.utils.ImgDetailUtil;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import cc.shinichi.sherlockutillibrary.utility.image.ImageUtil;

public class NoteAdapter extends RecyclerView.Adapter {
    private List<NoteBean> notes;
    private Context context;

    DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    public NoteAdapter(List<NoteBean> notes, Context context) {
        this.notes = notes;
        this.context = context;

    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new EventHolder(LayoutInflater.from(context),viewGroup);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
        NoteBean note = notes.get(i);
        ((EventHolder)viewHolder).bind(note);
    }

    @Override
    public int getItemCount() {
        return notes.size();
    }

    public void setNotes(List<NoteBean> notes) {
        this.notes = notes;
    }

    private class EventHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        private TextView titleText;
        private TextView summaryText;
        private TextView timeText;
        private ImageView moodImg;
        private RecyclerView recyclerView;
        private NoteBean note;
        public EventHolder(LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.item_note,parent,false));
            itemView.setOnClickListener(this);
            titleText = itemView.findViewById(R.id.note_title);
            summaryText = itemView.findViewById(R.id.note_summary);
            timeText = itemView.findViewById(R.id.note_time);
            moodImg = itemView.findViewById(R.id.mood_img);
            recyclerView = itemView.findViewById(R.id.note_pics);
        }

        void bind(final NoteBean note){
            this.note = note;
            titleText.setText(note.getTitle());
            summaryText.setText(note.getDetail());
            timeText.setText("刻入于：" + dateFormat.format(note.getTime()));
            Uri uri = Uri.parse("android.resource://" + context.getPackageName() + "/" + note.getEmojiId());
            RequestOptions options = new RequestOptions();
            Glide.with(context).applyDefaultRequestOptions(options).load(uri).into(moodImg);
            EasyImgAdapter easyImgAdapter = new EasyImgAdapter(note.getImgUrl(),context);
            easyImgAdapter.setListener(new EasyImgAdapter.ClickListener() {
                @Override
                public void onclick(int position) {
                    ImgDetailUtil.startImgDetail(context,note.getImgUrl(),position);
                }
            });
            recyclerView.setLayoutManager(new GridLayoutManager(context,3));
            recyclerView.setAdapter(easyImgAdapter);
        }
        @Override
        public void onClick(View v) {
            Intent intent = NoteDetailActivity.newIntent(context,note);
            context.startActivity(intent);
        }
    }
}
