package com.qqcs.smartHouse.widgets;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.qqcs.smartHouse.R;


public class SelectPictureDialog extends Dialog {

    private TextView mDemoTv;
    private TextView mTakePhotoTv;
    private TextView mSelectPhotoTv;
    private TextView mCancelTv;

    public SelectPictureDialog(Context context) {
        super(context, R.style.alert_dialog);
        setCancelable(true);
        setCanceledOnTouchOutside(false);
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.select_picture_dialog);
        mDemoTv = findViewById(R.id.demo_tv);
        mTakePhotoTv = findViewById(R.id.take_photo_tv);
        mSelectPhotoTv = findViewById(R.id.select_photo_tv);
        mCancelTv = findViewById(R.id.cancel_tv);

        mCancelTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

    }

    public void setDemoListener(View.OnClickListener listener ){
        mDemoTv.setOnClickListener(listener);
    }

    public void setTakePhotoListener(View.OnClickListener listener ){
        mTakePhotoTv.setOnClickListener(listener);
    }

    public void setSelectPhotoListener(View.OnClickListener listener ){
        mSelectPhotoTv.setOnClickListener(listener);
    }

}
