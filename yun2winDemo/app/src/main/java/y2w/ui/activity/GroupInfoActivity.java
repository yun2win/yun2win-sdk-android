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
import android.widget.TextView;
import android.widget.Toast;

import com.y2w.uikit.customcontrols.imageview.HeadImageView;
import com.y2w.uikit.utils.HeadTextBgProvider;
import com.y2w.uikit.utils.StringUtil;
import com.y2w.uikit.utils.ToastUtil;
import com.yun2win.demo.R;

import java.util.List;

import y2w.base.AppData;
import y2w.common.CallBackUpdate;
import y2w.manage.EnumManage;
import y2w.manage.Users;
import y2w.model.Contact;
import y2w.model.Session;
import y2w.model.SessionMember;
import y2w.service.Back;
import y2w.service.ErrorCode;
import y2w.ui.adapter.GroupMemberAdapter;

/**
 * Created by hejie on 2016/3/14.
 * 个人名片界面
 */
public class GroupInfoActivity extends Activity{
    private Context context;
    private Session _session;
    private GroupMemberAdapter groupMemberAdapter;
    private String sessionId,sessionName,sessionAvatar;
    private List<SessionMember> listMembers;
    private SessionMember mysessionMember;
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
        getsession();
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
    private LinearLayout ll_add_member;
    private Button bt_out_group;
    private void initUi(){
        iv_group_head = (HeadImageView) findViewById(R.id.group_head_image);
        tv_group_name = (TextView) findViewById(R.id.group_name_text);
        gv_member_preview = (GridView) findViewById(R.id.gv_member_preview);
        ll_add_member = (LinearLayout) findViewById(R.id.add_membaer_linear);
        bt_out_group = (Button) findViewById(R.id.bt_group_delete);
        tv_group_name.setText(sessionName);
        iv_group_head.loadBuddyAvatarbyurl(sessionAvatar, R.drawable.default_group_icon);
        iv_group_head.setBackgroundResource(HeadTextBgProvider.getTextBg(StringUtil.parseAscii(sessionId)));
        groupMemberAdapter = new GroupMemberAdapter(context);
        gv_member_preview.setAdapter(groupMemberAdapter);
    }
    private void addEvent(){
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
                startActivityForResult(intent, 100);
            }
        });
        bt_out_group.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (_session == null) {
                    ToastUtil.ToastMessage(context, "正在初始化数据");
                    return;
                }
                _session.getMembers().getRemote().sessionMemberDelete(mysessionMember, new Back.Callback() {
                    @Override
                    public void onSuccess() {
                        Users.getInstance().getCurrentUser().getUserConversations().delete(_session.getEntity().getId());
                        ((ChatActivity)ChatActivity._context).finish();
                        finish();
                    }

                    @Override
                    public void onError(int errorCode,String error) {
                        ToastUtil.ToastMessage(context, "退出失败");
                    }
                });
            }
        });

        gv_member_preview.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                if (EnumManage.GroupRole.master.toString().equals(mysessionMember.getEntity().getRole())) {
                    final SessionMember sessionMember = listMembers.get(position);
                    if (sessionMember.getEntity().getUserId().equals(Users.getInstance().getCurrentUser().getEntity().getId())) {
                        ToastUtil.ToastMessage(context, "不能删除自己");
                        return false;
                    }
                    deleteMember(sessionMember);

                } else {
                    ToastUtil.ToastMessage(context, "您没有权限删除");
                }
                return false;
            }
        });
    }
    private void getsession(){
        final ProgressDialog pd = new ProgressDialog(context);
        pd.setCanceledOnTouchOutside(false);
        pd.setMessage(getString(R.string.loading));
        Users.getInstance().getCurrentUser().getSessions().getRemote().getSession(sessionId, EnumManage.SessionType.group.toString(), new Back.Result<Session>() {
            @Override
            public void onSuccess(Session session) {
                _session = session;
                getLocalMembers();
                getmyMembers();
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
    private void getmyMembers(){
        _session.getMembers().getMember(Users.getInstance().getCurrentUser().getEntity().getId(), new Back.Result<SessionMember>() {
            @Override
            public void onSuccess(SessionMember sessionMember) {
                mysessionMember =sessionMember;
            }
            @Override
            public void onError(int Code, String error) {
                ToastUtil.ToastMessage(context, "退出失败");
            }
        });
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
                listMembers = sessionMembers;
                handlerUi.sendEmptyMessage(1);
            }

            @Override
            public void onError(int Code, String error) {
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 100) {
            getRemmoteMembers();
        }
    }
}
