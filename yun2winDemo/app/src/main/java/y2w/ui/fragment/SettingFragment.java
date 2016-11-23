package y2w.ui.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.yun2win.demo.R;

import java.util.ArrayList;
import java.util.List;

import y2w.base.AppContext;
import y2w.manage.Users;
import y2w.ui.activity.MainActivity;
import y2w.ui.activity.PersonalInfoModifyActivity;
import y2w.ui.activity.WebViewActivity;
import y2w.ui.adapter.SettingsAdapter;
import y2w.model.SettingTemplate;
import y2w.model.SettingType;

/**
 * Created by hejie on 2016/3/14.
 * 设置界面
 */
public class SettingFragment extends Fragment{

    private String DEFAULT = "default";
    private static Activity activity;
    private static Context context;

	public static SettingFragment newInstance(Activity _activity,Context _context){
		
			SettingFragment newFragment = new SettingFragment();
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
	       // type = args != null ? args.getString("type") : DEFAULT;
	        View view = null;
		    view = inflater.inflate(R.layout.fragment_settings_list, container, false);
			listView = (ListView) view.findViewById(R.id.lv_settings);
		    settingsInit();
	        return view;
	}

	private ListView listView;
	private List<SettingTemplate> items = new ArrayList<SettingTemplate>();
	private SettingsAdapter adapter;
	private SettingTemplate disturbItem;
	private static final int TAG_NAME = 1;
	private static final int TAG_CHANGE_PASS= 2;
	private static final int TAG_ABOUT_ME = 3;
	private static final int TAG_GETOUT_LOGIN = 4;
	private static final int TAG_STRONG_BROWSER  = 5;
	private void settingsInit() {
		initItems();
		adapter = new SettingsAdapter(activity,context,items);
		listView.setAdapter(adapter);
		listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				SettingTemplate item = items.get(position);
				onListItemClick(item);
			}
		});

	}
	public void refreshAll(){
		if(adapter != null)
			adapter.notifyDataSetChanged();
	}

	private void onListItemClick(SettingTemplate item) {
		if(item == null) return;

		switch (item.getId()) {
			case TAG_NAME:
				Intent intent = new Intent(context,
						PersonalInfoModifyActivity.class);
				Bundle bundle = new Bundle();
				bundle.putInt("flag", PersonalInfoModifyActivity.FLAG_MODIFY_USERNAME);
				intent.putExtras(bundle);
				activity.startActivityForResult(intent, MainActivity.MainResultCode.CODE_PERSON_INFO_CHANGE);
				break;
			case TAG_CHANGE_PASS:
				Intent intent1 = new Intent(context,
						PersonalInfoModifyActivity.class);
				Bundle bundle1 = new Bundle();
				bundle1.putInt("flag", PersonalInfoModifyActivity.FLAG_MODIFY_PASSWORD);
				intent1.putExtras(bundle1);
				activity.startActivity(intent1);
				break;
			case TAG_ABOUT_ME:
				break;
			case TAG_GETOUT_LOGIN:
				logout();
				break;
			case TAG_STRONG_BROWSER:
				Intent intent2 = new Intent();
				Bundle bundle2 = new Bundle();
				bundle2.putString("url", "http://www.yun2win7.icoc.me/");
				intent2.putExtras(bundle2);
				intent2.setClass(context, WebViewActivity.class);
				activity.startActivity(intent2);
				break;
			default:
				break;
		}
	}

	private void initItems() {
		items.clear();
		//items.add(SettingTemplate.makeSeperator());
		items.add(new SettingTemplate(TAG_NAME, SettingType.TYPE_HEAD));
		items.add(SettingTemplate.makeSeperator());
		items.add(new SettingTemplate(TAG_CHANGE_PASS, getString(R.string.change_pass),SettingType.TYPE_NEWUI));
		items.add(SettingTemplate.makeSeperator());
		/*items.add(new SettingTemplate(TAG_ABOUT_ME, getString(R.string.about_me),SettingType.TYPE_NEWUI));
		items.add(SettingTemplate.makeSeperator());*/
		/*items.add(new SettingTemplate(TAG_STRONG_BROWSER, "帮助",SettingType.TYPE_NEWUI));
		items.add(SettingTemplate.makeSeperator());*/
		items.add(new SettingTemplate(TAG_GETOUT_LOGIN, getString(R.string.getout_login)));

		/*items.add(new SettingTemplate(TAG_NOTICE, getString(R.string.msg_notice), SettingType.TYPE_TOGGLE,
				false));
		items.add(SettingTemplate.addLine());
		items.add(new SettingTemplate(TAG_SPEAKER, getString(R.string.msg_speaker), SettingType.TYPE_TOGGLE,
				false));
		items.add(SettingTemplate.makeSeperator());
		disturbItem = new SettingTemplate(TAG_NO_DISTURBE, getString(R.string.no_disturb), 0);
		items.add(disturbItem);
		items.add(SettingTemplate.makeSeperator());
		items.add(new SettingTemplate(TAG_CLEAR, getString(R.string.about_clear_msg_history)));
		items.add(SettingTemplate.addLine());
		items.add(new SettingTemplate(TAG_CUSTOM_NOTIFY, getString(R.string.custom_notification)));
		items.add(SettingTemplate.addLine());
		items.add(new SettingTemplate(TAG_ABOUT, getString(R.string.setting_about)));*/
	}

	private void logout() {
		try {
			Users.getInstance().getCurrentUser().getImBridges().disConnect();
		}catch (Exception e){
		}
		AppContext.getAppContext().logout();
		activity.finish();
	}

	
}

