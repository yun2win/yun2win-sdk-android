package y2w.ui.widget.emoji;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.PixelFormat;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ImageSpan;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;

import com.y2w.uikit.utils.StringUtil;

import java.io.File;
import java.lang.ref.SoftReference;
import java.util.HashMap;
import java.util.Map;

import y2w.base.AppContext;
import y2w.common.Config;
import y2w.common.Constants;
import y2w.manage.Users;
import y2w.model.Emoji;

public class Expression {
	public static String EXPR_START = "[expr_";
	public static String OTRER_START = "expr_";
	public static String EXPR_END = "]";

	public static String EMOJI_START = "[";
	public static String EMOJI_END = "]";

	private static String left = "/";
	public static int WH_0 = 56;
	public static int WH_1 = 80;
	public static int WH_2 = 140;
	public static int WH_3 = 280;
	public static int WH_4 = 400;

	public static class ExprMenu {
		private String name;
		private boolean choosebool;

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public boolean isChoosebool() {
			return choosebool;
		}

		public void setChoosebool(boolean choosebool) {
			this.choosebool = choosebool;
		}

	}

	public static int dip2px(Context context, float dipValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) ((dipValue * scale * scale + 0.5f) / 2);
	}

	public static int px2dip(Context context, float pxValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (pxValue / scale + 0.5f);
	}

	/*public static Integer[] expresion = { R.drawable.expr_1, R.drawable.expr_2,
			R.drawable.expr_3, R.drawable.expr_4, R.drawable.expr_5,
			R.drawable.expr_6, R.drawable.expr_7, R.drawable.expr_8,
			R.drawable.expr_9, R.drawable.expr_10, R.drawable.expr_11,
			R.drawable.expr_12, R.drawable.expr_13, R.drawable.expr_14,
			R.drawable.expr_15, R.drawable.expr_16, R.drawable.expr_17,
			R.drawable.expr_18, R.drawable.expr_19, R.drawable.expr_20,
			R.drawable.expr_21, R.drawable.expr_22, R.drawable.expr_23,
			R.drawable.expr_24, R.drawable.expr_25, R.drawable.expr_26,
			R.drawable.expr_27, R.drawable.expr_28, R.drawable.expr_29,
			R.drawable.expr_30, R.drawable.expr_31, R.drawable.expr_32,
			R.drawable.expr_33, R.drawable.expr_34, R.drawable.expr_35,
			R.drawable.expr_36, R.drawable.expr_37, R.drawable.expr_38,
			R.drawable.expr_39, R.drawable.expr_40 };*/

	public static String[] expresion_mean = { "微笑", "开心", "偷笑", "汗颜", "抓狂",
			"疑问", "耍酷", "发怒", "晕啊", "飞吻", "无语", "装酷", "郁闷", "白眼", "尴尬", "哭泣",
			"害羞", "惊讶", "沉默", "委屈", "期待", "奸笑", "困", "喜欢", "祈祷", "哼", "吃惊",
			"喷嚏", "鼓掌", "鄙视", "惊", "抠鼻", "剪刀", "拳头", "布", "赞", "玫瑰", "电话",
			"西瓜", "咖啡" };


	/**
	 * 获取表情显示大小
	 * @param activity
	 * @param type 会话：1；消息：2；
	 * @return
	 */
	public static int getEmojiScale(Activity activity,int type){
		int resolution = getPhoneResolution(activity);
		int value = 0;
		if(resolution <= 480){
			if(type == 1){
				value = WH_0/2;
			}else if(type == 2){
				value = WH_0;
			}else{
				value = WH_0/2;
			}
		}else if(resolution <= 960){
			if(type == 1){
				value = WH_1/2;
			}else if(type == 2){
				value = WH_1;
			}else{
				value = WH_1/2;
			}
		}else if(resolution <= 1280){
			if(type == 1){
				value = WH_1;
			}else if(type == 2){
				value = WH_2;
			}else{
				value = WH_1;
			}

		}else if(resolution <= 1920){
			if(type == 1){
				value = WH_2;
			}else if(type == 2){
				value = WH_3;
			}else{
				value = WH_2;
			}
		}else if(resolution <= 2560){
			if(type == 1){
				value = WH_3;
			}else if(type == 2){
				value = WH_4;
			}else{
				value = WH_3;
			}
		}else{
			if(type == 1){
				value = 400;
			}else if(type == 2){
				value = 600;
			}else{
				value = 400;
			}
		}
		return value;
	}

	public static int getPhoneResolution(Activity activity){
		if(activity == null){
			return 0;
		}

		WindowManager windowManager = activity.getWindowManager();
		Display display = windowManager.getDefaultDisplay();
		int screenWidth = display.getWidth();
		int screenHeight = display.getHeight();

		return screenHeight;
	}


	/**
	 * EditText or TextView 表情显示 某一控件显示,另一控件参数为null
	 * 
	 * @param context
	 * @param editText
	 * @param textView
	 * @param text
	 */
	public static void emojiDisplay(Context context,
			EditText editText, TextView textView, String text, int wh) {
		if (text != null && text.contains(EMOJI_START)) {
			SpannableString spannable = new SpannableString(text);
			text = text.replace("[", "|");
			String expre[] = text.split("\\|");
			int lengnum = 0;
			for (int i = 0; i < expre.length; i++) {
				if (!StringUtil.isEmpty(expre[i])) {
					int lastIndex = expre[i].indexOf("]");
					if(lastIndex > 0){
						String emojiName = expre[i].substring(0, lastIndex) + Constants.IMAGE_SUFFIXES_ENCRYPT ;
						String filePath = Config.CACHE_PATH_EMOJI + "base/" + emojiName;
						if (new File(filePath).exists()) {
							ImageSpan span = new ImageSpan(getDiskBitmap(filePath, wh),
									ImageSpan.ALIGN_BASELINE);
							// 开始替换，注意第2和第3个参数表示从哪里开始替换到哪里替换结束（start和end）
							// 最后一个参数类似数学中的集合,[5,12)表示从5到12，包括5但不包括12
							int length = spannable.length();
							if(lengnum + 4 > length){
								spannable.setSpan(span, lengnum, length,
										Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
							}else{
								spannable.setSpan(span, lengnum, lengnum + 4,
										Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
							}

						}
						lengnum = lengnum + expre[i].length() + 1;
					}else{
						lengnum = lengnum + expre[i].length();
					}

				} else {
					if (!StringUtil.isEmpty(expre[i])) {
						if (i == 0) {
							lengnum = lengnum + expre[i].length();
						} else {
							lengnum = lengnum + expre[i].length() + 1;
						}
					}
				}
			}
			if (editText != null) {
				editText.setText(spannable);
				editText.setSelection(text.length());
			} else {
				textView.setText(spannable);
			}
		} else {
			if (editText != null) {
				editText.setText(text);
				editText.setSelection(text.length());
			} else {
				textView.setText(text);
			}
		}
	}

	/**
	 * EditText 表情显示
	 * @param context
	 * @param editText
	 * @param text
	 */
	public static void emojiEditTextDisplay(Context context,
			EditText editText, String text, int wh) {
		int select = editText.getSelectionStart();
		Editable edit = editText.getEditableText();
		if (text != null && text.startsWith(EMOJI_START)) {
			SpannableString spannable = new SpannableString(text);
			if (text.endsWith(EMOJI_END)) {
				int lastIndex = text.indexOf("]");
				if(lastIndex > 0){
					String emojiName = text.substring(1, lastIndex)+ Constants.IMAGE_SUFFIXES_ENCRYPT;
					String filePath = Config.CACHE_PATH_EMOJI + "base/" + emojiName;
					if (new File(filePath).exists()) {
						ImageSpan span = new ImageSpan(getDiskBitmap(filePath, wh),
								ImageSpan.ALIGN_BASELINE);
						// 开始替换，注意第2和第3个参数表示从哪里开始替换到哪里替换结束（start和end）
						// 最后一个参数类似数学中的集合,[5,12)表示从5到12，包括5但不包括12
						try {
							spannable.setSpan(span, 0, text.length(),
									Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
						}catch (Exception e){

						}
						edit.insert(select, spannable);
						editText.setSelection(select + text.length());
					}
				} else {
					editText.setText(editText.getText().toString() + text);
					editText.setSelection(select + text.length());
				}
			} else {
				editText.setText(editText.getText().toString() + text);
				editText.setSelection(select + text.length());
			}
		} else {
			editText.setText(editText.getText().toString() + text);
			editText.setSelection(select + text.length());
		}
	}

	private static Map<Integer, SoftReference<Bitmap>> emosoftRef = new HashMap<Integer, SoftReference<Bitmap>>();

	// 缓存下表情图片
	private static Bitmap getBitmap(int resId, Context context,int wh) {
		Bitmap bitmap;
		SoftReference<Bitmap> ref = emosoftRef.get(resId);
		if (ref != null) {
			bitmap = ref.get();

			if (bitmap != null) {
				return bitmap;
			}
		}
		bitmap = BitmapFactory.decodeResource(context.getResources(), resId);
		bitmap = Bitmap.createScaledBitmap(bitmap, wh, wh, true);
		if (bitmap != null) {
			emosoftRef.put(resId, new SoftReference<Bitmap>(bitmap));
			return bitmap;
		}
		return bitmap;
	}

	private static Map<String, SoftReference<Bitmap>> emojiBitmaps = new HashMap<String, SoftReference<Bitmap>>();
	private static Bitmap getDiskBitmap(String pathString,int wh)
	{
		Bitmap bitmap = null;
		try
		{
			String key = pathString+wh;
			SoftReference<Bitmap> ref = emojiBitmaps.get(key);
			if (ref != null) {
				bitmap = ref.get();
				if (bitmap != null) {
					return bitmap;
				}
			}
			File file = new File(pathString);
			if(file.exists())
			{
				bitmap = BitmapFactory.decodeFile(pathString);
				bitmap = Bitmap.createScaledBitmap(bitmap, wh, wh, true);
				if (bitmap != null) {
					emojiBitmaps.put(key, new SoftReference<Bitmap>(bitmap));
					return bitmap;
				}
			}
		} catch (Exception e)
		{
		}
		return bitmap;
	}

	/**
	 * TextView 表情缩放显示
	 * 
	 * @param appContext
	 * @param textView
	 * @param text
	 * @param cut 是否截取，截取部分用...代替
	 */
	public static void messageExpressionZoomDisplay(AppContext appContext,
			TextView textView, String text,boolean cut) {
		if (text != null && text.contains(EXPR_START)) {
			int wh = 80;
			float scale = appContext.getResources().getDisplayMetrics().density;
			wh = (int) (wh * scale / 2);
			
			if(cut){
				int ind = text.indexOf(EXPR_END, 101);
				if(ind > 100){
					text = text.substring(0, ind + 1) + "...";
				}
			}
			SpannableString spannable = new SpannableString(text);
			boolean value = true;
			int start = 0;
			while (value) {
				int index = text.indexOf(EXPR_START, start);
				
				if (index >= 0) {
					if (text.length() - index > 9
							&& text.substring(index + 9, index + 10).equals(
									EXPR_END)) {
						String picindex = text.substring(index + 6, index + 9);
						int drawindex;
						try {
							drawindex = Integer.parseInt(picindex);
						} catch (Exception e) {
							drawindex = -1;
						}

					}
				} else {
					value = false;
				}
				start++;
			}
			textView.setText(spannable);
		} else {
			textView.setText(text);
		}
	}

	/**
	 * 表情中文意思替换
	 * 
	 * @param appContext
	 * @param text
	 * @return
	 */
	public static String getExpressionMean(AppContext appContext, String text) {
		String contenttemp = new String(text);
		if (text != null && text.contains(EXPR_START)) {
			text = text.replace("[", "|");
			String expre[] = text.split("\\|");
			for (int i = 0; i < expre.length; i++) {
				if (expre[i] != null && expre[i].startsWith(OTRER_START)) {
					String picindex = expre[i].substring(5, 8);
					int stringindex;
					try {
						stringindex = Integer.parseInt(picindex);
					} catch (Exception e) {
						stringindex = -1;
					}

				}
			}
			return contenttemp;
		} else {
			return contenttemp;
		}

	}

	/**
	 * 输入框，标签回删
	 * 
	 * @param editText
	 */
	public static void editTextExpressionDelete(EditText editText) {
		String string = editText.getText().toString();
		int select = editText.getSelectionEnd();
		Editable edit = editText.getEditableText();
		if (select > 0) {
			if (string.substring(select - 1, select)
					.equals(Expression.EMOJI_END)) {
				if (select >= 4
						&& string.substring(select - 4, select - 3).equals(
								Expression.EMOJI_START)) {
					edit.delete(select - 4, select);
				} else {
					edit.delete(select - 1, select);
				}
			} else {
				edit.delete(select - 1, select);
			}
		}
	}

	/**
	 * drawable 图片缩放
	 * 
	 * @param drawable
	 * @param w
	 * @param h
	 * @return
	 */
	public static Drawable zoomDrawable(Drawable drawable, int w, int h) {
		int width = drawable.getIntrinsicWidth();
		int height = drawable.getIntrinsicHeight();
		Bitmap oldbmp = drawableToBitmap(drawable);
		Matrix matrix = new Matrix();
		float scaleWidth = ((float) w / width);
		float scaleHeight = ((float) h / height);
		matrix.postScale(scaleWidth, scaleHeight);
		Bitmap newbmp = null;
		try {
			newbmp = Bitmap.createBitmap(oldbmp, 0, 0, width, height, matrix,
					true);
		} catch (Exception e) {
			newbmp = oldbmp;
		}
		return new BitmapDrawable(null, newbmp);
	}

	private static Bitmap drawableToBitmap(Drawable drawable) {
		int width = drawable.getIntrinsicWidth();
		int height = drawable.getIntrinsicHeight();
		Bitmap.Config config = drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888
				: Bitmap.Config.RGB_565;
		Bitmap bitmap = Bitmap.createBitmap(width, height, config);
		Canvas canvas = new Canvas(bitmap);
		drawable.setBounds(0, 0, width, height);
		drawable.draw(canvas);
		return bitmap;
	}

}
