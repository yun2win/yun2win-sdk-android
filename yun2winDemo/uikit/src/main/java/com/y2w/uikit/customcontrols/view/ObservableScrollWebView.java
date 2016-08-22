package com.y2w.uikit.customcontrols.view;

import android.content.Context;
import android.util.AttributeSet;
import android.webkit.WebView;


/**
 * 监听滑动的webview
 * @author Administrator
 *
 */
public class ObservableScrollWebView extends WebView {
	 private ObservableScrollView.ScrollViewListener scrollViewListener = null;

	public ObservableScrollWebView(Context context) {
		super(context);
	}

	public ObservableScrollWebView(Context context, AttributeSet attrs,
								   int defStyle) {
		super(context, attrs, defStyle);
	}

	public ObservableScrollWebView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	@Override
	protected void onScrollChanged(int l, int t, int oldl, int oldt) {

		super.onScrollChanged(l, t, oldl, oldt);

		 if (scrollViewListener != null) {  
	            scrollViewListener.onScrollChanged(this, l, t, oldl, oldt); 
	            
	            if(t <= 0){
	            	scrollViewListener.onScrollTop();
	            }else if(t + getHeight() >=  computeVerticalScrollRange()){  
	            	scrollViewListener.onScrollBottom();  
	            } 
	        }  

	}

	 public void setScrollViewListener(ObservableScrollView.ScrollViewListener scrollViewListener) {
	        this.scrollViewListener = scrollViewListener;  
	    }  

}
