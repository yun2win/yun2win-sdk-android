package y2w.ui.activity;

import android.app.ActionBar;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.y2w.uikit.customcontrols.view.SideBar;
import com.y2w.uikit.utils.StringUtil;
import com.y2w.uikit.utils.ToastUtil;
import com.y2w.uikit.utils.pinyinutils.CharacterParser;
import com.y2w.uikit.utils.pinyinutils.PinyinComparator;
import com.y2w.uikit.utils.pinyinutils.SortModel;
import com.yun2win.demo.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import y2w.manage.EnumManage;
import y2w.manage.Users;
import y2w.model.Session;
import y2w.model.SessionMember;
import y2w.service.Back;
import y2w.ui.adapter.GroupMemberAdapter;
import y2w.ui.adapter.GroupMemberSettingsAdapter;

public class ChooseAnswerActivity extends Activity{

	private String sessionId;
	private Session _session;
	private ListView lv_qun_member;
	public List<SortModel> SourceDataList=new ArrayList<SortModel>();
	private Context context;
	private GroupMemberAdapter memberAdapter;
	private CharacterParser characterParser;
	private PinyinComparator pinyinComparator;
	private SideBar sideBar;
	private TextView dialog;

	private String type="display";
	private List<SessionMember> memberList = new ArrayList<SessionMember>();
    private Intent intent;
	Handler handlerUi = new Handler(){
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			if(msg.what==1) {
				azListDisplay();
			}
		}
	};
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_group_member);
		context = this;
		try{
			init();
			initSideBar();
			initActionBar();
			getSession();
		}catch(Exception e){
			
		}
	}
    private void init(){
		Bundle bundle=getIntent().getExtras();
		sessionId=bundle.getString("sessionId","");
		characterParser = CharacterParser.getInstance();		
		pinyinComparator = new PinyinComparator();
		
		lv_qun_member = (ListView) findViewById(R.id.lv_qunmember_display);
		sideBar = (SideBar) findViewById(R.id.sb_sidebar);
		dialog = (TextView) findViewById(R.id.tv_dialog);
		lv_qun_member.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				SortModel clicksortModel = SourceDataList.get(arg2);
				intent = new Intent();
				intent.putExtra("result",clicksortModel.getUserId());
				setResult(RESULT_OK,intent);
				finish();
			}
		});

    }
	private TextView texttitle;
	private TextView tv_oper;
	private ImageButton imageButtongroup;
	private void initActionBar(){
		ActionBar actionbar = getActionBar();
		actionbar.setDisplayShowTitleEnabled(false);
		actionbar.setDisplayShowHomeEnabled(false);
		actionbar.setDisplayShowCustomEnabled(true);
		actionbar.setCustomView(R.layout.actionbar_chat);
		texttitle = (TextView) actionbar.getCustomView().findViewById(R.id.text_title);
		texttitle.setText("选择回复的人");
		tv_oper = (TextView) actionbar.getCustomView().findViewById(R.id.tv_right_oper);
		tv_oper.setVisibility(View.GONE);
		ImageButton imageButtonClose = (ImageButton) actionbar.getCustomView().findViewById(R.id.left_close);
		imageButtonClose.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
	}

	//初始化边栏
	private void initSideBar(){

		sideBar.setTextView(dialog);
		//设置右侧触摸监听
		sideBar.setOnTouchingLetterChangedListener(new SideBar.OnTouchingLetterChangedListener() {

			@Override
			public void onTouchingLetterChanged(String s) {
				//该字母首次出现的位置
				try {
					if (s != null) {
						int position = memberAdapter.getPositionForSection(s.charAt(0));
						if (position != -1) {
							lv_qun_member.setSelection(position);
						}
					}
				} catch (Exception e) {
				}
			}
		});
	}

	//读取群成员
	public void azListDisplay(){
		try {
			//查询本地数据库
			if (memberList != null) {
				List<SortModel> datas = new ArrayList<SortModel>();
				for(SessionMember member : memberList){
					SortModel sm = new SortModel();
					sm.setId(member.getEntity().getId());
					sm.setUserId(member.getEntity().getUserId());
					sm.setName(member.getEntity().getName());
					sm.setRole(member.getEntity().getRole());
					sm.setAvatarUrl(member.getEntity().getAvatarUrl());
					sm.setPinyin(StringUtil.getPinYinSortLetters(characterParser, member.getEntity().getName()));
					sm.setSelectedStatus(false);
					datas.add(sm);
				}
				List<SortModel> sempDataList = filledData(datas);
				// 根据a-z进行排序源数据
				Collections.sort(sempDataList, pinyinComparator);

				SourceDataList.clear();
				SourceDataList.addAll(sempDataList);
				memberAdapter = new GroupMemberAdapter(context,SourceDataList,type,0);
				lv_qun_member.setAdapter(memberAdapter);
			}

		} catch (Exception e) {

		}
	}

	/**
	 * 为ListView填充数据
	 * @return
	 */
	private List<SortModel> filledData(List<SortModel> mSortList){
		for(int i = 0; i< mSortList.size(); i++){
			try{
				//汉字转换成拼音
				if(StringUtil.isEmpty(mSortList.get(i).getPinyin())){
					mSortList.get(i).setSortLetters("#");
				}
				else{
					String pinyin = characterParser.getSelling(mSortList.get(i).getPinyin());
					String sortString = pinyin.substring(0, 1).toUpperCase();

					//正则表达式，判断首字母是否是英文字母
					if(sortString.matches("[A-Z]")){
						mSortList.get(i).setSortLetters(sortString.toUpperCase());
					}else{
						mSortList.get(i).setSortLetters("#");
					}
				}
			}catch(Exception ex){
				String s=ex.getMessage();
			}
		}
		return mSortList;
	}
	private void getSession(){
		Users.getInstance().getCurrentUser().getSessions().getRemote().getSession(sessionId, EnumManage.SessionType.group.toString(), new Back.Result<Session>() {
			@Override
			public void onSuccess(Session session) {
				_session = session;
				getLocalMembers();
			}

			@Override
			public void onError(int Code, String error) {
				if (_session == null) {
					ToastUtil.ToastMessage(context, "初始化失败,请重新打开");
					finish();
				}
			}
		});
	}

	private void getLocalMembers(){
		_session.getMembers().getMembers(new Back.Result<List<SessionMember>>() {
			@Override
			public void onSuccess(List<SessionMember> sessionMembers) {
				if(sessionMembers==null||sessionMembers.size()==0)
					return;
				memberList.clear();
				for(int i =0;i<sessionMembers.size();i++){
					if(EnumManage.UserStatus.active.toString().equals(sessionMembers.get(i).getEntity().getStatus())&&!sessionMembers.get(i).getEntity().getUserId().equals(Users.getInstance().getCurrentUser().getEntity().getId())) {
						memberList.add(sessionMembers.get(i));
					}
				}
				if(handlerUi != null)
				handlerUi.sendEmptyMessage(1);
			}

			@Override
			public void onError(int Code, String error) {
			}
		});
	}
	
}
