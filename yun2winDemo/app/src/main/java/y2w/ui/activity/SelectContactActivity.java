package y2w.ui.activity;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;
import com.y2w.uikit.customcontrols.listview.ListViewUtil;
import com.y2w.uikit.customcontrols.view.SideBar;
import com.y2w.uikit.utils.StringUtil;
import com.y2w.uikit.utils.pinyinutils.CharacterParser;
import com.y2w.uikit.utils.pinyinutils.PinyinComparator;
import com.y2w.uikit.utils.pinyinutils.SortModel;
import com.yun2win.demo.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import y2w.manage.EnumManage;
import y2w.manage.Users;
import y2w.model.Contact;
import y2w.ui.adapter.SessionStartAdapter;

/**
 * Created by hejie on 2016/3/14.
 * 选择联系人
 */
public class SelectContactActivity extends Activity{

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
    private ArrayList<SortModel> choiceContacts = new ArrayList<SortModel>();
    private boolean isavatar= false;
    private String select_mode;
    private String userIds[];
    private String title;
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
        setContentView(R.layout.activity_selectcontact);
        isavatar = getIntent().getExtras().getBoolean("avatar");
        select_mode = getIntent().getExtras().getString("mode");
        title = getIntent().getExtras().getString("title");
        String users = getIntent().getExtras().getString("userIds");
        userIds = users.split(";");

        context = this;
        contactInit();
        initActionBar();
    }
    /*
	***自定义aciontbar
	*/
    TextView textRight;
    private void initActionBar(){
        ActionBar actionbar = getActionBar();
        actionbar.setDisplayShowTitleEnabled(false);
        actionbar.setDisplayShowHomeEnabled(false);
        actionbar.setDisplayShowCustomEnabled(true);
        actionbar.setCustomView(R.layout.actionbar_chat);
        TextView texttitle = (TextView) actionbar.getCustomView().findViewById(R.id.text_title);
        texttitle.setText(title);
        ImageButton imageButtonclose = (ImageButton) actionbar.getCustomView().findViewById(R.id.left_close);
        ImageButton imageButtonright = (ImageButton) actionbar.getCustomView().findViewById(R.id.right_add);
        imageButtonright.setVisibility(View.GONE);
        textRight = (TextView) actionbar.getCustomView().findViewById(R.id.tv_right_oper);
        textRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                Bundle bundle = new Bundle();
                bundle.putSerializable("choiceperson",choiceContacts);
                intent.putExtras(bundle);
                setResult(RESULT_OK,intent);
                finish();
            }
        });
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
        sessionStartAdapter = new SessionStartAdapter(context);
        sessionStartAdapter.setIsavatar(isavatar);
        if(EnumManage.Select_Mode.single.toString().equals(select_mode)) {
            sessionStartAdapter.setShowselect(false);
        }
        lv_contact.setAdapter(sessionStartAdapter);

        initSideBar();
        initPinYin();
        lv_contact.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                SortModel model = SourceDataList.get(position);

                if(EnumManage.Select_Mode.single.toString().equals(select_mode)) {
                    selectconfirm(model);
                    /*if (model.isChoice()) {
                        model.setIsChoice(false);
                        choiceContacts.remove(model);
                    }else{
                       if(choiceContacts.size()>0){
                           for(int i =0;i<SourceDataList.size();i++){
                                if(SourceDataList.get(i).getUserId().equals(choiceContacts.get(0).getUserId())){
                                    SourceDataList.get(i).setIsChoice(false);
                                    choiceContacts.remove(SourceDataList.get(i));
                                    refreshViewHolderByIndex(i, SourceDataList.get(i));
                                    break;
                                }
                           }
                       }
                        model.setIsChoice(true);
                        choiceContacts.add(model);
                        refreshViewHolderByIndex(position, model);
                    }

                    textRight.setText("确认");*/
                }else {
                    if(textRight.getVisibility()==View.GONE){
                        textRight.setVisibility(View.VISIBLE);
                    }
                    if (model.isChoice()) {
                        model.setIsChoice(false);
                        choiceContacts.remove(model);
                    } else {
                        model.setIsChoice(true);
                        choiceContacts.add(model);
                    }
                    textRight.setText("确认("+choiceContacts.size()+")");
                    refreshViewHolderByIndex(position, model);
                }

            }
        });

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
                    if (userIds != null && userIds.length > 0) {
                        boolean find = false;
                        if(EnumManage.Select_Mode.single.toString().equals(select_mode)){
                           /* if (sm.getUserId().equals(userIds[0])) {
                                sm.setIsChoice(true);
                                choiceContacts.add(sm);
                            }*/
                        }else {
                            for (int i = 0; i < userIds.length; i++) {
                                if (sm.getUserId().equals(userIds[i])) {
                                    find = true;
                                    break;
                                }
                            }
                            if (find) {
                                sm.setIsChoice(true);
                                choiceContacts.add(sm);
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
    public  void selectconfirm(final SortModel model){
        if(model==null)
            return;
        new AlertDialog.Builder(context).setTitle("确认选择：").setMessage(model.getName()).setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                choiceContacts.clear();
                choiceContacts.add(model);
                Intent intent = new Intent();
                Bundle bundle = new Bundle();
                bundle.putSerializable("choiceperson",choiceContacts);
                intent.putExtras(bundle);
                setResult(RESULT_OK,intent);
                finish();
            }
        }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        }).create().show();
    }
}
