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
import android.widget.ListView;

import com.yun2win.demo.R;

import java.io.File;

import y2w.ui.widget.storeage.files.FileItem;
import y2w.ui.widget.storeage.files.FileItemForOperation;
import y2w.ui.widget.storeage.files.FileItemSet;
import y2w.ui.widget.storeage.files.FileManager;

public class OAFileBrowser extends Browser {
	static {
		TAG = OAFileBrowser.class.getCanonicalName();

	}
	private ListView mListView;
	private GridView mGridView;
	private boolean onResume = false;
	private final int MENU_FIRST = Menu.FIRST + 300;
	private final int MENU_SWITCH_MODE = MENU_FIRST;
	public float xtemp;
	public float ytemp;

	public OAFileBrowser(Context context) {
		super(context, "oa");
		initView();
		mViewMode = FileManager.ViewMode.LISTVIEW;
	}

	public void onResume() {
		if (!onResume) {
			QueryData(new File("/mnt/"), true, FileManager.FileFilter.OA);
			onResume = true;
		}
	}

	private void initView() {
		mView = mInflater.inflate(R.layout.video_browser, null);
		mListView = (ListView) mView.findViewById(R.id.lvVideoList);
		mListView.setOnItemClickListener(this);
		mGridView = (GridView) mView.findViewById(R.id.gvVideoList);
		mGridView.setOnItemClickListener(this);
		mGridView.setNumColumns(ScreenWidgh / 160);
		mGridView.setVisibility(View.GONE);
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
				 * VideoBrowerItemLongclick(fileItem);
				 */
				return true;
			}
		});
	}

	@Override
	public void QueryData(File preFile, boolean clear, FileManager.FileFilter filter) {
		super.QueryData(preFile, clear, filter);
		toggleViewMode();
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
	public void setOnVideoBrowerItemLongclick(ItemLongClick click) {
		super.setOnVideoBrowerItemLongclick(click);
	}

	@Override
	public void refreshToInit() {
		super.refreshToInit();
	}

	@Override
	public FileItemSet refreshDataAdapter() {
		return super.refreshDataAdapter();
	}

	public void refreshAdapter() {// 刷新adapter
		refreshData();
	}

	@Override
	public void setLongClickbool(boolean value) {
		super.setLongClickbool(value);
	}

	public void refreshOABrowser(FileItem fileItem) {
		refreshFileDelete(fileItem);
	}

	public boolean onPrepareOptionsMenu(Menu menu) {
		menu.add(
				1,
				MENU_SWITCH_MODE,
				Menu.NONE,
				mViewMode == FileManager.ViewMode.LISTVIEW ? R.string.menu_mode_grid
						: R.string.menu_mode_list).setIcon(
				mViewMode == FileManager.ViewMode.LISTVIEW ? R.drawable.toolbar_mode_icon
						: R.drawable.toolbar_mode_list);
		return true;
	}

	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case MENU_SWITCH_MODE:
			toggleMode();
			toggleViewMode();
			return true;
		default:
			break;
		}
		return false;
	}

	/**
	 * switch viewmode
	 */
	private void toggleViewMode() {
		switch (mViewMode) {
		case LISTVIEW:
			mItemsAdapter.setViewMode(FileManager.ViewMode.LISTVIEW);
			mListView.setVisibility(View.VISIBLE);
			mListView.setAdapter(mItemsAdapter);
			mGridView.setVisibility(View.GONE);
			mGridView.setAdapter(null);
			break;
		case GRIDVIEW:
			mItemsAdapter.setViewMode(FileManager.ViewMode.GRIDVIEW);
			mGridView.setVisibility(View.VISIBLE);
			mGridView.setAdapter(mItemsAdapter);
			mListView.setVisibility(View.GONE);
			mListView.setAdapter(null);
			break;
		default:
			break;
		}
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
