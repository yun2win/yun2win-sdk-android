package y2w.ui.activity;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.http.SslError;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.y2w.uikit.customcontrols.view.ObservableScrollWebView;
import com.y2w.uikit.utils.NetWorkUtil;
import com.y2w.uikit.utils.StringUtil;
import com.y2w.uikit.utils.ToastUtil;
import com.yun2win.demo.R;
import com.yun2win.utils.Json;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import y2w.base.AppContext;
import y2w.base.AppData;
import y2w.common.ImagePool;
import y2w.service.Androidmethods;
import y2w.service.Back;
import y2w.service.MakeWebmethods;
import y2w.ui.adapter.WebMenuAdapter;
import y2w.ui.widget.loading.LoadingView;

/**
 * 
 * @author Administrator
 * 
 */
public class StrongWebViewActivity extends Activity {

	private ObservableScrollWebView mWebView;
	private String url_share;
	private String fristLoadUrl = null;

	private String type = "其他";
	private String title = "超强浏览器";
	int i = 0;
	TextView tv_title;
	private String toolbarColor;
	LinearLayout toolbarLinear;
	private Context context;
	private LoadingView progressbar;
	private TextView textmenu1,textmenu2;
	private ImageView imagemenu1,imagemenu2;
    private List<String> waitSendmsg = new ArrayList<String>();
	private boolean iswebload = false;
	private Androidmethods androidmethods;
	private MakeWebmethods makeWebmothods;
	Handler handlerui = new Handler(){
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			try {
			if(msg.what ==1){
				tv_title.setText((String)msg.obj);
			}else if(msg.what ==2){
					JSONObject json = (JSONObject) msg.obj;
					String bgColor =json.getString("bgColor");
					toolbarColor = json.getString("textColor");
					textmenu1.setTextColor(Color.parseColor(toolbarColor));
					textmenu2.setTextColor(Color.parseColor(toolbarColor));
					toolbarLinear.setBackgroundColor(Color.parseColor(bgColor));

			}else if(msg.what ==3){
				initMenus((List<Json>) msg.obj);
			}else if(msg.what==4){//获得时间
				String dateTime = (String) msg.obj;
				makeWebmothods.sendDate(dateTime);
			}else if(msg.what==5){//显示webview
				progressbar.setVisibility(View.GONE);
				actionbar.getCustomView().setVisibility(View.VISIBLE);
				mWebView.setVisibility(View.VISIBLE);

			}else if(msg.what==6){//webview转新url
				newurl((String) msg.obj);
			}else if(msg.what==7){//回退
				if (mWebView.canGoBack()) {
					mWebView.goBack(); // goBack()表示返回WebView的上一页面
					munusGone();

					if(!mWebView.canGoBack()&& AppData.getInstance().getWebViewActivitys().size()<=1){
						imageButtonClose.setVisibility(View.GONE);
					}
				}
			}
			}catch (Exception e){
			}
		}
	};
	Handler handlermsgsend = new Handler(){
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			try {
				if (msg.what == 1) {
					if (iswebload) {
						sendMsgToWeb((String) msg.obj);

					} else {
						waitSendmsg.add((String) msg.obj);
					}
				}
			}catch (Exception e){}
		}
	};
	private RelativeLayout rl_menu_top;
	private ListView lv_menu_top;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_strongwebview);
		AppData.getInstance().getWebViewActivitys().add(this);
		context = this;
		mWebView = (ObservableScrollWebView) findViewById(R.id.wv_ytw);
		progressbar = (LoadingView) findViewById(R.id.loadView);
		rl_menu_top = (RelativeLayout) findViewById(R.id.rl_menu_top);
		lv_menu_top = (ListView) findViewById(R.id.lv_menu_top);
		progressbar.setVisibility(View.VISIBLE);
		mWebView.setVisibility(View.GONE);
		rl_menu_top.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				rl_menu_top.setVisibility(View.GONE);
			}
		});
		makeWebmothods = new MakeWebmethods(handlerui,this,this);
		androidmethods = new Androidmethods(handlermsgsend,makeWebmothods,mWebView);

		final WebSettings webSettings = mWebView.getSettings();
		webSettings.setJavaScriptEnabled(true); // 设置支持javascript脚本

		if(NetWorkUtil.isNetworkAvailable(this)){
			webSettings.setCacheMode(WebSettings.LOAD_DEFAULT);
		}else{
			webSettings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
		}
		mWebView.getSettings().setDomStorageEnabled(true);
		mWebView.getSettings().setAppCacheMaxSize(1024*1024*8);
		String appCachePath = AppContext.getAppContext().getCacheDir().getAbsolutePath();
		mWebView.getSettings().setAppCachePath(appCachePath);
		mWebView.getSettings().setAllowFileAccess(true);
		mWebView.getSettings().setAppCacheEnabled(true);

		mWebView.setWebChromeClient(new WebChromeClient() {

			public void onProgressChanged(WebView view, int newProgress) {
				// activity的进度是0 to 10000 (both inclusive),所以要*100
				StrongWebViewActivity.this.setProgress(newProgress * 100);
			}

			@Override
			public void onReceivedTitle(WebView view, String title1) {
				super.onReceivedTitle(view, title1);
				if (!StringUtil.isEmpty(title1)) {
					title = title1;
				}
				/*if (StringUtil.isEmpty(title)) {
					tv_title.setText("返回");
				} else {
					tv_title.setText("返回  "+title);
				}*/
			}
		});

		mWebView.setWebViewClient(new WebViewClient() {
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				newurl(url);
				return true;
			}

			@Override
			public void onReceivedSslError(WebView view,
					SslErrorHandler handler, SslError error) {
				// handler.cancel(); // Android默认的处理方式
				handler.proceed(); // 接受所有网站的证书
				// handleMessage(Message msg); // 进行其他处理
			}

			@Override
			public void onPageFinished(WebView view, String url) {
				super.onPageFinished(view, url);
				iswebload = true;
				if(waitSendmsg.size()>0){
					for(int i =0;i<waitSendmsg.size();i++){
						Message msg = new Message();
						msg.what =1;
						msg.obj = waitSendmsg.get(i);
						handlermsgsend.sendMessage(msg);
					}
					waitSendmsg.clear();
				}
			}
		});

		initActionBar();

		mWebView.addJavascriptInterface(androidmethods, "androidmethods");
		String webUrl = getIntent().getStringExtra("webUrl");

       if(StringUtil.isEmpty(webUrl) || !webUrl.startsWith("http")) {
		   //mWebView.loadUrl("file:///android_asset/test.html");
		   //fristLoadUrl = "file:///android_asset/test.html";
		   ToastUtil.ToastMessage(this,"参数不可用");
		   finish();
	   }else{
		   mWebView.loadUrl(webUrl);
		   fristLoadUrl =webUrl;
	   }
	}
   private void newurl(String url){
	   url_share = url;
	   if (fristLoadUrl == null) {
		   fristLoadUrl = url;
	   }
	   iswebload = false;
	   munusGone();
	   mWebView.loadUrl(url);
	   if(AppData.getInstance().getShowWebViewClose()) {
		   imageButtonClose.setVisibility(View.VISIBLE);
	   }
   }
   private void munusGone(){
	   tv_title.setText("");
	   textmenu1.setVisibility(View.GONE);
	   textmenu2.setVisibility(View.GONE);
	   imagemenu1.setVisibility(View.GONE);
	   imagemenu2.setVisibility(View.GONE);
   }
	@SuppressLint({ "JavascriptInterface", "SetJavaScriptEnabled" })
	public void sendMsgToWeb(String msg){
		String sendmsg = "javascript:onmessage(\"" + msg.replace("\\","\\\\").replace("\"","\\\"") + "\")";
		mWebView.loadUrl(sendmsg);
	}
	public void reload(){
		{
			final String index = System.currentTimeMillis() + "";
			JSONObject jsonObject = new JSONObject();
			jsonObject.put("index", index);
			jsonObject.put("action", "refresh");
			JSONArray params = new JSONArray();
			jsonObject.put("params", params);
			if (androidmethods != null) {
				androidmethods.sendwebMessage(jsonObject.toJSONString(), new Back.Result<String>() {
					@Override
					public void onSuccess(String s) {
					}
					@Override
					public void onError(int code, String error) {
					}
				}, index);
			}
		}
	}
	private ImageButton imageButtonClose;
	private ActionBar actionbar;
	private void initActionBar(){

		 actionbar = getActionBar();
		actionbar.setDisplayShowTitleEnabled(false);
		actionbar.setDisplayShowHomeEnabled(false);
		actionbar.setDisplayShowCustomEnabled(true);
		actionbar.setCustomView(R.layout.actionbar_webview);
		tv_title = (TextView) actionbar.getCustomView().findViewById(R.id.text_title);
		toolbarLinear = (LinearLayout) actionbar.getCustomView().findViewById(R.id.toolbarweb);
		textmenu1 = (TextView) actionbar.getCustomView().findViewById(R.id.textmenu1);
		textmenu2 = (TextView) actionbar.getCustomView().findViewById(R.id.textmenu2);
		imagemenu1 = (ImageView) actionbar.getCustomView().findViewById(R.id.imagemenu1);
		imagemenu2 = (ImageView) actionbar.getCustomView().findViewById(R.id.imagemenu2);

		ImageButton imageButtonBack = (ImageButton) actionbar.getCustomView().findViewById(R.id.left_back);
		imageButtonBack.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (mWebView.canGoBack()) {
					mWebView.goBack(); // goBack()表示返回WebView的上一页面
					munusGone();
					if(!mWebView.canGoBack() && AppData.getInstance().getWebViewActivitys().size()<=1){
						imageButtonClose.setVisibility(View.GONE);
					}else{
						AppData.getInstance().setShowWebViewClose(true);
						imageButtonClose.setVisibility(View.VISIBLE);
					}
				} else {
					finish();
				}
			}
		});
		imageButtonClose = (ImageButton) actionbar.getCustomView().findViewById(R.id.left_close);
		imageButtonClose.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				List<Activity> webviewActivitys = new ArrayList<Activity>();
				webviewActivitys.addAll(AppData.getInstance().getWebViewActivitys());
               for(int i=0;i<webviewActivitys.size();i++){
				   if(webviewActivitys.get(i)!=null){
					 try {
						 webviewActivitys.get(i).finish();
					 }catch (Exception e){
					 }
				   }
			   }
			}
		});
		if(AppData.getInstance().getWebViewActivitys().size()>1 && AppData.getInstance().getShowWebViewClose()){
			imageButtonClose.setVisibility(View.VISIBLE);
		}
		actionbar.getCustomView().setVisibility(View.GONE);
	}

   public void initMenus(final List<Json> menus){
         if(menus!=null&&menus.size()>0){
			 makemenu(menus.get(0),textmenu1,imagemenu1);
			 if(menus.size()>1){
				 textmenu2.setVisibility(View.VISIBLE);
				 textmenu2.setText("全部");
				 textmenu2.setOnClickListener(new View.OnClickListener() {
					 @Override
					 public void onClick(View v) {
						  if(rl_menu_top.getVisibility()==View.GONE){
							  rl_menu_top.setVisibility(View.VISIBLE);
							  lv_menu_top.setAdapter(new WebMenuAdapter(context,menus));
						  }else{
							  rl_menu_top.setVisibility(View.GONE);
						  }
						   ;
					 }
				 });
			 }
			 lv_menu_top.setOnItemClickListener(new AdapterView.OnItemClickListener() {
				 @Override
				 public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
					 if(position<menus.size()){
						 rl_menu_top.setVisibility(View.GONE);
						 org.json.JSONObject param = menus.get(position).toJSONObject();
						 String index = System.currentTimeMillis()+"";
						 JSONObject jsonObject = new JSONObject();
						 jsonObject.put("index",index);
						 jsonObject.put("action","onMenuClick");
						 JSONArray params = new JSONArray();
						 JSONObject jsonparam = new JSONObject();
						 Iterator<String> paramkeys = param.keys();
						 for (Iterator iter = paramkeys; iter.hasNext();) {
							 String str =(String)iter.next() ;
							 try {
								 jsonparam.put(str,param.get(str));
							 } catch (JSONException e) {
								 e.printStackTrace();
							 }
						 }
						 params.add(jsonparam);
						 jsonObject.put("params", params);
						 androidmethods.sendwebMessage(jsonObject.toJSONString(), new Back.Result<String>() {
							 @Override
							 public void onSuccess(String s) {
							 }
							 @Override
							 public void onError(int code, String error) {
							 }
						 }, index);
					 }
				 }
			 });
		 }
   }

	private void makemenu(Json jsonmenu, TextView textview, ImageView imageview){
		String menuName =jsonmenu.getStr("name");
		String menuType = jsonmenu.getStr("type");
		 org.json.JSONObject param = jsonmenu.toJSONObject();
		Iterator<String> paramkeys = param.keys();
		final JSONArray params = new JSONArray();
		JSONObject jsonparam = new JSONObject();
		for (Iterator iter = paramkeys; iter.hasNext();) {
			String str =(String)iter.next() ;
			try {
				jsonparam.put(str,param.get(str));
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		params.add(jsonparam);
		if(!StringUtil.isEmpty(menuType)){
			if(menuType.equals("text")){
				textview.setVisibility(View.VISIBLE);
				textview.setText(menuName);
				textview.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						String index = System.currentTimeMillis()+"";
						JSONObject jsonObject = new JSONObject();
						jsonObject.put("index",index);
						jsonObject.put("action","onMenuClick");
						jsonObject.put("params", params);

						androidmethods.sendwebMessage(jsonObject.toJSONString(), new Back.Result<String>() {
							@Override
							public void onSuccess(String s) {

							}
							@Override
							public void onError(int code, String error) {

							}
						}, index);

					}
				});
			}else if(menuType.equals("icon") || menuType.equals("iconText")){
				String iconSrc = jsonmenu.getStr("iconSrc");
				imageview.setVisibility(View.VISIBLE);
				ImagePool.getInstance(AppContext.getAppContext()).load(iconSrc, "", imageview, 0);
				imageview.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						String index = System.currentTimeMillis()+"";
						JSONObject jsonObject = new JSONObject();
						jsonObject.put("index",index);
						jsonObject.put("action","onMenuClick");
						jsonObject.put("params", params);

						androidmethods.sendwebMessage(jsonObject.toJSONString(), new Back.Result<String>() {
							@Override
							public void onSuccess(String s) {

							}
							@Override
							public void onError(int code, String error) {

							}
						}, index);
					}
				});
			}
		}
	}

	private long clickTime = 0;

	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if ((keyCode == KeyEvent.KEYCODE_BACK)) {
			if (System.currentTimeMillis() - clickTime < 200) {
				finish();
				return true;
			}
			clickTime = System.currentTimeMillis();

			if (mWebView.canGoBack()) {
				mWebView.goBack(); // goBack()表示返回WebView的上一页面
				munusGone();
			} else {
				finish();
			}
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	private boolean isCollened = false;

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public void startActivity(Intent intent) {
		try {
			super.startActivity(intent);
		}catch (Exception e){}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == android.R.id.home) {
			finish();
		}
		return true;
	}
	
	@Override
	public void finish() {
		super.finish();
		AppData.getInstance().getWebViewActivitys().remove(this);

		if(AppData.getInstance().getWebViewActivitys().size()>1){
			AppData.getInstance().setShowWebViewClose(true);
		}else if(AppData.getInstance().getWebViewActivitys().size()==1){
			AppData.getInstance().setShowWebViewClose(false);
		}
		if(MainActivity.activity!=null) {

		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		if(makeWebmothods!=null){
			makeWebmothods.onResumeActivity();
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if(requestCode>700){
			makeWebmothods.activityResult(requestCode,resultCode,data);
		}
	}
}
