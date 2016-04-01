package y2w.ui.fragment;

import android.app.Activity;
import android.content.Context;
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
		    settingsInit(view);
	        return view;
	}

	private ListView listView;
	private List<SettingTemplate> items = new ArrayList<SettingTemplate>();
	private SettingsAdapter adapter;
	private SettingTemplate disturbItem;
	private static final int TAG_HEAD = 1;
	private static final int TAG_CHANGE_PASS= 2;
	private static final int TAG_ABOUT_ME = 3;
	private static final int TAG_GETOUT_LOGIN = 4;

	private void settingsInit(View view) {
		initItems();
		listView = (ListView) view.findViewById(R.id.lv_settings);

		adapter = new SettingsAdapter(context,items);
		listView.setAdapter(adapter);
		listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				SettingTemplate item = items.get(position);
				onListItemClick(item);
			}
		});

	}

	private void onListItemClick(SettingTemplate item) {
		if(item == null) return;

		switch (item.getId()) {
			case TAG_HEAD:
				break;
			case TAG_CHANGE_PASS:
				break;
			case TAG_ABOUT_ME:
				break;
			case TAG_GETOUT_LOGIN:
				logout();
				break;
			default:
				break;
		}
	}

	private void initItems() {
		items.clear();
		items.add(SettingTemplate.makeSeperator());
		items.add(new SettingTemplate(TAG_HEAD, SettingType.TYPE_HEAD));
		items.add(SettingTemplate.makeSeperator());
		items.add(new SettingTemplate(TAG_CHANGE_PASS, getString(R.string.change_pass),SettingType.TYPE_NEWUI));
		items.add(SettingTemplate.makeSeperator());
		items.add(new SettingTemplate(TAG_ABOUT_ME, getString(R.string.about_me),SettingType.TYPE_NEWUI));
		items.add(SettingTemplate.makeSeperator());
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
			AppContext.getAppContext().logout();
			activity.finish();
		}catch (Exception e){

		}
	}

	
}

