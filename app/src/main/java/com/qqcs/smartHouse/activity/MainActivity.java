package com.qqcs.smartHouse.activity;

import android.os.Bundle;
import android.support.v4.app.FragmentTabHost;
import android.view.View;
import android.widget.ImageView;
import android.widget.TabHost;
import android.widget.TextView;

import com.qqcs.smartHouse.R;
import com.qqcs.smartHouse.fragment.HealthFragment;
import com.qqcs.smartHouse.fragment.HomeFragment;
import com.qqcs.smartHouse.fragment.MineFragment;
import com.qqcs.smartHouse.fragment.ScenceFragment;
import com.qqcs.smartHouse.fragment.ServiceFragment;
import com.qqcs.smartHouse.utils.ToastUtil;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends BaseActivity {


    @BindView(android.R.id.tabhost)
    FragmentTabHost mFragmentTabHost;

    private TextView mMessageNumTv;

    private long startTime;
    private String mTexts[] = {"家", "情景", "健康", "服务", "我的"};
    private Class mFragments[] = {HomeFragment.class, ScenceFragment.class,
            HealthFragment.class, ServiceFragment.class, MineFragment.class};
    private int mImages[] = {R.drawable.selector_tab_home, R.drawable.selector_tab_scence,
            R.drawable.selector_tab_health, R.drawable.selector_tab_service, R.drawable.selector_tab_mine};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        initTabHost();

    }

    private void initTabHost() {
        mFragmentTabHost.setup(this, getSupportFragmentManager(), R.id.realtabcontent);
        mFragmentTabHost.getTabWidget().setDividerDrawable(null);
        for (int i = 0; i < mTexts.length; i++) {
            TabHost.TabSpec spec = mFragmentTabHost.newTabSpec(mTexts[i]).setIndicator(getView(i));
            mFragmentTabHost.addTab(spec, mFragments[i], null);

        }
        mFragmentTabHost.setCurrentTab(0);

    }

    private View getView(int i) {
        View view = View.inflate(this, R.layout.item_tab_host, null);
        ImageView img = (ImageView) view.findViewById(R.id.tab_img);
        TextView tv = (TextView) view.findViewById(R.id.tab_tv);
        img.setImageResource(mImages[i]);
        tv.setText(mTexts[i]);

        if (i == 4) {
            mMessageNumTv = view.findViewById(R.id.unread_num_tv);
        }
        return view;
    }

    public void setmMessageNumTv(String number) {

        mMessageNumTv.setText(number);

        //如果超过99条消息显示99+
        try {
            int num = Integer.parseInt(number);
            if (num > 99) {
                mMessageNumTv.setText("99+");
            }
        } catch (Exception e) {

        }
        //如果没有消息不显示提示
        if (!number.equalsIgnoreCase("0")) {
            mMessageNumTv.setVisibility(View.VISIBLE);
        }
    }


    @Override
    public void onBackPressed() {

        long currentTime = System.currentTimeMillis();
        if ((currentTime - startTime) >= 2000) {
            ToastUtil.showToast(MainActivity.this, "再按一次退出");
            startTime = currentTime;
        } else {
            moveTaskToBack(true);
        }
    }


}
