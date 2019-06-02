package com.example.map.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.example.map.R;

import java.util.ArrayList;
import java.util.List;

/**
 * 选择图片适配器
 */

public class GridImageAdapter extends
        RecyclerView.Adapter<GridImageAdapter.ViewHolder> {
    private static final int TYPE_CAMERA = 1;
    private static final int TYPE_PICTURE = 2;
    private LayoutInflater mInflater;
    private List<String> list = new ArrayList<>();
    private int selectMax = 9;
    private boolean isAddedCover=false;
    private boolean canAdd = true; //是否能够添加
    private boolean hasDelet = true; //是否有删除按钮
    private boolean hasCover = true;
    private Context context;

    private int cover = R.drawable.east_1_1;
    private int add = R.drawable.add_pic;
    /**
     * 点击添加图片跳转
     */
    private onAddPicClickListener mOnAddPicClickListener;

    public interface onAddPicClickListener {
        void onAddPicClick();
    }

    public GridImageAdapter(Context context, onAddPicClickListener mOnAddPicClickListener) {
        this.context = context;
        mInflater = LayoutInflater.from(context);
        this.mOnAddPicClickListener = mOnAddPicClickListener;
    }

    public void setSelectMax(int selectMax) {
        this.selectMax = selectMax;
    }

    public void setList(List<String> list) {
        this.list = list;
    }

    public  class ViewHolder extends RecyclerView.ViewHolder {

        ImageView mImg;
        LinearLayout ll_del;

        public ViewHolder(View view) {
            super(view);
            mImg = view.findViewById(R.id.fiv);
            ll_del = view.findViewById(R.id.ll_del);
        }
    }

    @Override
    public int getItemCount() {
        if (canAdd && list.size() < selectMax) {
            return list.size() + 1;
        } else {
            return list.size();
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (isShowAddItem(position)) {
            return TYPE_CAMERA;
        } else {
            return TYPE_PICTURE;
        }
    }

    //创建ViewHolder
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = mInflater.inflate(R.layout.item_filter_image,
                viewGroup, false);
        return  new ViewHolder(view);
    }

    private boolean isShowAddItem(int position) {
        if (!canAdd)
            return false;
        return position == list.size();
    }

    //设置值
    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, final int position) {
        //少于最大数量时张，显示继续添加的图标
        if (getItemViewType(position) == TYPE_CAMERA) {
            if (canAdd){
                if(list.size()==0)
                    viewHolder.mImg.setImageResource(cover);
                else
                    viewHolder.mImg.setImageResource(add);
                viewHolder.mImg.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mOnAddPicClickListener.onAddPicClick();
                    }
                });
                viewHolder.ll_del.setVisibility(View.INVISIBLE);
            }

        } else {
           if (hasDelet){
               viewHolder.ll_del.setVisibility(View.VISIBLE);
               viewHolder.ll_del.setOnClickListener(new View.OnClickListener() {
                   @Override
                   public void onClick(View view) {
                       int index = viewHolder.getAdapterPosition();
                       // 这里有时会返回-1造成数据下标越界,具体可参考getAdapterPosition()源码，
                       // 通过源码分析应该是bindViewHolder()暂未绘制完成导致，知道原因的也可联系我~感谢
                       if (index != RecyclerView.NO_POSITION) {
                           if(index==0 && hasCover){//为0的时候显示封面图片
                               viewHolder.mImg.setImageResource(cover);
                               viewHolder.ll_del.setVisibility(View.INVISIBLE);
                               list.set(0,"");
                               isAddedCover=false;
                           }
                           else{
                               list.remove(index);
                               notifyItemRemoved(index);
                               notifyItemRangeChanged(index, list.size());
                           }
                       }
                   }
               });
           }
           else {
               viewHolder.ll_del.setVisibility(View.INVISIBLE);
           }
            String path = list.get(position);
                RequestOptions options = new RequestOptions()
                        .centerCrop()
                        .placeholder(R.color.color_f6)
                        .diskCacheStrategy(DiskCacheStrategy.ALL);
                Glide.with(viewHolder.itemView.getContext())
                        .load(path)
                        .apply(options)
                        .into(viewHolder.mImg);

            //itemView 的点击事件
            if (mItemClickListener != null) {
                viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int adapterPosition = viewHolder.getAdapterPosition();
                        mItemClickListener.onItemClick(adapterPosition, v);
                    }
                });
            }
        }
    }

    private OnItemClickListener mItemClickListener;

    public interface OnItemClickListener {
        void onItemClick(int position, View v);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.mItemClickListener = listener;
    }

    public boolean isAddedCover() {
        return isAddedCover;
    }

    public void setAddedCover(boolean addedCover) {
        isAddedCover = addedCover;
    }
    //是否能添加图片
    public void setCanAdd(boolean canAdd){
        this.canAdd = canAdd;
    }
    //是否有右上角的X
    public void setHasDelet(boolean hasDelet){
        this.hasDelet = hasDelet;
    }

    public void setHasCover(boolean hasCover){
        if (hasCover == false)
           cover = add;
        this.hasCover = hasCover;
    }
    public void setCover(int cover){
        this.cover = R.drawable.add_pic;
    }
}