package y2w.ui.activity;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.yun2win.demo.R;

import java.util.ArrayList;
import java.util.List;

import y2w.manage.SessionMembers;
import y2w.manage.Users;
import y2w.model.Contact;
import y2w.model.DataSaveModuel;
import y2w.model.NewDataModel;
import y2w.model.Session;
import y2w.model.SessionMember;
import y2w.model.UserConversation;
import y2w.model.UserSession;
import y2w.service.Back;
import y2w.ui.adapter.ResultListViewAdapter;

public class SearchActivity extends Activity {

    private Context context;
    private String input_text;
    private EditText imageButtonSearch;
    private LinearLayout varible_layout;
    private ListView result_list;
    private ResultListViewAdapter adapter;
    private List<String> str_list;
    private List<String> str_list_1;
    private  List<UserSession> listgroups;
    private List<NewDataModel> selected_group;
    private List<Integer> is_selected;
    private List<Integer> is_selected_1;
    private List<Contact> contacts;
    private Session ss;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_search);
        context = this;
        initActionBar();
        initUi();
        initData();
        initListener();
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void initActionBar() {
        ActionBar actionbar = getActionBar();
        actionbar.setDisplayShowTitleEnabled(false);
        actionbar.setDisplayShowHomeEnabled(false);
        actionbar.setDisplayShowCustomEnabled(true);
        actionbar.setCustomView(R.layout.actionbar_search);

        ImageButton imageButtonclose = (ImageButton) actionbar.getCustomView().findViewById(R.id.search_left_close);
        imageButtonSearch = (EditText) actionbar.getCustomView().findViewById(R.id.search_box);
        imageButtonclose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        imageButtonSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                //输入前
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //输入中
            }

            @Override
            public void afterTextChanged(Editable s) {
                //输入后
                //获取输入后数据
                input_text = imageButtonSearch.getText().toString().trim();
                if(input_text.isEmpty()){
                    varible_layout.setVisibility(View.GONE);
                    return;
                }
                Log.v("text", input_text);
                is_selected.clear();
                is_selected_1.clear();
                selected_group.clear();
                for(int i=0;i<str_list.size();i++){
                    if(str_list.get(i).contains(input_text)&&str_list.get(i).indexOf(input_text)>0){
                        is_selected.add(i);
                    }
                }
                for(int i=0;i<str_list_1.size();i++){
                    if(str_list_1.get(i).contains(input_text)&&str_list_1.get(i).indexOf(input_text)>0){
                        is_selected_1.add(i);
                    }
                }
                if(is_selected.size()>0) {
                    for (int j = 0; j < is_selected.size(); j++) {
                        NewDataModel ndm = new NewDataModel();
                        ndm.setName(listgroups.get(is_selected.get(j)).getEntity().getName());
                        ndm.setHead_url(listgroups.get(is_selected.get(j)).getEntity().getAvatarUrl());
                        ndm.setSession_id(listgroups.get(is_selected.get(j)).getEntity().getSessionId());
                        ndm.setType("1");//1代表群组
                        selected_group.add(ndm);
                    }

                }
                if(is_selected_1.size()>0) {
                    for (int j = 0; j < is_selected.size(); j++) {
                        NewDataModel ndm = new NewDataModel();
                        ndm.setName(contacts.get(is_selected.get(j)).getEntity().getName());
                        ndm.setHead_url(contacts.get(is_selected.get(j)).getEntity().getAvatarUrl());
                        ndm.setSession_id(contacts.get(is_selected.get(j)).getEntity().getId());
                        ndm.setUserId(contacts.get(is_selected.get(j)).getEntity().getUserId());
                        ndm.setEmail(contacts.get(is_selected.get(j)).getEntity().getEmail());
                        ndm.setType("2");//2代表联系人
                        selected_group.add(ndm);
                    }

                }
                varible_layout.setVisibility(View.VISIBLE);
                //数据更新
                adapter.notifyDataSetChanged();
            }
        });
    }

    private void initUi(){
        varible_layout = (LinearLayout)findViewById(R.id.varible_view);
        result_list = (ListView)findViewById(R.id.search_result_list);
    }
    private void initData(){
        varible_layout.setVisibility(View.GONE);
        //
        if(Integer.parseInt(getIntent().getStringExtra("index"))==0){
            List<UserConversation> conversations = DataSaveModuel.getInstance().conversations;
            for(int i=0;i<conversations.size();i++){
                conversations.get(i).getSession(new Back.Result<Session>() {
                    @Override
                    public void onSuccess(Session session) {
                        ss=session;
                    }

                    @Override
                    public void onError(int code, String error) {

                    }
                });
                //会话成员信息
                new SessionMembers(ss).getAllMembers(new Back.Result<List<SessionMember>>() {
                    @Override
                    public void onSuccess(List<SessionMember> sessionMembers) {
                        //返回某条会话里面所在的成员（包括群和个人对话）

                    }

                    @Override
                    public void onError(int code, String error) {

                    }
                });

            }

        }else {
            selected_group = new ArrayList<NewDataModel>();
            is_selected = new ArrayList<Integer>();
            is_selected_1 = new ArrayList<Integer>();
            //联系人
            contacts = Users.getInstance().getCurrentUser().getContacts().getContacts();
            //群组
            if(listgroups == null){
                listgroups = new ArrayList<UserSession>();
            }

            str_list = new ArrayList<String>();
            str_list_1 = new ArrayList<String>();
            if (listgroups.size() > 0) {
                for (int i = 0; i < listgroups.size(); i++) {
                    str_list.add(listgroups.get(i).getEntity().getName());
                }
            }
            if (contacts.size() > 0) {
                for (int j = 0; j < contacts.size(); j++) {
                    str_list_1.add(contacts.get(j).getEntity().getName());
                }
            }
        }

       adapter = new ResultListViewAdapter(selected_group,this);
        result_list.setAdapter(adapter);
    }
    private void initListener(){
        result_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final NewDataModel ndm = selected_group.get(position);
                String type = ndm.getType();
                if (type.equals("1")) {
                    Users.getInstance().getCurrentUser().getSessions().getSessionBySessionId(ndm.getSession_id(), new Back.Result<Session>() {
                        @Override
                        public void onSuccess(Session session) {
                            if (session == null) {
                                Toast.makeText(context, "sessionId不能为空", Toast.LENGTH_SHORT);
                                return;
                            }
                            Intent intent = new Intent(context, ChatActivity.class);
                            Bundle bundle = new Bundle();
                            bundle.putString("sessionid", session.getEntity().getId());
                            bundle.putString("sessiontype", session.getEntity().getType());
                            bundle.putString("otheruserId", session.getEntity().getOtherSideId());
                            bundle.putString("name", ndm.getName());
                            intent.putExtras(bundle);
                            startActivity(intent);
                            finish();
                        }

                        @Override
                        public void onError(int errorCode, String error) {

                        }
                    });
                }
                else{
                    Intent intent = new Intent(context, ContactInfoActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putString("otheruserid",ndm.getUserId());
                    bundle.putString("avatarUrl", ndm.getHead_url());
                    bundle.putString("username", ndm.getName());
                    bundle.putString("account", ndm.getEmail());
                    intent.putExtras(bundle);
                    startActivity(intent);
                }
            }
        });
    }
}
