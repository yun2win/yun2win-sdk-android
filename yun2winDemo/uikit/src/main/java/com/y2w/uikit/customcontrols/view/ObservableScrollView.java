package com.y2w.uikit.customcontrols.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ScrollView;

/**
 * 监听滑动的ScrollView
 * @author Administrator
 *
 */
public class ObservableScrollView extends ScrollView {
  
    private ScrollViewListener scrollViewListener = null;  
  
    public ObservableScrollView(Context context) {  
        super(context);  
    }  
  
    public ObservableScrollView(Context context, AttributeSet attrs,
                                int defStyle) {
        super(context, attrs, defStyle);  
    }  
  
    public ObservableScrollView(Context context, AttributeSet attrs) {  
        super(context, attrs);  
    }  
  
    public void setScrollViewListener(ScrollViewListener scrollViewListener) {  
        this.scrollViewListener = scrollViewListener;  
    }  
  
    @Override  
    protected void onScrollChanged(int x, int y, int oldx, int oldy) {  
        super.onScrollChanged(x, y, oldx, oldy);  
        if (scrollViewListener != null) {  
            scrollViewListener.onScrollChanged(this, x, y, oldx, oldy); 
            
            if(y <= 0){
            	scrollViewListener.onScrollTop();
            }else if(y + getHeight() >=  computeVerticalScrollRange()){  
            	scrollViewListener.onScrollBottom();  
            } 
        }  
        
    }  
  
    
    public interface ScrollViewListener {  
    	  
        void onScrollChanged(View scrollView, int x, int y, int oldx, int oldy);  
      
        void onScrollBottom();
        
        void onScrollTop();
    }  
}  