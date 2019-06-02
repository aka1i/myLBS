package com.example.map.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.map.R;

import java.util.List;

public class EasyImgAdapter extends RecyclerView.Adapter<EasyImgAdapter.ViewHolder>{
    private List<String> strings;
    private Context context;
    public EasyImgAdapter(List<String> strings, Context context){
        this.strings=strings;
        this.context=context;
    }

    static class ViewHolder extends RecyclerView.ViewHolder{
        ImageView view;

        private ViewHolder(View view){
            super(view);
            this.view = view.findViewById(R.id.activity_body_img);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        String s = strings.get(position);
        RequestOptions options=new RequestOptions()
                .error(R.drawable.default_head)
                .placeholder(R.color.grey_1)
                .fitCenter();
        Glide.with(context).load(s)
                .apply(options)
                .into(holder.view);

    }

    @Override
    public int getItemCount() {
        return strings.size();
    }

    @NonNull
    @Override
    public EasyImgAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View view= LayoutInflater.from(context)
                .inflate(R.layout.item_notelist_img,parent,false);
        return new EasyImgAdapter.ViewHolder(view);
    }


}
