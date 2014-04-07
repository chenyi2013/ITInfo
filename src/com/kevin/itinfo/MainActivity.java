package com.kevin.itinfo;

import com.kevin.fra.FranchiserFragment;
import com.kevin.fra.InformationFragment;
import com.kevin.fra.ManufacturerFragment;
import com.kevin.fra.MoreFragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.view.Window;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;

public class MainActivity extends BaseActivity implements
		RadioGroup.OnCheckedChangeListener {

	/** 打印日志的Tag */
	private final static String TAG = MainActivity.class.getName();

	/** 用于控制打印日志的boolean变量 */
	private static boolean IS_DEBUG = false;

	/** 菜单容器 */
	private RadioGroup mMenuContainer;

	/** 程序的title布局 */
	private RelativeLayout mTopTitleLayout;

	private FragmentManager mManager;

	private Fragment mCurrentFragment;

	private FragmentTransaction mTransaction;

	/** 是否隐藏顶部的title */
	private boolean mIsHideTop = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_main);
		initView();
	}

	private void initView() {

		mTopTitleLayout = (RelativeLayout) findViewById(R.id.top_title);

		mMenuContainer = (RadioGroup) findViewById(R.id.menu_container);
		mMenuContainer.setOnCheckedChangeListener(this);

		mCurrentFragment = new InformationFragment();
		mManager = getSupportFragmentManager();
		mTransaction = mManager.beginTransaction();
		mTransaction.add(R.id.main_container, mCurrentFragment);
		mTransaction.commit();

	}

	@Override
	public void onCheckedChanged(RadioGroup group, int checkedId) {

		switch (checkedId) {
		case R.id.information_button:
			mCurrentFragment = new InformationFragment();
			mIsHideTop = false;
			break;

		case R.id.franchiser_button:
			mCurrentFragment = new FranchiserFragment();
			mIsHideTop = false;
			break;

		case R.id.manufacturer_button:
			mCurrentFragment = new ManufacturerFragment();
			mIsHideTop = false;
			break;

		case R.id.more_button:
			mCurrentFragment = new MoreFragment();
			mIsHideTop = true;
			break;
		default:
			return;
		}

		if (mIsHideTop) {
			mTopTitleLayout.setVisibility(View.GONE);
		} else {
			mTopTitleLayout.setVisibility(View.VISIBLE);
		}

		if (mCurrentFragment != null) {
			mManager = getSupportFragmentManager();
			mTransaction = mManager.beginTransaction();
			mTransaction.replace(R.id.main_container, mCurrentFragment);
			mTransaction.commit();
		}

	}

}
