package y2w.ui.activity;

import android.app.ActionBar;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.yun2win.demo.R;

import java.util.ArrayList;
import java.util.List;

import y2w.base.AppData;
import y2w.common.TabPagerAdapter;
import y2w.ui.fragment.StoreageFileSelectFragment;

/**
 * 本地文档
 * @author Administrator
 *
 */
public class StoreageFileSelectActivity extends FragmentActivity {

	private List<Fragment> mFragmentList;
	private ViewPager mViewPager;
	private Context context;
	private String type;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_storeage_file);
		initActionBar();
		context = this;
		mViewPager = (ViewPager) findViewById(R.id.vp_storeage_file);
		init(this.getIntent().getExtras());
		context = this;
	}

	private void init(Bundle bundle) {

		type = bundle.getString("type", "");
		AppData.getInstance().getFileItems().clear();// 图片选择列表选择发送前 清空
		mFragmentList = new ArrayList<Fragment>();

		StoreageFileSelectFragment fragment = new StoreageFileSelectFragment();
		Bundle args = new Bundle();
		args.putString("arg", "档案");
		args.putString("type", type);
		fragment.setArguments(args);

		mFragmentList.add(fragment);

		mViewPager.setAdapter(new TabPagerAdapter(getSupportFragmentManager(),
				mFragmentList));

	}

	private void initActionBar(){
		ActionBar actionbar = getActionBar();
		actionbar.setDisplayShowTitleEnabled(false);
		actionbar.setDisplayShowHomeEnabled(false);
		actionbar.setDisplayShowCustomEnabled(true);
		actionbar.setCustomView(R.layout.actionbar_chat);
		TextView texttitle = (TextView) actionbar.getCustomView().findViewById(R.id.text_title);
		ImageButton imageButtonClose = (ImageButton) actionbar.getCustomView().findViewById(R.id.left_close);
		texttitle.setText("文档");
		imageButtonClose.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});

	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			AppData.getInstance().getFileItems().clear();
			finish();
			break;
		}
		return true;
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			AppData.getInstance().getFileItems().clear();
			finish();
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	public void finish() {
		super.finish();
	}
}
