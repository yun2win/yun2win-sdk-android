package y2w.ui.activity;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.y2w.uikit.utils.StringUtil;
import com.yun2win.demo.R;

import java.util.ArrayList;
import java.util.List;

import y2w.base.AppData;
import y2w.db.MessageDb;
import y2w.db.SessionDb;
import y2w.db.SessionMemberDb;
import y2w.entities.MessageEntity;
import y2w.entities.SessionEntity;
import y2w.entities.SessionMemberEntity;
import y2w.entities.searchEntities.SearchMessage;
import y2w.entities.searchEntities.SearchUserConversation;
import y2w.manage.Users;
import y2w.model.Contact;
import y2w.model.NewDataModel;
import y2w.model.Session;
import y2w.model.UserConversation;
import y2w.service.Back;
import y2w.ui.adapter.ResultListViewAdapter;

public class SearchActivity extends Activity {

    private Context context;
    private String input_text;
    private EditText imageButtonSearch;
    private LinearLayout varible_layout;
    private ListView result_list;
    private ResultListViewAdapter adapter;
    private List<NewDataModel> searchResults =new ArrayList<NewDataModel>();
    private List<NewDataModel> displayResults =new ArrayList<NewDataModel>();

    Handler updatehandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if(msg.what==1){
                List<NewDataModel> tempmodel = (List<NewDataModel>) msg.obj;
                if(tempmodel.size()>0){
                    varible_layout.setVisibility(View.VISIBLE);
                }
                //数据更新
                searchResults.clear();
                displayResults.clear();
                if(tempmodel.size()>0) {
                    searchResults.addAll(tempmodel);
                    int contactnum =0,sisonnum=0,messagenum=0;
                    for(int i =0;i<tempmodel.size();i++){
                       if(tempmodel.get(i).getType().equals("contactentity")){
                           contactnum++;
                           if(contactnum<4) {
                               displayResults.add(tempmodel.get(i));
                           }else if(contactnum ==4){
                               NewDataModel contactentity = new NewDataModel("contactentity", null, null,null);
                               displayResults.add(contactentity);
                           }
                       }else if(tempmodel.get(i).getType().equals("userconversationentity")){
                           sisonnum++;
                           if(sisonnum<4) {
                               displayResults.add(tempmodel.get(i));
                           }else if(sisonnum ==4){
                               NewDataModel sessionentity = new NewDataModel("userconversationentity", null, null,null);
                               displayResults.add(sessionentity);
                           }
                       }else if(tempmodel.get(i).getType().equals("messageentity")){
                           messagenum++;
                           if(messagenum<4) {
                               displayResults.add(tempmodel.get(i));
                           }else if(messagenum ==4){
                               NewDataModel messageentity = new NewDataModel("messageentity", null, null,null);
                               displayResults.add(messageentity);
                           }
                       }
                    }
                }

                adapter.setUserInput((String) msg.getData().get("searchkey"));
                try {
                    adapter.notifyDataSetChanged();
                }catch (Exception e){}
            }
        }
    };
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
                        final String nameKey =AppData.getInstance().getsampchina(input_text);
                        AppData.getInstance().getLooperExecutorDb().execute(new Runnable() {
                            @Override
                            public void run() {
                                List<NewDataModel> results =new ArrayList<NewDataModel>();
                                String myuserid = Users.getInstance().getCurrentUser().getEntity().getId();
                                List<Contact> searchContacts = Users.getInstance().getCurrentUser().getContacts().getContactsByNameKey(nameKey);
                                if (searchContacts != null && searchContacts.size() > 0){
                                    for(int i = 0;i<searchContacts.size();i++) {
                                        NewDataModel contactentity = new NewDataModel("contactentity", searchContacts.get(i), null,null);
                                        results.add(contactentity);
                                    }
                                }
                                List<NewDataModel> sessionresults =new ArrayList<NewDataModel>();
                                List<NewDataModel> messageresults =new ArrayList<NewDataModel>();
                                List<UserConversation> searchUserconversations = Users.getInstance().getCurrentUser().getUserConversations().getUserConversations();
                                if (searchUserconversations != null && searchUserconversations.size() > 0) {
                                    for (int i = 0; i < searchUserconversations.size(); i++) {
                                        String targetId =searchUserconversations.get(i).getEntity().getTargetId();
                                        //会话搜索
                                        if (searchUserconversations.get(i).getEntity().getType().equals("group")) {
                                            String usersationname =searchUserconversations.get(i).getEntity().getSimpchinaname();
                                           if(!StringUtil.isEmpty(usersationname) && usersationname.indexOf(nameKey)!=-1){//会话名称搜索
                                               SearchUserConversation searchUserConversation = new SearchUserConversation();
                                               searchUserConversation.setUserConversation(searchUserconversations.get(i));
                                               NewDataModel usersesiondata = new NewDataModel("userconversationentity", null, searchUserConversation,null);
                                               sessionresults.add(usersesiondata);
                                           }else{//会话成员搜索
                                               List<SessionMemberEntity> sessionMembers = SessionMemberDb.localquery(myuserid, targetId);
                                               List<SessionMemberEntity> tampMembers = new ArrayList<SessionMemberEntity>();
                                               for(int j =0;j<sessionMembers.size();j++){
                                                   String memberName= sessionMembers.get(j).getSimpchinaname();
                                                   if(!StringUtil.isEmpty(memberName) && memberName.indexOf(nameKey)!=-1){
                                                        tampMembers.add(sessionMembers.get(j));
                                                   }
                                               }
                                               if(tampMembers.size()>0){
                                                   SearchUserConversation searchUserConversation = new SearchUserConversation();
                                                   searchUserConversation.setUserConversation(searchUserconversations.get(i));
                                                   searchUserConversation.addAllSessionMeberEntity(tampMembers);
                                                   NewDataModel usersesiondata = new NewDataModel("userconversationentity", null, searchUserConversation,null);
                                                   sessionresults.add(usersesiondata);
                                               }
                                           }
                                        }
                                        //消息搜索

                                        SessionEntity entity = SessionDb.queryByTargetId(myuserid,targetId,searchUserconversations.get(i).getEntity().getType());
                                        if(entity!=null) {
                                            List<MessageEntity> resultmessages = MessageDb.querySessionTextMessageByKey(myuserid, entity.getId(), nameKey);
                                            if (resultmessages.size() > 0) {
                                                SearchMessage searchMessage = new SearchMessage();
                                                searchMessage.setUserConversation(searchUserconversations.get(i));
                                                searchMessage.addAllMessages(resultmessages);
                                                NewDataModel usersesiondata = new NewDataModel("messageentity", null, null, searchMessage);
                                                messageresults.add(usersesiondata);
                                            }
                                        }
                                    }
                                }
                                if(sessionresults.size()>0){
                                    results.addAll(sessionresults);
                                }
                                if(messageresults.size()>0){
                                    results.addAll(messageresults);
                                }

                                //List<SessionMemberEntity> searchSessionmembers = Users.getInstance().getCurrentUser().getAllMembersBynameKey(myuserid,nameKey);//会话成员

                                Message msg = new Message();
                                Bundle bundle = new Bundle();
                                bundle.putString("searchkey",nameKey);
                                msg.setData(bundle);
                                msg.obj = results;
                                msg.what = 1;
                                updatehandler.sendMessage(msg);
                            }
                 });
            }
        });
    }

    private void initUi(){
        varible_layout = (LinearLayout)findViewById(R.id.varible_view);
        result_list = (ListView)findViewById(R.id.search_result_list);
    }
    private void initData(){
        varible_layout.setVisibility(View.GONE);
       adapter = new ResultListViewAdapter(displayResults,this);
        result_list.setAdapter(adapter);
    }
    private void initListener(){
        result_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final NewDataModel ndm = displayResults.get(position);
                String type = ndm.getType();
                if (type.equals("contactentity")) {
                    Contact contact =ndm.getSearchContact();
                    if(contact!=null) {
                        Intent intent = new Intent(context, ContactInfoActivity.class);
                        Bundle bundle = new Bundle();
                        bundle.putString("otheruserid", contact.getEntity().getUserId());
                        bundle.putString("avatarUrl", contact.getEntity().getAvatarUrl());
                        bundle.putString("username", contact.getEntity().getName());
                        bundle.putString("account", contact.getEntity().getEmail());
                        intent.putExtras(bundle);
                        startActivity(intent);
                    }else{
                        AppData.getInstance().getSearchResults().clear();
                        List<NewDataModel> moreResults =new ArrayList<NewDataModel>();
                        for(int i =0;i<searchResults.size();i++){
                            if(searchResults.get(i).getType().equals("contactentity")){
                                moreResults.add(searchResults.get(i));
                            }
                        }
                       AppData.getInstance().getSearchResults().addAll(moreResults);
                        Intent intent = new Intent(context, SearchMoreActivity.class);
                        Bundle bundle = new Bundle();
                        bundle.putString("searchkey", adapter.getUserInput());
                        intent.putExtras(bundle);
                        startActivity(intent);
                    }
                }
                else if(type.equals("userconversationentity")){
                    SearchUserConversation conversation =  ndm.getSearchUserconversation();
                    if(conversation!=null) {
                        Users.getInstance().getCurrentUser().getSessions().getSessionByTargetId(conversation.getUserConversation().getEntity().getTargetId(), conversation.getUserConversation().getEntity().getType(), new Back.Result<Session>() {
                            @Override
                            public void onSuccess(final Session session) {
                                Intent intent = new Intent(context, ChatActivity.class);
                                Bundle bundle = new Bundle();
                                bundle.putString("sessionid", session.getEntity().getId());
                                bundle.putString("sessiontype", session.getEntity().getType());
                                bundle.putString("otheruserId", session.getEntity().getOtherSideId());
                                bundle.putString("name", session.getEntity().getName());
                                intent.putExtras(bundle);
                                startActivity(intent);
                            }

                            @Override
                            public void onError(int errorCode, String error) {

                            }
                        });
                    }else{
                        AppData.getInstance().getSearchResults().clear();
                        List<NewDataModel> moreResults =new ArrayList<NewDataModel>();
                        for(int i =0;i<searchResults.size();i++){
                            if(searchResults.get(i).getType().equals("userconversationentity")){
                                moreResults.add(searchResults.get(i));
                            }
                        }
                        AppData.getInstance().getSearchResults().addAll(moreResults);
                        Intent intent = new Intent(context, SearchMoreActivity.class);
                        Bundle bundle = new Bundle();
                        bundle.putString("searchkey", adapter.getUserInput());
                        intent.putExtras(bundle);
                        startActivity(intent);
                    }
                }else if(type.equals("messageentity")){
                    SearchMessage messagemodel=ndm.getSearchMessage();
                    if(messagemodel!=null){
                        if(messagemodel.getMessages().size()==1){
                            Users.getInstance().getCurrentUser().getSessions().getSessionByTargetId(messagemodel.getUserConversation().getEntity().getTargetId(), messagemodel.getUserConversation().getEntity().getType(), new Back.Result<Session>() {
                                @Override
                                public void onSuccess(final Session session) {
                                    Intent intent = new Intent(context, ChatActivity.class);
                                    Bundle bundle = new Bundle();
                                    bundle.putString("sessionid", session.getEntity().getId());
                                    bundle.putString("sessiontype", session.getEntity().getType());
                                    bundle.putString("otheruserId", session.getEntity().getOtherSideId());
                                    bundle.putString("name", session.getEntity().getName());
                                    bundle.putString("createTime", session.getEntity().getCreatedAt());
                                    intent.putExtras(bundle);
                                    startActivity(intent);
                                }

                                @Override
                                public void onError(int errorCode, String error) {

                                }
                            });
                        }else{
                            AppData.getInstance().getSearchResults().clear();
                            AppData.getInstance().getSearchResults().add(ndm);
                            Intent intent = new Intent(context, SearchMessageActivity.class);
                            Bundle bundle = new Bundle();
                            bundle.putString("searchkey", adapter.getUserInput());
                            intent.putExtras(bundle);
                            startActivity(intent);
                        }
                    }else{
                        AppData.getInstance().getSearchResults().clear();
                        List<NewDataModel> moreResults =new ArrayList<NewDataModel>();
                        for(int i =0;i<searchResults.size();i++){
                            if(searchResults.get(i).getType().equals("messageentity")){
                                moreResults.add(searchResults.get(i));
                            }
                        }
                        AppData.getInstance().getSearchResults().addAll(moreResults);
                        Intent intent = new Intent(context, SearchMoreActivity.class);
                        Bundle bundle = new Bundle();
                        bundle.putString("searchkey", adapter.getUserInput());
                        intent.putExtras(bundle);
                        startActivity(intent);
                    }
                }
            }
        });
    }
}
