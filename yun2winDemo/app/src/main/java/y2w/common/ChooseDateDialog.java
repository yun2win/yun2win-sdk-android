package y2w.common;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.TimePicker;

import com.y2w.uikit.utils.StringUtil;
import com.yun2win.demo.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

/**
 * Created by maa46 on 2016/9/18.
 */
public class ChooseDateDialog {
    private static DatePicker datePicker;
    private static TimePicker timePicker;
    private int mYear;
    private int mMonth;
    private int mDay;
    private int mHour;
    private int mMinute;
    public  void showDialog(Context context, final boolean time, String defaultTime, final Handler handler) {
        View view = View.inflate(context, R.layout.date_dialog_choose, null);
        datePicker = (DatePicker) view.findViewById(R.id.datepicker);
        timePicker = (TimePicker) view.findViewById(R.id.timepicker);
        timePicker.setIs24HourView(true);
        if(!time){
            timePicker.setVisibility(View.GONE);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            params.gravity= Gravity.CENTER_HORIZONTAL;
            datePicker.setLayoutParams(params);
        }else {
            resizePikcer(datePicker);
            resizePikcer(timePicker);
        }
        initData(time,defaultTime);
        new AlertDialog.Builder(context).setTitle("请选择日期").setView(view).setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        }).setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                SimpleDateFormat format;
                if(time){
                    format = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                }else{
                    format = new SimpleDateFormat("yyyy-MM-dd");
                }
                GregorianCalendar gregorianCalendar = new GregorianCalendar(mYear,mMonth,mDay,mHour,mMinute);
                String result = format.format(gregorianCalendar.getTime());
                Message msg = new Message();
                msg.what=4;
                msg.obj=result;
                handler.sendMessage(msg);

                dialog.dismiss();
            }
        }).create().show();
    };
    public void initData(boolean time,String defaultTime){
        if(!StringUtil.isEmpty(defaultTime)){
            String format;
            if(time){
                format = "yyyy-MM-dd HH:mm";
            }else{
                format = "yyyy-MM-dd";
            }
            SimpleDateFormat sdf = new SimpleDateFormat(format);
            try {
                Date date =sdf.parse(defaultTime);
                long mtime =date.getTime();
                mYear = date.getYear()+1900;
                mMonth = date.getMonth()+1;
                mDay = date.getDate();
                mHour = date.getHours();
                mMinute = date.getMinutes();
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
       if(mYear==0) {
           Calendar calendar = Calendar.getInstance();
           mYear = calendar.get(Calendar.YEAR);
           mMonth = calendar.get(Calendar.MONTH);
           mDay = calendar.get(Calendar.DAY_OF_MONTH);
           mHour = calendar.get(Calendar.HOUR_OF_DAY);
           mMinute = calendar.get(Calendar.MINUTE);
       }
        datePicker.init(mYear, mMonth, mDay, new DatePicker.OnDateChangedListener() {
            @Override
            public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                mYear = year;
                mMonth = monthOfYear;
                mDay = dayOfMonth;
            }
        });
        timePicker.setCurrentHour(mHour);
        timePicker.setCurrentMinute(mMinute);
        timePicker.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
            @Override
            public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
                mHour = hourOfDay;
                mMinute = minute;
            }
        });
    }
    private void resizePikcer(FrameLayout tp){
        List<NumberPicker> npList = findNumberPicker(tp);
        for(NumberPicker np:npList){
            resizeNumberPicker(np);
        }
    }

    /**
     * 得到viewGroup里面的numberpicker组件
     * @param viewGroup
     * @return
     */
    private List<NumberPicker> findNumberPicker(ViewGroup viewGroup){
        List<NumberPicker> npList = new ArrayList<NumberPicker>();
        View child = null;
        if(null != viewGroup){
            for(int i = 0; i < viewGroup.getChildCount(); i++){
                child = viewGroup.getChildAt(i);
                if(child instanceof NumberPicker){
                    npList.add((NumberPicker)child);
                }
                else if(child instanceof LinearLayout){
                    List<NumberPicker> result = findNumberPicker((ViewGroup)child);
                    if(result.size()>0){
                        return result;
                    }
                }
            }
        }
        return npList;
    }
    /*
     * 调整numberpicker大小
     */
    private void resizeNumberPicker(NumberPicker np){
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        np.setLayoutParams(params);
    }
}
