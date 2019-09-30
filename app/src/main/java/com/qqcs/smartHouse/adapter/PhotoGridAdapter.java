package com.qqcs.smartHouse.adapter;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.qqcs.smartHouse.R;
import com.qqcs.smartHouse.utils.ImageLoaderUtil;

import java.io.File;
import java.util.List;


/**
 * Created by Administrator on 2016/6/7.
 */
public class PhotoGridAdapter extends BaseAdapter {

    private Context mContext;
    private List<Integer> mlists;
    private LayoutInflater mInflater;

    public PhotoGridAdapter(Context context, List<Integer> lists) {
        mContext = context;
        mInflater = LayoutInflater.from(mContext);
        mlists = lists;

    }

    public void refreshData(List<Integer> lists) {
        mlists = lists;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return mlists.size();
    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return 0;
    }


    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = mInflater.inflate(R.layout.item_photo_list, null);
            viewHolder.mImg = convertView.findViewById(R.id.image);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }


        ImageLoaderUtil.getInstance().displayImage("drawable://"
                + mlists.get(position), viewHolder.mImg);


        return convertView;
    }

    private class ViewHolder {
        private ImageView mImg;
    }


}
