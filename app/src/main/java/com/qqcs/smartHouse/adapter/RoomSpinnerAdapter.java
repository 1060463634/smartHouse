package com.qqcs.smartHouse.adapter;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.qqcs.smartHouse.R;
import com.qqcs.smartHouse.models.RoomBean;

import java.util.List;


public class RoomSpinnerAdapter extends BaseAdapter {

    private Context mContext;
    private List<RoomBean> mlists;
    private LayoutInflater mInflater;

    public RoomSpinnerAdapter(Context context, List<RoomBean> lists) {
        mContext = context;
        mInflater = LayoutInflater.from(mContext);
        mlists = lists;
        
    }

    public void refreshData(List<RoomBean> lists) {
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
            convertView = mInflater.inflate(R.layout.item_spinner, null);
            viewHolder.text = convertView.findViewById(R.id.text);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.text.setText(mlists.get(position).getRoomName());

        return convertView;
    }

    private class ViewHolder {
        private TextView text;

    }
}
