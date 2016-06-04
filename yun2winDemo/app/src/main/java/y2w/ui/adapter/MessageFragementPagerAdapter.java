package y2w.ui.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.List;

public class MessageFragementPagerAdapter extends FragmentPagerAdapter {

	private List<Fragment> list;
	public MessageFragementPagerAdapter(FragmentManager fm, List<Fragment> _list) {
		super(fm);
		this.list = _list;
	}

	@Override
	public Fragment getItem(int arg0) {
		return list.get(arg0);
	}

	@Override
	public int getCount() {
		return list !=null ?list.size():0;
	}

}
