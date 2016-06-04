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
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import com.y2w.uikit.customcontrols.imageview.CircleImageView;
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

public class GroupMemberActivity extends Activity{

	private String sessionId;
	private String sessionName;
	private Session _session;
	private ListView lv_qun_member;
	public List<SortModel> SourceDataList;
	private Context context;
	
	private GroupMemberAdapter memberAdapter;
	private CharacterParser characterParser;
	private PinyinComparator pinyinComparator;
	private SideBar sideBar;
	private TextView dialog;
	private CircleImageView iv_creator;
	private TextView tv_creator_name;
	private String creatorname="";
	private String master="";
	private ProgressDialog progressDialog;
	private String mdeleteId;
	private int index_delete=1;
	
	private String addIds = "";
	private String addNames = "";
	private ListView lv_selection;
	private String authority;
	private LinearLayout ll_qun_manager;
	private Handler storeQunHandler;
	private Handler addQunMember;
	private Handler changeQunMaster;
	private Handler deleteQunMember;
	private List<String> list=new ArrayList<String>();
	private List<String> selectlist=new ArrayList<String>();
	private boolean selectOnly;//判断当前是选择群主还是管理员，群主只能是一个，管理员可以为多个
	private int lastselection = 0;//上一次选择的item
	private String managers="";
	private String opertype="";//delete(删除成员);setmanager(设置管理员)
	private String type="display";
	private List<SessionMember> memberList;

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
			azListDisplay();
			getSession();
		}catch(Exception e){
			
		}
	}
    private void init(){
    	
    	Intent intent=getIntent();
		Bundle bundle=intent.getExtras();
		sessionId=bundle.getString("sessionId","");
		sessionName=bundle.getString("sessionName","");
		this.setTitle(sessionName+" 群成员");
		characterParser = CharacterParser.getInstance();		
		pinyinComparator = new PinyinComparator();
		
		lv_qun_member = (ListView) findViewById(R.id.lv_qunmember_display);
		lv_selection = (ListView) findViewById(R.id.lv_quninfo_settings);
		sideBar = (SideBar) findViewById(R.id.sb_sidebar);
		dialog = (TextView) findViewById(R.id.tv_dialog);
		lv_qun_member.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				onItemClickOper(arg2);
			}
		});
		
		lv_qun_member.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {

				return true;
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
		texttitle.setText(sessionName+" 群成员");
		tv_oper = (TextView) actionbar.getCustomView().findViewById(R.id.tv_right_oper);
		tv_oper.setVisibility(View.VISIBLE);
		ImageButton imageButtonClose = (ImageButton) actionbar.getCustomView().findViewById(R.id.left_close);
		tv_oper.setBackgroundResource(R.drawable.action_bar_black_more_icon);
		tv_oper.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				groupMemberManagerDisplay();
			}
		});

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
					sm.setName(member.getEntity().getName());
					sm.setAvatarUrl(member.getEntity().getAvatarUrl());
					sm.setPinyin(StringUtil.getPinYinSortLetters(characterParser, member.getEntity().getName()));
					sm.setSelectedStatus("false");
					datas.add(sm);
				}
				SourceDataList = filledData(datas);
				// 根据a-z进行排序源数据
				Collections.sort(SourceDataList, pinyinComparator);
				memberAdapter = new GroupMemberAdapter(context,SourceDataList,type);
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

	private void onItemClickOper(int arg2){
		String value=isOperAble(SourceDataList.get(arg2).getId());
		if("true".equals(value)) {
			if ("false".equals(SourceDataList.get(arg2).getSelectedStatus())) {

				if (selectOnly) {//群主转让时，只能选择一个
					SourceDataList.get(lastselection).setSelectedStatus("false");
					selectlist.remove(SourceDataList.get(lastselection).getId());
				}
				SourceDataList.get(arg2).setSelectedStatus("true");
				selectlist.add(SourceDataList.get(arg2).getId());
				lastselection = arg2;
			} else {
				SourceDataList.get(arg2).setSelectedStatus("false");
				selectlist.remove(SourceDataList.get(arg2).getId());
				lastselection = arg2;
			}
			memberAdapter.notifyDataSetChanged();
		}
	}

	private String isOperAble(String uid){
		if("delete".equals(opertype)){
			if("master".equals(authority)){
				if(master.equals(uid)){
					return "不能删除自己";
				}else{
					return "true";
				}
			}else if("manager".equals(authority)){
				if(managers.contains(uid) || master.equals(uid)){
					if(uid.equals(_session.getSessions().getUser().getEntity().getId())){
						return "不能删除自己";
					}else if(managers.contains(uid)){
						return "没有删除此人权限";
					}else{
						return "不能删除群主";
					}

				}else{
					return "true";
				}
			}
		}else{
			if("master".equals(authority)){
				if(master.equals(uid)){
					return "您已经是群主";
				}else{
					return "true";
				}
			}
		}
		return "true";
	}

	private String getMemberAuthority(){
		try {
			SessionMember member = _session.getMembers().getLocalMember(Users.getInstance().getCurrentUser().getEntity().getId());
			if(member != null){
				return member.getEntity().getRole();
			}
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		return "";
	}

	private void groupMemberManagerDisplay(){
			setManagerSelectionDatas();
			settingsAdapter = new GroupMemberSettingsAdapter(context, settings,"memberOper");
			lv_selection.setAdapter(settingsAdapter);
			lv_selection.setOnItemClickListener(new settingsOnItemClick());
	}

	private List<String> settings;
	private GroupMemberSettingsAdapter settingsAdapter;
	private void setManagerSelectionDatas(){
		if(settings == null)
			settings = new ArrayList<String>();
		else
			settings.clear();
		if(EnumManage.GroupRole.master.toString().equals(getMemberAuthority())){
			settings.add("邀请人加入");
			settings.add("删除群成员");
			settings.add("设置管理员");
			settings.add("转让群主");
			settings.add("取消");
		}else if(EnumManage.GroupRole.admin.toString().equals(getMemberAuthority())){
			settings.add("邀请人加入");
			settings.add("删除群成员");
			settings.add("取消");
		}
	}

	private class settingsOnItemClick implements OnItemClickListener{

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			type = "select";
			azListDisplay();
		}
	}


	private void getSession(){
		final ProgressDialog pd = new ProgressDialog(context);
		pd.setCanceledOnTouchOutside(false);
		pd.setMessage(getString(R.string.loading));
		pd.show();
		Users.getInstance().getCurrentUser().getSessions().getRemote().getSession(sessionId, EnumManage.SessionType.group.toString(), new Back.Result<Session>() {
			@Override
			public void onSuccess(Session session) {
				_session = session;
				getLocalMembers();
				pd.dismiss();
			}

			@Override
			public void onError(int Code, String error) {
				if (_session == null) {
					ToastUtil.ToastMessage(context, "初始化失败,请重新打开");
					pd.dismiss();
					finish();
				}
			}
		});
	}

	private void getLocalMembers(){
		_session.getMembers().getMembers(new Back.Result<List<SessionMember>>() {
			@Override
			public void onSuccess(List<SessionMember> sessionMembers) {
				memberList = sessionMembers;
				if(handlerUi != null)
				handlerUi.sendEmptyMessage(1);
			}

			@Override
			public void onError(int Code, String error) {
			}
		});
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if(requestCode == 1){

		}else if(requestCode == 2){
			
		}else if(requestCode == 3){
			
		}
	}
	
}
