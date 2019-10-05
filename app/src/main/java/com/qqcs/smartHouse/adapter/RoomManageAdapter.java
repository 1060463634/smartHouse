package com.qqcs.smartHouse.adapter;


import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.qqcs.smartHouse.R;
import com.qqcs.smartHouse.application.Constants;
import com.qqcs.smartHouse.models.FamilyMemberBean;
import com.qqcs.smartHouse.models.RoomBean;

import java.util.List;


public class RoomManageAdapter extends BaseAdapter {

    private Context mContext;
    private List<RoomBean> mlists;
    private LayoutInflater mInflater;

    public RoomManageAdapter(Context context, List<RoomBean> lists) {
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
            convertView = mInflater.inflate(R.layout.item_room_manage_list, null);
            viewHolder.room_name_tv = convertView.findViewById(R.id.name_tv);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.room_name_tv.setText(mlists.get(position).getRoomName());

        return convertView;
    }

    private class ViewHolder {
        private TextView room_name_tv;

    }


}
