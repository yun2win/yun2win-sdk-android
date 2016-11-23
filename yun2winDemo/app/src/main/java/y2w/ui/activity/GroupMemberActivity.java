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
	public List<SortModel> SourceDataList=new ArrayList<SortModel>();
	private Context context;
	private ProgressDialog pd;
	private GroupMemberAdapter memberAdapter;
	private CharacterParser characterParser;
	private PinyinComparator pinyinComparator;
	private SideBar sideBar;
	private TextView dialog;
	private SessionMember mysessionMember;
	private int finishnet = 0,allMamge=0;

	private ListView lv_selection;

	private List<String> list=new ArrayList<String>();
	private List<String> selectlist=new ArrayList<String>();
	private boolean selectOnly;//判断当前是选择群主还是管理员，群主只能是一个，管理员可以为多个
	private int lastselection = 0;//上一次选择的item
	private String opertype="";//delete(删除成员);setmanager(设置管理员)
	private String type="display";
	private List<SessionMember> memberList = new ArrayList<SessionMember>();

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
				if(settings.size()>0){
					if(arg2>=memberAdapter.getManagenum())
					  onItemClickOper(arg2);
				}else{
					SortModel clicksortModel = SourceDataList.get(arg2);
					Intent intent = new Intent(GroupMemberActivity.this, ContactInfoActivity.class);
					Bundle bundle = new Bundle();
					bundle.putString("otheruserid", clicksortModel.getUserId());
					bundle.putString("avatarUrl", clicksortModel.getAvatarUrl());
					bundle.putString("username", clicksortModel.getName());
					bundle.putInt("flag", ContactInfoActivity.chat);
					intent.putExtras(bundle);
					startActivity(intent);
				}
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
		texttitle.setText(sessionName + " 群成员");
		tv_oper = (TextView) actionbar.getCustomView().findViewById(R.id.tv_right_oper);

		ImageButton imageButtonClose = (ImageButton) actionbar.getCustomView().findViewById(R.id.left_close);
		tv_oper.setBackgroundResource(R.drawable.action_bar_black_more_icon);
		tv_oper.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if(settings.size()>0) {
					type = "display";
					azListDisplay();
					settings.clear();
					settingsAdapter.notifyDataSetChanged();
				}else{
					groupMemberManagerDisplay();
				}
			}
		});
		settings = new ArrayList<String>();
		settingsAdapter = new GroupMemberSettingsAdapter(context, settings,"memberOper");
		lv_selection.setAdapter(settingsAdapter);
		lv_selection.setOnItemClickListener(new settingsOnItemClick());
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
				List<SortModel> managedatas = new ArrayList<SortModel>();
				for(SessionMember member : memberList){
					SortModel sm = new SortModel();
					sm.setId(member.getEntity().getId());
					sm.setUserId(member.getEntity().getUserId());
					sm.setName(member.getEntity().getName());
					sm.setRole(member.getEntity().getRole());
					sm.setAvatarUrl(member.getEntity().getAvatarUrl());
					sm.setPinyin(StringUtil.getPinYinSortLetters(characterParser, member.getEntity().getName()));
					sm.setSelectedStatus(false);
					if(!member.getEntity().getRole().equals(EnumManage.GroupRole.master.toString())) {
						datas.add(sm);
					}else{
						managedatas.add(sm);
					}
				}
				List<SortModel> sempDataList = filledData(datas);
				// 根据a-z进行排序源数据
				Collections.sort(sempDataList, pinyinComparator);
				for(int i = 0;i<sempDataList.size();i++){
					if(sempDataList.get(i).getRole().equals(EnumManage.GroupRole.admin.toString())){
						managedatas.add(sempDataList.get(i));
					}
				}
				SourceDataList.clear();
				SourceDataList.addAll(managedatas);
				SourceDataList.addAll(sempDataList);
				memberAdapter = new GroupMemberAdapter(context,SourceDataList,type,managedatas.size());
				lv_qun_member.setAdapter(memberAdapter);
				if(EnumManage.GroupRole.master.toString().equals(getMemberAuthority())|| EnumManage.GroupRole.admin.toString().equals(getMemberAuthority())){
					tv_oper.setVisibility(View.VISIBLE);
				}else{
					tv_oper.setVisibility(View.GONE);
				}
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
		if(isOperAble(SourceDataList.get(arg2).getId(),SourceDataList.get(arg2).getRole())) {
			if (!SourceDataList.get(arg2).getSelectedStatus()) {

				if (selectOnly) {//群主转让时，只能选择一个
					SourceDataList.get(lastselection).setSelectedStatus(false);
					selectlist.remove(SourceDataList.get(lastselection).getId());
				}
				SourceDataList.get(arg2).setSelectedStatus(true);
				selectlist.add(SourceDataList.get(arg2).getId());
				lastselection = arg2;
			} else {
				SourceDataList.get(arg2).setSelectedStatus(false);
				selectlist.remove(SourceDataList.get(arg2).getId());
				lastselection = arg2;
			}
			memberAdapter.notifyDataSetChanged();
		}
	}

	private boolean isOperAble(String uid,String role){
		if(mysessionMember==null)
			return false;
		if(mysessionMember.getEntity().getId().equals(uid)){
			ToastUtil.ToastMessage(this,"不能操作自己");
			return false;
		}
        if(mysessionMember.getEntity().getRole().equals(EnumManage.GroupRole.master.toString())){
			return true;
		}else if(mysessionMember.getEntity().getRole().equals(EnumManage.GroupRole.admin.toString())){
            if(EnumManage.GroupRole.admin.toString().equals(role)||EnumManage.GroupRole.master.toString().equals(role)){
			    	ToastUtil.ToastMessage(this,"您没有权限操作");
					return false;
			}else{
				return  true;
			}
		}else{
			ToastUtil.ToastMessage(this,"您没有权限操作");
			return false;
		}
	}

	private String getMemberAuthority(){
		try {
			mysessionMember = _session.getMembers().getLocalMember(Users.getInstance().getCurrentUser().getEntity().getId());
			if(mysessionMember != null){
				return mysessionMember.getEntity().getRole();
			}
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		return "";
	}

	private void groupMemberManagerDisplay(){
		setManagerSelectionDatas();
		settingsAdapter.notifyDataSetChanged();
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
			String action = settings.get(position);
			if(!StringUtil.isEmpty(action)){
				if(action.equals("邀请人加入")){
					Intent intent = new Intent(GroupMemberActivity.this, SessionStartActivity.class);
					Bundle bundle = new Bundle();
					bundle.putString("sessionId", _session.getEntity().getId());
					String userIds = "";
					if (memberList != null) {
						for (int i = 0; i < memberList.size(); i++) {
							userIds = userIds + memberList.get(i).getEntity().getUserId() + ";";
						}
					}
					bundle.putString("userIds", userIds);
					bundle.putBoolean("iscreate", false);
					intent.putExtras(bundle);
					startActivityForResult(intent, 100);
					settings.clear();
					settingsAdapter.notifyDataSetChanged();
				}else if(action.equals("删除群成员")){
					type = "select";
					selectOnly=false;
					opertype="delete";
					azListDisplay();
					settings.clear();
					settings.add("确认删除");
					settings.add("取消");
					settingsAdapter.notifyDataSetChanged();
				}else if(action.equals("设置管理员")){
					type = "select";
					selectOnly=false;
					opertype="setmanager";
					azListDisplay();
					settings.clear();
					settings.add("确认设置");
					settings.add("取消");
					settingsAdapter.notifyDataSetChanged();
				}else if(action.equals("转让群主")){
					type = "select";
					selectOnly=true;
					opertype="setmanager";
					azListDisplay();
					settings.clear();
					settings.add("确认转让");
					settings.add("取消");
					settingsAdapter.notifyDataSetChanged();
				}else if(action.equals("取消")){
					type = "display";
					azListDisplay();
					settings.clear();
					settingsAdapter.notifyDataSetChanged();
				}else if (action.equals("确认删除")){
					type = "display";
					settings.clear();
					settingsAdapter.notifyDataSetChanged();
					if(selectlist.size()<=0)
						return;

					pd = new ProgressDialog(GroupMemberActivity.this);
					pd.setCanceledOnTouchOutside(true);
					pd.setMessage(getString(R.string.operationing));
					final int allsize = selectlist.size();
					finishnet =0;
					for(int i =0;i<selectlist.size();i++){
						for(int j = 0;j<memberList.size();j++){
							if(selectlist.get(i).equals(memberList.get(j).getEntity().getId())){
								_session.getMembers().getRemote().sessionMemberDelete(memberList.get(j), new Back.Callback() {
									@Override
									public void onSuccess() {
										finishnet++;
										if(finishnet==allsize) {
											ToastUtil.ToastMessage(GroupMemberActivity.this, "删除完成");
											pd.dismiss();
											getLocalMembers();
										}
									}
									@Override
									public void onError(int code, String error) {
										finishnet++;
										if(finishnet==allsize) {
											ToastUtil.ToastMessage(GroupMemberActivity.this, "删除完成");
											pd.dismiss();
											getLocalMembers();
										}
									}
								});
								break;
							}
						}
					}
				}else if (action.equals("确认设置")){
					type = "display";
					settings.clear();
					settingsAdapter.notifyDataSetChanged();
					if(selectlist.size()<=0)
						return;
					pd = new ProgressDialog(GroupMemberActivity.this);
					pd.setCanceledOnTouchOutside(true);
					pd.setMessage(getString(R.string.operationing));
					allMamge = 0;
					finishnet=0;
					//设置管理员
					for(int i =0;i<selectlist.size();i++){
						for(int j = 0;j<memberList.size();j++){
							if(selectlist.get(i).equals(memberList.get(j).getEntity().getId())&&memberList.get(j).getEntity().getRole().equals(EnumManage.GroupRole.user.toString())){
								allMamge++;
								final String name = memberList.get(j).getEntity().getName();
								_session.getMembers().getRemote().sessionMemberUpdate(memberList.get(j), EnumManage.GroupRole.admin.toString(), EnumManage.UserStatus.active.toString(), new Back.Callback() {
									@Override
									public void onSuccess() {
										finishnet++;
										if(finishnet==allMamge) {
											ToastUtil.ToastMessage(GroupMemberActivity.this, "设置完成");
											pd.dismiss();
											getLocalMembers();
										}
										if(ChatActivity._context!=null)
										((ChatActivity)ChatActivity._context).sendSystemMessage(Users.getInstance().getCurrentUser().getEntity().getName()+"设"+name+"为管理员");
									}

									@Override
									public void onError(int code, String error) {
										finishnet++;
										if(finishnet==allMamge) {
											ToastUtil.ToastMessage(GroupMemberActivity.this, "设置完成");
											pd.dismiss();
											getLocalMembers();
										}
									}
								});
								break;
							}
						}
					}
                    //取消管理员
					for(int i =0;i<memberList.size();i++){
						if(memberList.get(i).getEntity().getRole().equals(EnumManage.GroupRole.admin.toString())){
						  boolean find = false;
							for(int j = 0;j<selectlist.size();j++) {
							     if(memberList.get(i).getEntity().getId().equals(selectlist.get(j))){
									find = true;
									break;
								}
							}
							if(!find){
								allMamge++;
								_session.getMembers().getRemote().sessionMemberUpdate(memberList.get(i), EnumManage.GroupRole.user.toString(), EnumManage.UserStatus.active.toString(), new Back.Callback() {
									@Override
									public void onSuccess() {
										finishnet++;
										if(finishnet==allMamge) {
											ToastUtil.ToastMessage(GroupMemberActivity.this, "设置完成");
											pd.dismiss();
											getLocalMembers();
										}
									}

									@Override
									public void onError(int code, String error) {
										finishnet++;
										if(finishnet==allMamge) {
											ToastUtil.ToastMessage(GroupMemberActivity.this, "设置完成");
											pd.dismiss();
											getLocalMembers();
										}
									}
								});
							}
						}
					}


				}else if (action.equals("确认转让")){
					type = "display";
					settings.clear();
					settingsAdapter.notifyDataSetChanged();
					if(selectlist.size()<=0)
						return;
					pd = new ProgressDialog(GroupMemberActivity.this);
					pd.setCanceledOnTouchOutside(true);
					pd.setMessage(getString(R.string.operationing));
					for(int i =0;i<selectlist.size();i++){
						for(int j = 0;j<memberList.size();j++){
							if(selectlist.get(i).equals(memberList.get(j).getEntity().getId())){
								final String name = memberList.get(j).getEntity().getName();
								_session.getMembers().getRemote().sessionMemberUpdate(memberList.get(j), EnumManage.GroupRole.master.toString(), EnumManage.UserStatus.active.toString(), new Back.Callback() {
									@Override
									public void onSuccess() {
										SessionMember member = _session.getMembers().getLocalMember(Users.getInstance().getCurrentUser().getEntity().getId());
										_session.getMembers().getRemote().sessionMemberUpdate(member, EnumManage.GroupRole.user.toString(), EnumManage.UserStatus.active.toString(), new Back.Callback() {
											@Override
											public void onSuccess() {
												getLocalMembers();
												ToastUtil.ToastMessage(GroupMemberActivity.this, "转让成功");
												pd.dismiss();
											}

											@Override
											public void onError(int code, String error) {
												pd.dismiss();
											}
										});
										if(ChatActivity._context!=null)
										((ChatActivity)ChatActivity._context).sendSystemMessage(Users.getInstance().getCurrentUser().getEntity().getName()+"转让群主给"+name);
									}

									@Override
									public void onError(int code, String error) {
											ToastUtil.ToastMessage(GroupMemberActivity.this, "转让失败");
										     pd.dismiss();
									}
								});
								break;
							}
						}
					}
				}
			}

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
				if(sessionMembers==null||sessionMembers.size()==0)
					return;
				memberList.clear();
				for(int i =0;i<sessionMembers.size();i++){
					if(EnumManage.UserStatus.active.toString().equals(sessionMembers.get(i).getEntity().getStatus())) {
						memberList.add(sessionMembers.get(i));
					}
					if(sessionMembers.get(i).getEntity().getUserId().equals(Users.getInstance().getCurrentUser().getEntity().getId())){
						mysessionMember =sessionMembers.get(i);
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

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if(requestCode == 100){
			getLocalMembers();
		}else if(requestCode == 2){
			
		}else if(requestCode == 3){
			
		}
	}
	
}
