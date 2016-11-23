package y2w.model;

import com.yun2win.utils.Json;

public class Update { 
	 	
		private String _vname;
		private String _downurl;
		private int _code;
		private String _log;	 
		private long _size;
		
		
		public String getVersionName() {
			return this._vname;
		}
		public void setVersionName(String name) {
			this._vname = name;
		}		 
		public int getVersionCode() {
			return _code;
		}
		public void setVersionCode(int code) {
			this._code = code;
		}		 
		public String getDownloadUrl() {
			return _downurl;
		}
		public void setDownloadUrl(String url) {
			this._downurl = url;
		}
		public String getUpdateLog() {
			return _log;
		}
		public void setUpdateLog(String log) {
			this._log = log;
		}
		
		public long getSize() {
			return _size;
		}
		public void setSize(long code) {
			this._size = code;
		}
	 
		public static Update parse(Json json) throws Exception {
			Update user = new Update();		 
		 	 
			user.setVersionName(json.get("versionName").toStr());
			user.setVersionCode(json.get("versionCode").toInt());
			user.setDownloadUrl(json.get("downloadUrl").toStr());
			user.setUpdateLog(json.get("updateLog").toStr());
			user.setSize(json.get("size").toLong());
		 
			return user;
		}
}
