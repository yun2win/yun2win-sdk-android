package y2w.ui.widget.storeage.mediacenter.filebrowser;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ExpandableListView;
import android.widget.GridView;

import com.yun2win.demo.R;

import y2w.ui.widget.storeage.files.FileManager.FileFilter;
import y2w.ui.widget.storeage.files.FileManager.FilesFor;
import y2w.ui.widget.storeage.files.FileManager.ViewMode;
import y2w.ui.widget.storeage.files.FileOperSet;

import java.io.File;

public class ImageFileBrowser extends ImgBrowser {
	final boolean DEBUG = false;
	static {
		TAG = ImageFileBrowser.class.getCanonicalName();

	}
	private GridView mGridView;
	private ExpandableListView mListView;
	private boolean onResume = false;
	private final int MENU_FIRST = Menu.FIRST + 400;
	private final int MENU_SWITCH_MODE = MENU_FIRST;
	public boolean longclick = false;
	public float xtemp;
	public float ytemp;

	public ImageFileBrowser(Context context) {
		super(context, "image");
		initView();
		mViewMode = ViewMode.LISTVIEW;
	}

	public void onResume() {
		if (!onResume) {
			QueryData(new File("/mnt/"), true, FileFilter.PICTURE);
			onResume = true;
		}
	}

	private void initView() {
		mView = mInflater.inflate(R.layout.image_browser, null);
		mListView = (ExpandableListView) mView.findViewById(R.id.lvImageList);
		mListView.setOnItemClickListener(this);
		mListView.setGroupIndicator(null);
		mGridView = (GridView) mView.findViewById(R.id.gvImageList);
		mGridView.setVisibility(View.GONE);

	}

	@Override
	public void QueryData(File preFile, boolean clear, FileFilter filter) {
		super.QueryData(preFile, clear, filter);
		mListView.setAdapter(imageAdapter);
	}

	@SuppressLint("NewApi")
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		/*
		 * FileItemForOperation fileItem = mData.getFileItems().get(position);
		 * xtemp = view.getX(); ytemp = view.getY();
		 * fileItem.getFileItem().setXlocation(xtemp);
		 * fileItem.getFileItem().setYlocation(ytemp); clickFileItem(fileItem,
		 * view);
		 */
	}

	public boolean onPrepareOptionsMenu(Menu menu) {
		menu.add(
				1,
				MENU_SWITCH_MODE,
				Menu.NONE,
				mViewMode == ViewMode.LISTVIEW ? R.string.menu_mode_grid
						: R.string.menu_mode_list).setIcon(
				mViewMode == ViewMode.LISTVIEW ? R.drawable.toolbar_mode_icon
						: R.drawable.toolbar_mode_list);
		return true;
	}

	@Override
	public void setLongClickbool(boolean value) {// 设置是否长按模式
		super.setLongClickbool(value);
	}

	@Override
	public FileOperSet refreshDataAdapter() {// 刷新数据源
		return super.refreshDataAdapter();
	}

	public void refreshAdapter() {// 刷新adapter
		refreshData();
	}

	@Override
	public void refreshToInit() {// 恢复到选择前
		super.refreshToInit();
	}

	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case MENU_SWITCH_MODE:
			toggleMode();
			return true;
		default:
			break;
		}
		return false;
	}

	@Override
	public void onClick(View v) {

	}

	@Override
	public void setOnBrowerItemLongclick(ItemLongClick click) {
		super.setOnBrowerItemLongclick(click);

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
