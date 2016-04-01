package y2w.db;

import android.content.Context;

import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.Dao;
import y2w.entities.ContactEntity;
import y2w.entities.SessionEntity;
import y2w.entities.SessionMemberEntity;
import y2w.entities.MessageEntity;
import y2w.entities.TimeStampEntity;
import y2w.entities.UserConversationEntity;
import y2w.entities.UserEntity;
import y2w.entities.UserSessionEntity;

/**
 * 数据库表管理器
 */
public class DaoManager {
	
	public DataBaseHelper helper;
	public Dao<ContactEntity, Integer> dao_contact = null;
	public Dao<UserConversationEntity, Integer> dao_userConversation = null;
	public Dao<SessionEntity, Integer> dao_session = null;
	public Dao<SessionMemberEntity, Integer> dao_sessionMember = null;
	public Dao<MessageEntity, Integer> dao_message = null;
	public Dao<UserSessionEntity, Integer> dao_userSession = null;
	public Dao<TimeStampEntity, Integer> dao_timeStamp = null;
	public Dao<UserEntity, Integer> dao_user = null;



	private static DaoManager manager = null;

	public DaoManager(Context context){

		if (helper == null) {
			helper = OpenHelperManager.getHelper(context, DataBaseHelper.class);
		}

		try {
			dao_contact = helper.getContactsDao();
			dao_userConversation = helper.getUserConversationDao();
			dao_session = helper.getSessionDao();
			dao_message = helper.getMessageDao();
			dao_sessionMember = helper.getSessionMemberDao();
			dao_userSession = helper.getUserSessionDao();
			dao_timeStamp = helper.getTimeStampDao();
			dao_user = helper.getUserDao();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	/**
	 * 获取管理器
	 *
	 * @param context
	 * @return
	 */
	public static DaoManager getInstance(Context context) {
		
		if (manager == null) {
			manager = new DaoManager(context);
		}
		return manager;
	}

	/**
	 * 关闭管理器
	 */
	public void close() {
		if (helper != null) {
			helper.close();
			OpenHelperManager.releaseHelper();
			helper = null;
		}
	}
}
