package y2w.ui.activity;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.y2w.uikit.utils.StringUtil;
import com.y2w.uikit.utils.ToastUtil;
import com.yun2win.demo.R;

import java.util.List;

import y2w.manage.Users;
import y2w.model.UserSession;
import y2w.service.Back;

/**
 * Created by yangrongfang on 2016/4/20.
 */
public class GroupNameActivity extends Activity{

    private String groupName;
    private String sessionId;
    private UserSession userSession;
    private EditText et_name;
    private Context context;
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
        userSession = Users.getInstance().getCurrentUser().getUserSessions().getUserSessionBySessionId(sessionId);
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
                if (userSession != null && userSession.getEntity() != null) {
                    String name = et_name.getText().toString();
                    if (StringUtil.isEmpty(name)) {
                        ToastUtil.ToastMessage(context,"群名称不为空");
                        return;
                    }
                    userSession.getEntity().setName(name);
                    Users.getInstance().getCurrentUser().getUserSessions().getRemote().userSessionUpdate(userSession, new Back.Result<UserSession>() {
                        @Override
                        public void onSuccess(UserSession userSession) {
                            Users.getInstance().getCurrentUser().getUserSessions().getRemote().sync(new Back.Result<List<UserSession>>() {
                                @Override
                                public void onSuccess(List<UserSession> userSessions) {
                                    ToastUtil.ToastMessage(context,"保存成功");
                                    finish();
                                }

                                @Override
                                public void onError(int code, String error) {
                                    ToastUtil.ToastMessage(context,"同步更新失败");
                                }
                            });
                        }

                        @Override
                        public void onError(int code, String error) {
                            ToastUtil.ToastMessage(context,"error");
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
}
