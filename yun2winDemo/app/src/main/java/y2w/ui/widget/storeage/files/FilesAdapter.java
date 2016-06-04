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

public class FilesAdapter extends BaseAdapter {
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

	public FilesAdapter(Context context, FileItemSet data) {
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
		if (mViewMode != FileManager.ViewMode.GRIDVIEW) {
			return getListViewItem(position, convertView);
		} else {
			return getGridViewItem(position, convertView);
		}
	}

	private View getListViewItem(int position, View convertView) {
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

			convertView.setTag(holder);

		} else {
			holder.img_head.setVisibility(View.VISIBLE);
			holder = (ViewHolder) convertView.getTag();
		}
		if (flagimg) {
			holder.img_head.setVisibility(View.VISIBLE);
			setImageView(holder.img_head, fileItem);
		} else {
			holder.img_head.setVisibility(View.GONE);
		}
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

	private View getGridViewItem(int position, View convertView) {
		FileItemForOperation file = mdata.getFileItems().get(position);
		FileItem fileItem = file.getFileItem();
		if (convertView == null) {
			holder = new ViewHolder();
			if (mFileFilter != null
					&& mFileFilter == FileManager.FileFilter.PICTURE) {
				convertView = mInflater.inflate(R.layout.image_grid_view_item,
						null);
				holder.img_head = (ImageView) convertView
						.findViewById(R.id.ivOfGVItem);
				holder.img_flag = (ImageView) convertView
						.findViewById(R.id.ivOfGVItem_flag);
				int w = mScreenWidth / 4;
				if (lp == null) {
					lp = new LayoutParams(w, w);
				}
				holder.img_head.setLayoutParams(lp);
			} else {
				convertView = mInflater.inflate(R.layout.grid_view_item, null);
				holder.img_head = (ImageView) convertView
						.findViewById(R.id.ivOfGVItem);
				holder.img_flag = (ImageView) convertView
						.findViewById(R.id.ivOfGVItem_flag);
			}

			holder.title = (TextView) convertView.findViewById(R.id.tvOfGVItem);
			if (!this.mShowTitle)
				holder.title.setVisibility(View.GONE);
			convertView.setTag(holder);

		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		if (fileItem.isChooser()) {
			holder.img_flag.setImageResource(R.drawable.file_choce);
		} else {
			holder.img_flag.setImageResource(R.drawable.file_unchoce);
		}

		setImageView(holder.img_head, fileItem);
		String displayName = fileItem.getFileName();
		if (fileItem.getFileSize() > 0 && mFileFilter != null
				&& mFileFilter == FileManager.FileFilter.APK) {
			TextView tvsize = (TextView) convertView
					.findViewById(R.id.tvOfGVItem_size);
			long size = fileItem.getFileSize();
			String sSize = "";
			if (size > 1000000)
				sSize = (size / 1000000) + "M";
			else if (size > 1000)
				sSize = (size / 1000) + "K";
			else
				sSize = size + "B";
			tvsize.setText(sSize);
			tvsize.setVisibility(View.VISIBLE);
			holder.title.setText(displayName);
			switch (file.getSelectState()) {
			case FileItemForOperation.SELECT_STATE_CUT:
				holder.title.setTextAppearance(mContext,
						R.style.tvInGridViewCut);
				break;
			case FileItemForOperation.SELECT_STATE_NOR:
				holder.title.setTextAppearance(mContext, R.style.tvInGridView);
				break;
			case FileItemForOperation.SELECT_STATE_SEL:
				holder.title.setTextAppearance(mContext,
						R.style.tvInGridViewSelected);
				break;
			default:
				break;
			}
		}
		// if(mFileFilter==FileFilter.PICTURE)
		// {
		// holder.img_head.setLayoutParams(new LayoutParams(
		// LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
		// }

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
			int w = mScreenWidth / 5;
			new AsyncLoadVideoicon(w, w).execute(fileItem);
		}
	}

	private final class ViewHolder {
		public ImageView img_head;
		public TextView title;
		public TextView info;
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
			FilesAdapter.this.notifyDataSetChanged();
		}

		@Override
		protected void onPostExecute(Object result) {

		}
	}

	class AsyncLoadVideoicon extends AsyncTask<FileItem, Void, Object> {
		private int width, height;

		public AsyncLoadVideoicon(int width, int height) {
			this.width = width;
			this.height = height;
		}

		@Override
		protected Object doInBackground(FileItem... params) {
			Bitmap bitmap = null;
			// 鑾峰彇瑙嗛鐨勭缉鐣ュ浘
			FileItem item = params[0];
			bitmap = ThumbnailUtils.createVideoThumbnail(item.getFilePath(),
					Thumbnails.MICRO_KIND);
			bitmap = ThumbnailUtils.extractThumbnail(bitmap, width, height,
					ThumbnailUtils.OPTIONS_RECYCLE_INPUT);
			if (bitmap != null) {
				item.setIcon(bitmap);
				publishProgress();
				return bitmap;
			} else {
				return null;
			}
		}

		@Override
		public void onProgressUpdate(Void... value) {
			FilesAdapter.this.notifyDataSetChanged();
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
			FilesAdapter.this.notifyDataSetChanged();
		}

		@Override
		protected void onPostExecute(Object result) {

		}
	}
}
