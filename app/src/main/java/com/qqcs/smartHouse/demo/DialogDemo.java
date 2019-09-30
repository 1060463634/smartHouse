package com.qqcs.smartHouse.demo;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.widget.DatePicker;
import android.widget.TextView;

import com.qqcs.smartHouse.utils.DateUtil;


@SuppressLint("NewApi")
public class DialogDemo {
	
	public static void popupCalender(Context context,final TextView mValidDayTv) throws ParseException {
		final Calendar cal = Calendar.getInstance();
		cal.setTime(new Date());
		if (!TextUtils.isEmpty(mValidDayTv.getText().toString())) {
			cal.setTime(DateUtil.string2Date(mValidDayTv.getText().toString()));
		} 
		
		final DatePickerDialog dialog = new DatePickerDialog(
				context,null, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH),cal.get(Calendar.DAY_OF_MONTH));
		dialog.setTitle("生日");
		dialog.setButton(DialogInterface.BUTTON_POSITIVE, "ok", 
				new DialogInterface.OnClickListener() {
				    @Override
					public void onClick(DialogInterface arg0, int arg1) {
				    	
				    	DatePicker datePicker = dialog.getDatePicker();  
		                int year = datePicker.getYear();  
		                int month = datePicker.getMonth();  
		                int day = datePicker.getDayOfMonth();  
		                cal.set(year, month, day);
		                mValidDayTv.setText(DateFormat.format("yyyy-MM-dd", cal));
					}
				});
		dialog.setButton(DialogInterface.BUTTON_NEGATIVE, "cancel",
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface arg0, int arg1) {
						
					}
				});
		dialog.show();
	}
	
	
	/*private void showDeleteDialog(){
        Dialog alertDialog = new AlertDialog.Builder(this).
                setTitle("确定删除？").
                setMessage("您确定删除选中图片吗？").
                setIcon(R.mipmap.left_arrow).
                setPositiveButton("确定", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        deleteSelect();
                    }
                }).
                setNegativeButton("取消", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        
                    }
                }).create();
        alertDialog.show();
        alertDialog.setOnCancelListener(new OnCancelListener() {
					
					@Override
					public void onCancel(DialogInterface dialog) {
						System.out.println("wang onCancel");
						
					}
				});
		alertDialog.setCanceledOnTouchOutside(true);
    }*/
	
	
	/*public void showPopupWindow( )
    {
        View view = LayoutInflater.from(this).inflate(R.layout.layout_popwindow_register_code, null);
        TextView content = (TextView) view.findViewById(R.id.tv_content);
        PopupWindow popupWindow=new PopupWindow(view, CommonUtil.dip2px(this,260), CommonUtil.dip2px(this,500));
        popupWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        popupWindow.setOutsideTouchable(true);
        popupWindow.setFocusable(true);

        popupWindow.showAtLocation(mProtocolTv, Gravity.CENTER, 0, 0);
    }*/
	

}
