package com.qqcs.smartHouse.adapter;


import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.qqcs.smartHouse.R;
import com.qqcs.smartHouse.application.Constants;
import com.qqcs.smartHouse.models.FamilyInfoBean;
import com.qqcs.smartHouse.utils.ImageLoaderUtil;

import java.io.File;
import java.util.List;



/**
 * Created by Administrator on 2016/6/7.
 */
public class HomeManageAdapter extends BaseAdapter {

    private Context mContext;
    private List<FamilyInfoBean> mlists;
    private LayoutInflater mInflater;

    public HomeManageAdapter(Context context, List<FamilyInfoBean> lists) {
        mContext = context;
        mInflater = LayoutInflater.from(mContext);
        mlists = lists;
        
    }

    public void refreshData(List<FamilyInfoBean> lists) {
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
            convertView = mInflater.inflate(R.layout.item_home_manage_list, null);
            viewHolder.home_img = convertView.findViewById(R.id.home_img);
            viewHolder.home_name_tv = convertView.findViewById(R.id.home_name_tv);
            viewHolder.home_master_name_tv = convertView.findViewById(R.id.home_master_name_tv);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        ImageLoaderUtil.getInstance().displayImage
                (Constants.HTTP_SERVER_DOMAIN + mlists.get(position).getImg(),viewHolder.home_img);

        viewHolder.home_name_tv.setText(mlists.get(position).getFamilyName());
        viewHolder.home_master_name_tv.setText(mlists.get(position).getHouseloadNickname());

        return convertView;
    }

    private class ViewHolder {
        private ImageView home_img;
        private TextView home_name_tv;
        private TextView home_master_name_tv;
    }


}
