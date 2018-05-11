package com.example.shizhuan.chelaile_ui;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.shizhuan.chelaile_ui.entity.Station;

import java.util.List;

/**
 * Created by ShiZhuan on 2018/4/24.
 */

public class LineAdapter extends BaseAdapter {

    private int selectItem = -1;
    //印章数据  
    private List<Station> list;
    private LayoutInflater mInflater;

    public LineAdapter(Context context, List<Station> list) {
        this.list = list;
        mInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public void setSelectItem(int selectItem) {
        this.selectItem = selectItem;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = mInflater.inflate(R.layout.line_item, null);
            viewHolder.tv_station = (TextView) convertView.findViewById(R.id.tv_station);
            viewHolder.tv_tag = (ImageView) convertView.findViewById(R.id.tv_tag);
            convertView.setTag(viewHolder);
        }else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        Station kd = list.get(position);
        viewHolder.tv_station.setText(kd.getContent());
        if (selectItem == position) {
            viewHolder.tv_tag.setSelected(true);
            viewHolder.tv_tag.setVisibility(View.VISIBLE);
            viewHolder.tv_station.setTextColor(Color.RED);
            viewHolder.tv_station.setTypeface(Typeface.DEFAULT_BOLD, Typeface.BOLD);
            viewHolder.tv_station.setTextSize(20);
        } else if (position < selectItem){
            viewHolder.tv_tag.setSelected(false);
            viewHolder.tv_tag.setVisibility(View.INVISIBLE);
            viewHolder.tv_station.setTextColor(Color.GRAY);
        }else {
            viewHolder.tv_tag.setSelected(false);
            viewHolder.tv_tag.setVisibility(View.INVISIBLE);
            viewHolder.tv_station.setTextColor(Color.BLACK);
        }
        return convertView;
    }


    /**
     * 控件管理类 
     */
    private class ViewHolder {
        private TextView tv_station;
        private ImageView tv_tag;
    }
}  
