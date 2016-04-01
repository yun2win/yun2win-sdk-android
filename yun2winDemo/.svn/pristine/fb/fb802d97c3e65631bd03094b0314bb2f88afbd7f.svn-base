package y2w.ui.adapter;

import java.util.List;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;


public class FragmentAdapter extends FragmentPagerAdapter{

	private List<Fragment> _list;
	public FragmentAdapter(FragmentManager fm, List<Fragment> list) {
		super(fm);
		this._list = list;
	}

	@Override
	public Fragment getItem(int arg0) {
		return _list.get(arg0);
	}

	@Override
	public int getCount() {
		return _list !=null ?_list.size():0;
	}

}