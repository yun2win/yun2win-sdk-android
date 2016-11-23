package y2w.ui.adapter;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.webkit.MimeTypeMap;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.yun2win.demo.R;

import java.util.List;

import y2w.ui.activity.ImageSendChooseActivity;
import com.y2w.uikit.utils.ToastUtil;

public class ImgAdapter extends BaseAdapter {
	ViewHolder holder = null;
	private LayoutInflater mInflater;
	private Activity activity;
	private int mScreenWidth;
	LayoutParams lp;
	private List<String> listpath;
	List<Boolean> chooses;
	private ImageLoader imageLoader;
	private int flag;// 1为FeelSendAcitivity 0 为FeelimgchooseAcitivity
	private int imgnum;

	private TextView textnum;

	public ImgAdapter(Activity activity, int flag) {
		super();
		this.activity = activity;
		mInflater = LayoutInflater.from(this.activity);
		WindowManager windowManager = activity.getWindowManager();
		Display display = windowManager.getDefaultDisplay();
		mScreenWidth = display.getWidth();
		this.flag = flag;
	}

	public void setdate(List<String> listpath) {
		this.listpath = listpath;
	}

	public void setbool(List<Boolean> chooses) {
		this.chooses = chooses;
	}

	public void setimgnum(int imgnum) {
		this.imgnum = imgnum;
	}

	public void setTextview(TextView textnum) {
		this.textnum = textnum;
	}

	@Override
	public int getCount() {
		return listpath.size();
	}

	@Override
	public Object getItem(int position) {
		return listpath.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public void notifyDataSetChanged() {
		super.notifyDataSetChanged();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = mInflater.inflate(R.layout.feel_img_grid, null);
			holder.img_head = (ImageView) convertView
					.findViewById(R.id.ivOfGVItem);
			holder.img_flag = (ImageView) convertView
					.findViewById(R.id.ivOfGVItem_flag);
			int w = 0;
			if (flag == 0) {
				w = mScreenWidth / 3;
			} else if (flag == 1) {
				float scale = activity.getResources().getDisplayMetrics().density;
				int cha = (int) (18 * scale + 0.5f);
				w = (mScreenWidth - cha) / 6;
			}
			if (lp == null) {
				lp = new LayoutParams(w, w);
			}
			holder.img_head.setLayoutParams(lp);
			convertView.setTag(holder);

		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		holder.img_flag.setVisibility(View.GONE);
		holder.img_head.setTag(position);
		if (listpath.get(position).equals(ImageSendChooseActivity.getPhoto)) {
			holder.img_head.setImageResource(R.drawable.feelphoto);
		} else {
			if (flag == 0) {
				setImageView(holder.img_head, listpath.get(position));
				holder.img_flag.setVisibility(View.VISIBLE);
				holder.img_flag.setTag(position);
				if (chooses != null && chooses.size() > position) {
					if (chooses.get(position)) {
						holder.img_flag.setImageResource(R.drawable.file_choose);
					} else {
						holder.img_flag
								.setImageResource(R.drawable.file_unchoose);
					}
				}
			} else if (flag == 1) {
				holder.img_head.setImageURI(Uri.parse("file://"
						+ listpath.get(position)));
				holder.img_head.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						int sign = (Integer) v.getTag();
						Uri uri = Uri.parse("file://" + listpath.get(sign));
						MimeTypeMap myMime = MimeTypeMap.getSingleton();
						Intent newIntent = new Intent(
								Intent.ACTION_VIEW);
						String mimeType = myMime
								.getMimeTypeFromExtension(fileExt(
										uri.toString()).substring(1));
						newIntent.setDataAndType(uri, mimeType);
						newIntent.setFlags(newIntent.FLAG_ACTIVITY_NEW_TASK);
						if (mimeType != null) {
							activity.startActivity(newIntent);
						} else {
							ToastUtil.ToastMessage(activity, "没有找到相应的程序打开");
						}
					}
				});

			}
		}
		return convertView;
	}

	private String fileExt(String url) {
		if (url.indexOf("?") > -1) {
			url = url.substring(0, url.indexOf("?"));
		}
		if (url.lastIndexOf(".") == -1) {
			return null;
		} else {
			String ext = url.substring(url.lastIndexOf("."));
			if (ext.indexOf("%") > -1) {
				ext = ext.substring(0, ext.indexOf("%"));
			}
			if (ext.indexOf("/") > -1) {
				ext = ext.substring(0, ext.indexOf("/"));
			}
			return ext.toLowerCase();
		}
	}

	private void setImageView(ImageView iv, String path) {

		DisplayImageOptions options = new DisplayImageOptions.Builder()
				.showImageOnLoading(R.drawable.new_img_nom)
				.showImageForEmptyUri(null).showImageOnFail(null)
				.cacheInMemory(true).cacheOnDisk(true).considerExifParams(true)
				.bitmapConfig(Bitmap.Config.RGB_565).build();
		getImageLoader().displayImage("file://" + path,null, iv, options);
	}

	private ImageLoader getImageLoader() {
		try {
			if (imageLoader == null) {
				imageLoader = ImageLoader.getInstance();
				ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(
						activity).build();
				imageLoader.init(config);
			}
		} catch (Exception e) {

		}
		return imageLoader;
	}

	private final class ViewHolder {
		public ImageView img_head;
		public ImageView img_flag;
	}
}
