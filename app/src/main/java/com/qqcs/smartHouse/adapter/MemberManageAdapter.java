package com.qqcs.smartHouse.adapter;


import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.mcxtzhang.swipemenulib.SwipeMenuLayout;
import com.qqcs.smartHouse.R;
import com.qqcs.smartHouse.activity.FillInfomationActivity;
import com.qqcs.smartHouse.application.Constants;
import com.qqcs.smartHouse.models.FamilyMemberBean;

import java.util.List;


public class MemberManageAdapter extends BaseAdapter {

    private Context mContext;
    private List<FamilyMemberBean> mlists;
    private LayoutInflater mInflater;

    public MemberManageAdapter(Context context, List<FamilyMemberBean> lists) {
        mContext = context;
        mInflater = LayoutInflater.from(mContext);
        mlists = lists;
        
    }

    public void refreshData(List<FamilyMemberBean> lists) {
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
            convertView = mInflater.inflate(R.layout.item_member_manage_list, null);
            viewHolder.nick_name_tv = convertView.findViewById(R.id.name_tv);
            viewHolder.detail_tv = convertView.findViewById(R.id.detail_tv);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.nick_name_tv.setText(mlists.get(position).getRemarkName());
        if(TextUtils.isEmpty(mlists.get(position).getRemarkName())){
            viewHolder.nick_name_tv.setText(mlists.get(position).getNickName());
        }
        String phone = mlists.get(position).getMobile();
        String roleStr = "";
        String role = mlists.get(position).getUserRole();
        if(role.equalsIgnoreCase(Constants.ROLE_LOAD)){
            roleStr = "户主";
        } else if(role.equalsIgnoreCase(Constants.ROLE_MANAGE)){
            roleStr = "管理员";
        } else {
            roleStr = "普通成员";
        }

        viewHolder.detail_tv.setText("电话：" + phone + "   |   " + roleStr);
        return convertView;
    }

    private class ViewHolder {
        private TextView nick_name_tv;
        private TextView detail_tv;

    }


}
