package y2w.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

import java.sql.SQLException;

import y2w.entities.ContactEntity;
import y2w.entities.EmojiEntity;
import y2w.entities.WebValueEntity;
import y2w.entities.SessionEntity;
import y2w.entities.SessionMemberEntity;
import y2w.entities.MessageEntity;
import y2w.entities.TimeStampEntity;
import y2w.entities.UserConversationEntity;
import y2w.entities.UserEntity;
import y2w.entities.UserSessionEntity;

/**
 * 创建数据库
 */
public class DataBaseHelper extends OrmLiteSqliteOpenHelper {


	private static final String TAG = "DataBaseHelper";
	private static final String DATABASE_NAME = "y2w.db";
	private static final int DATABASE_VERSION = 10;

	private Dao<ContactEntity, Integer> dao_contact = null;
	private Dao<UserConversationEntity, Integer> dao_userConversation = null;
	private Dao<SessionEntity, Integer> dao_session = null;
	private Dao<SessionMemberEntity, Integer> dao_sessionMember = null;
	private Dao<MessageEntity, Integer> dao_message = null;
	private Dao<UserSessionEntity, Integer> dao_userSession = null;
	private Dao<TimeStampEntity, Integer> dao_timeStamp = null;
	private Dao<UserEntity, Integer> dao_user = null;
	private Dao<EmojiEntity, Integer> dao_emoji = null;
	private Dao<WebValueEntity,Integer> dao_webValue=null;
	private Context context;

	public DataBaseHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
		this.context = context;
	}

	@Override
	public void onCreate(SQLiteDatabase arg0, ConnectionSource arg1) {
		try {
			//通讯录表
			TableUtils.createTableIfNotExists(arg1, ContactEntity.class);
		} catch (Exception ex) {
			Log.i(TAG,"----ContactEntity create failure");
		}

		try {
			//用户会话表
			TableUtils.createTableIfNotExists(arg1, UserConversationEntity.class);
		} catch (Exception ex) {
			Log.i(TAG,"----UserConversationEntity create failure");
		}
		try {
			//会话表
			TableUtils.createTableIfNotExists(arg1, SessionEntity.class);
		} catch (Exception ex) {
			Log.i(TAG,"----SessionEntity create failure");
		}

		try {
			//群聊表
			TableUtils.createTableIfNotExists(arg1, UserSessionEntity.class);
		} catch (Exception ex) {
			Log.i(TAG,"----UserSessionEntity create failure");
		}

		try {
			//同步时间戳表
			TableUtils.createTableIfNotExists(arg1, TimeStampEntity.class);
		} catch (Exception ex) {
			Log.i(TAG,"----ContactEntity create failure");
		}

		try {
			//会话成员表
			TableUtils.createTableIfNotExists(arg1, SessionMemberEntity.class);
		} catch (Exception ex) {
			Log.i(TAG,"----SessionMemberEntity create failure");
		}

		try {
			//消息表
			TableUtils.createTableIfNotExists(arg1, MessageEntity.class);
		} catch (Exception ex) {
			Log.i(TAG,"----MessageEntity create failure");
		}

		try {
			//用户表
			TableUtils.createTableIfNotExists(arg1, UserEntity.class);
		} catch (Exception ex) {
			Log.i(TAG,"----UserEntity create failure");
		}

		try {
			//表情表
			TableUtils.createTableIfNotExists(arg1, EmojiEntity.class);
		} catch (Exception ex) {
			Log.i(TAG,"----EmojiEntity create failure");
		}

		try {
			//图片本地缓存表
			TableUtils.createTableIfNotExists(arg1, WebValueEntity.class);
		} catch (Exception ex) {
			Log.i(TAG,"----WebValueEntity create failure");
		}

	}


	@Override
	public void onUpgrade(SQLiteDatabase arg0, ConnectionSource arg1, int arg2,
			int arg3) {
			if(arg2 < 10){
				try {
					//每次数据库版本更新，此表必清
					TableUtils.dropTable(arg1, TimeStampEntity.class, true);
				} catch (Exception e) {
				}

				try {
					TableUtils.dropTable(arg1, ContactEntity.class, true);
				} catch (Exception e) {
				}
				try {
					TableUtils.dropTable(arg1, UserConversationEntity.class, true);
				} catch (Exception e) {
				}
				try {
					TableUtils.dropTable(arg1,SessionEntity.class,true);
				} catch (Exception e) {
				}
				try {
					TableUtils.dropTable(arg1, MessageEntity.class, true);
				} catch (Exception e) {
				}
				try {
					TableUtils.dropTable(arg1, SessionMemberEntity.class, true);
				} catch (Exception e) {
				}

				try {
					TableUtils.dropTable(arg1, UserSessionEntity.class, true);
				} catch (Exception e) {
				}
			}
			onCreate(arg0, arg1);
	}

	public Dao<ContactEntity, Integer> getContactsDao() throws SQLException {
		if (dao_contact == null) {
			dao_contact = getDao(ContactEntity.class);
		}
		return dao_contact;
	}

	public Dao<SessionEntity, Integer> getSessionDao() throws SQLException {
		if (dao_session == null) {
			dao_session = getDao(SessionEntity.class);
		}
		return dao_session;
	}

	public Dao<SessionMemberEntity, Integer> getSessionMemberDao() throws SQLException {
		if (dao_sessionMember == null) {
			dao_sessionMember = getDao(SessionMemberEntity.class);
		}
		return dao_sessionMember;
	}

	public Dao<UserConversationEntity, Integer> getUserConversationDao() throws SQLException {
		if (dao_userConversation == null) {
			dao_userConversation = getDao(UserConversationEntity.class);
		}
		return dao_userConversation;
	}

	public Dao<MessageEntity, Integer> getMessageDao() throws SQLException {
		if (dao_message == null) {
			dao_message = getDao(MessageEntity.class);
		}
		return dao_message;
	}

	public Dao<UserSessionEntity, Integer> getUserSessionDao() throws SQLException {
		if (dao_userSession == null) {
			dao_userSession = getDao(UserSessionEntity.class);
		}
		return dao_userSession;
	}

	public Dao<TimeStampEntity, Integer> getTimeStampDao() throws SQLException {
		if (dao_timeStamp == null) {
			dao_timeStamp = getDao(TimeStampEntity.class);
		}
		return dao_timeStamp;
	}

	public Dao<UserEntity, Integer> getUserDao() throws SQLException {
		if (dao_user == null) {
			dao_user = getDao(UserEntity.class);
		}
		return dao_user;
	}

	public Dao<EmojiEntity, Integer> getEmojiDao() throws SQLException {
		if (dao_emoji == null) {
			dao_emoji = getDao(EmojiEntity.class);
		}
		return dao_emoji;
	}
	public Dao<WebValueEntity, Integer> getWebValueDao() throws SQLException {
		if (dao_webValue == null) {
			dao_webValue = getDao(WebValueEntity.class);
		}
		return dao_webValue;
	}


	@Override
	public void close() {
		super.close();
		dao_contact = null;
		dao_session = null;
		dao_sessionMember = null;
		dao_userConversation =null;
		dao_message =null;
		dao_userSession =null;
		dao_timeStamp = null;
		dao_user =null;
		dao_emoji =null;
		dao_webValue =null;
	}
}
