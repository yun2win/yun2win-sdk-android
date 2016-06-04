/*******************************************************************************
 * Copyright 2014 Federico Iosue (federico.iosue@gmail.com)
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package y2w.common;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.StatFs;
import android.provider.MediaStore;
import android.webkit.MimeTypeMap;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class StorageManager {

	public static boolean checkStorage() {
		boolean mExternalStorageAvailable = false;
		boolean mExternalStorageWriteable = false;
		String state = Environment.getExternalStorageState();

		if (Environment.MEDIA_MOUNTED.equals(state)) {
			// We can read and write the media
			mExternalStorageAvailable = mExternalStorageWriteable = true;
		} else if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
			// We can only read the media
			mExternalStorageAvailable = true;
			mExternalStorageWriteable = false;
		} else {
			// Something else is wrong. It may be one of many other states, but
			// all we need
			// to know is we can neither read nor write
			mExternalStorageAvailable = mExternalStorageWriteable = false;
		}
		return mExternalStorageAvailable && mExternalStorageWriteable;
	}

	public static String getStorageDir() {
		// return Environment.getExternalStorageDirectory() + File.separator +
		// Constants.TAG + File.separator;
		return Environment.getExternalStoragePublicDirectory(
				Environment.DIRECTORY_DOWNLOADS).toString();
	}

	public static File getAttachmentDir(Context mContext) {
		return mContext.getExternalFilesDir(null);
	}

	/**
	 * Retrieves the folderwhere to store data to sync notes
	 * 
	 * @param mContext
	 * @return
	 */
	public static File getDbSyncDir(Context mContext) {
		File extFilesDir = mContext.getExternalFilesDir(null);
		File dbSyncDir = new File(extFilesDir,
				Constants.APP_STORAGE_DIRECTORY_SB_SYNC);
		dbSyncDir.mkdirs();
		if (dbSyncDir.exists() && dbSyncDir.isDirectory()) {
			return dbSyncDir;
		} else {
			return null;
		}
	}



	public static boolean copyFile(File source, File destination) {
		try {
			return copyFile(new FileInputStream(source), new FileOutputStream(
					destination));
		} catch (FileNotFoundException e) {
			return false;
		}
	}

	public static boolean copyFile(InputStream is, OutputStream os) {
		boolean res = false;
		byte[] data = new byte[1024];
		int len;
		try {
			while ((len = is.read(data)) > 0) {
				os.write(data, 0, len);
			}
			is.close();
			os.close();
			res = true;
		} catch (IOException e) {
		}
		return res;
	}

	public static String getRealPathFromURI(Context mContext, Uri contentUri) {
		String[] proj = { MediaStore.Images.Media.DATA };
		Cursor cursor = null;
		try {
			mContext.getContentResolver().query(contentUri, proj, null, null,
					null);
		} catch (Exception e) {
		}
		if (cursor == null) {
			return null;
		}
		int column_index = cursor
				.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
		cursor.moveToFirst();
		return cursor.getString(column_index);
	}

	public static File createNewAttachmentFile(Context mContext,
			String extension) {
		File f = null;
		if (checkStorage()) {
			f = new File(new File(Config.CACHE_PATH_FILE),
					createNewAttachmentName(extension));
		}
		return f;
	}

	public static String createNewAttachmentName(String extension) {
		Calendar now = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat(
				Constants.DATE_FORMAT_SORTABLE);
		String name = sdf.format(now.getTime());
		name += extension != null ? extension : "";
		return name;
	}


	/**
	 * Create a path where we will place our private file on external
	 * @backupDir
	 * @file
	 * @return
	 */
	public static File copyToBackupDir(File backupDir, File file) {

		if (!checkStorage()) {
			return null;
		}

		if (!backupDir.exists()) {
			backupDir.mkdirs();
		}

		File destination = new File(backupDir, file.getName());

		try {
			copyFile(new FileInputStream(file), new FileOutputStream(
					destination));
		} catch (FileNotFoundException e) {
			destination = null;
		}

		return destination;
	}

	public static File getCacheDir(Context mContext) {
		File dir = mContext.getExternalCacheDir();
		if (!dir.exists())
			dir.mkdirs();
		return dir;
	}

	public static File getExternalStoragePublicDir() {
		File dir = new File(Environment.getExternalStorageDirectory()
				+ File.separator + Constants.TAG + File.separator);
		if (!dir.exists())
			dir.mkdirs();
		return dir;
	}

	public static File getBackupDir(String backupName) {
		File backupDir = new File(getExternalStoragePublicDir(), backupName);
		if (!backupDir.exists())
			backupDir.mkdirs();
		return backupDir;
	}

	public static File getSharedPreferencesFile(Context mContext) {
		File appData = mContext.getFilesDir().getParentFile();
		String packageName = mContext.getApplicationContext().getPackageName();
		File prefsPath = new File(appData
				+ System.getProperty("file.separator") + "shared_prefs"
				+ System.getProperty("file.separator") + packageName
				+ "_preferences.xml");
		return prefsPath;
	}

	/**
	 * Returns a directory size in bytes
	 * 
	 * @param directory
	 * @return
	 */
	@SuppressWarnings("deprecation")
	@SuppressLint("NewApi")
	public static long getSize(File directory) {
		StatFs statFs = new StatFs(directory.getAbsolutePath());
		long blockSize = 0;
		try {
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
				blockSize = statFs.getBlockSizeLong();
			} else {
				blockSize = statFs.getBlockSize();
			}
			// Can't understand why on some devices this fails
		} catch (NoSuchMethodError e) {
		}

		return getSize(directory, blockSize);
	}

	private static long getSize(File directory, long blockSize) {
		File[] files = directory.listFiles();
		if (files != null) {

			// space used by directory itself
			long size = directory.length();

			for (File file : files) {
				if (file.isDirectory()) {
					// space used by subdirectory
					size += getSize(file, blockSize);
				} else {
					// file size need to rounded up to full block sizes
					// (not a perfect function, it adds additional block to 0
					// sized files
					// and file who perfectly fill their blocks)
					size += (file.length() / blockSize + 1) * blockSize;
				}
			}
			return size;
		} else {
			return 0;
		}
	}

	public static boolean copyDirectory(File sourceLocation, File targetLocation) {
		boolean res = true;

		// If target is a directory the method will be iterated
		if (sourceLocation.isDirectory()) {
			if (!targetLocation.exists()) {
				targetLocation.mkdirs();
			}

			String[] children = sourceLocation.list();
			for (int i = 0; i < sourceLocation.listFiles().length; i++) {
				res = res
						&& copyDirectory(new File(sourceLocation, children[i]),
								new File(targetLocation, children[i]));
			}

			// Otherwise a file copy will be performed
		} else {
			try {
				res = res
						&& copyFile(new FileInputStream(sourceLocation),
								new FileOutputStream(targetLocation));
			} catch (FileNotFoundException e) {
				res = false;
			}
		}
		return res;
	}

	/**
	 * Retrieves uri mime-type using ContentResolver
	 * 
	 * @param mContext
	 * @param uri
	 * @return
	 */
	public static String getMimeType(Context mContext, Uri uri) {
		ContentResolver cR = mContext.getContentResolver();
		String mimeType = cR.getType(uri);
		if (mimeType == null) {
			mimeType = getMimeType(uri.toString());
		}
		return mimeType;
	}

	/**
	 * Tries to retrieve mime types from file extension
	 * 
	 * @param url
	 * @return
	 */
	public static String getMimeType(String url) {
		String type = null;
		String extension = MimeTypeMap.getFileExtensionFromUrl(url);
		if (extension != null) {
			MimeTypeMap mime = MimeTypeMap.getSingleton();
			type = mime.getMimeTypeFromExtension(extension);
		}
		return type;
	}

	/**
	 * Retrieves uri mime-type between the ones managed by application
	 * 
	 * @param mContext
	 * @param uri
	 * @return
	 */
	public static String getMimeTypeInternal(Context mContext, Uri uri) {
		// String mimeType = getMimeType(mContext, uri);
		// mimeType = getMimeTypeInternal(mContext, mimeType);
		String uripath = uri.toString();
		String mimeType = uripath.substring(uripath.lastIndexOf("."));
		if (mimeType != null && !mimeType.equals("")) {
			return mimeType;
		} else {
			return Constants.MIME_TYPE_FILES;
		}
	}

	/**
	 * Retrieves mime-type between the ones managed by application from given
	 * string
	 * 
	 * @param mContext
	 * @param mimeType
	 * @return
	 */
	public static String getMimeTypeInternal(Context mContext, String mimeType) {
		if (mimeType != null) {
			if (mimeType.contains("image/")) {
				mimeType = Constants.MIME_TYPE_IMAGE;
			} else if (mimeType.contains("audio/")) {
				mimeType = Constants.MIME_TYPE_AUDIO;
			} else if (mimeType.contains("video/")) {
				mimeType = Constants.MIME_TYPE_VIDEO;
			} else {
				if (mimeType.startsWith(".")) {
					return mimeType;
				} else if (mimeType != null && !mimeType.equals("")) {
					mimeType = mimeType.substring(mimeType.lastIndexOf("."));
				} else {
					mimeType = Constants.MIME_TYPE_FILES;
				}
			}
		}
		return mimeType;
	}

}
