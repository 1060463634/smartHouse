package com.qqcs.smartHouse.adapter;


import android.content.Context;
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
import com.qqcs.smartHouse.models.FamilyMemberBean;

import java.util.List;



public class FillInfoAdapter extends BaseAdapter {

    private Context mContext;
    private List<FamilyMemberBean> mlists;
    private LayoutInflater mInflater;

    public FillInfoAdapter(Context context, List<FamilyMemberBean> lists) {
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
            convertView = mInflater.inflate(R.layout.item_fill_info, null);
            viewHolder.nick_name_tv = convertView.findViewById(R.id.nick_name_tv);
            viewHolder.phone_tv = convertView.findViewById(R.id.phone_tv);
            viewHolder.delete_img = convertView.findViewById(R.id.delete_img);
            viewHolder.delete_btn = convertView.findViewById(R.id.delete_btn);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        final View finalCloseView = convertView;// listView的itemView

        viewHolder.nick_name_tv.setText(mlists.get(position).getNickName());
        viewHolder.phone_tv.setText(mlists.get(position).getMobile());
        viewHolder.delete_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((SwipeMenuLayout)(finalCloseView)).quickClose();// 关闭侧滑菜单
                ((FillInfomationActivity)mContext).
                        deleteFamilyMember(mlists.get(position).getMemberId());

            }
        });

        viewHolder.delete_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((SwipeMenuLayout)(finalCloseView)).smoothExpand();
            }
        });
        
        return convertView;
    }

    private class ViewHolder {
        private TextView nick_name_tv;
        private TextView phone_tv;
        private ImageView delete_img;
        private Button delete_btn;

    }


	

}
