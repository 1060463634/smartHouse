package com.qqcs.smartHouse.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;


import com.qqcs.smartHouse.R;

import java.util.List;

public class MyExpandableListAdapter extends BaseExpandableListAdapter {

	private Context mContext;
	private Integer mFrom;
	private List<List<String>> mChildData;
	private List<String> mGroupData;
	private LayoutInflater mInflater;

	public MyExpandableListAdapter(Context context, List<List<String>> ChildData, List<String> GroupData) {
		this.mContext = context;
		mInflater = LayoutInflater.from(mContext);
		this.mChildData = ChildData;
		this.mGroupData = GroupData;
	}

	public void refreshData(List<List<String>> ChildData, List<String> GroupData) {
		this.mChildData = ChildData;
		this.mGroupData = GroupData;
		notifyDataSetChanged();
	}

	@Override
	public int getGroupCount() {
		// TODO Auto-generated method stub
		return mGroupData.size();
	}

	@Override
	public int getChildrenCount(int groupPosition) {
		// TODO Auto-generated method stub
		return mChildData.get(groupPosition).size();
	}

	@Override
	public Object getGroup(int groupPosition) {
		// TODO Auto-generated method stub
		return mGroupData.get(groupPosition);
	}

	@Override
	public Object getChild(int groupPosition, int childPosition) {
		// TODO Auto-generated method stub
		return mChildData.get(groupPosition).get(childPosition);
	}

	@Override
	public long getGroupId(int groupPosition) {
		// TODO Auto-generated method stub
		return groupPosition;
	}

	@Override
	public long getChildId(int groupPosition, int childPosition) {
		return childPosition;
	}

	@Override
	public boolean hasStableIds() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isChildSelectable(int arg0, int arg1) {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {

		GroupViewHolder holder = null;
		if (convertView == null) {
			holder = new GroupViewHolder();
			convertView = mInflater.inflate(R.layout.item_list, null);
			holder.tv_date = (TextView) convertView.findViewById(R.id.list_tv);
			convertView.setTag(holder);
		} else {
			holder = (GroupViewHolder) convertView.getTag();
		}

		holder.tv_date.setText(mGroupData.get(groupPosition));
		return convertView;
	}

	@Override
	public View getChildView(final int groupPosition, final int childPosition, boolean isLastChild, View convertView,
			ViewGroup parent) {
		ChildViewHolder viewHolder;
		if (null == convertView) {
			viewHolder = new ChildViewHolder();
			convertView = mInflater.inflate(R.layout.item_list, null);
			viewHolder.tv_time = (TextView) convertView.findViewById(R.id.list_tv);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ChildViewHolder) convertView.getTag();
		}

		viewHolder.tv_time.setText("ss");

		return convertView;
	}

	class ChildViewHolder {
		TextView tv_time;
		ImageView btn_delete, btn_edit;
		ImageView pill_img;
		TextView amount_tv;
		LinearLayout itemLayout;
	}

	class GroupViewHolder {
		TextView tv_date;
		ImageView group_img;
		RelativeLayout group_layout;
	}

}
