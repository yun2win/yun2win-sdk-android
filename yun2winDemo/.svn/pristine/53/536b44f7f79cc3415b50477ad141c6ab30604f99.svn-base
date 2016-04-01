package y2w.ui.activity;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.y2w.uikit.customcontrols.imageview.HeadImageView;
import com.y2w.uikit.utils.HeadTextBgProvider;
import com.y2w.uikit.utils.StringUtil;
import com.y2w.uikit.utils.ToastUtil;
import com.yun2win.demo.R;

import java.util.List;

import y2w.manage.EnumManage;
import y2w.manage.Users;
import y2w.model.Contact;
import y2w.model.Session;
import y2w.model.UserSession;
import y2w.service.Back;
import y2w.service.ErrorCode;
import y2w.ui.adapter.GroupAdapter;

/**
 * Created by hejie on 2016/3/14.
 * 个人名片界面
 */
public class GroupListActivity extends Activity{
    private Context context;
    private ListView lv_groups;
    private GroupAdapter groupAdapter;
    private List<UserSession> listgroups;
    Handler handUiupdate = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if(msg.what==1){
                groupAdapter.notifyDataSetChanged();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_group);
        context = this;
        initActionBar();
        lv_groups = (ListView) findViewById(R.id.lv_groups);
        groupAdapter = new GroupAdapter(context);
        lv_groups.setAdapter(groupAdapter);
        getgroupsDate();
        groupAdapter.setListViewdate(listgroups);
        lv_groups.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                UserSession userSession = listgroups.get(position);
                Users.getInstance().getCurrentUser().getSessions().getSessionBySessionId(userSession.getEntity().getSessionId(), new Back.Result<Session>() {
                    @Override
                    public void onSuccess(Session session) {
                        if (session == null) {
                            Toast.makeText(context, "sessionId 不能为空", Toast.LENGTH_SHORT);
                            return;
                        }
                        Intent intent = new Intent(context, ChatActivity.class);
                        Bundle bundle = new Bundle();
                        bundle.putSerializable("session", session);
                        intent.putExtras(bundle);
                        startActivity(intent);
                        finish();
                    }

                    @Override
                    public void onError(int errorCode, String error) {

                    }
                });
            }
        });
    }

    private void getgroupsDate(){
        Users.getInstance().getCurrentUser().getUserSessions().getRemote().sync(new Back.Result<List<UserSession>>(){
            @Override
            public void onSuccess(List<UserSession> userSessions) {
                listgroups =Users.getInstance().getCurrentUser().getUserSessions().getUserSessions();
                handUiupdate.sendEmptyMessage(1);
            }
            @Override
            public void onError(int code, String error) {

            }
        });
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
        texttitle.setText("我的群");

        ImageButton imageButtonclose = (ImageButton) actionbar.getCustomView().findViewById(R.id.left_close);
        ImageButton imageButtonright = (ImageButton) actionbar.getCustomView().findViewById(R.id.right_add);
        imageButtonclose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        imageButtonright.setVisibility(View.VISIBLE);
        imageButtonright.setImageResource(R.drawable.lyy_main_add);
        imageButtonright.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, SessionStartActivity.class);
                Bundle bundle = new Bundle();
                bundle.putBoolean("iscreate",true);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });
    }
}
