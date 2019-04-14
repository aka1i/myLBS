package com.example.map.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.map.R;

import java.util.List;

public class DaohangAdapter extends RecyclerView.Adapter {
    private List<String> positions;
    private Context context;
    public DaohangAdapter(Context context,List<String> positions) {
        this.context = context;
        this.positions = positions;
        Log.d("21323232132", "bind: " + positions.size());
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new DaohangtHolder(LayoutInflater.from(context), viewGroup);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, final int i) {
        ((DaohangtHolder)viewHolder).bind(positions.get(i));
    }

    @Override
    public int getItemCount() {
        return positions.size();
    }

    private class DaohangtHolder extends RecyclerView.ViewHolder{
        Button button;

        public DaohangtHolder(LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.item_position_button,parent,false));
            button = itemView.findViewById(R.id.position_button);
        }

        private void bind(final String bean){
            button.setText(bean);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
          //          context.startActivity(ContactDetailActivity.newSchoolIntent(context,bean));
                }
            });
        }
    }

}
