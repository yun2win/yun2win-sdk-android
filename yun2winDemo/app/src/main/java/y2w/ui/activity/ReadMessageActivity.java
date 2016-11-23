package y2w.ui.activity;

import android.app.ActionBar;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.TabLayout;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.y2w.uikit.customcontrols.view.SideBar;
import com.y2w.uikit.utils.HeadTextBgProvider;
import com.y2w.uikit.utils.StringUtil;
import com.y2w.uikit.utils.ToastUtil;
import com.y2w.uikit.utils.pinyinutils.SortModel;
import com.yun2win.demo.R;

import java.util.ArrayList;
import java.util.List;

import y2w.base.AppData;
import y2w.base.Urls;
import y2w.common.CallBackUpdate;
import y2w.common.HeadImageView;
import y2w.manage.EnumManage;
import y2w.manage.Users;
import y2w.model.Contact;
import y2w.model.Session;
import y2w.model.SessionMember;
import y2w.model.User;
import y2w.service.Back;
import y2w.ui.adapter.MessageReadAdapter;

/**
 * Created by hejie on 2016/3/14.
 * 个人名片界面
 */
public class ReadMessageActivity extends Activity{
    private TabLayout mTabLayout;
    private ViewPager mViewPager;

    private LayoutInflater mInflater;
    private List<String> mTitleList = new ArrayList<>();//页卡标题集合
    private View view1, view2;//页卡视图
    private List<View> mViewList = new ArrayList<>();//页卡视图集合
    private String sessionId,messageCreatAt;
    private List<SessionMember> unreadMembers = new ArrayList<SessionMember>();
    private List<SessionMember> readMembers = new ArrayList<SessionMember>();
    private String myuserId = Users.getInstance().getCurrentUser().getEntity().getId();
    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if(msg.what==1){
                initpages();
            }
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_readmessage);
        initActionBar();
        mViewPager = (ViewPager) findViewById(R.id.vp_view);
        mTabLayout = (TabLayout) findViewById(R.id.tabs);
        mInflater = LayoutInflater.from(this);
        getExtras(this.getIntent().getExtras());
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
        texttitle.setText("消息接收人列表");

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
        sessionId = bundle.getString("sessionId");
        messageCreatAt =bundle.getString("updateAt");
        unreadMembers.clear();
        readMembers.clear();
        Users.getInstance().getCurrentUser().getSessions().getSessionBySessionId(sessionId, new Back.Result<Session>() {
            @Override
            public void onSuccess(Session session) {
                session.getMembers().localAllMembers(sessionId,new Back.Result<List<SessionMember>>() {
                    @Override
                    public void onSuccess(List<SessionMember> sessionMembers) {

                       if(sessionMembers!=null&&sessionMembers.size()>0){
                           for(int i =0;i<sessionMembers.size();i++){
                               if(!myuserId.equals(sessionMembers.get(i).getEntity().getUserId())) {
                                   if (StringUtil.timeCompare(sessionMembers.get(i).getEntity().getUpdatedAt(), messageCreatAt) > 0) {//未读
                                       unreadMembers.add(sessionMembers.get(i));
                                   } else {
                                       readMembers.add(sessionMembers.get(i));
                                   }
                               }
                           }
                           handler.sendEmptyMessage(1);
                       }
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
    private void initpages(){
        view1 = mInflater.inflate(R.layout.activity_selectcontact, null);
        view2 = mInflater.inflate(R.layout.activity_selectcontact, null);
        showPageContext(view1,unreadMembers);
        showPageContext(view2,readMembers);
        //添加页卡视图
        mViewList.clear();
        mViewList.add(view1);
        mViewList.add(view2);
        //添加页卡标题
        mTitleList.clear();
        mTitleList.add("未读("+unreadMembers.size()+")");
        mTitleList.add("已读("+readMembers.size()+")");

        mTabLayout.setTabMode(TabLayout.MODE_FIXED);//设置tab模式，当前为系统默认模式
        mTabLayout.addTab(mTabLayout.newTab().setText(mTitleList.get(0)));//添加tab选项卡
        mTabLayout.addTab(mTabLayout.newTab().setText(mTitleList.get(1)));

        MyPagerAdapter mAdapter = new MyPagerAdapter(mViewList);
        mViewPager.setAdapter(mAdapter);//给ViewPager设置适配器
        mTabLayout.setupWithViewPager(mViewPager);//将TabLayout和ViewPager关联起来。
        mTabLayout.setTabsFromPagerAdapter(mAdapter);//给Tabs设置适配器

    }
    private void showPageContext(View view, final List<SessionMember> members){
        ListView lv_contact = (ListView) view.findViewById(R.id.lv_contact);
        TextView textview = (TextView) view.findViewById(R.id.dialog);
        SideBar sidebar = (SideBar) view.findViewById(R.id.sidebar);
        textview.setVisibility(View.GONE);
        sidebar.setVisibility(View.GONE);
        MessageReadAdapter messageReadAdapter = new MessageReadAdapter(this);
        messageReadAdapter.setListViewdate(members);
        lv_contact.setAdapter(messageReadAdapter);
        lv_contact.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                SessionMember sessionMember = members.get(position);
                Intent intent = new Intent(ReadMessageActivity.this, ContactInfoActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("otheruserid", sessionMember.getEntity().getUserId());
                bundle.putString("avatarUrl", sessionMember.getEntity().getAvatarUrl());
                bundle.putString("username", sessionMember.getEntity().getName());
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });

    }
    //ViewPager适配器
    class MyPagerAdapter extends PagerAdapter {
        private List<View> mViewList;

        public MyPagerAdapter(List<View> mViewList) {
            this.mViewList = mViewList;
        }
        @Override
        public int getCount() {
            return mViewList.size();//页卡数
        }
        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;//官方推荐写法
        }
        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            container.addView(mViewList.get(position));//添加页卡
            return mViewList.get(position);
        }
        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView(mViewList.get(position));//删除页卡
        }
        @Override
        public CharSequence getPageTitle(int position) {
            return mTitleList.get(position);//页卡标题
        }
    }

}
