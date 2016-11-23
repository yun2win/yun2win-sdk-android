package y2w.ui.activity;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.y2w.uikit.utils.StringUtil;
import com.y2w.uikit.utils.ToastUtil;
import com.yun2win.demo.R;

import java.util.List;

import y2w.base.AppData;
import y2w.manage.Users;
import y2w.model.Session;
import y2w.model.UserConversation;
import y2w.model.UserSession;
import y2w.service.Back;

/**
 * Created by yangrongfang on 2016/4/20.
 */
public class GroupNameActivity extends Activity{

    private String groupName;
    private String sessionId;
    private Session session;
    private EditText et_name;
    private Context context;
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            if(msg.what == 1){
                ToastUtil.ToastMessage(context, "保存成功");
                finish();
            }else{
                ToastUtil.ToastMessage(context, "保存失败");
            }
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_groupname);
        context = this;
        et_name = (EditText) findViewById(R.id.et_groupname);
        initData(this.getIntent().getExtras());
        initActionBar();
    }

    private void initData(Bundle bundle){
        if(bundle == null)
            return;
        sessionId = bundle.getString("sessionId","");
        groupName = bundle.getString("groupName","");
        et_name.setText(groupName);
       Users.getInstance().getCurrentUser().getSessions().getSessionBySessionId(sessionId, new Back.Result<Session>() {
           @Override
           public void onSuccess(Session s) {
               session = s;
           }

           @Override
           public void onError(int code, String error) {

           }
       });
    }

    private TextView texttitle;
    private TextView tv_oper;
    private void initActionBar(){
        ActionBar actionbar = getActionBar();
        actionbar.setDisplayShowTitleEnabled(false);
        actionbar.setDisplayShowHomeEnabled(false);
        actionbar.setDisplayShowCustomEnabled(true);
        actionbar.setCustomView(R.layout.actionbar_chat);
        texttitle = (TextView) actionbar.getCustomView().findViewById(R.id.text_title);
        tv_oper = (TextView) actionbar.getCustomView().findViewById(R.id.tv_right_oper);
        ImageButton imageButtonClose = (ImageButton) actionbar.getCustomView().findViewById(R.id.left_close);
        texttitle.setText("修改群名称");
        tv_oper.setVisibility(View.VISIBLE);
        tv_oper.setText("保存");

        tv_oper.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (session != null && session.getEntity() != null) {
                    String name = et_name.getText().toString();
                    if (StringUtil.isEmpty(name)) {
                        ToastUtil.ToastMessage(context,"群名称不为空");
                        return;
                    }
                    session.getEntity().setName(name);
                    session.getEntity().setNameChanged(true);
                    session.getSessions().getRemote().sessionUpdate(session,true, new Back.Result<Session>() {
                        @Override
                        public void onSuccess(Session session) {
                            AppData.isRefreshConversation = true;
                            UserConversation userConversation = Users.getInstance().getCurrentUser().getUserConversations().get(sessionId,session.getEntity().getType());
                            if(userConversation.getEntity() != null){
                                userConversation.getEntity().setName(session.getEntity().getName());
                                Users.getInstance().getCurrentUser().getUserConversations().addUserConversation(userConversation);
                            }
                            handler.sendEmptyMessage(1);
                            if(ChatActivity._context!=null)
                             ((ChatActivity)ChatActivity._context).sendSystemMessage(Users.getInstance().getCurrentUser().getEntity().getName()+"将群更名为"+session.getEntity().getName());
                        }

                        @Override
                        public void onError(int code, String error) {
                            handler.sendEmptyMessage(-1);
                        }
                    });
                }
            }
        });

        imageButtonClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch(keyCode){
            case KeyEvent.KEYCODE_BACK:
                finish();
                break;
        }
        return true;
    }
}
