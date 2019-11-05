package com.qqcs.smartHouse.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.inuker.bluetooth.library.beacon.Beacon;
import com.inuker.bluetooth.library.search.SearchResult;
import com.qqcs.smartHouse.R;
import com.qqcs.smartHouse.activity.BlueToothDetailActivity;
import com.qqcs.smartHouse.widgets.MyAlertDialog;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by dingjikerbo on 2016/9/1.
 */
public class BlueDeviceListAdapter extends BaseAdapter implements Comparator<SearchResult> {

    private Context mContext;

    private List<SearchResult> mDataList;

    public BlueDeviceListAdapter(Context context) {
        mContext = context;
        mDataList = new ArrayList<SearchResult>();
    }

    public void setDataList(List<SearchResult> datas) {
        mDataList.clear();
        mDataList.addAll(datas);
        Collections.sort(mDataList, this);
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return mDataList.size();
    }

    @Override
    public Object getItem(int position) {
        return mDataList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int compare(SearchResult lhs, SearchResult rhs) {
        return rhs.rssi - lhs.rssi;
    }

    private static class ViewHolder {
        TextView name;

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;

        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(
                    R.layout.item_blue_device_list, null, false);

            holder = new ViewHolder();
            holder.name = (TextView) convertView.findViewById(R.id.blue_device_name);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        final SearchResult result = (SearchResult) getItem(position);

//        holder.name.setText(result.getName());
//        holder.mac.setText(result.getAddress());
//        holder.rssi.setText(String.format("Rssi: %d", result.rssi));
//
//        Beacon beacon = new Beacon(result.scanRecord);
//        holder.adv.setText(beacon.toString());

        convertView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                showConfirmDialog(result.getAddress());
            }
        });

        return convertView;
    }

    private void showConfirmDialog(final String address){
        final MyAlertDialog dialog = new MyAlertDialog(mContext);
        dialog.show();
        dialog.setTitle("是否与该网关进行蓝牙连接？");
        dialog.setText("连接成功后，您可为该智能网关输入新的Wi-Fi账号和密码。");


        dialog.setPositiveListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                Intent intent = new Intent();
                intent.setClass(mContext, BlueToothDetailActivity.class);
                intent.putExtra("mac", address);
                mContext.startActivity(intent);

            }
        });

        dialog.setNegativeListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });


    }

}
