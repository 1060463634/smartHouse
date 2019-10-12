package com.qqcs.smartHouse.fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.qqcs.smartHouse.R;

@SuppressLint("ValidFragment")
public class SimpleCardFragment extends Fragment {

    public static SimpleCardFragment getInstance() {
        SimpleCardFragment sf = new SimpleCardFragment();
        return sf;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.item_list, null);

        return v;
    }
}