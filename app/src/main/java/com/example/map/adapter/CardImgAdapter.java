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

public class CardImgAdapter extends RecyclerView.Adapter<CardImgAdapter.ViewHolder>{
    private List<String> strings;
    private Context context;
    private ClickListener listener;
    public CardImgAdapter(List<String> strings, Context context){
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
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onclick(position);
            }
        });

    }

    @Override
    public int getItemCount() {
        return strings.size();
    }

    @NonNull
    @Override
    public CardImgAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View view= LayoutInflater.from(context)
                .inflate(R.layout.item_notelist_img,parent,false);
        return new CardImgAdapter.ViewHolder(view);
    }

    public void setListener(ClickListener listener) {
        this.listener = listener;
    }

    public interface ClickListener{
        void onclick(int position);
    }

}
