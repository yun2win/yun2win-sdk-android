package y2w.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.provider.MediaStore.Images;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.yun2win.demo.R;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import y2w.common.Constants;
import y2w.common.IntentChecker;
import y2w.common.StorageManager;
import com.y2w.uikit.utils.SystemHardwareUtil;
import y2w.ui.adapter.ImgAdapter;
import com.y2w.uikit.utils.ImageUtils;
import com.y2w.uikit.utils.ToastUtil;

/**
 * Created by hejie on 2016/3/14.
 * 图片选择界面
 */
public class ImageSendChooseActivity extends Activity {
	public static final String getPhoto = "拍照";
	private GridView mGridView;
	private List<String> listpath = new ArrayList<String>();
	private ImgAdapter imgadapter;
	public static final int TAKE_PHOTO = 1;
	public static String path = "";
	private LinearLayout linehome;
	private TextView textimgnum;
	/**可选择图片的最大张数**/
	private int maxChooseNum = 6;
	private int imgnum;
	private List<Boolean> chooses = new ArrayList<Boolean>();
	private List<String> choosepath = new ArrayList<String>();
	Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			if (msg.what == 1) {
				if (imgadapter != null) {
					imgadapter.notifyDataSetChanged();
				}
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_feelchooseimg);
		linehome = (LinearLayout) findViewById(R.id.home_lllist);
		linehome.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		imgnum = getIntent().getIntExtra("imgnum", 0);
		mGridView = (GridView) findViewById(R.id.gvImageList);
		mGridView.setNumColumns(3);
		listpath.add(getPhoto);
		chooses.add(false);
		new RefreshDataThread().start();
		imgadapter = new ImgAdapter(this, 0);
		imgadapter.setdate(listpath);
		imgadapter.setbool(chooses);

		imgadapter.setimgnum(imgnum);
		mGridView.setAdapter(imgadapter);
		textimgnum = (TextView) findViewById(R.id.imgtextnum);
		textimgnum.setClickable(false);
		imgadapter.setTextview(textimgnum);
		if (imgnum == 0) {
			textimgnum.setText("完成");
			textimgnum.setBackgroundResource(R.drawable.button_image_choose_none);
		}else if (imgnum == 10001) {
			imgnum = 0;
			maxChooseNum = 1;
			textimgnum.setText("完成");
			textimgnum.setBackgroundResource(R.drawable.button_image_choose_none);
		} else {
			textimgnum.setText("完成(" + 0 + "/" + maxChooseNum
					+ ")");
			textimgnum.setBackgroundResource(R.drawable.button_image_choose_normal);
		}
		textimgnum.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent reintent = new Intent();
				reintent.putStringArrayListExtra("result",
						(ArrayList<String>) choosepath);
				setResult(RESULT_OK, reintent);
				finish();
			}
		});
		mGridView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				if (position == 0) {
					takePhoto();
					return;
				}
				
				if (chooses != null && chooses.size() >= position) {
					ImageView imgview = (ImageView) view
							.findViewById(R.id.ivOfGVItem_flag);
					if (chooses.get(position)) {
						choosepath.remove(listpath.get(position));
						chooses.set(position, false);
						imgview.setImageResource(R.drawable.file_unchoose);
						if (choosepath.size() == 0) {
							textimgnum.setClickable(false);
							if (imgnum == 0) {
								textimgnum.setText("完成");
								textimgnum.setBackgroundResource(R.drawable.button_image_choose_none);
							} else {
								textimgnum.setText("完成(" + imgnum + "/"
										+ maxChooseNum + ")");
								textimgnum.setBackgroundResource(R.drawable.button_image_choose_normal);
							}
							
						} else {
							textimgnum.setText("完成("
									+ (imgnum + choosepath.size()) + "/"
									+ maxChooseNum + ")");
							textimgnum.setBackgroundResource(R.drawable.button_image_choose_normal);
						}
					} else {
						if ((imgnum + choosepath.size()) == maxChooseNum) {
							ToastUtil.ToastMessage(ImageSendChooseActivity.this,
									"你最多只能选择" + maxChooseNum + "张照片");
						} else {
							choosepath.add(listpath.get(position));
							chooses.set(position, true);
							imgview.setImageResource(R.drawable.file_choosed);
							textimgnum.setClickable(true);
							textimgnum.setText("完成("
									+ (imgnum + choosepath.size()) + "/"
									+ maxChooseNum + ")");
							textimgnum.setBackgroundResource(R.drawable.button_image_choose_normal);
						}
					}
					
				}
			}
		});
	}

	private void takePhoto() {
		// Checks for camera app available
		Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		if (!IntentChecker.isAvailable(this, intent,
				new String[]{PackageManager.FEATURE_CAMERA})) {
			ToastUtil.ToastMessage(
					this,
					getResources().getString(
							R.string.feature_not_available_on_this_device));
			return;
		}
		// Checks for created file validity
		File f = StorageManager.createNewAttachmentFile(this,
				Constants.MIME_TYPE_IMAGE_EXT);
		if (f == null) {
			ToastUtil.ToastMessage(this, getResources().getString(R.string.error));
			return;
		}
		path = f.getPath();
		if(!SystemHardwareUtil.isHuaweiPhoto()){
			Uri uri = Uri.fromFile(f);
			intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
		}
		startActivityForResult(intent, TAKE_PHOTO);
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent intent) {
		if (resultCode == Activity.RESULT_OK) {
			switch (requestCode) {
			case TAKE_PHOTO:
				if(SystemHardwareUtil.isHuaweiPhoto()){
					Bitmap bitmap = (Bitmap) intent.getExtras().get("data");
					try {
						ImageUtils.saveImageToSD(this, path, bitmap, 100);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				Intent reintent = new Intent();// 数据是使用Intent返回
				List<String> phtopath = new ArrayList<String>();
				phtopath.add(path);
				reintent.putStringArrayListExtra("result",
						(ArrayList<String>) phtopath);
				setResult(RESULT_OK, reintent);// 设置返回数据
				finish();
				break;
			}
		}
	}

	class RefreshDataThread extends Thread {

		public void run() {
			String volumeName = "external";
			Uri uri = Images.Media.getContentUri(volumeName); // 图片文件
			Cursor cursor = ImageSendChooseActivity.this.getContentResolver()
					.query(uri, null, null, null,
							Images.Media.DATE_MODIFIED + " DESC");
			if (cursor != null) {
				while (cursor.moveToNext()) {
					String path = cursor
							.getString(cursor
									.getColumnIndexOrThrow(Images.Media.DATA));
					long size = cursor
							.getLong(cursor
									.getColumnIndexOrThrow(Images.Media.SIZE));
					if (size > 10 * 1024) {
						int num1 = countSum(path, '/');
						if (num1 <= 10) {
							int num2 = countSum(path, '.');
							if (num2 == 1) {
								listpath.add(path);
								chooses.add(false);
							}
						}
					}
				}
				Message msg = new Message();
				if (handler != null)
					handler.sendEmptyMessage(1);
				cursor.close();
			}
		}
	}

	private int countSum(String str, char x) {
		int Count = 0;
		for (int i = 0; i < str.length(); i++) {

			char c = str.charAt(i);
			if (c == x) {
				Count++;
			}
		}
		return Count;
	}
}
