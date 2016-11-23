package y2w.manage;

import com.y2w.uikit.utils.StringUtil;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import y2w.db.SessionDb;
import y2w.db.SessionMemberDb;
import y2w.db.UserDb;
import y2w.entities.ContactEntity;
import y2w.entities.SessionEntity;
import y2w.entities.SessionMemberEntity;
import y2w.entities.UserEntity;
import y2w.model.Contact;
import y2w.model.Session;
import y2w.model.SessionMember;
import y2w.model.User;
import y2w.service.Back;
import y2w.service.ErrorCode;
import y2w.service.SessionSrv;
import y2w.service.UserSrv;

/**
 * 会话成员管理类
 * Created by yangrongfang on 2016/1/16.
 */
public class SessionMembers implements Serializable {
    private Session session;
    private Remote remote;
    public SessionMembers(Session session){
        this.session=session;
    }

    /**
     * 获取某个成员信息
     * @param userId 用户唯一标识码
     * @return 返回结果
     */
    public SessionMember getLocalMember(String userId){
        SessionMemberEntity entity = SessionMemberDb.queryByMemberId(session.getEntity().getMyId(), session.getEntity().getId(), userId);
        return new SessionMember(this,entity);
    }

    /**
     * 根据userId,获取成员信息
     * @param userId 用户唯一标识码
     * @param result 回调
     */
    public void getMember(String userId,Back.Result<SessionMember> result){
        SessionMember member = getLocalMember(userId);
        if(member.getEntity() != null){
            result.onSuccess(member);
        }else{
            getRemote().sessionMemberGet(userId, result);
        }
    }
    /**
     * 异步获取所有会话成员，本地没有服务器获取
     * @param result 回调
     */
    List<SessionMember> locmemberList = new ArrayList<SessionMember>();//剔除了无效成员
    public void getMembers(Back.Result<List<SessionMember>> result){
        List<SessionMemberEntity> entities = SessionMemberDb.query(session.getEntity().getMyId(), session.getEntity().getId());
        List<SessionMember> memberList = new ArrayList<SessionMember>();
        if(entities.size() > 0){
            locmemberList.clear();
            for(SessionMemberEntity entity:entities){
                SessionMember sessionMember = new SessionMember(this,entity);
                memberList.add(sessionMember);
                if(EnumManage.UserStatus.active.toString().equals(entity.getStatus())){
                    locmemberList.add(sessionMember);
                }
            }
            result.onSuccess(memberList);
        }else{
            getRemote().sync(result);
        }
    }
    //返回有效成员（无效指没有注册账号但是发送了邀请或者在我们平台销号）
    public void localAllMembers(String sessionId,final Back.Result<List<SessionMember>> result){
        if(StringUtil.isEmpty(sessionId))
            return;
        if(locmemberList==null||locmemberList.size()==0){
            getMembers(new Back.Result<List<SessionMember>>() {
                @Override
                public void onSuccess(List<SessionMember> sessionMembers) {
                    result.onSuccess(locmemberList);
                }
                @Override
                public void onError(int code, String error) {
                    result.onError(code,error);
                }
            });
        }else{
            String smpsessionId =locmemberList.get(0).getEntity().getSessionId();
           if(!StringUtil.isEmpty(smpsessionId)&&smpsessionId.equals(sessionId)) {
               result.onSuccess(locmemberList);
           }else{
               locmemberList.clear();
               localAllMembers(sessionId,result);
           }
        }
    }
    /*
     *
    异步获取所有会话成员，根据搜索输入的name
     *
     */

    public void getAllMembersBySesionName(Back.Result<List<SessionMember>> result){
        List<SessionMemberEntity> entities = SessionMemberDb.searchByName(session.getEntity().getMyId(),session.getEntity().getName());
        List<SessionMember> memberList = new ArrayList<SessionMember>();
        if(entities.size() > 0){
            for(SessionMemberEntity entity:entities){
                memberList.add(new SessionMember(this,entity));
            }
            result.onSuccess(memberList);
        }else{
            getRemote().sync(result);
        }
    }

    /**
     * 将会话成员列表保存到数据集
     * @param sessionMemberList 会话列表
     */
    public void add(List<SessionMember> sessionMemberList){
        for(SessionMember member:sessionMemberList){
            addSessionMember(member);
        }
    }
    /**
     * 将某个会员成员保存到数据库
     * @param member 会话成员
     */
    public void addSessionMember(SessionMember member){
        member.getEntity().setSessionId(session.getEntity().getId());
        member.getEntity().setMyId(session.getEntity().getMyId());
        refreshSessionMTS(member);
        SessionMemberDb.addSessionMemberEntity(member.getEntity());
    }
    /**
     * 更新会话时间戳
     * 会话所有成员的最大值，为时间戳
     * @param member 会话成员
     */
    private void refreshSessionMTS(SessionMember member){
        String createMTS = session.getEntity().getCreateMTS();
        String updateMTS = session.getEntity().getUpdateMTS();
        boolean update =false;
        if(StringUtil.isEmpty(createMTS)){
            session.getEntity().setCreateMTS(member.getEntity().getCreatedAt());
            update = true;
        }else{
            if(StringUtil.timeCompare(createMTS,member.getEntity().getCreatedAt()) == 0){
                session.getEntity().setCreateMTS(member.getEntity().getCreatedAt());
                update = true;
            }
        }
        if(StringUtil.isEmpty(updateMTS)){
            session.getEntity().setUpdateMTS(member.getEntity().getUpdatedAt());
            update = true;
        }else{
            if(StringUtil.timeCompare(updateMTS,member.getEntity().getUpdatedAt()) == 0){
                session.getEntity().setUpdateMTS(member.getEntity().getUpdatedAt());
                update = true;
            }
        }
        if(update) {
            SessionDb.addSessionEntity(session.getEntity());
        }
    }

    /**
     * 获取远程访问实例
     * @return 返回结果
     */
    public Remote getRemote(){
        if(remote == null){
            remote = new Remote(this);
        }
        return  remote;
    }

    /*****************************remote*****************************/
    /**
     * 远程访问类
     */
    public class Remote implements Serializable{

        private SessionMembers sessionMembers;
        public Remote(SessionMembers sessionMembers){
            this.sessionMembers = sessionMembers;
        }

        /**
         * 添加会话成员
         * @param userId 用户唯一标识码
         * @param name 用户名称
         * @param role 用户成员角色
         * @param avatarUrl 用户头像
         * @param status 用户状态
         * @param result 回调
         */
        public void sessionMemberAdd(final String userId,final String name,final String role,final String avatarUrl,final String status, final Back.Result<SessionMember> result){
            SessionSrv.getInstance().sessionMemberAdd(session.getSessions().getUser().getToken(), session.getEntity().getId(), userId, name, role, avatarUrl, status, new Back.Result<SessionMemberEntity>() {
                @Override
                public void onSuccess(SessionMemberEntity Entity) {
                    SessionMember sessionMember = new SessionMember(sessionMembers, Entity);
                    Entity.setSessionId(session.getEntity().getId());
                    Entity.setMyId(session.getEntity().getMyId());
                    refreshSessionMTS(sessionMember);
                    SessionMemberDb.addSessionMemberEntity(Entity);
                    result.onSuccess(sessionMember);
                }

                @Override
                public void onError(int errorCode, String error) {
                    result.onError(errorCode, error);
                }
            });
        }

        /**
         * 删除会话成员
         * @param sessionMember 会话成员
         * @param callback 回调
         */

        public void sessionMemberDelete(final SessionMember sessionMember, final Back.Callback callback){
            SessionSrv.getInstance().sessionMembersDelete(session.getSessions().getUser().getToken(), sessionMember.getEntity().getSessionId(), sessionMember.getEntity().getId(), new Back.Callback() {
                @Override
                public void onSuccess() {
                    sessionMember.getEntity().setIsDelete(true);
                    SessionMemberDb.addSessionMemberEntity(sessionMember.getEntity());
                    callback.onSuccess();
                }

                @Override
                public void onError(int errorCode, String error) {
                    callback.onError(errorCode, error);
                }
            });

        }

        /**
         *
         */
        public void sessionMemberUpdate(SessionMember sessionMember,String role,String status,final Back.Callback callback){
            SessionSrv.getInstance().sessionMemberUpdate(session.getSessions().getUser().getToken(), sessionMember.getEntity().getSessionId(), sessionMember.getEntity().getId(),sessionMember.getEntity().getUserId(),
                    sessionMember.getEntity().getName(),role,sessionMember.getEntity().getAvatarUrl(),status,new Back.Result<SessionMemberEntity>() {
                        @Override
                        public void onSuccess(SessionMemberEntity sessionMemberEntity) {
                            sessionMemberEntity.setSessionId(session.getEntity().getId());
                            sessionMemberEntity.setMyId(session.getEntity().getMyId());
                            SessionMember sessionMember = new SessionMember(sessionMembers, sessionMemberEntity);
                            refreshSessionMTS(sessionMember);
                            SessionMemberDb.addSessionMemberEntity(sessionMemberEntity);
                            callback.onSuccess();
                        }
                        @Override
                    public void onError(int errorCode, String error) {
                    callback.onError(errorCode, error);
                   }
            });
        }

        public void sessionMemberGet(final String userId, Back.Result<SessionMember> result){

        }
        /**
         * 同步会话成员
         * @param result 回调
         */
        public void sync(final Back.Result<List<SessionMember>> result){
            SessionSrv.getInstance().sessionMembersGet(session.getSessions().getUser().getToken(), session.getEntity().getUpdatedAt(), session.getEntity().getId(), new Back.Result<List<SessionMemberEntity>>() {
                @Override
                public void onSuccess(List<SessionMemberEntity> sessionMemberEntities) {
                    List<SessionMember> sessionMemberList = new ArrayList<SessionMember>();
                    for (SessionMemberEntity entity : sessionMemberEntities) {
                        sessionMemberList.add(new SessionMember(sessionMembers, entity));
                    }
                    add(sessionMemberList);
                    getMembers(new Back.Result<List<SessionMember>>() {
                        @Override
                        public void onSuccess(List<SessionMember> sessionMembers) {
                            result.onSuccess(sessionMembers);
                        }
                        @Override
                        public void onError(int errorCode, String error) {
                            result.onError(errorCode, error);
                        }
                    });
                }

                @Override
                public void onError(int errorCode, String error) {
                    result.onError(errorCode, error);
                }
            });
        }
    }
}
