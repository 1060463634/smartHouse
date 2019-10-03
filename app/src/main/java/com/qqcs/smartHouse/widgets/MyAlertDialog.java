package com.qqcs.smartHouse.widgets;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.qqcs.smartHouse.R;


public class MyAlertDialog extends Dialog {
    private Button mConfirmButton;
    private Button mCancelButton;
    private TextView mTextTv;
    private TextView mTitleTv;

    public MyAlertDialog(Context context) {
        super(context, R.style.alert_dialog);
        setCancelable(true);
        setCanceledOnTouchOutside(false);
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.my_alert_dialog);

        mConfirmButton = (Button)findViewById(R.id.confirm_btn);
        mCancelButton = (Button)findViewById(R.id.cancel_btn);
        mTextTv = (TextView) findViewById(R.id.text_tv);
        mTitleTv = (TextView) findViewById(R.id.title_tv);
    }

    public void setTitle(String text ){
        mTitleTv.setText(text);
    }
    public void setText(String text ){
        mTextTv.setText(text);
    }
    public void setPositiveText(String text ){
        mConfirmButton.setText(text);
    }
    public void setNagtiveText(String text ){
        mCancelButton.setText(text);
    }

    public void setPositiveListener(View.OnClickListener listener ){
        mConfirmButton.setOnClickListener(listener);
    }

    public void setNegativeListener(View.OnClickListener listener ){
        mCancelButton.setOnClickListener(listener);
    }

}
