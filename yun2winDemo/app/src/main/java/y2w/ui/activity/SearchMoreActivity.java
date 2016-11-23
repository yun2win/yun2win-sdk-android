package y2w.ui.activity;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
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
import android.widget.TextView;

import com.y2w.uikit.utils.StringUtil;
import com.yun2win.demo.R;

import java.util.ArrayList;
import java.util.List;

import y2w.base.AppData;
import y2w.db.MessageDb;
import y2w.db.SessionMemberDb;
import y2w.entities.MessageEntity;
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

public class SearchMoreActivity extends Activity {

    private String searchKey="";
    private List<NewDataModel> searchResults =new ArrayList<NewDataModel>();
    private LinearLayout varible_layout;
    private ListView result_list;
    private ResultListViewAdapter adapter;
    private Context context;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_more_search);
        context =this;
        searchResults=AppData.getInstance().getSearchResults();
        Bundle bundle =this.getIntent().getExtras();
        searchKey=bundle.getString("searchkey");
        if(searchKey==null)
            searchKey="";
        initActionBar();
        varible_layout = (LinearLayout)findViewById(R.id.varible_view);
        result_list = (ListView)findViewById(R.id.search_result_list);
        varible_layout.setVisibility(View.VISIBLE);
        adapter = new ResultListViewAdapter(searchResults,this);
        adapter.setUserInput(searchKey);
        result_list.setAdapter(adapter);
        result_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            final NewDataModel ndm = searchResults.get(position);
                            String type = ndm.getType();
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
                            } else if(type.equals("userconversationentity")){
                                SearchUserConversation conversation =  ndm.getSearchUserconversation();
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
                            }else if(type.equals("messageentity")){
                                SearchMessage messagemodel=ndm.getSearchMessage();
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
                            }
            }
        });
    }
    private void initActionBar(){
        ActionBar actionbar = getActionBar();
        actionbar.setDisplayShowTitleEnabled(false);
        actionbar.setDisplayShowHomeEnabled(false);
        actionbar.setDisplayShowCustomEnabled(true);
        actionbar.setCustomView(R.layout.actionbar_chat);
        TextView texttitle = (TextView) actionbar.getCustomView().findViewById(R.id.text_title);
        texttitle.setText("关键字'"+searchKey+"'"+"搜索");

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
}
