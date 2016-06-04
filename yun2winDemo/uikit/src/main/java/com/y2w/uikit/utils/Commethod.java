package com.y2w.uikit.utils;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class Commethod {
	
	public interface Action{
		void exec();
	}
	public interface Function {
		Object exec() throws Exception;
	}
	public interface ActionBack{
		void exec(String err, Object obj);
	}


	public static void setTimout(final Action action,final int time){

		 final Handler handler=new Handler(){
			 @Override
			 public void handleMessage(Message msg){
				 action.exec();
			 }
		 };

		 Thread t = new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					Thread.sleep(time);
				} catch (InterruptedException e) {
				}
				handler.sendEmptyMessage(0);
			}
		 });
		 t.start();

	}

	public static void doBackground(final Function backgroundFunction, final ActionBack back){
		final Handler handler=new Handler(){
			 @Override
			 public void handleMessage(Message msg){
				 if(msg.what==1){
					 back.exec(null,msg.obj);
				 }
				 else{
					back.exec(msg.obj.toString(),null);
				 }
			 }
		 };

		 ThreadPool.getThreadPool().executNet(new Runnable() {
				@Override
				public void run() {
					Message msg=new Message();
					try {
						Object obj=backgroundFunction.exec();
						msg.what=1;
						msg.obj=obj;
					} catch (Exception e) {
						msg.what=-1;
						msg.obj=e.getMessage();
					}
					handler.sendMessage(msg);
				}
			 });
	}

	public static String toString(Object obj){
		try{
			return obj.toString();
		}
		catch(Exception ex) {
			return "";
		}
	}
	public static boolean isNullOrEmtpy(String str){
		if(str==null || "".equals(str))
			return true;
		return false;
	}

	public static boolean equals(Object obj1, Object obj2){
		try{
			return obj1.equals(obj2);
		}
		catch(Exception ex){
			return false;
		}
	}

	public static boolean equalsIgnoreCase(String obj1, String obj2){
		try{
			return obj1.equalsIgnoreCase(obj2);
		}
		catch(Exception ex){
			return false;
		}
	}

	public static Date toDate(String dateStr){
		try{
			return dateFormater.get().parse(dateStr);
		}catch(Exception ex){
			return new Date(10,1,1);
		}
	}

	public static int getApiLevel(){

		return android.os.Build.VERSION.SDK_INT;

	}

	private final static ThreadLocal<SimpleDateFormat> dateFormater = new ThreadLocal<SimpleDateFormat>() {
		@Override
		protected SimpleDateFormat initialValue() {
			return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		}
	};
	
	private final static ThreadLocal<SimpleDateFormat> dateFormater2 = new ThreadLocal<SimpleDateFormat>() {
		@Override
		protected SimpleDateFormat initialValue() {
			return new SimpleDateFormat("yyyy-MM-dd");
		}
	};
	
	private final static ThreadLocal<SimpleDateFormat> dateFormater3 = new ThreadLocal<SimpleDateFormat>() {
		@Override
		protected SimpleDateFormat initialValue() {
			return new SimpleDateFormat("HH:mm");
		}
	};
	
	private final static ThreadLocal<SimpleDateFormat> dateFormater4 = new ThreadLocal<SimpleDateFormat>() {
		@Override
		protected SimpleDateFormat initialValue() {
			return new SimpleDateFormat("MM-dd");
		}
	};
	
	public static void hideSoftInput(Activity mActivity, EditText etMySearch){
		try{
			InputMethodManager imm = (InputMethodManager) mActivity.getSystemService(Context.INPUT_METHOD_SERVICE);  
			imm.hideSoftInputFromWindow(etMySearch.getWindowToken(), 0);
		}
		catch(Exception ex){
		}
	}
	
	public static void showSoftInput(Activity mActivity, EditText etMySearch){
		try{
			InputMethodManager imm = (InputMethodManager) mActivity.getSystemService(Context.INPUT_METHOD_SERVICE);  
			if (etMySearch.getText().length() == 0) {
				etMySearch.requestFocus();
			}
			imm.showSoftInput(etMySearch,InputMethodManager.SHOW_FORCED);  
			 
		}
		catch(Exception ex){
		}		 
	}
}
