package y2w.ui.fragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.webkit.MimeTypeMap;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.y2w.uikit.utils.ImagePool;

import y2w.base.AppData;
import y2w.ui.activity.ChatActivity;
import y2w.ui.adapter.SeletFileAdapter;
import y2w.ui.widget.storeage.files.FileItemForOperation;
import y2w.ui.widget.storeage.files.FileOperSet;
import y2w.ui.widget.storeage.mediacenter.filebrowser.Browser.ItemLongClick;

import com.y2w.uikit.utils.ToastUtil;
import com.yun2win.demo.R;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import y2w.common.AnimTabLayout;
import y2w.ui.widget.storeage.files.FileItem;
import y2w.ui.widget.storeage.files.FileItemSet;
import y2w.ui.widget.storeage.mediacenter.A;
import y2w.ui.widget.storeage.mediacenter.MyLayout;
import y2w.ui.widget.storeage.mediacenter.filebrowser.AppBrowser;
import y2w.ui.widget.storeage.mediacenter.filebrowser.FileBrowser;
import y2w.ui.widget.storeage.mediacenter.filebrowser.ImageFileBrowser;
import y2w.ui.widget.storeage.mediacenter.filebrowser.MusicFileBrowser;
import y2w.ui.widget.storeage.mediacenter.filebrowser.OAFileBrowser;
import y2w.ui.widget.storeage.mediacenter.filebrowser.VideoFileBrowser;

public class StoreageFileSelectFragment extends Fragment {

	//	public LeaveWordView _lwView;
	private Context _context;
	private View seletview;
	private Dialog seletdialog;
	//
	private ViewPager mViewPager;
	private MyLayout myLayout;
	private static FileBrowser mFileBrowser;
	private static MusicFileBrowser mMusicFileBrowser;
	private static VideoFileBrowser mVideoFileBrowser;
	private static ImageFileBrowser mImageFileBrowser;
	private static OAFileBrowser mOAFileBrowser;
	public static AppBrowser mAppFileBrowser;
	private List<View> mViews;
	private List<String> mTitles;
	public static int mScreenWidth;
	private AnimTabLayout mAnimTab;
	private LayoutInflater mInflater;
	private View myDocView;
	private String TAG = "";
	private BaseAdapter tabAdapter;
	private Intent intent = new Intent();
	private PagerAdapter mPagerAdapter;
	//

	// 档案
	private static FileItem fileItemTemp;
	public static LinearLayout ll_menu_maneger;
	private TextView tv_delete;
	private TextView tv_send;

	private FileItemSet fileItemSet;
	//	private ProgressDialog progressDialog;
	private Activity activity;
	public static boolean appBrowserbool = false;

	private final int SUB_MENU_TXT = Menu.FIRST + 10;
	private final int SUB_MENU_AUDIO = Menu.FIRST + 11;
	private final int SUB_MENU_VIDEO = Menu.FIRST + 12;
	private final int SUB_MENU_PIC = Menu.FIRST + 13;
	RelativeLayout rootView;
	LinearLayout pbLL;
	private List<String> fileStrings = new ArrayList<String>();

	private RelativeLayout ll_root;
	private String type = "chat";
	private boolean flagsetview = true;
	private long videomaxsize = 209715200;//200M  1024*1024*20

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {

		_context = this.getActivity().getApplicationContext();
		activity = this.getActivity();
		mInflater = inflater;
		View v = initMyDocumentView();
		return v;

	}

	@Override
	public void onResume() {
		super.onResume();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
	}

	// 初始化档案室
	private View initMyDocumentView() {
		View v = mInflater.inflate(R.layout.layout_mydocument, null);
		ll_root = (RelativeLayout) v.findViewById(R.id.rl_storeage);

		ll_menu_maneger = (LinearLayout) v
				.findViewById(R.id.ll_storage_menu_manager);
		tv_delete = (TextView) v.findViewById(R.id.tv_storage_local_delete);
		tv_send = (TextView) v.findViewById(R.id.tv_storage_file_send);
		ll_menu_maneger.setVisibility(View.VISIBLE);

		myDocView = v;

		tv_delete.setOnClickListener(new setTvDeleteClick());
		tv_send.setOnClickListener(new setTvSendClick());
		mScreenWidth = ((Activity) mInflater.getContext()).getWindowManager()
				.getDefaultDisplay().getWidth();

		initAllBrower();
		myDocument();

		return v;
	}

	private void initAllBrower() {
		mAppFileBrowser = new AppBrowser(mInflater.getContext());
		mImageFileBrowser = new ImageFileBrowser(mInflater.getContext());
		mMusicFileBrowser = new MusicFileBrowser(mInflater.getContext());
		mVideoFileBrowser = new VideoFileBrowser(mInflater.getContext());
		mOAFileBrowser = new OAFileBrowser(mInflater.getContext());

		mAppFileBrowser.setOnBrowerItemclick(new setOnBrowerItemClick());
		mImageFileBrowser.setOnBrowerItemclick(new setOnBrowerItemClick());
		mMusicFileBrowser.setOnBrowerItemclick(new setOnBrowerItemClick());
		mVideoFileBrowser.setOnBrowerItemclick(new setOnBrowerItemClick());
		mOAFileBrowser.setOnBrowerItemclick(new setOnBrowerItemClick());

	}

	/**
	 * 所有单击事件
	 */
	private class setOnBrowerItemClick implements A {
		@Override
		public void b(FileItem fileItem, String type, Drawable drawable) {
			fileItemTemp = fileItem;
			if (ll_menu_maneger.getVisibility() == View.VISIBLE) {
				if (fileItem.isChooser()) {
					AppData.getInstance().getFileItems().add(fileItem);
					if (flagsetview) {
						flagsetview = false;
						//setAnimationStart(fileItem, type, drawable);
					}
				} else {
					fileItem.setChooser(true);
					AppData.getInstance().getFileItems()
							.remove(fileItem);
					fileItem.setChooser(false);
				}
				fileSelectCountsDisplay();
			} else {
				doOpenFile(null, fileItemTemp);
			}
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
			/*if (ImageUtils.isApkFileWithSuffixName(fileItem.getExtraName())) {
				fileSendDialogShow();
			} else {*/
			intent.setDataAndType(uri, type);
			try {
				startActivityForResult(intent, 1);
			} catch (ActivityNotFoundException e) {
				// ViewEffect.showToast(this,
				// formatStr(R.string.toast_cont_open_file,fileItem.getFileName()));
				openAsDialog(fileItem).show();
			}
			//}

		} else {
			openAsDialog(fileItem).show();
		}

	}

	protected Dialog openAsDialog(final FileItem fileItem) {
		return new AlertDialog.Builder(this.getActivity())
				.setTitle(R.string.menu_open_as)
				.setItems(R.array.open_as_items,
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
												int which) {
								openAs(Menu.FIRST + 10 + which, fileItem);
							}
						}).create();
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

	private void fileSelectCountsDisplay() {
		int count = AppData.getInstance().getFileItems().size();
		tv_send.setText("已选(" + count + ")");
	}

	private class setTvSendClick implements OnClickListener {

		@Override
		public void onClick(View v) {
			// TODO档案室本地传输
			if (AppData.getInstance().getFileItems().size() > 0) {
				setseletdialog();
			} else {
				ToastUtil.ToastMessage(_context, "请选择要发送的文件");
			}
		}
	}

	private void setseletdialog() {
		seletview = LayoutInflater.from(this.getActivity()).inflate(
				R.layout.dialog_fileselect, null);
		seletdialog = new Dialog(this.getActivity());
		seletdialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		seletdialog.setContentView(seletview);
		seletdialog.setCanceledOnTouchOutside(true);
		TextView textshan = (TextView) seletview.findViewById(R.id.seletshan);
		final TextView textnum = (TextView) seletview
				.findViewById(R.id.seletxuan);
		textnum.setText("已选"
				+ AppData.getInstance().getFileItems().size() + "个");
		ListView listview = (ListView) seletview.findViewById(R.id.listselet);
		SeletFileAdapter seletfileadapter = new SeletFileAdapter(
				AppData.getInstance().getFileItems(),
				this.getActivity(), this, textnum);
		listview.setAdapter(seletfileadapter);

		textshan.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				for (int i = 0; i < AppData.getInstance()
						.getFileItems().size(); i++) {
					AppData.getInstance().getFileItems().get(i)
							.setChooser(false);
				}
				AppData.getInstance().getFileItems().clear();
				textnum.setText("已选" + 0 + "个");
				tv_send.setText("已选(" + 0 + ")");
				seletdialog.dismiss();
				refreshadapter();
			}
		});
		ViewTreeObserver vto = seletview.getViewTreeObserver();
		vto.addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
			@Override
			public void onGlobalLayout() {
				seletview.getViewTreeObserver().removeGlobalOnLayoutListener(
						this);
				int height = seletview.getMeasuredHeight();
				int width = seletview.getMeasuredWidth();
				Window dialogWindow = seletdialog.getWindow();
				WindowManager m = getActivity().getWindowManager();
				Display d = m.getDefaultDisplay();
				WindowManager.LayoutParams p = dialogWindow.getAttributes();
				if (height > d.getHeight() * 0.8) {
					p.height = (int) (d.getHeight() * 0.8);
				} else {
					p.height = height;
				}
				p.width = (int) (d.getWidth() * 0.9);
				dialogWindow.setAttributes(p);
			}
		});

		seletdialog.show();
	}

	public void setadapter(FileItem fileitem, TextView textnum) {
		fileitem.setChooser(false);
		AppData.getInstance().getFileItems().remove(fileitem);
		int size = AppData.getInstance().getFileItems().size();
		textnum.setText("已选" + size + "个");
		tv_send.setText("已选(" + size + ")");
		if (size <= 0) {
			seletdialog.dismiss();
		}
	}

	public void refreshadapter() {
		try {
			mImageFileBrowser.refreshAdapter();
			mMusicFileBrowser.refreshAdapter();
			mVideoFileBrowser.refreshAdapter();
			mOAFileBrowser.refreshAdapter();
			mAppFileBrowser.refreshAdapter();
		} catch (Exception e) {
		}
	}

	private class setTvDeleteClick implements OnClickListener {

		@Override
		public void onClick(View v) {
			// TODO档案室本地选择
			if (AppData.getInstance().getFileItems().size() > 0) {
				if ("chat".equals(type)) {
					if (AppData.getInstance().getFileItems().size() > 0
							&& AppData.getInstance().getFileItems()
							.size() <= 6) {
						List<String> fileList = new ArrayList<String>();
						for(FileItem item : AppData.getInstance().getFileItems()){
							fileList.add(item.getFilePath());
						}
						AppData.getInstance().getFileItems().clear();
						Intent intent = new Intent();
						intent.putStringArrayListExtra("result",
								(ArrayList<String>) fileList);
						activity.setResult(ChatActivity.ResultCode.CODE_FILE,intent);
						activity.finish();
					} else if (AppData.getInstance().getFileItems()
							.size() > 6) {
						ToastUtil.ToastMessage(_context, "多张图片同时发送时最多为6张");
					} else {
						ToastUtil.ToastMessage(_context, "请选择要发送的图片");
					}
				}
			} else {
				ToastUtil.ToastMessage(_context, "请选择要发送的文件");
			}
		}
	}

	private void myDocument() {
		if (mTitles == null) {
			mTitles = new ArrayList<String>();
			mTitles.add(getString(R.string.image_browser));
			mTitles.add(getString(R.string.oa_browser));
			mTitles.add(getString(R.string.app_browser));
			mTitles.add(getString(R.string.music_browser));
			mTitles.add(getString(R.string.video_browser));
		}

		TAG = "MyDocument";
		mPagerAdapter = new PagerAdapter() {

			@Override
			public boolean isViewFromObject(View arg0, Object arg1) {
				return arg0 == arg1;
			}

			@Override
			public int getCount() {
				return mTitles.size();
			}

			@Override
			public void destroyItem(View container, int position, Object object) {
				// ((ViewPager)container).removeView(mViews.get(position %
				// mViews.size()));
			}

			@Override
			public CharSequence getPageTitle(int position) {
				return mTitles.get(position);
			}

			@Override
			public Object instantiateItem(View container, int position) {
				try {
					((ViewPager) container).addView(
							mViews.get(position % mViews.size()), 0);
				} catch (Exception e) {
				}
				return mViews.get(position % mViews.size());
			}
		};
		tabAdapter = new BaseAdapter() {

			@Override
			public View getView(int position, View convertView, ViewGroup parent) {
				if (convertView == null) {
					convertView = new TextView(mInflater.getContext());
					((TextView) convertView).setMinHeight(getResources()
							.getDimensionPixelSize(R.dimen.dp_40));
					((TextView) convertView).setMinWidth(getResources()
							.getDimensionPixelSize(R.dimen.dp_70));
					((TextView) convertView).setGravity(Gravity.CENTER);
					((TextView) convertView).setTextColor(getResources()
							.getColor(R.color.doc_btn));
					((TextView) convertView).setTextSize(getResources()
							.getInteger(R.integer.eletfilesize));
				}
				((TextView) convertView).setText(mTitles.get(position));
				return convertView;
			}

			@Override
			public long getItemId(int position) {
				return position;
			}

			@Override
			public Object getItem(int position) {
				return null;
			}

			@Override
			public int getCount() {
				return mTitles.size();
			}
		};

		OnPageChangeListener pcl = new OnPageChangeListener() {

			@Override
			public void onPageSelected(int index) {
				index = index % mViews.size();
				mAnimTab.moveTo(index);

				switch (index) {
					case 0:
						mImageFileBrowser.onResume();
						break;
					case 1:
						mOAFileBrowser.onResume();
						break;
					case 2:
						mAppFileBrowser.onResume();
						break;
					case 3:
						mMusicFileBrowser.onResume();
						break;
					case 4:
						mVideoFileBrowser.onResume();
						break;
					case 5:
						// mFileBrowser.onResume();
						break;
					default:
						break;
				}
			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onPageScrollStateChanged(int arg0) {
				// TODO Auto-generated method stub

			}
		};

		AnimTabLayout.OnTabChangeListener tcl = new AnimTabLayout.OnTabChangeListener() {

			@Override
			public void tabChange(int index) {
				int curr = mViewPager.getCurrentItem();
				int realIndex = curr % mViews.size();
				int toIndex = curr + (index - realIndex);
				Log.i(TAG, "index:" + index + " curr:" + curr + " realIndex:"
						+ realIndex + " toIndex:" + toIndex);
				mViewPager.setCurrentItem(toIndex, false);
			}
		};

		((ViewGroup) myDocView).requestDisallowInterceptTouchEvent(true);
		mViewPager = (ViewPager) myDocView.findViewById(R.id.viewpager);
		mViewPager.setAdapter(mPagerAdapter);
		mViewPager.setCurrentItem(0);
		mImageFileBrowser.onResume();
		// mViewPager.setOnPageChangeListener(this);
		mViewPager.setOnPageChangeListener(pcl);

		myLayout = (MyLayout) myDocView.findViewById(R.id.mylayout);

		myLayout.setChild_viewpager(mViewPager);

		mViews = new ArrayList<View>();
		mViews.add(mImageFileBrowser.getView());
		mViews.add(mOAFileBrowser.getView());
		mViews.add(mAppFileBrowser.getView());
		mViews.add(mMusicFileBrowser.getView());
		mViews.add(mVideoFileBrowser.getView());
		// mViews.add(mFileBrowser.getView());

		mAnimTab = (AnimTabLayout) myDocView.findViewById(R.id.animTab);
		// mAnimTab.setBackgroundResource(R.drawable.topbar_bg);
		mAnimTab.setAdapter(tabAdapter);
		// mAnimTab.setOnTabChangeListener(this);
		mAnimTab.setOnTabChangeListener(tcl);
	}

	private void fileSendDialogShow() {
		appBrowserbool = false;
		new AlertDialog.Builder(_context).setTitle("提示")
				.setMessage(fileItemTemp.getFileName())
				.setPositiveButton("打开", new Listener())
				.setNegativeButton("取消", new Listener())
				.setNeutralButton("卸载", new Listener()).show();
	}

	private class Listener implements DialogInterface.OnClickListener {

		@Override
		public void onClick(DialogInterface dialogInterface, int which) {
			try {
				switch (which) {
					case -1:
						dialogInterface.dismiss();// 打开
						Intent LaunchIntent = activity.getPackageManager()
								.getLaunchIntentForPackage(
										fileItemTemp.getPackagename());
						startActivity(LaunchIntent);
						break;
					case -2:
						dialogInterface.dismiss();// 取消
						break;
					case -3:
						dialogInterface.dismiss();// 卸载
						if (!"com.rd.yun2win".equals(fileItemTemp.getPackagename())) {
							Uri packageURI = Uri.parse("package:"
									+ fileItemTemp.getPackagename());
							Intent uninstallIntent = new Intent(
									Intent.ACTION_DELETE, packageURI);
							startActivity(uninstallIntent);
							appBrowserbool = true;
						} else {
							new AlertDialog.Builder(activity)
									.setTitle("提示")
									.setMessage("亲,生命诚可贵,不可以卸载自己哟!")
									.setPositiveButton("确定",
											new DialogInterface.OnClickListener() {

												@Override
												public void onClick(
														DialogInterface dialog,
														int which) {

													dialog.dismiss();
												}
											}).show();
						}
						break;
					default:
						break;
				}
			} catch (Exception e) {

			}

		}
	}

}



