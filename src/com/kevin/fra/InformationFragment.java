package com.kevin.fra;

import com.kevin.itinfo.R;
import com.kevin.view.TabPageIndicator;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class InformationFragment extends Fragment {
	private static final String[] INFO_MENU = new String[] { "新品", "最新", "管理",
			"行情", "测评", "招聘" };
	private static final int[] INFO_MENU_IDS = {2,1,6,3,24,5};
	public static final String NEWS_ID = "news_id";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		return inflater
				.inflate(R.layout.information_fragment, container, false);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
		FragmentPagerAdapter adapter = new InfoPagerAdapter(
				getChildFragmentManager());
		ViewPager pager = (ViewPager) getView().findViewById(
				R.id.info_view_pager);
		pager.setAdapter(adapter);
		TabPageIndicator indicator = (TabPageIndicator) getView().findViewById(
				R.id.indicator);
		indicator.setViewPager(pager);
	}

	class InfoPagerAdapter extends FragmentPagerAdapter {

		public InfoPagerAdapter(FragmentManager fm) {
			super(fm);
			// TODO Auto-generated constructor stub
		}

		@Override
		public Fragment getItem(int position) {
			InfoDetailFragment infoDetailFragment = new InfoDetailFragment();
			Bundle bundle = new Bundle();
			bundle.putInt(NEWS_ID, INFO_MENU_IDS[position]);
			infoDetailFragment.setArguments(bundle);
			return infoDetailFragment;
		}

		@Override
		public CharSequence getPageTitle(int position) {
			// TODO Auto-generated method stub
			return INFO_MENU[position % INFO_MENU.length];
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return INFO_MENU.length;
		}

	}

}
