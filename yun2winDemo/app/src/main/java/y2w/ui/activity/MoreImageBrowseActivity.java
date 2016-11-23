package y2w.ui.activity;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.y2w.uikit.utils.ToastUtil;
import com.yun2win.demo.R;

import java.util.ArrayList;
import java.util.List;

import y2w.base.AppContext;
import y2w.manage.Users;
import y2w.model.MessageModel;
import y2w.model.Session;
import y2w.service.Back;
import y2w.ui.adapter.MessageFragementPagerAdapter;
import y2w.ui.fragment.ImageBrowseFragment;
import y2w.ui.fragment.MoreImageBrowseFragment;

public class MoreImageBrowseActivity extends FragmentActivity {
	private Context context;
	private AppContext appContext;

	/* 查询类容 */
	private RelativeLayout ll_preview;
	private ViewPager vp_pager;
	private TextView tv_index;
	private List<String> imgurls;
	private List<Fragment> fragmentList = new ArrayList<Fragment>();
    private int currentpage;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getActionBar().hide();
		this.setContentView(R.layout.activity_image_browse);
		appContext = (AppContext) getApplication();
		context = getApplicationContext();
		imgurls = new ArrayList<String>();
		this.getActionBar().hide();
		/*WindowManager.LayoutParams lp=getWindow().getAttributes();
		lp.alpha = 0.9f;
		getWindow().setAttributes(lp);*/
		ll_preview = (RelativeLayout) findViewById(R.id.ll_preview);
		vp_pager = (ViewPager) findViewById(R.id.vp_image_viewPager);
		tv_index = (TextView) findViewById(R.id.tv_image_index);
		ll_preview.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View arg0, MotionEvent arg1) {
				finish();
				return false;
			}
		});
		Bundle bundle = this.getIntent().getExtras();
		if(bundle == null)
			return;
		currentpage = this.getIntent().getIntExtra("currentpage",0);
		imgurls = this.getIntent().getStringArrayListExtra("imgurls");
		netWorkNotice();
		setData();
	}

	private void netWorkNotice(){
		ConnectivityManager connectivityManager=(ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

		try {
			if (networkInfo == null || !networkInfo.isConnected()) {
				ToastUtil.ToastMessage(context, "请检查网络链接");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private void setData(){
		try {
			if(imgurls == null || imgurls.size() == 0){
				return ;
			}
			for(String data:imgurls){
				Fragment firstFragment = MoreImageBrowseFragment.newInstance(this, appContext,data);
				fragmentList.add(firstFragment);
			}
			vp_pager.setAdapter(new MessageFragementPagerAdapter(this.getSupportFragmentManager(), (ArrayList<Fragment>) fragmentList));
					vp_pager.setCurrentItem(currentpage);
					tv_index.setText(currentpage + 1 + "/" + imgurls.size());


			vp_pager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
				@Override
				public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

				}

				@Override
				public void onPageSelected(int position) {
					tv_index.setText(position+1+"/"+imgurls.size());
				}

				@Override
				public void onPageScrollStateChanged(int state) {

				}
			});
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}