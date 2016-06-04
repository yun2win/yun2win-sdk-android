package y2w.ui.widget.storeage.mediacenter;

import android.content.Context;
import android.graphics.Bitmap;
import android.webkit.MimeTypeMap;

import com.yun2win.demo.R;

import java.util.HashMap;
import java.util.HashSet;

public class PreparedResource {
	// private final String TAG = PreparedResource.class.getCanonicalName();
	private final static String FILE_TYPE_DISK = "disk";
	private final static String FILE_TYPE_AAC = "aac";
	private final static String FILE_TYPE_BIN = "bin";
	private final static String FILE_TYPE_BMP = "bmp";
	private final static String FILE_TYPE_DOC = "doc";
	private final static String FILE_TYPE_DOCX = "docx";
	private final static String FILE_TYPE_WPS = "wps";
	private final static String FILE_TYPE_PDF = "pdf";
	private final static String FILE_TYPE_PPT = "ppt";
	private final static String FILE_TYPE_PPTX = "pptx";
	private final static String FILE_TYPE_TXT = "txt";
	private final static String FILE_TYPE_WAV = "wav";
	private final static String FILE_TYPE_WMA = "wma";
	private final static String FILE_TYPE_MP3 = "mp3";
	private final static String FILE_TYPE_XML = "xml";
	private final static String FILE_TYPE_XLS = "xls";
	private final static String FILE_TYPE_XLSX = "xlsx";
	private final static String FILE_TYPE_HTML = "html";
	private final static String FILE_TYPE_APK = "apk";
	private final static String FILE_TYPE_ZIP = "zip";
	private final static String FILE_TYPE_RAR = "rar";
	private final static String FILE_TYPE_7z = "7z";
	private final static String FILE_TYPE_FOLDER = "folder";
	private static MimeTypeMap myMime = MimeTypeMap.getSingleton();
	public static HashSet<String> sDocMimeTypesSet = new HashSet<String>() {
		{
			// add(getminetype("tif"));
			/*
			 * add(getminetype("pdf")); add(getminetype("doc"));
			 * add(getminetype("ppt")); add(getminetype("xls"));
			 * add(getminetype("zip")); add(getminetype("7z"));
			 * add(getminetype("rar")); add(getminetype("wps"));
			 * add(getminetype("docx")); add(getminetype("xlsx"));
			 * add(getminetype("pptx"));
			 */
			add("%.tif");
			add("%.pdf");
			add("%.doc");
			add("%.ppt");
			add("%.xls");
			add("%.zip");
			add("%.7z");
			add("%.rar");
			add("%.wps");
			add("%.docx");
			add("%.xlsx");
			add("%.pptx");
		}
	};

	public static String getminetype(String type) {
		return myMime.getMimeTypeFromExtension(type);
	}

	private HashMap<String, Integer> fileType = new HashMap<String, Integer>();
	private HashMap<String, Bitmap> apkIcon = new HashMap<String, Bitmap>();
	private HashMap<String, String> defaultVideoType = new HashMap<String, String>();
	private HashMap<String, String> defaultAudioType = new HashMap<String, String>();
	private HashMap<String, String> defaultImageType = new HashMap<String, String>();
	private HashMap<String, String> defaultOAType = new HashMap<String, String>();

	private Context mContext;

	public PreparedResource(Context context) {
		this.mContext = context;
		fileType.put(FILE_TYPE_DISK, R.drawable.disk);
		fileType.put(FILE_TYPE_AAC, R.drawable.file_music);
		fileType.put(FILE_TYPE_BIN, R.drawable.file_other);
		fileType.put(FILE_TYPE_BMP, R.drawable.file_picture);
		fileType.put(FILE_TYPE_DOC, R.drawable.file_doc);
		fileType.put(FILE_TYPE_DOCX, R.drawable.file_doc); // docx
		fileType.put(FILE_TYPE_WPS, R.drawable.file_doc);
		fileType.put(FILE_TYPE_PDF, R.drawable.file_pdf);
		fileType.put(FILE_TYPE_PPT, R.drawable.file_ppt);
		fileType.put(FILE_TYPE_PPTX, R.drawable.file_ppt);
		fileType.put(FILE_TYPE_TXT, R.drawable.file_txt);
		fileType.put(FILE_TYPE_WAV, R.drawable.file_music);
		fileType.put(FILE_TYPE_WMA, R.drawable.file_music);
		fileType.put(FILE_TYPE_MP3, R.drawable.file_music);
		fileType.put(FILE_TYPE_XML, R.drawable.file_other);
		fileType.put(FILE_TYPE_XLS, R.drawable.file_xls);
		fileType.put(FILE_TYPE_XLSX, R.drawable.file_xls);
		fileType.put(FILE_TYPE_HTML, R.drawable.file_other); // html
		fileType.put(FILE_TYPE_ZIP, R.drawable.file_zip); // zip
		fileType.put(FILE_TYPE_RAR, R.drawable.file_zip); // rar
		fileType.put(FILE_TYPE_7z, R.drawable.file_zip);
		fileType.put(FILE_TYPE_FOLDER, R.drawable.folder);
		fileType.put(FILE_TYPE_APK, R.drawable.file_apk);
		loadDefaultMineType();
	}

	private void loadDefaultMineType() {
		defaultVideoType.put("avi", "video/*");
		defaultVideoType.put("flv", "video/*");
		defaultVideoType.put("f4v", "video/*");
		defaultVideoType.put("mpg", "video/*");
		defaultVideoType.put("mp4", "video/*");
		defaultVideoType.put("rmvb", "video/*");
		defaultVideoType.put("rm", "video/*");
		defaultVideoType.put("mkv", "video/*");
		defaultVideoType.put("vob", "video/*");
		defaultVideoType.put("ts", "video/*");
		defaultVideoType.put("m2ts", "video/*");
		defaultVideoType.put("m2p", "video/*");
		defaultVideoType.put("wmv", "video/*");
		defaultVideoType.put("asf", "video/*");
		defaultVideoType.put("d2v", "video/*");
		defaultVideoType.put("ogm", "video/*");
		defaultVideoType.put("3gp", "video/*");
		defaultVideoType.put("divx", "video/*");
		defaultVideoType.put("mpeg", "video/*");
		defaultVideoType.put("m4v", "video/*");
		defaultVideoType.put("mov", "video/*");
		defaultVideoType.put("tp", "video/*");
		defaultVideoType.put("iso", "video/*");
		defaultVideoType.put("rt", "video/*");
		defaultVideoType.put("qt", "video/*");
		defaultVideoType.put("ram", "video/*");
		defaultVideoType.put("vod", "video/*");
		/* defaultVideoType.put("dat", "video/*"); */

		defaultImageType.put("png", "image/*");
		defaultImageType.put("jpg", "image/*");
		defaultImageType.put("jpeg", "image/*");
		defaultImageType.put("gif", "image/*");
		defaultImageType.put("bmp", "image/*");
		defaultImageType.put("tif", "image/*");

		defaultAudioType.put("mp3", "audio/*");
		defaultAudioType.put("wav", "audio/*");
		defaultAudioType.put("ogg", "audio/*");
		defaultAudioType.put("wma", "audio/*");
		defaultAudioType.put("wave", "audio/*");
		defaultAudioType.put("midi", "audio/*");
		defaultAudioType.put("mp2", "audio/*");
		defaultAudioType.put("aac", "audio/*");
		defaultAudioType.put("amr", "audio/*");
		defaultAudioType.put("ape", "audio/*");
		defaultAudioType.put("flac", "audio/*");
		defaultAudioType.put("m4a", "audio/*");

		defaultOAType.put("doc", "");
		defaultOAType.put("docx", "");
		defaultOAType.put("ppt", "");
		defaultOAType.put("pdf", "");
		defaultOAType.put("xlsx", "");
		defaultOAType.put("xls", "");
		defaultOAType.put("zip", "");
		defaultOAType.put("rar", "");
		defaultOAType.put("7z", "");
	}

	public String getMineType(String str) {
		if (defaultAudioType.containsKey(str)) {
			return defaultAudioType.get(str);
		} else if (defaultVideoType.containsKey(str)) {
			return defaultVideoType.get(str);
		} else {
			return defaultImageType.get(str);
		}
	}

	public boolean isAudioFile(String key) {
		if (defaultAudioType.containsKey(key)) {
			return true;
		}
		return false;
	}

	public boolean isOAFile(String key) {
		if (defaultOAType.containsKey(key)) {
			return true;
		}
		return false;
	}

	public boolean isVideoFile(String key) {
		if (defaultVideoType.containsKey(key))
			return true;
		return false;
	}

	public boolean isImageFile(String key) {
		if (defaultImageType.containsKey(key)) {
			return true;
		}
		return false;
	}

	public int getBitMap(String key) {
		if (fileType.containsKey(key)) {
			return fileType.get(key);
		} else if (isImageFile(key)) {
			return R.drawable.file_picture;
		} else if (isVideoFile(key)) {
			return R.drawable.file_video;
		} else {
			return R.drawable.file_other;
		}
	}

	public void recycle() {
		this.apkIcon.clear();
		this.fileType.clear();
	}
}
