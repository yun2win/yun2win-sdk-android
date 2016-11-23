package y2w.ui.activity;

import android.app.ActionBar;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.HorizontalScrollView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.y2w.uikit.customcontrols.listview.ListViewUtil;
import com.y2w.uikit.utils.ToastUtil;
import com.yun2win.demo.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import y2w.entities.UserEntity;
import y2w.manage.EnumManage;
import y2w.manage.Users;
import y2w.model.Contact;
import y2w.model.Session;
import y2w.model.SessionMember;
import y2w.model.UserConversation;
import y2w.model.UserSession;
import y2w.service.Back;
import y2w.ui.adapter.ContactsSelectAdapter;
import y2w.ui.adapter.SessionStartAdapter;
import com.y2w.uikit.utils.StringUtil;
import com.y2w.uikit.utils.pinyinutils.CharacterParser;
import com.y2w.uikit.utils.pinyinutils.PinyinComparator;
import com.y2w.uikit.customcontrols.view.SideBar;
import com.y2w.uikit.utils.pinyinutils.SortModel;
import com.yun2win.imlib.IMClient;
import com.yun2win.imlib.IMSession;

import y2w.base.Urls;

/**
 * Created by hejie on 2016/3/14.
 * 发起群聊界面
 */
public class SessionStartActivity extends Activity{

    private ListView lv_contact;
    private TextView noContact;
    private SessionStartAdapter sessionStartAdapter;
    private ProgressDialog pd;
    /** 拼音排序 **/
    private CharacterParser characterParser;
    private List<SortModel> SourceDataList;
    private PinyinComparator pinyinComparator;
    private SideBar sideBar;
    private TextView dialog;
    private List<Contact> contactList;
    private Context context;
    private List<Contact> contacts;
    private AcyContactdate acyContactdate;
    private TextView tv_choose_Ok;
    private GridView gridView;
    private ContactsSelectAdapter contactSelectedAdapter;
    private HorizontalScrollView horizontal_scrollView;
    private List<SortModel> choiceContacts = new ArrayList<SortModel>();
    private boolean iscreate= false;
    private String sessionId;
    private String userIds[];
    private Session _session;
    Handler updatecontactHandler= new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if(msg.what==1){
                if(acyContactdate!=null&& acyContactdate.isAlive()){
                    acyContactdate.interrupt();
                    acyContactdate=null;
                }
                acyContactdate = new AcyContactdate();
                acyContactdate.start();
            }else if(msg.what==0){
                if(SourceDataList!=null&&SourceDataList.size()>0){
                    lv_contact.setVisibility(View.VISIBLE);
                    sideBar.setVisibility(View.VISIBLE);
                    noContact.setVisibility(View.GONE);
                }else{
                    lv_contact.setVisibility(View.GONE);
                    sideBar.setVisibility(View.GONE);
                    noContact.setVisibility(View.VISIBLE);
                }
                sessionStartAdapter.notifyDataSetChanged();
            }
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sessionstart);
        iscreate = getIntent().getExtras().getBoolean("iscreate");
        if(!iscreate) {
            sessionId = getIntent().getExtras().getString("sessionId");
            String users = getIntent().getExtras().getString("userIds");
            userIds = users.split(";");
            Users.getInstance().getCurrentUser().getSessions().getSessionBySessionId(sessionId, new Back.Result<Session>() {
                @Override
                public void onSuccess(Session session) {
                    _session = session;
                }
                @Override
                public void onError(int Code, String error) {
                }
            });
        }
        context = this;
        contactInit();
        initActionBar();
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
        if(iscreate){
            texttitle.setText(getResources().getString(R.string.group_start));
        }else{
            texttitle.setText(getResources().getString(R.string.group_request));
        }
        ImageButton imageButtonclose = (ImageButton) actionbar.getCustomView().findViewById(R.id.left_close);
        ImageButton imageButtonright = (ImageButton) actionbar.getCustomView().findViewById(R.id.right_add);
        imageButtonright.setVisibility(View.GONE);
        imageButtonclose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        updatecontactHandler.sendEmptyMessage(1);
    }
    public void contactInit(){
        lv_contact = (ListView) findViewById(R.id.lv_contact);
        noContact = (TextView) findViewById(R.id.nocontact);
        sessionStartAdapter = new SessionStartAdapter(context);
        lv_contact.setAdapter(sessionStartAdapter);
        initSideBar();
        initPinYin();
        lv_contact.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                SortModel model = SourceDataList.get(position);
                if (model.isMember())
                    return;
                if (model.isChoice()) {
                    model.setIsChoice(false);
                    choiceContacts.remove(model);
                } else {
                    model.setIsChoice(true);
                    choiceContacts.add(model);
                }
                if(choiceContacts.size()>0){
                    tv_choose_Ok.setBackgroundResource(R.drawable.button_person_choose_after);
                }else{
                    tv_choose_Ok.setBackgroundResource(R.drawable.button_person_choose_before);
                }
                tv_choose_Ok.setText("确认(" + choiceContacts.size() + ")");
                notifySelectAreaDataSetChanged();
                refreshViewHolderByIndex(position, model);
            }
        });
          tv_choose_Ok = (TextView) findViewById(R.id.tv_choose_ok);
          gridView = (GridView) findViewById(R.id.gv_selector_preview);

          horizontal_scrollView = (HorizontalScrollView) findViewById(R.id.hs_preview);
         contactSelectedAdapter = new ContactsSelectAdapter(context);
         gridView.setAdapter(contactSelectedAdapter);
         contactSelectedAdapter.setListViewdate(choiceContacts);
        tv_choose_Ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (choiceContacts.size() <= 0) {
                    ToastUtil.ToastMessage(SessionStartActivity.this, "请添加成员");
                    return;
                }
                pd = new ProgressDialog(SessionStartActivity.this);
                pd.setCanceledOnTouchOutside(false);
                if(iscreate) {
                    pd.setMessage(getString(R.string.creategrups));
                }else{
                    pd.setMessage(getString(R.string.addmemgrups));
                }
                String names = "";
                for (int i = 0; i < choiceContacts.size(); i++) {
                    if (i != 0)
                        names = names + "、";
                    names = names + choiceContacts.get(i).getName();
                }
                if (names.length() > 15) {
                    names = names.substring(0, 14);
                    names = names + "...";
                }
                final String finalNames = names;
                if (iscreate) {
                    Users.getInstance().getCurrentUser().getSessions().getRemote().sessionCreate(names, EnumManage.SecureType_public, EnumManage.SessionType.group.toString(), Urls.User_Avatar_Def, new Back.Result<Session>() {
                        @Override
                        public void onSuccess(Session session) {
                            addMytoGroupMembers(session);
                        }

                        @Override
                        public void onError(int errorCode, String error) {
                            ToastUtil.ToastMessage(SessionStartActivity.this, "创建失败");
                            pd.dismiss();
                        }
                    });
                } else {
                    if (_session != null) {
                        addotherMembers(_session);
                    } else {
                        ToastUtil.ToastMessage(SessionStartActivity.this, "正在初始化数据");
                    }
                }
                pd.show();
            }
        });
    }

    private void addMytoGroupMembers(final Session session){

        UserEntity userEntity = Users.getInstance().getCurrentUser().getEntity();
        session.getMembers().getRemote().sessionMemberAdd(userEntity.getId(),
                userEntity.getName(), EnumManage.GroupRole.master.toString(), userEntity.getAvatarUrl(),  EnumManage.UserStatus.active.toString(), new Back.Result<SessionMember>(){
                    @Override
                    public void onSuccess(SessionMember sessionMember) {
                        addotherMembers(session);
                        //addMyGroups(session);

                    }
                    @Override
                    public void onError(int errorCode,String error) {
                        pd.dismiss();
                        ToastUtil.ToastMessage(SessionStartActivity.this, "创建失败");
                    }
                });
    }

    private int membersCount = 0;
    private void addotherMembers(final Session session){
        membersCount = choiceContacts.size();
        Log.i("SessionStartActivity", "----------=membersCount = "+ membersCount);
        for(int i =0;i<choiceContacts.size();i++){
            session.getMembers().getRemote().sessionMemberAdd(choiceContacts.get(i).getUserId(),
                    choiceContacts.get(i).getName(),EnumManage.GroupRole.user.toString(), choiceContacts.get(i).getAvatarUrl(), EnumManage.UserStatus.active.toString(), new Back.Result<SessionMember>(){
                        @Override
                        public void onSuccess(SessionMember sessionMember) {
                            membersCount--;
                            Log.i("SessionStartActivity", "----------=sessionMemberAdd onSuccess");
                            if(membersCount==0){
                                pd.dismiss();
                                setResult(100);
                                Log.i("SessionStartActivity", "----------=sessionMemberAdd iscreate");
                                try {
                                    Thread.sleep(400);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                                session.getMessages().getRemote().sendMessage("",false,new IMClient.SendCallback() {
                                    @Override
                                    public void onReturnCode(int i, IMSession imSession, String s) {
                                        Log.i("SessionStartActivity", "----------=sendMessage = " + i);
                                    }
                                });
                                Users.getInstance().getCurrentUser().getUserConversations().getRemote().sync(new Back.Result<List<UserConversation>>() {
                                    @Override
                                    public void onSuccess(List<UserConversation> userConversations) {
                                        if(iscreate) {
                                            ToastUtil.ToastMessage(SessionStartActivity.this, "创建成功");
                                            Intent intent = new Intent(context, ChatActivity.class);
                                            Bundle bundle = new Bundle();
                                            bundle.putString("sessionid", session.getEntity().getId());
                                            bundle.putString("sessiontype", session.getEntity().getType());
                                            bundle.putString("otheruserId", session.getEntity().getOtherSideId());
                                            bundle.putString("name", session.getEntity().getName());
                                            intent.putExtras(bundle);
                                            startActivity(intent);
                                        }
                                        finish();
                                    }
                                    @Override
                                    public void onError(int code, String error) {
                                    }
                                });
                                //finish();

                            }
                        }
                        @Override
                        public void onError(int errorCode,String error) {
                            pd.dismiss();
                            ToastUtil.ToastMessage(SessionStartActivity.this, "添加好友失败");
                        }
                    });
        }
    }

    private void notifySelectAreaDataSetChanged() {
        int converViewWidth = Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 46, this.getResources()
                .getDisplayMetrics()));
        ViewGroup.LayoutParams layoutParams = gridView.getLayoutParams();
        layoutParams.width = converViewWidth * choiceContacts.size();
        layoutParams.height = converViewWidth;
        gridView.setLayoutParams(layoutParams);
        gridView.setNumColumns(choiceContacts.size());

        try {
            final int x = layoutParams.width;
            final int y = layoutParams.height;
            new Handler().post(new Runnable() {
                @Override
                public void run() {
                    horizontal_scrollView.scrollTo(x, y);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }

        contactSelectedAdapter.notifyDataSetChanged();
    }
    /**
     * 刷新单条消息
     *
     * @param index
     */
    private void refreshViewHolderByIndex(final int index,final SortModel model) {
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (index < 0) {
                    return;
                }
                Object tag = ListViewUtil.getViewHolderByIndex(lv_contact, index);
                if (tag instanceof SessionStartAdapter.SessionHoldView) {
                    SessionStartAdapter.SessionHoldView viewHolder = (SessionStartAdapter.SessionHoldView) tag;
                    sessionStartAdapter.setIndexview(viewHolder, model, index);
                }
            }
        });
    }
    private void initPinYin(){
        characterParser = CharacterParser.getInstance();
        pinyinComparator = new PinyinComparator();
    }

    private void initSideBar() {
        sideBar = (SideBar) findViewById(R.id.sidebar);
        dialog = (TextView) findViewById(R.id.dialog);
        sideBar.setTextView(dialog);
        // 设置右侧触摸监听
        sideBar.setOnTouchingLetterChangedListener(new SideBar.OnTouchingLetterChangedListener() {

            @Override
            public void onTouchingLetterChanged(String s) {
                // 该字母首次出现的位置
                try {
                    if (s != null) {
                        int position = sessionStartAdapter.getPositionForSection(s
                                .charAt(0));
                        if (position != -1) {
                            lv_contact.setSelection(position);
                        }
                    }
                } catch (Exception e) {
                }
            }
        });
    }
    class AcyContactdate extends Thread{
        @Override
        public void run() {
            super.run();
            if(SourceDataList == null){
                SourceDataList = new ArrayList<SortModel>();
            }else{
                SourceDataList.clear();
            }
            contacts =  Users.getInstance().getCurrentUser().getContacts().getContacts();
            for (Contact data : contacts) {
                SortModel sm = new SortModel();
                sm.setId(data.getEntity().getId());
                sm.setUserId(data.getEntity().getUserId());
                sm.setName(data.getEntity().getName());
                sm.setPinyin(data.getEntity().getName());
                sm.setEmail(data.getEntity().getEmail());
                sm.setAvatarUrl(data.getEntity().getAvatarUrl());
                sm.setStatus(data.getEntity().getStatus());
                sm.setRole(data.getEntity().getRole());
                sm.setSortLetters(StringUtil.getPinYinSortLetters(characterParser,sm.getPinyin()));
                if(!iscreate) {
                    if (userIds != null && userIds.length > 0) {
                        boolean find = false;
                        for (int i = 0; i < userIds.length; i++) {
                            if (sm.getUserId().equals(userIds[i])) {
                                find = true;
                                break;
                            }
                        }
                        if (find) {
                            sm.setIsMember(true);
                            sm.setIsChoice(true);
                        }
                    }
                }
                SourceDataList.add(sm);
            }
            // 根据a-z进行排序源数据
            Collections.sort(SourceDataList, pinyinComparator);
            sessionStartAdapter.setListView(SourceDataList);
            Message msg = new Message();
            msg.what =0;
            updatecontactHandler.sendMessage(msg);
        }
    }
}
