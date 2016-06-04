package y2w.ui.widget.storeage.files;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseExpandableListAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;


import com.yun2win.demo.R;

import y2w.ui.widget.storeage.mediacenter.A;
import y2w.ui.widget.storeage.mediacenter.filebrowser.ImgBrowser;

public class ImageAdapter extends BaseExpandableListAdapter {
	private LayoutInflater layoutInflater;
	private Context mContext;
	private FileOperSet mdata;
	public float xtemp;
	public float ytemp;
	private String type;
	private ImgBrowser imgbrowser;
	public FileManager.FileFilter FilterType;

	public ImageAdapter(Context mContext, FileOperSet mdata,
			ImgBrowser imgbrowser) {
		this.mContext = mContext;
		this.mdata = mdata;
		this.imgbrowser = imgbrowser;
		layoutInflater = LayoutInflater.from(mContext);
	}

	public Object getChild(int groupPosition, int childPosition) {
		// TODO Auto-generated method stub
		return mdata.getFileOpers().get(groupPosition).getFileItems()
				.get(childPosition);
	}

	public void setType(String type) {
		this.type = type;
	}

	@Override
	public long getChildId(int groupPosition, int childPosition) {
		return childPosition;
	}

	@Override
	public View getChildView(int groupPosition, int childPosition,
			boolean isLastChild, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = layoutInflater.inflate(R.layout.pcture_browser, null);
		}
		final int sign = groupPosition;
		GridView gridview = (GridView) convertView.findViewById(R.id.gvApkList);
		FilesAdapter mItemsAdapter = new FilesAdapter(mContext, mdata
				.getFileOpers().get(groupPosition));
		mItemsAdapter.setViewMode(FileManager.ViewMode.GRIDVIEW);
		mItemsAdapter.ShowTitle(false);
		mItemsAdapter.setFilterType(FilterType.PICTURE);
		gridview.setAdapter(mItemsAdapter);
		gridview.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				FileItemForOperation fileItem = mdata.getFileOpers().get(sign)
						.getFileItems().get(position);
				xtemp = view.getX();
				ytemp = view.getY();
				fileItem.getFileItem().setXlocation(xtemp);
				fileItem.getFileItem().setYlocation(ytemp);
				clickFileItem(fileItem, view);
			}
		});
		return convertView;
	}

	protected void clickFileItem(FileItemForOperation fileItem, View view) {
		// doOpenFile(null,fileItem.getFileItem());
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
		Bitmap bitmap = fileItem.getFileItem().getIcon();
		Drawable drawable = null;
		if (bitmap != null) {
			drawable = new BitmapDrawable(bitmap);
		}
		A a = imgbrowser.getA();
		if (a != null) {
			a.b(fileItem.getFileItem(), type, drawable);
		}
	}

	@Override
	public int getChildrenCount(int groupPosition) {
		// TODO Auto-generated method stub
		return 1;
	}

	@Override
	public Object getGroup(int groupPosition) {
		// TODO Auto-generated method stub
		return mdata.getFileOpers().get(groupPosition);
	}

	@Override
	public int getGroupCount() {
		// TODO Auto-generated method stub
		return mdata.getFileOpers().size();
	}

	@Override
	public long getGroupId(int groupPosition) {
		// TODO Auto-generated method stub
		return groupPosition;
	}

	@Override
	public View getGroupView(int groupPosition, boolean isExpanded,
			View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = layoutInflater.inflate(R.layout.img_browser, null);
		}
		final TextView opername = (TextView) convertView
				.findViewById(R.id.opername);
		final TextView opersize = (TextView) convertView
				.findViewById(R.id.opersize);
		opername.setText(mdata.getFileOpers().get(groupPosition).getOpername());
		opersize.setText("("
				+ mdata.getFileOpers().get(groupPosition).getFileItems().size()
				+ ")");
		final ImageView parentImageViw = (ImageView) convertView
				.findViewById(R.id.imgsuola);
		if (isExpanded) {
			parentImageViw.setBackgroundResource(R.drawable.img_up);
		} else {
			parentImageViw.setBackgroundResource(R.drawable.img_down);
		}
		return convertView;
	}

	@Override
	public boolean hasStableIds() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public boolean isChildSelectable(int groupPosition, int childPosition) {
		return true;
	}
}
