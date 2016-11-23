package y2w.ui.activity;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.yun2win.demo.R;

import java.util.ArrayList;
import java.util.List;

import y2w.base.AppData;
import y2w.entities.MessageEntity;
import y2w.entities.searchEntities.SearchMessage;
import y2w.entities.searchEntities.SearchUserConversation;
import y2w.manage.Users;
import y2w.model.Contact;
import y2w.model.NewDataModel;
import y2w.model.Session;
import y2w.service.Back;
import y2w.ui.adapter.MessageSearchAdapter;
import y2w.ui.adapter.ResultListViewAdapter;

public class SearchMessageActivity extends Activity {

    private String searchKey="";
    private NewDataModel searchMessages;
    private LinearLayout varible_layout;
    private ListView result_list;
    private MessageSearchAdapter adapter;
    private Context context;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_more_search);
        context =this;
        searchMessages=AppData.getInstance().getSearchResults().get(0);
        Bundle bundle =this.getIntent().getExtras();
        searchKey=bundle.getString("searchkey");
        if(searchKey==null)
            searchKey="";
        initActionBar();
        varible_layout = (LinearLayout)findViewById(R.id.varible_view);
        result_list = (ListView)findViewById(R.id.search_result_list);
        varible_layout.setVisibility(View.VISIBLE);
        adapter = new MessageSearchAdapter(searchMessages.getSearchMessage().getMessages(),this);
        adapter.setUserInput(searchKey);
        adapter.setUserConversation(searchMessages.getSearchMessage().getUserConversation());
        result_list.setAdapter(adapter);
        result_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(searchMessages.getSearchMessage().getMessages()!=null&&searchMessages.getSearchMessage().getMessages().size()>position) {
                    final MessageEntity messageEntity =  searchMessages.getSearchMessage().getMessages().get(position);
                    Users.getInstance().getCurrentUser().getSessions().getSessionByTargetId(searchMessages.getSearchMessage().getUserConversation().getEntity().getTargetId(), searchMessages.getSearchMessage().getUserConversation().getEntity().getType(), new Back.Result<Session>() {
                        @Override
                        public void onSuccess(final Session session) {

                            Intent intent = new Intent(context, ChatActivity.class);
                            Bundle bundle = new Bundle();
                            bundle.putString("sessionid", session.getEntity().getId());
                            bundle.putString("sessiontype", session.getEntity().getType());
                            bundle.putString("otheruserId", session.getEntity().getOtherSideId());
                            bundle.putString("name", session.getEntity().getName());
                            bundle.putString("createTime", messageEntity.getCreatedAt());
                            intent.putExtras(bundle);
                            startActivity(intent);
                        }

                        @Override
                        public void onError(int errorCode, String error) {

                        }
                    });
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
        texttitle.setText(searchMessages.getSearchMessage().getUserConversation().getEntity().getName());
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
