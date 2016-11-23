package y2w.ui.widget.storeage.files;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Handler;
import android.provider.MediaStore.Audio;
import android.provider.MediaStore.Files;
import android.provider.MediaStore.Files.FileColumns;
import android.provider.MediaStore.Images;
import android.provider.MediaStore.Video;
import android.util.Log;

import com.yun2win.demo.R;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import y2w.ui.widget.storeage.mediacenter.PreparedResource;

@SuppressLint("NewApi")
public class RefreshData {
	static final int FINISHED = 1;
	static final int QUERY_MATCH = 2;
	static final int QUERY_MATCH_FOR_FOLDER = 3;
	public static final int LOAD_APK_ICON_FINISHED = 4;
	Handler responsHandler;
	File folder;
	Context mContext;
	FileManager.FileFilter mFileFilter;
	private List<FileItemForOperation> mItems;
	private List<FileItemSet> fileOpers;
	private PreparedResource mPreResource;
	private String newwork;
	public static HashMap<String, String> usbIndex = new HashMap<String, String>();

	public RefreshData(Context context, Handler handler) {
		mContext = context;
		responsHandler = handler;
		newwork = context.getResources().getString(R.string.newwork);
		mPreResource = new PreparedResource(context);// MApplication.getInstance().getPreparedResource();
	}

	public void setFolder(File folder) {
		this.folder = folder;
	}

	RefreshDataThread thread;

	public void queryData(FileManager.FileFilter filter) {
		mFileFilter = filter;
		mItems = new ArrayList<FileItemForOperation>();
		fileOpers = new ArrayList<FileItemSet>();
		thread = new RefreshDataThread();
		thread.setShouldStop(false);
		thread.start();
	}

	public List<FileItemForOperation> getItems() {
		return mItems;
	}

	public List<FileItemSet> getopers() {
		return fileOpers;
	}

	public void stopGetData() {
		thread.setShouldStop(true);
	}

	class RefreshDataThread extends Thread {
		private boolean shouldStop;

		private List<File> mediaFiles = new ArrayList<File>();

		public void setShouldStop(boolean b) {
			shouldStop = b;
		}

		private void fetchMediaFiles(File folder) {
			if (folder.getAbsolutePath().split("/").length >= 6) {
				return;
			}
			File[] files = null;
			switch (mFileFilter) {
			case APK:
				files = folder.listFiles(new FilenameFilter() {
					@Override
					public boolean accept(File dir, String filename) {
						File file = new File(dir + "/" + filename);

						if (file.isDirectory()) {
							return true;
						}
						return filename.toLowerCase().endsWith(".apk");
						// return mPreResource.isAudioFile(filename.substring(
						// filename.lastIndexOf(".") + 1).toLowerCase());
					}
				});
				break;
			case MUSIC:
				files = folder.listFiles(new FilenameFilter() {
					@Override
					public boolean accept(File dir, String filename) {
						File file = new File(dir + "/" + filename);
						if (file.isDirectory()) {
							return true;
						}
						return mPreResource.isAudioFile(filename.substring(
								filename.lastIndexOf(".") + 1).toLowerCase());
					}
				});
				break;
			case VIDEO:
				files = folder.listFiles(new FilenameFilter() {
					@Override
					public boolean accept(File dir, String filename) {
						File file = new File(dir + "/" + filename);
						if (file.isDirectory()) {
							return true;
						}
						return mPreResource.isVideoFile(filename.substring(
								filename.lastIndexOf(".") + 1).toLowerCase());
					}
				});
				break;
			case OA:
				files = folder.listFiles(new FilenameFilter() {
					@Override
					public boolean accept(File dir, String filename) {
						File file = new File(dir + "/" + filename);
						if (file.isDirectory()) {
							return true;
						} else {

							return mPreResource.isOAFile(filename.substring(
									filename.lastIndexOf(".") + 1)
									.toLowerCase());
						}
					}
				});
				break;
			case PICTURE:

				files = folder.listFiles(new FilenameFilter() {
					@Override
					public boolean accept(File dir, String filename) {
						File file = new File(dir + "/" + filename);
						if (file.isDirectory()) {
							return true;
						}
						return mPreResource.isImageFile(filename.substring(
								filename.lastIndexOf(".") + 1).toLowerCase());
					}
				});
				break;
			}
			if (files != null) {
				for (File file : files) {
					if (file.isFile()) {
						mediaFiles.add(file);
					} else {
						fetchMediaFiles(file);
					}
				}
			}
		}

		public void run() {
			if (mFileFilter == FileManager.FileFilter.MUSIC) {
				String volumeName = "external";
				Uri uri = Audio.Media.getContentUri(volumeName); // 音频文件
				Cursor cursor = mContext.getContentResolver().query(uri, null,
						null, null,
						Audio.Media.DATE_MODIFIED + " DESC");
				// MediaStore.Audio.Media._ID + " DESC"
				int i = 0;
				if (cursor != null) {
					while (cursor.moveToNext()) {
						int id = cursor
								.getInt(cursor
										.getColumnIndexOrThrow(Audio.Media._ID));
						String title = cursor
								.getString(cursor
										.getColumnIndexOrThrow(Audio.Media.TITLE));
						String path = cursor
								.getString(cursor
										.getColumnIndexOrThrow(Audio.Media.DATA));
						String displayName = cursor
								.getString(cursor
										.getColumnIndexOrThrow(Audio.Media.DISPLAY_NAME));
						String mimeType = cursor
								.getString(cursor
										.getColumnIndexOrThrow(Audio.Media.MIME_TYPE));
						long size = cursor
								.getLong(cursor
										.getColumnIndexOrThrow(Audio.Media.SIZE));
						int num1 = countSum(path, '/');
						File file = new File(path);
						if (file != null && file.length() >= 5 * 1024) {
							if (num1 <= 10) {
								i++;
								getFileItem(file, i, null, "music");
							}
						}
					}
					cursor.close();
				}
				responsHandler.sendEmptyMessage(QUERY_MATCH);
			} else if (mFileFilter == FileManager.FileFilter.VIDEO) {
				String volumeName = "external";
				Uri uri = Video.Media.getContentUri(volumeName); // 视频文件
				Cursor cursor = mContext.getContentResolver().query(uri, null,
						null, null,
						Video.Media.DATE_MODIFIED + " DESC");
				int i = 0;
				if (cursor != null) {
					while (cursor.moveToNext()) {
						int id = cursor
								.getInt(cursor
										.getColumnIndexOrThrow(Video.Media._ID));
						String title = cursor
								.getString(cursor
										.getColumnIndexOrThrow(Video.Media.TITLE));
						String path = cursor
								.getString(cursor
										.getColumnIndexOrThrow(Video.Media.DATA));
						String displayName = cursor
								.getString(cursor
										.getColumnIndexOrThrow(Video.Media.DISPLAY_NAME));
						String mimeType = cursor
								.getString(cursor
										.getColumnIndexOrThrow(Video.Media.MIME_TYPE));
						long size = cursor
								.getLong(cursor
										.getColumnIndexOrThrow(Video.Media.SIZE));
						int num1 = countSum(path, '/');
						File file = new File(path);
						if (file != null && file.length() >= 5 * 1024) {
							if (num1 <= 10) {
								i++;
								getFileItem(file, i, null, "video");
							}
						}
					}
					cursor.close();
				}
				responsHandler.sendEmptyMessage(QUERY_MATCH);
			} else if (mFileFilter == FileManager.FileFilter.PICTURE) {
				String volumeName = "external";
				Uri uri = Images.Media.getContentUri(volumeName); // 图片文件
				long oneHourAgo = System.currentTimeMillis() / 1000
						- (60 * 60 * 24 * 7);
				String[] whereValues = { "" + oneHourAgo };
				Cursor cursor = mContext.getContentResolver().query(uri, null,
						Images.Media.DATE_MODIFIED + " > ?",
						whereValues,
						Images.Media.DATE_MODIFIED + " DESC");
				/*
				 * Cursor cursor = mContext.getContentResolver().query(uri,
				 * null, null, null, MediaStore.Images.Media.DATE_MODIFIED +
				 * " DESC");
				 */
				if (cursor != null) {
					while (cursor.moveToNext()) {
						int id = cursor
								.getInt(cursor
										.getColumnIndexOrThrow(Images.Media._ID));
						String title = cursor
								.getString(cursor
										.getColumnIndexOrThrow(Images.Media.TITLE));
						String path = cursor
								.getString(cursor
										.getColumnIndexOrThrow(Images.Media.DATA));
						String filename="";
						int pos = path.lastIndexOf('/');
						if (pos != -1) {
							filename= path.substring(pos + 1);
						}
						int num1 = countSum(path, '/');
						File file = new File(path);
						if (file != null && file.length() >= 5 * 1024) {
							if (num1 <= 10) {
								if (!filename.startsWith(".")) {
									getImgFileItem(file, newwork, "image");
								}
							}
						}
					}
					cursor.close();
				}
				Cursor cursor1 = mContext.getContentResolver().query(uri, null,
						null, null,
						Images.Media.DATE_MODIFIED + " DESC");
				if (cursor1 != null) {
					while (cursor1.moveToNext()) {
						String path = cursor1
								.getString(cursor1
										.getColumnIndexOrThrow(Images.Media.DATA));

						String opername = cursor1
								.getString(cursor1
										.getColumnIndexOrThrow(Images.Media.BUCKET_DISPLAY_NAME));
						String filename="";
						int pos = path.lastIndexOf('/');
						if (pos != -1) {
							filename= path.substring(pos + 1);
						}
						int num1 = countSum(path, '/');
						File file = new File(path);
						if (file != null && file.length() >= 5 * 1024) {
							if (num1 <= 10) {
								if (!filename.startsWith(".")) {
									if (opername != null
											&& !opername.startsWith("drawable")) {
										getImgFileItem(file, opername, "image");
									}
								}
							}
						}
					}
					cursor1.close();
				}
				arraylistsort();
				responsHandler.sendEmptyMessage(QUERY_MATCH);
			} else if (mFileFilter == FileManager.FileFilter.OA) {
				String volumeName = "external";
				Cursor cursor = mContext.getContentResolver().query(
						Files.getContentUri(volumeName), null,
						buildDocSelection(), null,
						FileColumns.DATE_MODIFIED + " DESC");
				int i = 0;
				if (cursor != null) {
					while (cursor.moveToNext()) {
						int id = cursor
								.getInt(cursor
										.getColumnIndexOrThrow(FileColumns._ID));
						String title = cursor
								.getString(cursor
										.getColumnIndexOrThrow(FileColumns.TITLE));
						String path = cursor
								.getString(cursor
										.getColumnIndexOrThrow(FileColumns.DATA));
						String displayName = cursor
								.getString(cursor
										.getColumnIndexOrThrow(FileColumns.DISPLAY_NAME));
						String mimeType = cursor
								.getString(cursor
										.getColumnIndexOrThrow(FileColumns.MIME_TYPE));
						long size = cursor
								.getLong(cursor
										.getColumnIndexOrThrow(FileColumns.SIZE));
						int num1 = countSum(path, '/');
						if (num1 <= 20) {
							i++;
							getFileItem(new File(path), i, null, "oa");
						}
					}
					cursor.close();
					if ((i + 1) % 20 != 0) {
						responsHandler.sendEmptyMessage(QUERY_MATCH);
					}
				}
			} else if (mFileFilter == FileManager.FileFilter.APK) {
				PackageManager pm = mContext.getPackageManager();

				List<PackageInfo> packages = pm.getInstalledPackages(0);
				for (int i = 0; i < packages.size(); i++) {

					PackageInfo packageInfo = packages.get(i);
					if (packageInfo != null
							&& (packageInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 0) {
						FileItem fileOrfolder = new FileItem();
						fileOrfolder.setDirectory(false);
						String appname = packageInfo.applicationInfo.loadLabel(
								pm).toString();
						if (appname == null || "".equals(appname)) {
							appname = packageInfo.packageName;
						}
						fileOrfolder.setFileName(appname);
						fileOrfolder.setExtraName("apk");
						fileOrfolder
								.setPackagename(packageInfo.applicationInfo.packageName);
						try {
							String path = pm.getApplicationInfo(
									packageInfo.packageName, 0).sourceDir;
							fileOrfolder.setFilePath(path);
							File file = new File(path);
							fileOrfolder.setFileSize(file.length());
						} catch (NameNotFoundException e) {
							e.printStackTrace();
							break;
						}
						fileOrfolder.setCanRead(true);
						fileOrfolder.setCanWrite(false);
						fileOrfolder.setHide(false);
						fileOrfolder.setIconId(mPreResource.getBitMap("apk"));
						fileOrfolder
								.setIcon(((BitmapDrawable) packageInfo.applicationInfo
										.loadIcon(pm)).getBitmap());

						FilePropertyAdapter propAdapter = new FilePropertyAdapter(
								mContext, fileOrfolder);
						FileItemForOperation fileItem = new FileItemForOperation();
						fileItem.setPropAdapter(propAdapter);
						fileOrfolder.setSourse("apk");
						fileItem.setFileItem(fileOrfolder);
						RefreshData.this.addFileItem(fileItem);

						if (mItems != null && mItems.size() % 20 == 0) {
							responsHandler.sendEmptyMessage(QUERY_MATCH);
						}
					}
				}
				if (mItems != null && mItems.size() % 20 != 0) {
					responsHandler.sendEmptyMessage(QUERY_MATCH);
				}
			} else if (mFileFilter == FileManager.FileFilter.ALL) {
				if (folder.exists()) {
					if (folder.isFile()) {
						responsHandler.sendEmptyMessage(FINISHED);
					} else if (folder.isDirectory()) {
						File[] files = null;
						if (mFileFilter == FileManager.FileFilter.ALL) {
							files = folder.listFiles();
						} else {
							fetchMediaFiles(folder);
							files = mediaFiles.toArray(new File[mediaFiles
									.size()]);
						}

						if (files != null) {
							Arrays.sort(files, FileManager.mComparator);
							// QuickSorter.sort(files, com);
							int i;
							for (i = 0; i < files.length && !shouldStop; i++) {
								FileItem fileOrfolder = new FileItem();
								String fileName = files[i].getName();

								String extraName = fileName.substring(fileName
										.lastIndexOf(".") + 1);
								extraName = extraName.toLowerCase();
								long size = files[i].length();
								String path = files[i].getAbsolutePath();
								boolean isDir = files[i].isDirectory();
								if (isDir) {
									extraName = "folder";
									size = -1;
									path += "/";
								}
								fileOrfolder.setDirectory(isDir);
								fileOrfolder.setFileName(fileName);
								fileOrfolder.setExtraName(extraName);
								fileOrfolder.setFilePath(path);
								fileOrfolder.setFileSize(size);
								fileOrfolder.setCanRead(files[i].canRead());
								fileOrfolder.setCanWrite(files[i].canWrite());
								fileOrfolder.setHide(files[i].isHidden());
								fileOrfolder.setIconId(mPreResource
										.getBitMap(extraName));
								fileOrfolder.setSourse("file");
								// 图片排序添加该字段
								fileOrfolder.setModifyData(files[i]
										.lastModified());

								FilePropertyAdapter propAdapter = new FilePropertyAdapter(
										mContext, fileOrfolder);
								FileItemForOperation fileItem = new FileItemForOperation();
								fileItem.setPropAdapter(propAdapter);
								fileItem.setFileItem(fileOrfolder);
								RefreshData.this.addFileItem(fileItem);
								// 每搜索40个文件，刷新一下屏幕
								if ((i + 1) % 20 == 0) {
									responsHandler
											.sendEmptyMessage(QUERY_MATCH);
								}
								/*
								 * else{ //搜索整个文件夹完毕，刷新屏幕 if(i + 1 ==
								 * files.length){ if(isFolder){
								 * responsHandler.sendEmptyMessage(
								 * QUERY_MATCH_FOR_FOLDER); }else{
								 * responsHandler.sendEmptyMessage(QUERY_MATCH);
								 * } } }
								 */
							}
							responsHandler.sendEmptyMessage(QUERY_MATCH);
						} else {
							responsHandler.sendEmptyMessage(QUERY_MATCH);
						}
					}
				} else {
					responsHandler.sendEmptyMessage(QUERY_MATCH);
					responsHandler.sendEmptyMessage(FINISHED);
				}

			}
		}
	}

	private void arraylistsort() {
		Comparator<FileItemSet> comparator = new Comparator<FileItemSet>() {
			public int compare(FileItemSet s1, FileItemSet s2) {
				if (s1.getOpername().equals(newwork)
						|| s2.getOpername().equals(newwork)) {
					return 0;
				} else {
					return (s1.getFileItems().size() - s2.getFileItems().size())
							* (-1);
				}
			}
		};
		Collections.sort(fileOpers, comparator);
	}

	private int countSum(String str, char x) {
		int Count = 0;

		for (int i = 0; i < str.length(); i++) {

			char c = str.charAt(i);
			if (c == x) {
				Count++;
			}
		}
		return Count;
	}

	private String buildDocSelection() {
		StringBuilder selection = new StringBuilder();
		Iterator<String> iter = PreparedResource.sDocMimeTypesSet.iterator();
		while (iter.hasNext()) {
			/*
			 * selection.append("(" + FileColumns.MIME_TYPE + "=='" +
			 * iter.next() + "') OR ");
			 */
			selection.append("(" + FileColumns.DATA + " LIKE '" + iter.next()
					+ "') OR ");
		}
		Log.e("hejie", selection.toString());
		return selection.substring(0, selection.lastIndexOf(")") + 1);
	}

	private void getImgFileItem(File file, String Opername, String sourse) {
		boolean flag = false;
		for (int i = 0; i < fileOpers.size(); i++) {
			FileItemSet fileitenmset = fileOpers.get(i);
			if (fileitenmset.getOpername().equals(Opername)) {
				flag = true;
				fileitenmset.getFileItems().add(getoperation(file, sourse));
				break;
			}
		}
		if (!flag) {
			FileItemSet fileitenmset = new FileItemSet();
			fileitenmset.setOpername(Opername);
			fileitenmset.getFileItems().add(getoperation(file, sourse));
			fileOpers.add(fileitenmset);
		}
	}

	private FileItemForOperation getoperation(File file, String sourse) {
		FileItem fileOrfolder = new FileItem();
		String fileName = file.getName();
		String extraName = fileName.substring(fileName.lastIndexOf(".") + 1);
		extraName = extraName.toLowerCase();
		long size = file.length();

		String path = file.getAbsolutePath();
		boolean isDir = file.isDirectory();
		if (isDir) {
			extraName = "folder";
			size = -1;
			path += "/";
		}
		fileOrfolder.setDirectory(isDir);
		fileOrfolder.setFileName(fileName);
		fileOrfolder.setExtraName(extraName);
		fileOrfolder.setFilePath(path);
		fileOrfolder.setFileSize(size);
		fileOrfolder.setCanRead(file.canRead());
		fileOrfolder.setCanWrite(file.canWrite());
		fileOrfolder.setHide(file.isHidden());
		fileOrfolder.setIconId(mPreResource.getBitMap(extraName));
		// 图片排序添加该字段
		fileOrfolder.setModifyData(file.lastModified());
		fileOrfolder.setSourse(sourse);

		FilePropertyAdapter propAdapter = new FilePropertyAdapter(mContext,
				fileOrfolder);
		FileItemForOperation fileItem = new FileItemForOperation();
		fileItem.setPropAdapter(propAdapter);
		fileItem.setFileItem(fileOrfolder);
		return fileItem;
	}

	private void getFileItem(File file, int i, Bitmap bitmap, String sourse) {
		FileItem fileOrfolder = new FileItem();
		String fileName = file.getName();

		String extraName = fileName.substring(fileName.lastIndexOf(".") + 1);
		extraName = extraName.toLowerCase();
		long size = file.length();

		String path = file.getAbsolutePath();
		boolean isDir = file.isDirectory();
		if (isDir) {
			extraName = "folder";
			size = -1;
			path += "/";
		}
		fileOrfolder.setDirectory(isDir);
		fileOrfolder.setFileName(fileName);
		fileOrfolder.setExtraName(extraName);
		fileOrfolder.setFilePath(path);
		fileOrfolder.setFileSize(size);
		fileOrfolder.setCanRead(file.canRead());
		fileOrfolder.setCanWrite(file.canWrite());
		fileOrfolder.setHide(file.isHidden());
		fileOrfolder.setIconId(mPreResource.getBitMap(extraName));
		if (bitmap != null) {
			fileOrfolder.setIcon(bitmap);
		}
		fileOrfolder.setSourse(sourse);
		// 图片排序添加该字段
		fileOrfolder.setModifyData(file.lastModified());

		FilePropertyAdapter propAdapter = new FilePropertyAdapter(mContext,
				fileOrfolder);
		FileItemForOperation fileItem = new FileItemForOperation();
		fileItem.setPropAdapter(propAdapter);
		fileItem.setFileItem(fileOrfolder);
		RefreshData.this.addFileItem(fileItem);
	}

	public void addFileItem(FileItemForOperation fileItem) {
		int length = mItems.size();
		// 过滤0.5M一下的图片
		if (fileItem.getFileItem().getIconId() == R.drawable.file_picture
				&& fileItem.getFileItem().getFileSize() < 512 * 1024) {
			return;
		}
		if (length > 0) {
			for (int k = 0; k < length; k++) {
				if (mItems.get(k).getFileItem().getModifyData() < fileItem
						.getFileItem().getModifyData()) {
					mItems.add(k, fileItem);
					break;
				}
			}
			if (length == mItems.size()) {
				mItems.add(fileItem);
			}
		} else {
			mItems.add(fileItem);
		}
	}

}
