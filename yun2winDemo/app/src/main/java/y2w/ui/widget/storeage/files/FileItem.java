package y2w.ui.widget.storeage.files;

import android.graphics.Bitmap;

public class FileItem {

	private String FileName;
	private String extraName;
	private String FilePath;
	private boolean isDirectory;

	private long fileSize;
	private long downSize;
	private String downpross = "0%";
	private long createData;
	private long modifyData;

	private boolean canWrite;
	private boolean canRead;
	private boolean isHide;
	private String sourse;// 来源地方

	private int iconId;
	private Bitmap icon;

	private boolean chooser;
	private String packagename;
	private float xlocation;
	private float ylocation;
	private String allsize;
	private long sizegb = 1024 * 1024 * 1024;
	private long sizemb = 1024 * 1024 / 10;

	public void setFileName(String fileName) {
		this.FileName = fileName;
	}

	public String getFileName() {
		return FileName;
	}

	public void setExtraName(String extraName) {
		this.extraName = extraName;
	}

	public String getExtraName() {
		return extraName;
	}

	public void setFilePath(String filePath) {
		FilePath = filePath;
	}

	public String getFilePath() {
		return FilePath;
	}

	public String getSourse() {
		return sourse;
	}

	public void setSourse(String sourse) {
		this.sourse = sourse;
	}

	public void setDirectory(boolean isDirectory) {
		this.isDirectory = isDirectory;
	}

	public boolean isDirectory() {
		return isDirectory;
	}

	public void setFileSize(long fileSize) {
		this.fileSize = fileSize;
		allsize = bytetosize(fileSize);
	}

	public long getFileSize() {
		return fileSize;
	}

	public void setCreateData(long createData) {
		this.createData = createData;
	}

	public long getCreateData() {
		return createData;
	}

	public void setModifyData(long modifyData) {
		this.modifyData = modifyData;
	}

	public long getModifyData() {
		return modifyData;
	}

	public void setCanWrite(boolean canWrite) {
		this.canWrite = canWrite;
	}

	public boolean isCanWrite() {
		return canWrite;
	}

	public void setCanRead(boolean canRead) {
		this.canRead = canRead;
	}

	public boolean isCanRead() {
		return canRead;
	}

	public void setHide(boolean isHide) {
		this.isHide = isHide;
	}

	public boolean isHide() {
		return isHide;
	}

	public void setIconId(int iconId) {
		this.iconId = iconId;
	}

	public int getIconId() {
		return iconId;
	}

	public Bitmap getIcon() {
		return icon;
	}

	public void setIcon(Bitmap icon) {
		this.icon = icon;
	}

	public boolean isChooser() {
		return chooser;
	}

	public void setChooser(boolean chooser) {
		this.chooser = chooser;
	}

	public String getPackagename() {
		return packagename;
	}

	public void setPackagename(String packagename) {
		this.packagename = packagename;
	}

	public float getXlocation() {
		return xlocation;
	}

	public void setXlocation(float xlocation) {
		this.xlocation = xlocation;
	}

	public float getYlocation() {
		return ylocation;
	}

	public void setYlocation(float ylocation) {
		this.ylocation = ylocation;
	}

	public long getDownSize() {
		return downSize;
	}

	public void setDownSize(long downSize) {
		this.downSize = downSize;
		this.downpross = downSize * 100 / fileSize + "%";
	}

	public String getDownpross() {
		return downpross;
	}

	public void setDownpross(String downpross) {
		this.downpross = downpross;
	}

	public String getDownsizepross() {
		return bytetosize(downSize) + "/" + allsize;
	}

	public String bytetosize(long bytesize) {
		if (bytesize >= sizegb) {
			long getsize = bytesize * 10 / sizegb;
			long yusize = getsize % 10;
			if (yusize == 0) {
				return getsize / 10 + "GB";
			} else {
				return getsize / 10 + "." + yusize + "GB";
			}
		} else if (bytesize >= sizemb) {
			long getsize = bytesize * 10 / (1024 * 1024);
			long yusize = getsize % 10;
			if (yusize == 0) {
				return getsize / 10 + "MB";
			} else {
				return getsize / 10 + "." + yusize + "MB";
			}
		} else {
			long getsize = bytesize * 10 / 1024;
			long yusize = getsize % 10;
			if (yusize == 0) {
				return getsize / 10 + "KB";
			} else {
				return getsize / 10 + "." + yusize + "KB";
			}
		}
	}
}
