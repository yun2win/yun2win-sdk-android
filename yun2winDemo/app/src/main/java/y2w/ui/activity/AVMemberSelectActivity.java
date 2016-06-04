package y2w.ui.activity;
import android.app.ActionBar;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
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

import y2w.manage.CurrentUser;
import y2w.manage.EnumManage;
import y2w.manage.Users;
import y2w.model.Contact;
import y2w.model.Session;
import y2w.model.SessionMember;
import y2w.service.Back;
import y2w.ui.adapter.ContactsSelectAdapter;
import y2w.ui.adapter.SessionStartAdapter;
import com.y2w.uikit.utils.StringUtil;
import com.y2w.uikit.utils.pinyinutils.CharacterParser;
import com.y2w.uikit.utils.pinyinutils.PinyinComparator;
import com.y2w.uikit.customcontrols.view.SideBar;
import com.y2w.uikit.utils.pinyinutils.SortModel;

/**
 * Created by yangrongfang on 2016/5/17.
 */
public class AVMemberSelectActivity extends Activity{

    private ListView lv_contact;
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
    private List<SortModel> choiceMembers = new ArrayList<SortModel>();
    private boolean isCreate= false;
    private String sessionId;
    private String memberIds;
    private String callType;
    private String sessionName;
    private List<String> memberList = new ArrayList<String>();
    private CurrentUser currentUser;
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
                sessionStartAdapter.notifyDataSetChanged();
            }
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sessionstart);
        context = this;
        currentUser = Users.getInstance().getCurrentUser();
        callType = getIntent().getExtras().getString("callType", EnumManage.AvCallType.audio.toString());
        isCreate = getIntent().getExtras().getBoolean("isCreate", true);
        sessionId = getIntent().getExtras().getString("sessionId", "");
        sessionName = getIntent().getExtras().getString("sessionName","");
        memberIds = getIntent().getExtras().getString("memberIds", "");
        this.setTitle(sessionName);
        if(isCreate){
            SortModel model = new SortModel();
            model.setId(currentUser.getEntity().getId());
            model.setUserId(currentUser.getEntity().getId());
            model.setName(currentUser.getEntity().getName());
            model.setPinyin(currentUser.getEntity().getName());
            model.setAvatarUrl(currentUser.getEntity().getAvatarUrl());
            model.setSortLetters(StringUtil.getPinYinSortLetters(characterParser, model.getPinyin()));
            model.setIsMember(true);
            model.setIsChoice(true);
            choiceMembers.add(model);
            memberList.add(currentUser.getEntity().getId());
        }else{
            String[] ids = memberIds.split(";");
            for(String id : ids){
                memberList.add(id);
            }
        }
        memberInit();
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
        texttitle.setText(getResources().getString(R.string.group_start));
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

    public void memberInit(){
        lv_contact = (ListView) findViewById(R.id.lv_contact);
        sessionStartAdapter = new SessionStartAdapter(context);
        lv_contact.setAdapter(sessionStartAdapter);
        initSideBar();
        initPinYin();
        lv_contact.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                SortModel model = SourceDataList.get(position);
                if(memberList.contains(model.getUserId())){
                    return;
                }
                if (model.isMember())
                    return;
                if (model.isChoice()) {
                    model.setIsChoice(false);
                    choiceMembers.remove(model);
                } else {
                    model.setIsChoice(true);
                    choiceMembers.add(model);
                }
                tv_choose_Ok.setText("确认(" + choiceMembers.size() + ")");
                notifySelectAreaDataSetChanged();
                refreshViewHolderByIndex(position, model);
            }
        });
        tv_choose_Ok = (TextView) findViewById(R.id.tv_choose_ok);
        gridView = (GridView) findViewById(R.id.gv_selector_preview);

        horizontal_scrollView = (HorizontalScrollView) findViewById(R.id.hs_preview);
        contactSelectedAdapter = new ContactsSelectAdapter(context);
        gridView.setAdapter(contactSelectedAdapter);
        contactSelectedAdapter.setListViewdate(choiceMembers);



        tv_choose_Ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (choiceMembers.size() <= 0) {
                    ToastUtil.ToastMessage(context, "请添加成员");
                    return;
                }
                pd = new ProgressDialog(context);
                pd.setCanceledOnTouchOutside(false);
                pd.setMessage("正在添加中...");
                String names = "";
                for (int i = 0; i < choiceMembers.size(); i++) {
                    names = names + choiceMembers.get(i).getUserId() + ";";
                }

                if (isCreate) {
                    Intent intent = new Intent(context, AVCallActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putString("chatType", EnumManage.SessionType.group.toString());
                    bundle.putString("type", EnumManage.AvType.launch.toString());
                    bundle.putString("callType", callType);
                    bundle.putString("sessionId", sessionId);
                    bundle.putString("sessionName",sessionName);
                    bundle.putString("memberIds", names);
                    bundle.putBoolean("isCreate", true);
                    intent.putExtras(bundle);
                    startActivity(intent);
                    finish();
                } else {
                    String memberIds = "";
                    for(SortModel model : choiceMembers){
                        memberIds += model.getId()+";";
                    }
                    Intent intent = new Intent();
                    Bundle bundle = new Bundle();
                    bundle.putString("memberIds",memberIds);
                    intent.putExtras(bundle);
                    setResult(1,intent);
                    finish();
                }
                pd.show();
            }
        });
    }

    private void notifySelectAreaDataSetChanged() {
        int converViewWidth = Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 46, this.getResources()
                .getDisplayMetrics()));
        ViewGroup.LayoutParams layoutParams = gridView.getLayoutParams();
        layoutParams.width = converViewWidth * choiceMembers.size();
        layoutParams.height = converViewWidth;
        gridView.setLayoutParams(layoutParams);
        gridView.setNumColumns(choiceMembers.size());

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
            Users.getInstance().getCurrentUser().getSessions().getSessionBySessionId(sessionId, new Back.Result<Session>() {
                @Override
                public void onSuccess(Session session) {
                    session.getMembers().getMembers(new Back.Result<List<SessionMember>>() {
                        @Override
                        public void onSuccess(List<SessionMember> sessionMemberList) {
                            for (SessionMember data : sessionMemberList) {
                                SortModel sm = new SortModel();
                                sm.setId(data.getEntity().getId());
                                sm.setUserId(data.getEntity().getUserId());
                                sm.setName(data.getEntity().getName());
                                sm.setPinyin(data.getEntity().getName());
                                sm.setAvatarUrl(data.getEntity().getAvatarUrl());
                                sm.setStatus(data.getEntity().getStatus());
                                sm.setRole(data.getEntity().getRole());
                                sm.setSortLetters(StringUtil.getPinYinSortLetters(characterParser,sm.getPinyin()));
                                if (memberList.contains(sm.getUserId())) {
                                    sm.setIsMember(true);
                                    sm.setIsChoice(true);
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

                        @Override
                        public void onError(int code, String error) {

                        }
                    });
                }

                @Override
                public void onError(int code, String error) {

                }
            });

        }
    }
}

