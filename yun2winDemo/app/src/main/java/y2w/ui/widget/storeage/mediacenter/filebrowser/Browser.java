package y2w.ui.widget.storeage.mediacenter.filebrowser;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.provider.MediaStore.Audio;
import android.provider.MediaStore.Images;
import android.provider.MediaStore.Video;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.webkit.MimeTypeMap;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ImageView;

import com.yun2win.demo.R;

import java.io.File;

import y2w.ui.widget.storeage.files.FileItem;
import y2w.ui.widget.storeage.files.FileItemForOperation;
import y2w.ui.widget.storeage.files.FileItemSet;
import y2w.ui.widget.storeage.files.FileManager;
import y2w.ui.widget.storeage.files.FilesAdapter;
import y2w.ui.widget.storeage.files.MusicsAdapter;
import y2w.ui.widget.storeage.mediacenter.A;
import y2w.ui.widget.storeage.mediacenter.PreparedResource;

public abstract class Browser implements OnItemClickListener,
		OnItemLongClickListener, OnClickListener, OnLongClickListener,
		FileManager.OnWhichOperation, FileManager.OnFileSetUpdated {
	protected static String TAG = "";
	protected FileManager mFileManager;
	protected MusicsAdapter musicsadapter;
	protected LayoutInflater mInflater;
	protected Context mContext;
	protected FileItemSet mData;
	protected FilesAdapter mItemsAdapter;
	protected boolean pickPath = false;
	public boolean longclick = true;

	public int ScreenWidgh = 1080;
	// public static String CACHE_PATH =
	// Environment.getExternalStorageDirectory().getAbsolutePath() +
	// "/mediacenter/imgcache/";
	public PreparedResource BrowserPreparedResource;

	public FileManager.FileFilter FilterType;
	/**
     * 
     */
	protected FileManager.ViewMode mViewMode;
	protected View mView;
	protected String type;

	private final int SUB_MENU_TXT = Menu.FIRST + 10;
	private final int SUB_MENU_AUDIO = Menu.FIRST + 11;
	private final int SUB_MENU_VIDEO = Menu.FIRST + 12;
	private final int SUB_MENU_PIC = Menu.FIRST + 13;
	/**
     * 
     */
	protected PreparedResource preResource;

	public View getView() {
		return mView;
	}

	public abstract boolean onPrepareOptionsMenu(Menu menu);

	public abstract void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo);

	public abstract void onContextMenuClosed(Menu menu);

	public abstract boolean onContextItemSelected(MenuItem item);

	public abstract boolean onOptionsItemSelected(MenuItem item);

	public abstract boolean onBackPressed();

	protected Browser(Context context, String tp) {
		// ScreenWidgh=((Activity)context)
		type = tp;
		mContext = context;
		mInflater = LayoutInflater.from(context);
		preResource = new PreparedResource(context);
		BrowserPreparedResource = preResource;
		mData = new FileItemSet();
		mFileManager = new FileManager(context, mData, null, type);
		mFileManager.setOnWhichoperation(this);
		if ("music".equals(type)) {
			musicsadapter = new MusicsAdapter(mContext, mData);
		} else {
			mItemsAdapter = new FilesAdapter(mContext, mData);
		}
		// mItemsAdapter.setFilterType(FilterType.PICTURE);
	}

	protected void QueryData(File preFile, boolean clear, FileManager.FileFilter filter) {
		if (clear) {
			mData.clear();
		}
		mFileManager.setOnFileSetUpdated(this);
		mFileManager.query(preFile.getAbsolutePath(), filter);
	}

	/**
	 * refresh adapter
	 */
	protected void refreshData() {
		if ("music".equals(type)) {
			if (musicsadapter != null) {
				musicsadapter.notifyDataSetChanged();
			}
		} else {
			if (mItemsAdapter != null) {
				mItemsAdapter.notifyDataSetChanged();
			}
		}
	}

	public FileItemSet refreshDataAdapter() {
		return mData;
	}

	public void refreshToInit() {
		for (FileItemForOperation fileItemOper : mData.getFileItems()) {
			if (fileItemOper.getFileItem().isChooser()) {
				fileItemOper.getFileItem().setChooser(false);
			}
		}
		refreshData();
	}

	public void refreshFileDelete(FileItem fileItem) {
		FileItemForOperation fileItemForOperation = null;
		for (FileItemForOperation fileItemOper : mData.getFileItems()) {
			if (fileItemOper.getFileItem().getFilePath()
					.equals(fileItem.getFilePath())) {
				fileItemForOperation = fileItemOper;
				break;
			}
		}
		mData.getFileItems().remove(fileItemForOperation);
		refreshData();
	}

	protected void clickFileItem(FileItemForOperation fileItem, View view) {
		if (pickPath) {
			Intent intent = new Intent();
			Uri uri = getContentUri(fileItem.getFileItem());
			intent.setData(uri);
			Log.i(TAG, "uri:" + uri);
			((Activity) mContext).setResult(Activity.RESULT_OK, intent);
			((Activity) mContext).finish();
		} else {
			// doOpenFile(null,fileItem.getFileItem());
			if (longclick) {
				ImageView imageview = (ImageView) view
						.findViewById(R.id.ivOfGVItem_flag);
				if (fileItem.getFileItem().isChooser()) {
					fileItem.getFileItem().setChooser(false);
					imageview.setImageResource(R.drawable.file_unchoce);
				} else {
					fileItem.getFileItem().setChooser(true);
					imageview.setImageResource(R.drawable.file_choce);
				}

				// refreshData();
			}
			
			Bitmap bitmap = fileItem.getFileItem().getIcon();
			Drawable drawable =null;
			if(bitmap!=null){
			    drawable = new BitmapDrawable(bitmap);
			}
			if (a != null) {
				a.b(fileItem.getFileItem(), type, drawable);
			}
		}
	}

	A a;

	public void setOnBrowerItemclick(A a) {
		if (a != null) {
			this.a = a;
		}
	}

	/**
	 * 图片
	 * 
	 * @param fileItem
	 */
	protected void BrowerItemLongclick(FileItemForOperation fileItem) {
		longclick = true;
		if (fileItem.getFileItem().isChooser()) {
			fileItem.getFileItem().setChooser(false);
		} else {
			fileItem.getFileItem().setChooser(true);
		}
		if (click != null) {
			click.onItemLongClick(fileItem.getFileItem());
		}
		refreshData();
	}

	ItemLongClick click;

	public void setOnBrowerItemLongclick(ItemLongClick click) {
		if (click != null) {
			this.click = click;
		}
	}

	public interface ItemLongClick {
		void onItemLongClick(FileItem fileItem);
	}

	/**
	 * 控制长按模式
	 * 
	 * @param value
	 */
	public void setLongClickbool(boolean value) {
		longclick = value;
	}

	/**
	 * apk
	 * 
	 * @param fileItem
	 */
	protected void AppBrowerItemLongclick(FileItemForOperation fileItem) {
		if (click != null) {
			click.onItemLongClick(fileItem.getFileItem());
		}
	}

	public void setOnAppBrowerItemLongclick(ItemLongClick click) {
		if (click != null) {
			this.click = click;
		}
	}

	/**
	 * 文件
	 * 
	 * @param fileItem
	 */
	protected void FileBrowerItemLongclick(FileItemForOperation fileItem) {
		longclick = true;
		if (fileItem.getFileItem().isChooser()) {
			fileItem.getFileItem().setChooser(false);
		} else {
			fileItem.getFileItem().setChooser(true);
		}
		if (click != null) {
			click.onItemLongClick(fileItem.getFileItem());
		}
		refreshData();
	}

	public void setOnFileBrowerItemLongclick(ItemLongClick click) {
		if (click != null) {
			this.click = click;
		}
	}

	/**
	 * 音乐
	 * 
	 * @param fileItem
	 */
	protected void MusicBrowerItemLongclick(FileItemForOperation fileItem) {
		longclick = true;
		if (fileItem.getFileItem().isChooser()) {
			fileItem.getFileItem().setChooser(false);
		} else {
			fileItem.getFileItem().setChooser(true);
		}
		if (click != null) {
			click.onItemLongClick(fileItem.getFileItem());
		}
		refreshData();
	}

	public void setOnMusicBrowerItemLongclick(ItemLongClick click) {
		if (click != null) {
			this.click = click;
		}
	}

	/**
	 * 视频
	 * 
	 * @param fileItem
	 */
	protected void VideoBrowerItemLongclick(FileItemForOperation fileItem) {
		longclick = true;
		if (fileItem.getFileItem().isChooser()) {
			fileItem.getFileItem().setChooser(false);
		} else {
			fileItem.getFileItem().setChooser(true);
		}
		if (click != null) {
			click.onItemLongClick(fileItem.getFileItem());
		}
		refreshData();
	}

	public void setOnVideoBrowerItemLongclick(ItemLongClick click) {
		if (click != null) {
			this.click = click;
		}
	}

	/**
	 * open file depending on file type
	 */
	protected void doOpenFile(String type, FileItem fileItem) {
		Intent intent = new Intent(Intent.ACTION_VIEW);
		Uri uri = Uri.parse("file://" + fileItem.getFilePath());
		if (type == null) {
			type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(
					fileItem.getExtraName().toLowerCase());
		}
		if (type != null) {
			intent.setDataAndType(uri, type);
			try {
				((Activity) mContext).startActivityForResult(intent, 1);
			} catch (ActivityNotFoundException e) {
				// ViewEffect.showToast(this,
				// formatStr(R.string.toast_cont_open_file,fileItem.getFileName()));
				openAsDialog(fileItem).show();
			}
		} else {
			openAsDialog(fileItem).show();
		}

		/** */
	}

	private void openAs(int id, FileItem fileItem) {
		String type = null;
		switch (id) {
		case SUB_MENU_TXT:
			type = "text/plain";
			break;
		case SUB_MENU_AUDIO:
			type = "audio/*";
			break;
		case SUB_MENU_VIDEO:
			type = "video/*";
			break;
		case SUB_MENU_PIC:
			type = "image/*";
			break;
		default:
			break;
		}
		doOpenFile(type, fileItem);
	}

	protected Dialog openAsDialog(final FileItem fileItem) {
		return new AlertDialog.Builder(mContext)
				.setTitle(R.string.menu_open_as)
				.setItems(R.array.open_as_items,
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int which) {
								openAs(Menu.FIRST + 10 + which, fileItem);
							}
						}).create();
	}

	protected void toggleMode() {
		if (mViewMode == FileManager.ViewMode.LISTVIEW) {
			mViewMode = FileManager.ViewMode.GRIDVIEW;
		} else {
			mViewMode = FileManager.ViewMode.LISTVIEW;
		}
	}

	protected Uri getContentUri(FileItem item) {
		Uri uri = null;
		String type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(
				item.getExtraName().toLowerCase());
		ContentResolver cr = mContext.getContentResolver();
		if (type != null) {
			if (type.startsWith("image/")) {
				final String[] BUCKET_PROJECTION_IMAGES = new String[] { Images.ImageColumns._ID };
				final String where = Images.ImageColumns.DATA + " = '"
						+ item.getFilePath() + "'";
				final Cursor cursor = cr.query(
						Images.Media.EXTERNAL_CONTENT_URI,
						BUCKET_PROJECTION_IMAGES, where, null, null);
				if (null != cursor && cursor.moveToFirst()) {
					int mediaId = cursor.getInt(0);
					uri = ContentUris.withAppendedId(
							Images.Media.EXTERNAL_CONTENT_URI, mediaId);
				}
			} else if (type.startsWith("video/")) {
				final String[] BUCKET_PROJECTION_VIDEO = new String[] { Video.VideoColumns._ID };
				final String where = Video.VideoColumns.DATA + " = '"
						+ item.getFilePath() + "'";
				final Cursor cursor = cr.query(
						Video.Media.EXTERNAL_CONTENT_URI,
						BUCKET_PROJECTION_VIDEO, where, null, null);
				if (null != cursor && cursor.moveToFirst()) {
					int mediaId = cursor.getInt(0);
					uri = ContentUris.withAppendedId(
							Video.Media.EXTERNAL_CONTENT_URI, mediaId);
				}
			} else if (type.startsWith("audio/")) {
				final String[] BUCKET_PROJECTION_AUDIO = new String[] { Audio.AudioColumns._ID };
				final String where = Audio.AudioColumns.DATA + " = '"
						+ item.getFilePath() + "'";
				final Cursor cursor = cr.query(
						Audio.Media.EXTERNAL_CONTENT_URI,
						BUCKET_PROJECTION_AUDIO, where, null, null);
				if (null != cursor && cursor.moveToFirst()) {
					int mediaId = cursor.getInt(0);
					uri = ContentUris.withAppendedId(
							Audio.Media.EXTERNAL_CONTENT_URI, mediaId);
				}
			}
		}

		if (uri == null) {
			File file = new File(item.getFilePath());
			uri = Uri.fromFile(file);
		}
		return uri;
	}
}
