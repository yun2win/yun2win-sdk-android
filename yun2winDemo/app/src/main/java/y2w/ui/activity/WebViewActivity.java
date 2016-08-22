package y2w.ui.activity;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.net.http.SslError;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.webkit.JavascriptInterface;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageButton;
import android.widget.TextView;

import com.y2w.uikit.customcontrols.view.ObservableScrollWebView;
import com.y2w.uikit.utils.StringUtil;
import com.yun2win.demo.R;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * 收藏暂时没搞定....
 * 
 * @author Administrator
 * 
 */
public class WebViewActivity extends Activity {

	private ObservableScrollWebView mWebView;
	private String URL;
	private String url_share;
	private String fristLoadUrl = null;

	private String type = "其他";
	private String title = "";
	int i = 0;
	TextView tv_title;
	@SuppressLint("SetJavaScriptEnabled")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		//getWindow().requestFeature(Window.FEATURE_PROGRESS);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_webview);
		Bundle bundle = this.getIntent().getExtras();
		/*title = getIntent().getStringExtra("Title");
		if (StringUtil.isEmpty(title)) {
			title = "返回";
		}
		setTitle(title);*/
		if(bundle != null)
		URL = bundle.getString("url");
		if (StringUtil.isEmpty(URL)) {
			URL = "www.yun2win.com";
		}
		//URL = URL.replace("~/", URLs.HTTP + URLs.HOST + URLs.URL_SPLITTER);

		if (bundle.containsKey("type")) {
			type = bundle.getString("type");
		}
		mWebView = (ObservableScrollWebView) findViewById(R.id.wv_ytw);

		// mWebView.getSettings().setJavaScriptEnabled(true); //
		// 设置支持javascript的例子

		WebSettings webSettings = mWebView.getSettings();
		webSettings.setJavaScriptEnabled(true); // 设置支持javascript脚本
		webSettings.setAllowFileAccess(true); // 允许访问文件
		webSettings.setBuiltInZoomControls(true); // 设置显示缩放按钮
		webSettings.setSupportZoom(true); // 支持缩放
		webSettings.setDomStorageEnabled(true);
		// webSettings.setDefaultZoom(ZoomDensity.FAR);

		// webSettings.setLayoutAlgorithm(LayoutAlgorithm.NARROW_COLUMNS);

		webSettings.setUseWideViewPort(true);// 关键点 ,可任意比例缩放
		webSettings.setLoadWithOverviewMode(true);

		// webSettings.setDefaultZoom(ZoomDensity.FAR);

		// webSettings.setLayoutAlgorithm(LayoutAlgorithm.NARROW_COLUMNS);
		// 通过WebChromeClient可以处理JS对话框，titles, 进度，等 ，这个例子，我们处理
		// ，我们将websit下载的进度同步到acitity的进度条上。
		mWebView.setWebChromeClient(new WebChromeClient() {

			public void onProgressChanged(WebView view, int newProgress) {
				// activity的进度是0 to 10000 (both inclusive),所以要*100
				WebViewActivity.this.setProgress(newProgress * 100);
			}

			@Override
			public void onReceivedTitle(WebView view, String title1) {
				super.onReceivedTitle(view, title1);
				if (!StringUtil.isEmpty(title1)) {
					title = title1;
				}
				if (StringUtil.isEmpty(title)) {
					tv_title.setText("返回");
				} else {
					tv_title.setText("返回  "+title);
				}
			}
		});

		mWebView.setWebViewClient(new WebViewClient() {
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				url_share = url;
				if (fristLoadUrl == null) {
					fristLoadUrl = url;
				}
				view.loadUrl(url);
				return true;
			}

			@Override
			public void onReceivedSslError(WebView view,
					SslErrorHandler handler, SslError error) {
				// handler.cancel(); // Android默认的处理方式
				handler.proceed(); // 接受所有网站的证书
				// handleMessage(Message msg); // 进行其他处理
			}

		});

		if (URL.startsWith("http")) {
			mWebView.loadUrl(URL);
		} else {
			URL = "http://" + URL;
			mWebView.loadUrl(URL);
		}
		url_share = URL;
		/*if (ChatActivity.adapter != null)
			ChatActivity.adapter.notifyDataSetChanged();// URL颜色发送变化，在此恢复刷新
*/
		/*if (URL.indexOf(URLs.HOST) >= 0) {
			InitMethodForJS(mWebView);
		}*/
		initActionBar();
	}

	private void initActionBar(){
		ActionBar actionbar = getActionBar();
		actionbar.setDisplayShowTitleEnabled(false);
		actionbar.setDisplayShowHomeEnabled(false);
		actionbar.setDisplayShowCustomEnabled(true);
		actionbar.setCustomView(R.layout.actionbar_chat);
		tv_title = (TextView) actionbar.getCustomView().findViewById(R.id.text_title);
		ImageButton imageButtonClose = (ImageButton) actionbar.getCustomView().findViewById(R.id.left_close);
		tv_title.setText("返回");
		imageButtonClose.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});

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
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == android.R.id.home) {
			finish();
		}
		return true;
	}

	@SuppressLint("JavascriptInterface")
	private void InitMethodForJS(WebView webview) {

	}
	
	@Override
	public void finish() {
		super.finish();
	}
}
