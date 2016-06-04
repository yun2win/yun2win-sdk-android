package y2w.common;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

public class TabPagerAdapter extends FragmentPagerAdapter {
	private List<Fragment> list;
	private List<String> titles;
	
	public TabPagerAdapter(FragmentManager fm, List<Fragment> list) {
		super(fm);
		this.list = list;
	}
	
	public TabPagerAdapter(FragmentManager fm, List<Fragment> list, List<String> titles) {
		super(fm);
		this.list = list;
		this.titles = titles;
	}
	
	public List<Fragment> getList(){
		return list;
	}
	
	public void setList(List<Fragment> list,List<String> titles){
		this.list = list;
		this.titles = titles;
		notifyDataSetChanged();
	}
	
	public void setList(List<Fragment> list){
		this.list = list;
		notifyDataSetChanged();
	}

	@Override
	public Fragment getItem(int arg0) {
		Fragment fragment = list.get(arg0);
		return fragment;
	}

	@Override
	public int getCount() {
		return list == null ? 0 : list.size();
	}
	
	@Override
	public CharSequence getPageTitle(int position) {
		if(titles != null && titles.size() > position){
			return titles.get(position);
		}else{
			return super.getPageTitle(position);
		}
	}
	
	@Override
	public void destroyItem(View container, int position, Object object) {
		// TODO Auto-generated method stub
//		super.destroyItem(container, position, object);
//		Log.e("", "TabPagerAdapter destroyItem 111");
	}
	
	@Override
	public void destroyItem(ViewGroup container, int position, Object object) {
		// TODO Auto-generated method stub
//		super.destroyItem(container, position, object);
//		Log.e("", "TabPagerAdapter destroyItem 2222");
	}
	
}
