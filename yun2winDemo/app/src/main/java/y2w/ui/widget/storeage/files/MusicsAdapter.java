package y2w.ui.widget.storeage.files;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.ThumbnailUtils;
import android.os.AsyncTask;
import android.provider.MediaStore.Images.Thumbnails;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.yun2win.demo.R;

import java.io.File;

import y2w.ui.widget.storeage.utils.Helper;

public class MusicsAdapter extends BaseAdapter {
	private LayoutInflater mInflater;
	private FileItemSet mdata;
	private Context mContext;
	private Boolean mShowTitle = true;
	private FileManager.FileFilter mFileFilter;
	private int mScreenWidth;

	ViewHolder holder = null;
	private FileManager.ViewMode mViewMode;
	private boolean flagimg = true;

	public void setViewMode(FileManager.ViewMode mode) {
		mViewMode = mode;
	}

	public void setFilterType(FileManager.FileFilter filter) {
		mFileFilter = filter;
	}

	public void setflagimg(boolean flagimg) {
		this.flagimg = flagimg;
	}

	public MusicsAdapter(Context context, FileItemSet data) {
		super();
		this.mContext = context;
		this.mdata = data;
		mInflater = LayoutInflater.from(this.mContext);
		WindowManager windowManager = ((Activity) context).getWindowManager();
		Display display = windowManager.getDefaultDisplay();
		mScreenWidth = display.getWidth();
	}

	public void ShowTitle(Boolean show) {
		mShowTitle = show;
	}

	@Override
	public int getCount() {
		return mdata.getFileItems().size();
	}

	@Override
	public Object getItem(int position) {
		return mdata.getFileItems().get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		FileItemForOperation file = mdata.getFileItems().get(position);
		FileItem fileItem = file.getFileItem();
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = mInflater.inflate(R.layout.list_view_item_file, null);
			holder.img_head = (ImageView) convertView
					.findViewById(R.id.ivOfGVItem);
			holder.title = (TextView) convertView
					.findViewById(R.id.paraList_title);
			holder.info = (TextView) convertView
					.findViewById(R.id.paraList_size);
			holder.img_flag = (ImageView) convertView
					.findViewById(R.id.ivOfGVItem_flag);
			holder.kong = (TextView) convertView.findViewById(R.id.textkong);
			convertView.setTag(holder);

		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		holder.img_head.setVisibility(View.GONE);
		holder.kong.setVisibility(View.VISIBLE);
		String displayName = fileItem.getFileName();
		holder.title.setText(displayName);

		if (fileItem.getFileSize() != -1) {
			holder.info.setText(Helper.formatFromSize(fileItem.getFileSize()));
			holder.info.setVisibility(View.VISIBLE);
			holder.img_flag.setVisibility(View.VISIBLE);
		} else {
			holder.info.setVisibility(View.GONE);
			holder.img_flag.setVisibility(View.GONE);
		}

		if (fileItem.isChooser()) {
			holder.img_flag.setImageResource(R.drawable.file_choce);
		} else {
			holder.img_flag.setImageResource(R.drawable.file_unchoce);
		}
		/*
		 * switch (file.getSelectState()) { case
		 * FileItemForOperation.SELECT_STATE_CUT:
		 * holder.title.setTextAppearance(mContext, R.style.tvInListViewCut);
		 * break; case FileItemForOperation.SELECT_STATE_NOR:
		 * holder.title.setTextAppearance(mContext, R.style.tvInListView);
		 * break; case FileItemForOperation.SELECT_STATE_SEL:
		 * holder.title.setTextAppearance(mContext,
		 * R.style.tvInListViewSelected); break; default: break; }
		 */
		return convertView;
	}

	LayoutParams lp;

	private Bitmap getVideoThumbnail(String videoPath, int width, int height,
			int kind) {
		Bitmap bitmap = null;
		// 获取视频的缩略图
		bitmap = ThumbnailUtils.createVideoThumbnail(videoPath, kind);
		bitmap = ThumbnailUtils.extractThumbnail(bitmap, width, height,
				ThumbnailUtils.OPTIONS_RECYCLE_INPUT);
		return bitmap;
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
			getImageLoader().displayImage("file://" + fileItem.getFilePath(),null,
					iv, options);
		} else if (iconId == R.drawable.file_video) {
			int w = mScreenWidth / 5;
			Bitmap b = getVideoThumbnail(fileItem.getFilePath(), w, w,
					Thumbnails.MICRO_KIND);
			if (b != null) {
				iv.setImageBitmap(b);
				fileItem.setIcon(b);
			}
		}
	}

	private final class ViewHolder {
		public ImageView img_head;
		public TextView title;
		public TextView info;
		public TextView kong;
		public ImageView img_flag;
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

	class AsyncLoadImage extends AsyncTask<FileItem, Void, Object> {
		@Override
		protected Object doInBackground(FileItem... params) {
			String path = Config.CACHE_PATH;// MApplication.CACHE_PATH;
			File file = new File(path);
			if (!file.exists()) {
				file.mkdirs();
			} else {
				// Bitmap bitmap;
				// Bitmap newBitmap;
				FileItem item = params[0];
				// File thumbFile = new File(path +
				// item.getFileName().replace(".", ""));

				// if (thumbFile.exists()) {
				// newBitmap =
				// BitmapFactory.decodeFile(thumbFile.getAbsolutePath());
				// item.setIcon(newBitmap);
				// publishProgress();
				// }
				// else {
				// try {
				// Log.e("", "asynloadimage : " + item.getFilePath());
				// BitmapFactory.Options options = new BitmapFactory.Options();
				// options.inSampleSize = 4;
				// bitmap = BitmapFactory.decodeFile(item.getFilePath(),
				// options);
				//
				// //int w=mScreenWidth/3;
				//
				// newBitmap = ThumbnailUtils.extractThumbnail(bitmap, 200,200);
				// // bitmap.recycle();
				// if (newBitmap != null) {
				// item.setIcon(newBitmap);
				// thumbFile.createNewFile();
				// OutputStream out = new FileOutputStream(thumbFile);
				// newBitmap.compress(CompressFormat.JPEG, 100, out);
				// publishProgress();
				// Thread.sleep(100);
				// }
				// } catch (Exception e) {
				// e.printStackTrace();
				// }
				// }
			}
			return null;
		}

		@Override
		public void onProgressUpdate(Void... value) {
			MusicsAdapter.this.notifyDataSetChanged();
		}

		@Override
		protected void onPostExecute(Object result) {

		}
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
			MusicsAdapter.this.notifyDataSetChanged();
		}

		@Override
		protected void onPostExecute(Object result) {

		}
	}
}
