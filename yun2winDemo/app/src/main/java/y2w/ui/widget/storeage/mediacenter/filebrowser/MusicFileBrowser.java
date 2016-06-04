package y2w.ui.widget.storeage.mediacenter.filebrowser;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ListView;

import com.yun2win.demo.R;

import java.io.File;

import y2w.ui.widget.storeage.files.FileItem;
import y2w.ui.widget.storeage.files.FileItemForOperation;
import y2w.ui.widget.storeage.files.FileItemSet;
import y2w.ui.widget.storeage.files.FileManager;

public class MusicFileBrowser extends Browser {
	final boolean DEBUG = false;
	static {
		TAG = MusicFileBrowser.class.getCanonicalName();

	}
	private ListView mListView;
	private boolean onResume = false;
	public float xtemp;
	public float ytemp;

	public MusicFileBrowser(Context context) {
		super(context, "music");
		initView();
		mViewMode = FileManager.ViewMode.LISTVIEW;
	}

	public void onResume() {
		if (!onResume) {
			QueryData(new File("/mnt/"), true, FileManager.FileFilter.MUSIC);
			onResume = true;
		}
	}

	private void initView() {
		mView = mInflater.inflate(R.layout.music_browser, null);
		mListView = (ListView) mView.findViewById(R.id.lvMusicList);
		mListView.setOnItemClickListener(this);
		mListView.setOnItemLongClickListener(new OnItemLongClickListener() {

			@SuppressLint("NewApi")
			@Override
			public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				/*
				 * FileItemForOperation fileItem =
				 * mData.getFileItems().get(arg2); xtemp = arg1.getX(); ytemp
				 * =arg1.getY(); fileItem.getFileItem().setXlocation(xtemp);
				 * fileItem.getFileItem().setYlocation(ytemp);
				 * MusicBrowerItemLongclick(fileItem);
				 */
				return true;
			}
		});
	}

	@Override
	public void QueryData(File preFile, boolean clear, FileManager.FileFilter filter) {
		super.QueryData(preFile, clear, filter);
		mListView.setAdapter(musicsadapter);
		musicsadapter.setflagimg(false);
	}

	@SuppressLint("NewApi")
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		FileItemForOperation fileItem = mData.getFileItems().get(position);
		xtemp = view.getX();
		ytemp = view.getY();
		fileItem.getFileItem().setXlocation(xtemp);
		fileItem.getFileItem().setYlocation(ytemp);
		clickFileItem(fileItem, view);
	}

	@Override
	public void setOnMusicBrowerItemLongclick(ItemLongClick click) {
		super.setOnMusicBrowerItemLongclick(click);
	}

	public void refreshMusicBrowser(FileItem fileItem) {
		refreshFileDelete(fileItem);
	}

	public boolean onPrepareOptionsMenu(Menu menu) {
		return true;
	}

	public boolean onOptionsItemSelected(MenuItem item) {
		return false;
	}

	@Override
	public void refreshToInit() {
		super.refreshToInit();
	}

	public void refreshAdapter() {// 刷新adapter
		refreshData();
	}

	@Override
	public FileItemSet refreshDataAdapter() {
		return super.refreshDataAdapter();
	}

	@Override
	public void setLongClickbool(boolean value) {
		super.setLongClickbool(value);
	}

	@Override
	public void onClick(View v) {

	}

	@Override
	public boolean onLongClick(View v) {
		return false;
	}

	@Override
	public void whichOperation(FileManager.FilesFor filesFor, int size) {

	}

	@Override
	public void queryFinished() {

	}

	@Override
	public void queryMatched() {
		refreshData();
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {

	}

	@Override
	public void onContextMenuClosed(Menu menu) {

	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		return false;
	}

	@Override
	public boolean onBackPressed() {
		return false;
	}

	@Override
	public boolean onItemLongClick(AdapterView<?> arg0, View arg1, int arg2,
			long arg3) {
		// TODO Auto-generated method stub
		return false;
	}
}
