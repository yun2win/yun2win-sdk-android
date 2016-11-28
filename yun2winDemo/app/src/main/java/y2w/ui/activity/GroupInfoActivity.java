package y2w.ui.activity;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.y2w.uikit.customcontrols.view.SwitchButton;
import com.y2w.uikit.utils.HeadTextBgProvider;
import com.y2w.uikit.utils.NetUtil;
import com.y2w.uikit.utils.StringUtil;
import com.y2w.uikit.utils.ToastUtil;
import com.yun2win.demo.R;
import com.yun2win.imlib.IMClient;
import com.yun2win.imlib.IMSession;

import java.util.ArrayList;
import java.util.List;

import y2w.base.Urls;
import y2w.common.Config;
import y2w.common.HeadImageView;
import y2w.manage.EnumManage;
import y2w.manage.Users;
import y2w.model.Session;
import y2w.model.SessionMember;
import y2w.model.UserConversation;
import y2w.model.UserSession;
import y2w.service.Back;
import y2w.ui.adapter.GroupInfoAdapter;

/**
 * Created by hejie on 2016/3/14.
 * 个人名片界面
 */
public class GroupInfoActivity extends Activity{
    private Context context;
    private Session _session;
    private GroupInfoAdapter groupMemberAdapter;
    private String sessionId,sessionName,sessionAvatar;
    private List<SessionMember> listMembers = new ArrayList<SessionMember>();
    private SessionMember mysessionMember;
    private String fileToken = "?access_token=" + Users.getInstance().getCurrentUser().getToken();
    Handler handlerUi = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if(msg.what==1) {
                if (listMembers != null) {
                    int converViewWidth = Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 50, context.getResources()
                            .getDisplayMetrics()));
                    ViewGroup.LayoutParams layoutParams = gv_member_preview.getLayoutParams();
                    layoutParams.width = converViewWidth * listMembers.size();
                    layoutParams.height = converViewWidth;
                    gv_member_preview.setLayoutParams(layoutParams);
                    gv_member_preview.setNumColumns(listMembers.size());
                    groupMemberAdapter.setListViewdate(listMembers);
                    groupMemberAdapter.notifyDataSetChanged();
                }
                if(mysessionMember==null){
                    bt_out_group.setVisibility(View.GONE);
                    ll_add_member.setVisibility(View.GONE);
                    findViewById(R.id.savecotactRe).setVisibility(View.GONE);
                }else{
                    bt_out_group.setVisibility(View.VISIBLE);
                    ll_add_member.setVisibility(View.VISIBLE);
                    findViewById(R.id.savecotactRe).setVisibility(View.VISIBLE);
                }
            }else if(msg.what==2){
                iv_group_head.loadBuddyAvatarbyurl(Urls.User_Messages_File_DownLoad + sessionAvatar, R.drawable.default_group_icon);
            }else if(msg.what==3){//保存到通讯录
                String result = (String) msg.obj;
                if("success".equals(result)){
                    ToastUtil.ToastMessage(context,"保存成功");
                }else{
                    sb_store_to_contacts.setCheck(false);
                    ToastUtil.ToastMessage(context,"保存失败");
                }
            }else if(msg.what==4){//取消保存到通讯录
                String result = (String) msg.obj;
                if("success".equals(result)){
                    ToastUtil.ToastMessage(context,"取消成功");
                }else{
                    sb_store_to_contacts.setCheck(true);
                    ToastUtil.ToastMessage(context,"取消失败");
                }
            }
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_groupinfo);
        context = this;
        getExtras(this.getIntent().getExtras());
        initActionBar();
        initUi();
        addEvent();
        getSession();
        getLocalMembers();
    }

    /*
   ***自定义aciontbar
   */
    private void initActionBar(){
        ActionBar actionbar = getActionBar();
        actionbar.setDisplayShowTitleEnabled(false);
        actionbar.setDisplayShowHomeEnabled(false);
        actionbar.setDisplayShowCustomEnabled(true);
        actionbar.setCustomView(R.layout.actionbar_chat);
        TextView texttitle = (TextView) actionbar.getCustomView().findViewById(R.id.text_title);
        texttitle.setText("群信息");

        ImageButton imageButtonclose = (ImageButton) actionbar.getCustomView().findViewById(R.id.left_close);
        ImageButton imageButtonright = (ImageButton) actionbar.getCustomView().findViewById(R.id.right_add);
        imageButtonclose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        imageButtonright.setVisibility(View.GONE);
    }
    private void getExtras(Bundle bundle){
        if(bundle != null) {
            sessionId =bundle.getString("sessionId");
            sessionName =bundle.getString("sessionName");
            sessionAvatar =bundle.getString("sessionAvatar");
        }
    }
    private HeadImageView iv_group_head;
    private TextView tv_group_name;
    private GridView gv_member_preview;
    private SwitchButton sb_store_to_contacts;
    private LinearLayout ll_add_member;
    private Button bt_out_group;
    private void initUi(){
        iv_group_head = (HeadImageView) findViewById(R.id.group_head_image);
        tv_group_name = (TextView) findViewById(R.id.group_name_text);
        gv_member_preview = (GridView) findViewById(R.id.gv_member_preview);
        sb_store_to_contacts = (SwitchButton) findViewById(R.id.sb_to_contacts);
        ll_add_member = (LinearLayout) findViewById(R.id.add_membaer_linear);
        bt_out_group = (Button) findViewById(R.id.bt_group_delete);
        tv_group_name.setText(sessionName);
        iv_group_head.loadBuddyAvatarbyurl(sessionAvatar , R.drawable.default_group_icon);

        iv_group_head.setBackgroundResource(HeadTextBgProvider.getTextBg(StringUtil.parseAscii(sessionId)));
        groupMemberAdapter = new GroupInfoAdapter(context);
        gv_member_preview.setAdapter(groupMemberAdapter);
        UserSession userSession = Users.getInstance().getCurrentUser().getUserSessions().getUserSessionBySessionId(sessionId);
        if(userSession.getEntity() != null && !userSession.getEntity().getIsDelete()){
            sb_store_to_contacts.setCheck(true);
        }else{
            sb_store_to_contacts.setCheck(false);
        }
        bt_out_group.setVisibility(View.GONE);
        ll_add_member.setVisibility(View.GONE);
        findViewById(R.id.savecotactRe).setVisibility(View.GONE);

        RelativeLayout rela_groupfiles = (RelativeLayout) findViewById(R.id.rela_groupfiles);
         rela_groupfiles.setVisibility(View.GONE);
        rela_groupfiles.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(GroupInfoActivity.this, StrongWebViewActivity.class);
                intent.putExtra("webUrl", Config.File_Host+"?title=群文件&sessionId="+sessionId+"&token="+Users.getInstance().getCurrentUser().getToken());
                startActivity(intent);
            }
        });
    }
    private void addEvent(){

        iv_group_head.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mysessionMember != null && mysessionMember.getEntity() != null && mysessionMember.getEntity().getRole().equals(EnumManage.GroupRole.master.toString())){
                    Intent headSculpture1 = new Intent(context,
                            HeadSculptureActivity.class);
                    Bundle bundle1 = new Bundle();
                    bundle1.putString("type", EnumManage.SessionType.group.toString());
                    bundle1.putString("sessionId", sessionId);
                    headSculpture1.putExtras(bundle1);
                    startActivityForResult(headSculpture1,100);
                }else{
                    ToastUtil.ToastMessage(context,"群主才能修改群头像");
                }
            }
        });

        tv_group_name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mysessionMember != null && mysessionMember.getEntity() != null && mysessionMember.getEntity().getRole().equals(EnumManage.GroupRole.master.toString())) {
                    Intent intent = new Intent(GroupInfoActivity.this, GroupNameActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putString("sessionId", sessionId);
                    bundle.putString("groupName", tv_group_name.getText().toString());
                    intent.putExtras(bundle);
                    startActivityForResult(intent, 101);
                }else{
                    ToastUtil.ToastMessage(context,"群主才能修改群名称");
                }
            }
        });

        ll_add_member.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (_session == null) {
                    ToastUtil.ToastMessage(context, "正在初始化数据");
                    return;
                }
                Intent intent = new Intent(GroupInfoActivity.this, SessionStartActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("sessionId", _session.getEntity().getId());
                String userIds = "";
                if (listMembers != null) {
                    for (int i = 0; i < listMembers.size(); i++) {
                        userIds = userIds + listMembers.get(i).getEntity().getUserId() + ";";
                    }
                }
                bundle.putString("userIds", userIds);
                bundle.putBoolean("iscreate", false);
                intent.putExtras(bundle);
                startActivityForResult(intent, 102);
            }
        });
        bt_out_group.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (_session == null||mysessionMember==null) {
                    ToastUtil.ToastMessage(context, "正在初始化数据");
                    return;
                }
                _session.getMembers().getRemote().sessionMemberDelete(mysessionMember, new Back.Callback() {
                    @Override
                    public void onSuccess() {
                        Users.getInstance().getCurrentUser().getUserConversations().delete(_session.getEntity().getId(),_session.getEntity().getType());
                        ((ChatActivity) ChatActivity._context).finish();
                        _session.getMessages().getRemote().sendMessage("",false, new IMClient.SendCallback() {
                            @Override
                            public void onReturnCode(int i, IMSession imSession, String s) {
                            }
                        });
                        finish();
                    }

                    @Override
                    public void onError(int errorCode, String error) {
                        ToastUtil.ToastMessage(context, "退出失败");
                    }
                });
            }
        });

        sb_store_to_contacts.setOnChangedListener(new SwitchButton.OnChangedListener() {
            @Override
            public void OnChanged(View v, boolean checkState) {
                if(NetUtil.isNetworkAvailable(context)){
                    if(checkState){
                        storeToContacts(_session);
                    }else{
                        unStoreToContacts();
                    }
                }else{
                    sb_store_to_contacts.setCheck(!checkState);
                    ToastUtil.ToastMessage(context, "请检查网络连接");
                }
            }
        });

        gv_member_preview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(context, GroupMemberActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("sessionId", sessionId);
                bundle.putString("sessionName", sessionName);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });


    }
    private void getSession(){
        final ProgressDialog pd = new ProgressDialog(context);
        pd.setCanceledOnTouchOutside(false);
        pd.setMessage(getString(R.string.loading));
        Users.getInstance().getCurrentUser().getSessions().getSessionBySessionId(sessionId, new Back.Result<Session>() {
            @Override
            public void onSuccess(Session session) {
                _session = session;
                getRemmoteMembers();
                pd.dismiss();
            }

            @Override
            public void onError(int Code, String error) {
                if (_session == null) {
                    ToastUtil.ToastMessage(context, "初始化失败,请重新打开");
                    pd.dismiss();
                    finish();
                }
            }
        });
    }

    private void storeToContacts(final Session session){

        if(session == null)
            return;
        final ProgressDialog pd = new ProgressDialog(context);
        pd.setCanceledOnTouchOutside(false);
        pd.setMessage(getString(R.string.loading));
        Users.getInstance().getCurrentUser().getUserSessions().getRemote().sessionStore(session.getEntity().getId(), session.getEntity().getName(), session.getEntity().getAvatarUrl(), new Back.Result<UserSession>() {
            @Override
            public void onSuccess(UserSession userSession) {
                Message msg = new Message();
                msg.what = 3;
                msg.obj = "success";
                handlerUi.sendMessage(msg);
                pd.dismiss();
            }

            @Override
            public void onError(int Code, String error) {
                Message msg = new Message();
                msg.what = 3;
                msg.obj = "error";
                handlerUi.sendMessage(msg);
                pd.dismiss();
            }
        });
    }

    private void unStoreToContacts(){

        UserSession userSession = Users.getInstance().getCurrentUser().getUserSessions().getUserSessionBySessionId(sessionId);
        if(userSession != null){
            final ProgressDialog pd = new ProgressDialog(context);
            pd.setCanceledOnTouchOutside(false);
            pd.setMessage(getString(R.string.loading));
            Users.getInstance().getCurrentUser().getUserSessions().getRemote().userSessionDelete(userSession.getEntity().getId(), new Back.Callback() {
                @Override
                public void onSuccess() {
                    Message msg = new Message();
                    msg.what = 4;
                    msg.obj = "success";
                    handlerUi.sendMessage(msg);
                    pd.dismiss();
                }

                @Override
                public void onError(int Code, String error) {
                    Message msg = new Message();
                    msg.what = 4;
                    msg.obj = "error";
                    handlerUi.sendMessage(msg);
                    pd.dismiss();
                }
            });
        }

    }
    private void deleteMember(final SessionMember sessionMember){
        new AlertDialog.Builder(context)
                .setTitle("删除成员")
                .setMessage("是否删除"+sessionMember.getEntity().getName()+"？")
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        _session.getMembers().getRemote().sessionMemberDelete(sessionMember, new Back.Callback() {
                            @Override
                            public void onSuccess() {
                                listMembers.remove(sessionMember);
                                handlerUi.sendEmptyMessage(1);
                                ToastUtil.ToastMessage(context, "删除成功");
                            }

                            @Override
                            public void onError(int errorCode,String error) {
                            }
                        });
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .show();
    }

    private void getLocalMembers(){
        _session.getMembers().getMembers(new Back.Result<List<SessionMember>>() {
            @Override
            public void onSuccess(List<SessionMember> sessionMembers) {
                listMembers = sessionMembers;
                handlerUi.sendEmptyMessage(1);
            }

            @Override
            public void onError(int Code, String error) {
            }
        });
    }
    private void getRemmoteMembers(){
        _session.getMembers().getRemote().sync(new Back.Result<List<SessionMember>>() {
            @Override
            public void onSuccess(List<SessionMember> sessionMembers) {
                if(sessionMembers==null||sessionMembers.size()==0) {
                    ToastUtil.ToastMessage(context,"当前群已解散");
                    return;
                }
                listMembers.clear();
                for(int i =0;i<sessionMembers.size();i++){
                    if(EnumManage.UserStatus.active.toString().equals(sessionMembers.get(i).getEntity().getStatus())) {
                        listMembers.add(sessionMembers.get(i));
                    }
                    if(sessionMembers.get(i).getEntity().getUserId().equals(Users.getInstance().getCurrentUser().getEntity().getId())){
                        mysessionMember =sessionMembers.get(i);
                    }
                }
                if(listMembers.size()==0){
                    ToastUtil.ToastMessage(context,"当前群已解散");
                }
                handlerUi.sendEmptyMessage(1);
            }

            @Override
            public void onError(int Code, String error) {
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 100) {//刷新头像
            _session.getSessions().getSessionBySessionId(sessionId, new Back.Result<Session>() {
                @Override
                public void onSuccess(Session session) {
                    _session = session;
                    sessionAvatar = _session.getEntity().getAvatarUrl();
                    handlerUi.sendEmptyMessage(2);
                }

                @Override
                public void onError(int code, String error) {

                }
            });

        }else if (requestCode == 101) {
            Users.getInstance().getCurrentUser().getSessions().getSessionBySessionId(sessionId, new Back.Result<Session>() {
                @Override
                public void onSuccess(Session session) {
                    if(session.getEntity() != null){
                        sessionName = session.getEntity().getName();
                        tv_group_name.setText(sessionName);
                    }
                }

                @Override
                public void onError(int code, String error) {

                }
            });

        }else if (requestCode == 102) {
            getRemmoteMembers();
        }
    }
}
