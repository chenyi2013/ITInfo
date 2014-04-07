package com.kevin.itinfo;

import android.os.Bundle;
import android.os.Handler;
import android.view.Window;

public class SplashActivity extends BaseActivity {

	private Handler handelr = new Handler();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_splash);
		
		handelr.postDelayed(new Runnable() {

			@Override
			public void run() {
				goHome();
			}
		}, 3000);
	}

	private void goHome() {
		openActivity(MainActivity.class);
		finish();
	}
	
	/**
	 * 启动下载服务加载首页数据
	 */
	private void startDownloadService()
	{
		
	}

}
