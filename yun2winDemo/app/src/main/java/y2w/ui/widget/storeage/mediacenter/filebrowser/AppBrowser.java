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
import android.widget.GridView;

import com.yun2win.demo.R;

import y2w.ui.widget.storeage.files.FileItemForOperation;
import y2w.ui.widget.storeage.files.FileItemSet;
import y2w.ui.widget.storeage.files.FileManager.FileFilter;
import y2w.ui.widget.storeage.files.FileManager.FilesFor;
import y2w.ui.widget.storeage.files.FileManager.ViewMode;
import y2w.ui.widget.storeage.mediacenter.A;

import java.io.File;

public class AppBrowser extends Browser {
	final boolean DEBUG = false;
	static {
		TAG = AppBrowser.class.getCanonicalName();

	}
	private GridView mListView;
	public boolean onResume = false;
	public float xtemp;
	public float ytemp;

	public AppBrowser(Context context) {
		super(context, "app");
		FilterType = FileFilter.APK;
		initView();
		mViewMode = ViewMode.GRIDVIEW;
	}

	public void onResume() {
		if (!onResume) {
			QueryData(new File("/mnt/"), true, FileFilter.APK);
			onResume = true;
		}
		if (mItemsAdapter != null) {
			mItemsAdapter.notifyDataSetChanged();
		}
	}

	private void initView() {
		mView = mInflater.inflate(R.layout.apk_browser, null);
		mListView = (GridView) mView.findViewById(R.id.gvApkList);
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
				 * BrowerItemLongclick(fileItem);
				 */
				return true;
			}
		});
	}

	@Override
	public void QueryData(File preFile, boolean clear, FileFilter filter) {
		super.QueryData(preFile, clear, filter);
		mItemsAdapter.setViewMode(mViewMode);
		mItemsAdapter.setFilterType(FilterType.APK);
		mListView.setAdapter(mItemsAdapter);
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
	public void setOnAppBrowerItemLongclick(ItemLongClick click) {
		super.setOnAppBrowerItemLongclick(click);
	}

	public boolean onPrepareOptionsMenu(Menu menu) {
		return true;
	}

	public boolean onOptionsItemSelected(MenuItem item) {
		return false;
	}

	public void refreshAdapter() {// 刷新adapter
		refreshData();
	}

	@Override
	public void refreshToInit() {
		super.refreshToInit();
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
	public void setOnBrowerItemclick(A a) {
		super.setOnBrowerItemclick(a);
	}

	@Override
	public boolean onLongClick(View v) {
		return false;
	}

	@Override
	public void whichOperation(FilesFor filesFor, int size) {

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
