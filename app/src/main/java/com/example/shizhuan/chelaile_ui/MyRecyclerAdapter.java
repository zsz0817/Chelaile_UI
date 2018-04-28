package com.example.shizhuan.chelaile_ui;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

/**
 * Created by ShiZhuan on 2018/4/27.
 */

public class MyRecyclerAdapter extends RecyclerView.Adapter<MyRecyclerAdapter.MyHolder> {

    private Context mContext;
    private List<Station> mDatas;
    private OnItemClickListener mItemClickListener;
    private int selectItem = -1;

    private List<Boolean> isClicks;//控件是否被点击,默认为false，如果被点击，改变值，控件根据值改变自身颜色

    public MyRecyclerAdapter(Context context, List<Station> datas) {
        super();
        this.mContext = context;
        this.mDatas = datas;
    }

    //定义一个设置点击监听器的方法
    public void setOnItemClickListener(OnItemClickListener itemClickListener) {
        this.mItemClickListener = itemClickListener;
    }

    //item的回调接口
    public interface OnItemClickListener{
        void onItemClick(View view,int Position);
    }

    @Override
    public int getItemCount() {
        // TODO Auto-generated method stub
        return mDatas.size();
    }

    public void setSelectItem(int selectItem) {
        this.selectItem = selectItem;
    }

    @Override
    // 填充onCreateViewHolder方法返回的holder中的控件
    public void onBindViewHolder(final MyHolder holder, final int position) {
        // TODO Auto-generated method stub
        holder.tv_station.setText(mDatas.get(position).getContent());
        if (selectItem == position) {
            holder.tv_tag.setSelected(true);
            holder.tv_tag.setVisibility(View.VISIBLE);
            holder.tv_station.setTextColor(Color.RED);
            holder.tv_station.setTypeface(Typeface.DEFAULT_BOLD, Typeface.BOLD);
            holder.tv_station.setTextSize(20);
        } else if (position < selectItem){
            holder.tv_tag.setSelected(false);
            holder.tv_tag.setVisibility(View.INVISIBLE);
            holder.tv_station.setTypeface(Typeface.DEFAULT, 1);
            holder.tv_station.setTextColor(Color.GRAY);
            holder.tv_station.setTextSize(15);
        }else {
            holder.tv_tag.setSelected(false);
            holder.tv_tag.setVisibility(View.INVISIBLE);
            holder.tv_station.setTypeface(Typeface.DEFAULT, 1);
            holder.tv_station.setTextColor(Color.BLACK);
            holder.tv_station.setTextSize(15);
        }
        //如果设置了回调，则设置点击事件
        if(mItemClickListener != null){
            holder.itemView.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    selectItem = position;
                    notifyDataSetChanged();
                    mItemClickListener.onItemClick(holder.itemView,position);
                }
            });
        }
    }

    @Override
    // 重写onCreateViewHolder方法，返回一个自定义的ViewHolder
    public MyHolder onCreateViewHolder(ViewGroup arg0, int arg1) {
        // 填充布局
        View view = LayoutInflater.from(mContext).inflate(R.layout.line_item, null);
        MyHolder holder = new MyHolder(view);
        return holder;
    }

    // 定义内部类继承ViewHolder
    class MyHolder extends RecyclerView.ViewHolder {

        private ImageView tv_tag;
        private TextView tv_station;

        public MyHolder(View view) {
            super(view);
            tv_tag = (ImageView) view.findViewById(R.id.tv_tag);
            tv_station = (TextView)view.findViewById(R.id.tv_station);
        }

    }

}
