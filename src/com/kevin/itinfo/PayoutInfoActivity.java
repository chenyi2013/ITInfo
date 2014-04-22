package com.kevin.itinfo;

import com.kevin.fra.ManufacturerFragment;
import com.kevin.util.ImageCache;
import com.kevin.util.ImageFetcher;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

public class PayoutInfoActivity extends FragmentActivity {

	private ImageView mIv;
	private TextView mTitle;
	private TextView mContent;

	private static final String IMAGE_CACHE_DIR = "thumbs";
	private int mImageThumbSize;
	private ImageFetcher mImageFetcher;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.payout_info_activity);

		mImageThumbSize = getResources().getDrawable(
				R.drawable.new_payout_info_logo_640_330).getIntrinsicWidth();
		ImageCache.ImageCacheParams cacheParams = new ImageCache.ImageCacheParams(
				this, IMAGE_CACHE_DIR);
		cacheParams.setMemCacheSizePercent(0.05f);
		mImageFetcher = new ImageFetcher(this, mImageThumbSize);
		mImageFetcher.setLoadingImage(R.drawable.new_payout_info_logo_640_330);
		mImageFetcher.addImageCache(getSupportFragmentManager(), cacheParams);

		mTitle = (TextView) findViewById(R.id.payout_info_title);
		mIv = (ImageView) findViewById(R.id.payout_info_logo);
		mContent = (TextView) findViewById(R.id.payout_info_content);

		Intent intent = getIntent();

		mImageFetcher.loadImage(
				intent.getStringExtra(ManufacturerFragment.FOCUS), mIv);
		mTitle.setText(intent.getStringExtra(ManufacturerFragment.TITLE));
		mContent.setText(intent.getStringExtra(ManufacturerFragment.CONTENT));

	}

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		mImageFetcher.setExitTasksEarly(false);

	}

	@Override
	public void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		mImageFetcher.setPauseWork(false);
		mImageFetcher.setExitTasksEarly(true);
		mImageFetcher.flushCache();
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		mImageFetcher.closeCache();
	}

}
