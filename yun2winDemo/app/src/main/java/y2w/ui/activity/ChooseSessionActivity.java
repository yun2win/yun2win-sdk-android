package y2w.ui.activity;

import android.annotation.TargetApi;
import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSONObject;
import com.y2w.uikit.utils.HeadTextBgProvider;
import com.y2w.uikit.utils.StringUtil;
import com.y2w.uikit.utils.ToastUtil;
import com.yun2win.demo.R;

import java.util.ArrayList;
import java.util.List;

import y2w.base.AppContext;
import y2w.base.AppData;
import y2w.base.PushService;
import y2w.common.CallBackUpdate;
import y2w.common.HeadImageView;
import y2w.db.DaoManager;
import y2w.entities.MessageEntity;
import y2w.manage.EnumManage;
import y2w.manage.Users;
import y2w.model.Contact;
import y2w.model.MessageModel;
import y2w.model.Session;
import y2w.model.User;
import y2w.model.UserConversation;
import y2w.model.messages.MessageCrypto;
import y2w.model.messages.MessageType;
import y2w.service.Back;
import y2w.ui.adapter.ChooseRepeatAdapter;

/**
 * Created by hejie on 2016/3/14.
 * 选择会话转发
 */
public class ChooseSessionActivity extends Activity{
   private MessageEntity repeatMessage;
    private EditText search_box;
    private TextView create_session,session_tag;
    private ListView choose_session;
    private ChooseRepeatAdapter chooseRepeatAdapter;
    private String choosetype;
    private Intent intent;
    List<UserConversation> conversations = new ArrayList<UserConversation>();
    List<Contact> contacts = new ArrayList<Contact>();
    private String shareText,leavemessage;
    private Uri imageUri;
    private String sharetype;//image text
    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if(msg.what==1){
                List<UserConversation> tmpconversations = (List<UserConversation>) msg.obj;
                conversations.clear();
                if(tmpconversations!=null&&tmpconversations.size()>0){
                    for(int i = 0;i<tmpconversations.size();i++){
                        String type = tmpconversations.get(i).getEntity().getType();
                        if(type!=null){
                            if(type.equals(EnumManage.SessionType.group.toString())||type.equals(EnumManage.SessionType.p2p.toString())){
                                conversations.add(tmpconversations.get(i));
                            }
                        }
                    }
                }
                chooseRepeatAdapter.setListViewdate(conversations,null,"conversation");
            }else if(msg.what ==2){
                contacts = (List<Contact>) msg.obj;
                chooseRepeatAdapter.setListViewdate(null,contacts,"contact");
            }
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choosesession);
        getExtras(getIntent().getExtras());
        initActionBar();
        initUI();
        if(StringUtil.isEmpty(choosetype)||choosetype.equals("conversation")){
            searchConversationKey("");
        }else if(choosetype.equals("contact")){
            searchContactKey("");
        }
        AppData.getInstance().getChooseSessionActivitys().add(this);
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
        texttitle.setText("选择");

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

        if(bundle == null)
            return;
        repeatMessage = (MessageEntity) bundle.getSerializable("repeatMessage");
        choosetype = bundle.getString("type");

        shareText =  bundle.getString("sharecontext");
        sharetype = bundle.getString("sharetype");

        if(choosetype==null)
            choosetype ="conversation";
        Intent intent = getIntent();
        String action = intent.getAction();
        String type = intent.getType();
        if (Intent.ACTION_SEND.equals(action) && type != null) {
            if ("text/plain".equals(type)) {
                String sharedText = intent.getStringExtra(Intent.EXTRA_TEXT);
                String sharedTitle = intent.getStringExtra(Intent.EXTRA_TITLE);
                if(!StringUtil.isEmpty(sharedText)){
                    this.shareText = sharedText;
                    this.sharetype = "text";
                }else{
                    ToastUtil.ToastMessage(this, "分享内容为空！");
                }
            } /*else if (type.startsWith("image/")) {
                Uri imageUri = (Uri) intent.getParcelableExtra(Intent.EXTRA_STREAM);
                if(imageUri!=null){
                    this.imageUri =imageUri;
                    sharetype="image";
                }
            }*/
        }

        String account = Users.getInstance().getCurrentUser().getEntity().getAccount();
        if(StringUtil.isEmpty(account)){
            if(AppData.getInstance().getMainActivity()!=null) {
                try {
                    Users.getInstance().getCurrentUser().getImBridges().disConnect();
                    DaoManager.getInstance(AppContext.getAppContext()).close();
                    AppData.getInstance().getMainActivity().finish();
                } catch (Exception e) {
                }
            }
            Intent intentSplash = new Intent(this, SplashActivity.class);
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("activity","chooseSessionActivity");
            JSONObject jsonresult = new JSONObject();
            jsonresult.put("context",shareText);
            jsonresult.put("type",sharetype);
            jsonObject.put("result",jsonresult);
            intentSplash.putExtra("skipActivity",jsonObject.toJSONString());
            startActivity(intentSplash);
            //ToastUtil.ToastMessage(this,"请先登录");
            finish();
            return;
        }
    }
    private void initUI(){
        search_box = (EditText) findViewById(R.id.search_box);
        create_session = (TextView) findViewById(R.id.create_session);
        session_tag = (TextView) findViewById(R.id.session_tag);
        choose_session = (ListView) findViewById(R.id.choose_session);
        chooseRepeatAdapter = new ChooseRepeatAdapter(this);
        choose_session.setAdapter(chooseRepeatAdapter);
        choose_session.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(choosetype.equals("conversation")){
                    if(conversations!=null&&conversations.size()>position){
                         UserConversation userConversation = conversations.get(position);
                        if(userConversation!=null)
                         sendrepateMessage( userConversation.getEntity().getName(),userConversation.getEntity().getTargetId(), userConversation.getEntity().getType());
                    }
                }else if(choosetype.equals("contact")){
                    if(contacts!=null&&contacts.size()>position){
                        Contact contact = contacts.get(position);
                       if(contact!=null)
                           sendrepateMessage( contact.getEntity().getName(),contact.getEntity().getUserId(), EnumManage.SessionType.p2p.toString());
                    }
                }
            }
        });
        search_box.addTextChangedListener(new TextWatcher(){
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
            @Override
            public void afterTextChanged(Editable s) {
                String input_text = search_box.getText().toString().trim();
                 String nameKey =AppData.getInstance().getsampchina(input_text);
                if(choosetype.equals("conversation")){
                    searchConversationKey(nameKey);
                }else if(choosetype.equals("contact")){
                    searchContactKey(nameKey);
                }
            }
        });
        if(choosetype!=null){
            if(choosetype.equals("conversation")){
                create_session.setVisibility(View.VISIBLE);
                create_session.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(ChooseSessionActivity.this, ChooseSessionActivity.class);
                        Bundle bundle = new Bundle();
                        bundle.putSerializable("repeatMessage",repeatMessage);
                        bundle.putString("type","contact");
                        intent.putExtras(bundle);
                        startActivity(intent);
                    }
                });
                session_tag.setText("最近交流");
            }else if(choosetype.equals("contact")){
                create_session.setVisibility(View.GONE);
                session_tag.setText("联系人");
            }
        }
    }
    private void searchConversationKey(final String nameKey){
        AppData.getInstance().getLooperExecutorDb().execute(new Runnable() {
            @Override
            public void run() {
                if(StringUtil.isEmpty(nameKey)){
                    List<UserConversation> searchUserconversations = Users.getInstance().getCurrentUser().getUserConversations().getUserConversations();
                    if(searchUserconversations==null)
                        searchUserconversations = new ArrayList<UserConversation>();
                    Message msg = new Message();
                    msg.what=1;
                    msg.obj = searchUserconversations;
                    handler.sendMessage(msg);
                }else {
                    List<UserConversation> searchUserconversations = Users.getInstance().getCurrentUser().getUserConversations().getUserConversationsByNameKey(nameKey);
                    if(searchUserconversations==null)
                        searchUserconversations = new ArrayList<UserConversation>();
                    Message msg = new Message();
                    msg.what=1;
                    msg.obj = searchUserconversations;
                    handler.sendMessage(msg);
                }
            }});
    }
    private void searchContactKey(final String nameKey){
        AppData.getInstance().getLooperExecutorDb().execute(new Runnable() {
            @Override
            public void run() {
                if(StringUtil.isEmpty(nameKey)){
                    List<Contact> searchContacts = Users.getInstance().getCurrentUser().getContacts().getContacts();
                    if(searchContacts==null)
                        searchContacts = new ArrayList<Contact>();
                    Message msg = new Message();
                    msg.what=2;
                    msg.obj = searchContacts;
                    handler.sendMessage(msg);
                }else {
                    List<Contact> searchContacts = Users.getInstance().getCurrentUser().getContacts().getContactsByNameKey(nameKey);
                    if(searchContacts==null)
                        searchContacts = new ArrayList<Contact>();
                    Message msg = new Message();
                    msg.what=2;
                    msg.obj = searchContacts;
                    handler.sendMessage(msg);
                }
       }});
    }

    private void sendrepateMessage(final String name, final String targetId, final String type){

        View view = View.inflate(this, R.layout.dialog_repatemessage, null);
        TextView texttarget = (TextView) view.findViewById(R.id.texttarget);
        TextView textcontext = (TextView) view.findViewById(R.id.textcontext);
        final EditText editcontext = (EditText) view.findViewById(R.id.editcontext);
        texttarget.setText(name);

        if(sharetype!=null&&sharetype.equals("text")){
            textcontext.setText(shareText);
        }else if(repeatMessage!=null &&repeatMessage.getType()!=null){
            String context = MessageCrypto.getInstance().decryText(repeatMessage.getContent());
            if(!StringUtil.isEmpty(context) && (repeatMessage.getType().equals(MessageType.Text)||repeatMessage.getType().equals(MessageType.Task))){
                textcontext.setText(context);
            }else{
                textcontext.setText("分享文件");
            }
        }else{
            textcontext.setText("分享文件");
        }

        new AlertDialog.Builder(this).setTitle("确定发送给:").setView(view).setPositiveButton("是", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                final ProgressDialog pd = new ProgressDialog(ChooseSessionActivity.this);
                pd.setCanceledOnTouchOutside(false);
                pd.setMessage(getString(R.string.operationing));
                pd.show();
                leavemessage = editcontext.getText().toString().trim();

                if(choosetype.equals("conversation")){
                    Users.getInstance().getCurrentUser().getSessions().getSessionByTargetId(targetId, type, new Back.Result<Session>() {
                        @Override
                        public void onSuccess(final Session session) {
                            sendRepeatMessage(session,pd,name);
                        }
                        @Override
                        public void onError(int errorCode, String error) {
                            pd.dismiss();
                        }
                    });
                }else if(choosetype.equals("contact")){
                    Users.getInstance().getCurrentUser().getSessions().getSessionByTargetId(targetId, type, new Back.Result<Session>() {
                        @Override
                        public void onSuccess(Session session) {
                            sendRepeatMessage(session,pd,name);
                        }
                        @Override
                        public void onError(int errorCode,String error) {
                            pd.dismiss();
                        }
                    });
                }
            }
        }).setNegativeButton("否", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        }).create().show();
    }
    public void sendRepeatMessage(final Session session, final ProgressDialog pd, final String name){
        String messageContext,messageType;
        if(sharetype!=null&&sharetype.equals("text")){
            messageContext =  MessageCrypto.getInstance().encryText(shareText);
            messageType = MessageType.Text;
        }else{
            messageContext =repeatMessage.getContent();
            messageType =  repeatMessage.getType();
        }
        if(StringUtil.isEmpty(messageContext))
            return;
        final MessageModel temp = session.getMessages().createMessage(messageContext,messageType);
       session.getMessages().getRemote().store(temp, new Back.Result<MessageModel>() {
            @Override
            public void onSuccess(MessageModel model) {

                if(StringUtil.isEmpty(leavemessage)){
                    pd.dismiss();
                    shareSuccess(session,name);
                }else{
                    String leavemessageContext =  MessageCrypto.getInstance().encryText(leavemessage);
                    String leavemessageType = MessageType.Text;
                    MessageModel leavetemp = session.getMessages().createMessage(leavemessageContext,leavemessageType);
                    session.getMessages().getRemote().store(leavetemp, new Back.Result<MessageModel>() {
                        @Override
                        public void onSuccess(MessageModel model) {
                            pd.dismiss();
                            shareSuccess(session,name);
                        }
                        @Override
                        public void onError(int errorCode, String error) {
                            pd.dismiss();
                        }
                    });
                }
            }

            @Override
            public void onError(int errorCode, String error) {
                pd.dismiss();
            }
        });
    }
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
                finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    private void shareSuccess(Session session,String name){
        intent = new Intent(ChooseSessionActivity.this, ChatActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString("sessionid", session.getEntity().getId());
        bundle.putString("sessiontype", session.getEntity().getType());
        bundle.putString("otheruserId", session.getEntity().getOtherSideId());
        bundle.putString("name", name);
        intent.putExtras(bundle);
        startActivity(intent);

        List<Activity> choosesessionActivitys = new ArrayList<Activity>();
        choosesessionActivitys.addAll(AppData.getInstance().getChooseSessionActivitys());
        for(int i=0;i<choosesessionActivitys.size();i++){
            if(choosesessionActivitys.get(i)!=null){
                try {
                    choosesessionActivitys.get(i).finish();
                }catch (Exception e){
                }
            }
        }
        if(!StringUtil.isEmpty(shareText)){
            ToastUtil.ToastMessage(ChooseSessionActivity.this,"分享成功");
        }
    }
    @Override
    public void finish() {
        super.finish();
        AppData.getInstance().getChooseSessionActivitys().remove(this);
    }
}
