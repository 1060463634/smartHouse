package com.qqcs.smartHouse.adapter;


import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;


import com.qqcs.smartHouse.R;

import java.util.List;


/**
 * Created by Administrator on 2016/6/7.
 */
public class MyListViewAdapter extends BaseAdapter {

    private Context mContext;
    private List<String> mlists;
    private LayoutInflater mInflater;

    public MyListViewAdapter(Context context, List<String> lists) {
        mContext = context;
        mInflater = LayoutInflater.from(mContext);
        mlists = lists;
        
    }

    public void refreshData(List<String> lists) {
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
    public View getView(int position, View convertView, ViewGroup parent) {
    	Log.v("wang", "getView:"+position);
    	ViewHolder viewHolder;
        if (convertView == null) {
        	viewHolder = new ViewHolder();
            convertView = mInflater.inflate(R.layout.item_list, null);
            viewHolder.mTv = (TextView) convertView.findViewById(R.id.list_tv);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.mTv.setText(mlists.get(position));
        
        return convertView;
    }

    private class ViewHolder {
        private TextView mTv;
    }

	

}
