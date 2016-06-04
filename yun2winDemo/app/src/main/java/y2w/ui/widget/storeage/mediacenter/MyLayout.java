package y2w.ui.widget.storeage.mediacenter;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.RelativeLayout;

public class MyLayout extends RelativeLayout {
	ViewPager child_viewpager;
	float startX;

	/**
	 * @param context
	 * @param attrs
	 */
	public MyLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public boolean onInterceptTouchEvent(MotionEvent event) {
		int action = event.getAction();
		switch (action) {
		case MotionEvent.ACTION_DOWN:// 按下
			startX = event.getX();
			getParent().requestDisallowInterceptTouchEvent(true);
			break;
		case MotionEvent.ACTION_MOVE:
			if (Math.abs(startX - event.getX()) < 100) {
				if (0 == child_viewpager.getCurrentItem()
						|| child_viewpager.getCurrentItem() == child_viewpager
								.getAdapter().getCount() - 1) {
					getParent().requestDisallowInterceptTouchEvent(true);
				}
			}
			else if (startX > event.getX()) {
				if (child_viewpager.getCurrentItem() == child_viewpager
						.getAdapter().getCount() - 1) {
					getParent().requestDisallowInterceptTouchEvent(false);
				}
			}
			else if (startX < event.getX()) {
				if (child_viewpager.getCurrentItem() == 0) {
					getParent().requestDisallowInterceptTouchEvent(false);
				}
			} else {
				getParent().requestDisallowInterceptTouchEvent(true);
			}
			break;
		case MotionEvent.ACTION_UP:// 抬起
		case MotionEvent.ACTION_CANCEL:
			getParent().requestDisallowInterceptTouchEvent(false);
			break;
		}
		return false;
	}

	public void setChild_viewpager(ViewPager child_viewpager) {
		this.child_viewpager = child_viewpager;
	}
}
