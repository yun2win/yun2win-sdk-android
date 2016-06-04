package y2w.ui.widget.storeage.files;

import java.util.ArrayList;
import java.util.List;

public class FileOperSet {
	private List<FileItemSet> fileOpers;

	public FileOperSet() {
		setFileOpers(new ArrayList<FileItemSet>());
	}

	public void setFileOpers(List<FileItemSet> fileOpers) {
		this.fileOpers = fileOpers;
	}

	public List<FileItemSet> getFileOpers() {
		return fileOpers;
	}

	public void Add(FileItemSet fileoper) {
		this.fileOpers.add(fileoper);
	}

	public void remove(FileItemSet fileoper) {
		this.fileOpers.remove(fileoper);
	}

	public void clear() {
		this.fileOpers.clear();
	}

	public void insertAt(int location, FileItemSet fileoper) {
		this.fileOpers.add(location, fileoper);
	}

	public int size() {
		return this.fileOpers.size();
	}
}
