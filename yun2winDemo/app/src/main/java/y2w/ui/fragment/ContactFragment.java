package y2w.ui.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.y2w.uikit.customcontrols.imageview.HeadImageView;
import com.yun2win.demo.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import y2w.manage.Users;
import y2w.base.AppData;
import y2w.common.CallBackUpdate;
import y2w.model.Contact;
import y2w.service.Back;
import y2w.service.ErrorCode;
import y2w.ui.activity.ContactInfoActivity;
import y2w.ui.activity.GroupListActivity;
import y2w.ui.adapter.ContactAdapter;
import com.y2w.uikit.utils.StringUtil;
import com.y2w.uikit.utils.pinyinutils.CharacterParser;
import com.y2w.uikit.utils.pinyinutils.PinyinComparator;
import com.y2w.uikit.customcontrols.view.SideBar;
import com.y2w.uikit.utils.pinyinutils.SortModel;

import org.w3c.dom.Text;

/**
 * Created by hejie on 2016/3/14.
 * 通讯录界面
 */
public class ContactFragment extends Fragment{

    private String DEFAULT = "default";
    private static Activity activity;
    private static Context context;
	private CallBackUpdate callBackUpdate;
	private AcyContactdate acyContactdate;
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
				contactAdapter.updateListView();
			}else if(msg.what ==2){
				SortModel entity = (SortModel) msg.obj;
				boolean find = false;
				for(int i =1;i<SourceDataList.size();i++){
					if(SourceDataList.get(i).getId().equals(entity.getId())){
						find = true;
						break;
					}
				}
				if(!find){
					SourceDataList.add(entity);
				}
				contactAdapter.updateListView();
			}else if(msg.what ==3){
				SortModel entity = (SortModel) msg.obj;
				boolean find = false;
				for(int i =1;i<SourceDataList.size();i++){
					if(SourceDataList.get(i).getId().equals(entity.getId())){
						find = true;
						break;
					}
				}
				if(find){
					SourceDataList.remove(entity);
				}
				contactAdapter.updateListView();
			}else if(msg.what ==4){//联系人同步
				Users.getInstance().getCurrentUser().getContacts().getRemote().sync(new Back.Result<List<Contact>>(){
					@Override
					public void onSuccess(List<Contact> contacts) {
						callBackUpdate.updateUI();
					}
					@Override
					public void onError(int Code, String error) {
					}
				});
			}
		}
	};
	public static ContactFragment newInstance(Activity _activity,Context _context){
		
			ContactFragment newFragment = new ContactFragment();
	        Bundle bundle = new Bundle();
	       // bundle.putString("type", str);
	        newFragment.setArguments(bundle);
	        activity = _activity;
	        context = _context;
	        return newFragment;
		
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
	        Bundle args = getArguments();
	        //type = args != null ? args.getString("type") : DEFAULT;
	        View view = null;
			view = inflater.inflate(R.layout.fragment_contact_list, container, false);
			contactInit(view);

	        return view;
	}

	private ListView lv_contact;
	private ContactAdapter contactAdapter;

	/** 拼音排序 **/
	private CharacterParser characterParser;
	private List<SortModel> SourceDataList = new ArrayList<SortModel>();
	private PinyinComparator pinyinComparator;
	private SideBar sideBar;
	private TextView dialog;
	private List<Contact> contacts;

	public void contactInit(View view){
		lv_contact = (ListView) view.findViewById(R.id.lv_contact);
		initSideBar(view);
		initPinYin();

		contactAdapter = new ContactAdapter(context);
		lv_contact.setAdapter(contactAdapter);
		callBackUpdate = new CallBackUpdate(updatecontactHandler);
		callBackUpdate.updateUI();
		AppData.getInstance().getUpdateHashMap().put(CallBackUpdate.updateType.contact.toString(), callBackUpdate);

		lv_contact.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				if (position == 0) {
					Intent intent = new Intent(context, GroupListActivity.class);
					startActivity(intent);
				} else {
					SortModel model = SourceDataList.get(position);
					Intent intent = new Intent(context, ContactInfoActivity.class);
					Bundle bundle = new Bundle();
					bundle.putString("otheruserid",model.getUserId());
					bundle.putString("avatarUrl", model.getAvatarUrl());
					bundle.putString("username", model.getName());
					bundle.putString("account", model.getEmail());
					intent.putExtras(bundle);
					startActivity(intent);
				}
			}
		});

	}

	private void initPinYin(){
		characterParser = CharacterParser.getInstance();
		pinyinComparator = new PinyinComparator();
	}

	private void initSideBar(View view) {
		sideBar = (SideBar) view.findViewById(R.id.sidebar);
		dialog = (TextView) view.findViewById(R.id.dialog);
		sideBar.setTextView(dialog);
		// 设置右侧触摸监听
		sideBar.setOnTouchingLetterChangedListener(new SideBar.OnTouchingLetterChangedListener() {

			@Override
			public void onTouchingLetterChanged(String s) {
				// 该字母首次出现的位置
				try {
					if (s != null) {
						int position = contactAdapter.getPositionForSection(s
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
			//我的群
			SortModel smqun = new SortModel();
			smqun.setId("0");
			smqun.setUserId("0");
			smqun.setName("我的群");
			smqun.setPinyin("");
			smqun.setEmail("");
			smqun.setAvatarUrl("");
			smqun.setSortLetters("");
			SourceDataList.add(smqun);
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
				SourceDataList.add(sm);
			}
			// 根据a-z进行排序源数据
			Collections.sort(SourceDataList, pinyinComparator);
			contactAdapter.setListViewdate(SourceDataList);
			Message msg = new Message();
			msg.what =0;
			updatecontactHandler.sendMessage(msg);
		}
	}

}

