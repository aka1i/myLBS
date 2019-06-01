package com.example.map.adapter;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.map.R;
import com.example.map.utils.ImgDetailUtil;

import java.util.ArrayList;
import java.util.List;

public class IndoorPicAdapter extends RecyclerView.Adapter<IndoorPicAdapter.ViewHolder> {
    private int[] ids;
    private Context context;
    public IndoorPicAdapter(Context context, int[] ids) {
        this.context = context;
        this.ids = ids;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new ViewHolder(LayoutInflater.from(context), viewGroup);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, final int i) {
        (viewHolder).bind(ids[i],i);
    }

    @Override
    public int getItemCount() {
        return ids.length;
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        ImageView imageView;

        public ViewHolder(LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.item_img,parent,false));
            imageView = itemView.findViewById(R.id.image);
        }

        private void bind(final int id,final int position){
            imageView.setBackground(context.getDrawable(id));
            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    List<String> strings = new ArrayList<>();
                    for (int i:ids){
                        Uri uri = Uri.parse("android.resource://" + context.getPackageName() + "/" + i);
                        strings.add(uri.toString());
                    }
                    ImgDetailUtil.startImgDetail(context,strings,position);
                }
            });
        }
    }
}
