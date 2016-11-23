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
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.yun2win.demo.R;

import java.util.List;

import y2w.ui.dialog.Y2wDialog;
import y2w.manage.Users;
import y2w.model.DataSaveModuel;
import y2w.model.Session;
import y2w.model.UserSession;
import y2w.service.Back;
import y2w.ui.adapter.GroupAdapter;

/**
 * Created by hejie on 2016/3/14.
 * 个人名片界面
 */
public class GroupListActivity extends Activity{
    private Context context;
    private ListView lv_groups;
    private TextView noGroups;
    private GroupAdapter groupAdapter;
    private List<UserSession> listgroups;

    Handler handUiupdate = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if(msg.what==1){
                if(groupAdapter == null)
                    return;
                if(listgroups!=null&&listgroups.size()>0){
                    lv_groups.setVisibility(View.VISIBLE);
                    noGroups.setVisibility(View.GONE);
                }else{
                    lv_groups.setVisibility(View.GONE);
                    noGroups.setVisibility(View.VISIBLE);
                }
                groupAdapter.updateListView(listgroups);
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
        noGroups = (TextView) findViewById(R.id.nogroup);
        groupAdapter = new GroupAdapter(context);
        lv_groups.setAdapter(groupAdapter);
        lv_groups.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final UserSession userSession = listgroups.get(position);
                Users.getInstance().getCurrentUser().getSessions().getSessionBySessionId(userSession.getEntity().getSessionId(), new Back.Result<Session>() {
                    @Override
                    public void onSuccess(Session session) {
                        if (session == null) {
                            Toast.makeText(context, "sessionId 不能为空", Toast.LENGTH_SHORT);
                            return;
                        }
                        Intent intent = new Intent(context, ChatActivity.class);
                        Bundle bundle = new Bundle();
                        bundle.putString("sessionid", session.getEntity().getId());
                        bundle.putString("sessiontype", session.getEntity().getType());
                        bundle.putString("otheruserId", session.getEntity().getOtherSideId());
                        bundle.putString("name", userSession.getEntity().getName());
                        intent.putExtras(bundle);
                        startActivity(intent);
                    }

                    @Override
                    public void onError(int errorCode, String error) {

                    }
                });
            }
        });

        lv_groups.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                final UserSession userSession = listgroups.get(position);
                Y2wDialog dialog = new Y2wDialog(context);
                dialog.addOption("删除");
                dialog.show();
                dialog.setOnOptionClickListener(new Y2wDialog.onOptionClickListener() {
                    @Override
                    public void onOptionClick(String option, int position) {
                        Users.getInstance().getCurrentUser().getUserSessions().getRemote().userSessionDelete(userSession.getEntity().getId(), new Back.Callback() {
                            @Override
                            public void onSuccess() {
                                getGroups();
                            }

                            @Override
                            public void onError(int code, String error) {
                                Toast.makeText(context, "error", Toast.LENGTH_SHORT);
                            }
                        });
                    }
                });
                return true;
            }
        });
        getGroups();
    }

    private void getGroups(){
        Users.getInstance().getCurrentUser().getUserSessions().getRemote().sync(new Back.Result<List<UserSession>>(){
            @Override
            public void onSuccess(List<UserSession> userSessions) {
                listgroups =Users.getInstance().getCurrentUser().getUserSessions().getUserSessions();
                handUiupdate.sendEmptyMessage(1);
                DataSaveModuel.getInstance().listgroups = listgroups;
            }
            @Override
            public void onError(int code, String error) {
                Toast.makeText(context, "error", Toast.LENGTH_SHORT);
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
        ImageButton imageButtonSearch = (ImageButton) actionbar.getCustomView().findViewById(R.id.right_other_third);
        imageButtonclose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        imageButtonright.setVisibility(View.GONE);
        imageButtonSearch.setVisibility(View.GONE);
        imageButtonright.setImageResource(R.drawable.lyy_main_add);
        imageButtonright.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, SessionStartActivity.class);
                Bundle bundle = new Bundle();
                bundle.putBoolean("iscreate", true);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });
    }
}
