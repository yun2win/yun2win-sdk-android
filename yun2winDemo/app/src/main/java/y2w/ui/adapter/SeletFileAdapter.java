package y2w.ui.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.yun2win.demo.R;


import java.io.File;
import java.util.List;

import y2w.ui.fragment.StoreageFileSelectFragment;
import y2w.ui.widget.storeage.files.Config;
import y2w.ui.widget.storeage.files.FileItem;
import y2w.ui.widget.storeage.utils.Helper;

public class SeletFileAdapter extends BaseAdapter {

	public List<FileItem> seletfiles;
	private StoreageFileSelectFragment stoselect;
	private TextView textnum;
	private Context mContext;
	private Intent intent;

	public SeletFileAdapter(List<FileItem> seletfiles, Context mContext,
							StoreageFileSelectFragment stoselect, TextView textnum) {

		this.seletfiles = seletfiles;
		this.mContext = mContext;
		this.stoselect = stoselect;
		this.textnum = textnum;
	}

	@Override
	public int getCount() {
		return seletfiles.size();
	}

	@Override
	public Object getItem(int position) {
		return seletfiles.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ImageView fileicon = null;
		TextView nameTxt = null;
		ImageView seletimg = null;
		if (convertView == null) {
			convertView = View.inflate(mContext, R.layout.seletfile_layout,
					null);
		}
		FileItem fileitem = seletfiles.get(position);
		nameTxt = (TextView) convertView.findViewById(R.id.filename);
		nameTxt.setText(fileitem.getFileName());
		fileicon = (ImageView) convertView.findViewById(R.id.imgicon);
		setImageView(fileicon, fileitem);
		seletimg = (ImageView) convertView.findViewById(R.id.seletimg);
		seletimg.setTag(position);
		seletimg.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				int sign = (Integer) view.getTag();
				stoselect.setadapter(seletfiles.get(sign), textnum);
				notifyDataSetChanged();
				stoselect.refreshadapter();
			}
		});
		return convertView;
	}

	private void setImageView(ImageView iv, FileItem fileItem) {
		if (fileItem.getIcon() != null) {
			iv.setImageBitmap(fileItem.getIcon());
			return;
		}
		int iconId = fileItem.getIconId();
		if (iconId > 0 && iconId != R.drawable.file_picture) {
			iv.setImageResource(iconId);
			Bitmap bitmap = BitmapFactory.decodeResource(
					mContext.getResources(), iconId);
			fileItem.setIcon(bitmap);
		}
		if (iconId == R.drawable.file_apk) {
			new AsyncLoadApkicon().execute(fileItem);
			Bitmap bitmap = BitmapFactory.decodeResource(
					mContext.getResources(), iconId);
			fileItem.setIcon(bitmap);
		} else if (iconId == R.drawable.file_picture) {
			DisplayImageOptions options = new DisplayImageOptions.Builder()
					.showImageOnLoading(R.drawable.new_img_nom)
					.showImageForEmptyUri(null).showImageOnFail(null)
					.cacheInMemory(true).considerExifParams(true)
					.bitmapConfig(Bitmap.Config.RGB_565).build();
			getImageLoader().displayImage("file://" + fileItem.getFilePath(),
					iv, options);
		} else if (iconId == R.drawable.file_video) {
			Bitmap bitmap = BitmapFactory.decodeResource(
					mContext.getResources(), iconId);
			fileItem.setIcon(bitmap);
			iv.setImageBitmap(bitmap);
		}
	}

	private ImageLoader imageLoader;

	private ImageLoader getImageLoader() {
		try {
			if (imageLoader == null) {
				imageLoader = ImageLoader.getInstance();
				ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(
						mContext).build();
				imageLoader.init(config);
			}
		} catch (Exception e) {

		}
		return imageLoader;
	}

	class AsyncLoadApkicon extends AsyncTask<FileItem, Void, Object> {
		@Override
		protected Object doInBackground(FileItem... params) {
			String path = Config.CACHE_PATH;// MApplication.CACHE_PATH;
			File file = new File(path);
			if (!file.exists()) {
				file.mkdirs();
			} else {
				Bitmap bm;
				FileItem item = params[0];
				File thumbFile = new File(path
						+ item.getFileName().replace(".", ""));
				if (thumbFile.exists()) {
					bm = BitmapFactory.decodeFile(thumbFile.getAbsolutePath());
					item.setIcon(bm);
					publishProgress();
				} else {
					try {
						Drawable dw = Helper.showUninstallAPKIcon(mContext,
								item.getFilePath());
						if (dw != null) {
							BitmapDrawable bd = (BitmapDrawable) dw;
							bm = bd.getBitmap();
							item.setIcon(bm);
							publishProgress();
							Thread.sleep(10);
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
			return null;
		}

		@Override
		public void onProgressUpdate(Void... value) {
			SeletFileAdapter.this.notifyDataSetChanged();
		}

		@Override
		protected void onPostExecute(Object result) {

		}
	}
}
