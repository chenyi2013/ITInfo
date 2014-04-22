package com.kevin.itinfo;

import com.kevin.config.Urls;
import com.kevin.fra.InfoDetailFragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Window;
import android.webkit.WebView;
import android.widget.TextView;

public class NewsContent extends Activity {

	private WebView mWv;
	private TextView mShareButton;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.itinformation_article_activity);
		initView();

	}

	private void initView() {
		mWv = (WebView) findViewById(R.id.article_wv);
		mShareButton = (TextView) findViewById(R.id.shareButton);

		Intent intent = getIntent();
		String id = intent.getStringExtra(InfoDetailFragment.NEWS_ID);
		String title = intent.getStringExtra(InfoDetailFragment.NEWS_TITLE);
		String cover = intent.getStringExtra(InfoDetailFragment.NEWS_COVER);

		String url = Urls.NEWS_CONTENT + id + ".html";
		mWv.loadUrl(url);
		;
	}

}
