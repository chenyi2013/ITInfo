package com.kevin.itinfo;

import android.app.Activity;
import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.kevin.bean.Retailer;
import com.kevin.bean.RetailerInfo;
import com.kevin.map.BasicMapActivity;
import com.kevin.services.DownloadService;
import com.kevin.services.DownloadService.LocalServiceBinder;

public class ShopDetailActivity extends Activity {

	private TextView titleTextView;
	private TextView contentTextView;
	private TextView addressTextView;
	private TextView mainSaleTextView;
	private TextView qqTextView;
	private TextView phoneTextView1;
	private TextView phoneTextView2;
	private LinearLayout layout;
	private ImageView imageView1;
	private ImageView imageView2;
	private ImageView imageView3;
	private Retailer retailer;
	private RetailerInfo retailerInfo;
	private FrameLayout frameLayout;

	public static final int SUCCESS = 1;

	/** 用于下载数据的Services */
	private DownloadService mDownloadService;
	/** 用于与DownloadService通信用的Handler */
	private Handler handler = new Handler() {

		public void handleMessage(Message msg) {

			if (msg != null) {
				retailerInfo = (RetailerInfo) msg.obj;
				if (retailerInfo != null) {

					contentTextView.setText(retailerInfo.getDescription());
					titleTextView.setText(retailerInfo.getTitle());
					addressTextView.setText(retailerInfo.getAddress());
					mainSaleTextView.setText(retailerInfo.getArea());
					if (retailerInfo.getImg().size() == 1) {
						setBitmapToView(imageView1, retailerInfo.getImg()
								.get(0));
					} else if (retailerInfo.getImg().size() == 2) {
						setBitmapToView(imageView1, retailerInfo.getImg()
								.get(0));
						setBitmapToView(imageView2, retailerInfo.getImg()
								.get(1));
					} else if (retailerInfo.getImg().size() == 3) {
						setBitmapToView(imageView1, retailerInfo.getImg()
								.get(0));
						setBitmapToView(imageView2, retailerInfo.getImg()
								.get(1));
						setBitmapToView(imageView3, retailerInfo.getImg()
								.get(2));
					}

					StringBuffer stringBuffer = new StringBuffer();
					for (String qq : retailerInfo.getQq()) {
						stringBuffer.append(qq + "\t");
					}
					qqTextView.setText(stringBuffer);

					if (retailerInfo.getTel().size() == 1) {
						setPhoneTextToView(phoneTextView1, retailerInfo
								.getTel().get(0));
					} else if (retailerInfo.getTel().size() == 2) {
						setPhoneTextToView(phoneTextView1, retailerInfo
								.getTel().get(0));
						layout.setVisibility(View.VISIBLE);
						setPhoneTextToView(phoneTextView2, retailerInfo
								.getTel().get(1));

					}
				}
			}
		}
	};

	private ServiceConnection mServiceConnection = new ServiceConnection() {

		@Override
		public void onServiceDisconnected(ComponentName name) {
			mDownloadService = null;

		}

		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			Log.i("kevin", "onServiceConnected");
			if (service != null) {
				mDownloadService = ((LocalServiceBinder) service)
						.getDownloadService();
				mDownloadService.loadRetailerInfo(handler, SUCCESS,
						retailer.getAgency_id());
			}

		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.saler_shop_detail_activity);
		retailer = (Retailer) getIntent().getBundleExtra(
				SearchShopListActivity.BUNDLE).getSerializable(
				SearchShopListActivity.RETAILER);
		frameLayout = (FrameLayout) findViewById(R.id.shop_detail_map_fl);
		titleTextView = (TextView) findViewById(R.id.shop_detail_title_tv);
		contentTextView = (TextView) findViewById(R.id.shop_detail_content_tv);
		addressTextView = (TextView) findViewById(R.id.shop_detail_address_tv);
		mainSaleTextView = (TextView) findViewById(R.id.shop_detail_main_sale_tv);
		imageView1 = (ImageView) findViewById(R.id.shop_detail_image1_iv);
		imageView2 = (ImageView) findViewById(R.id.shop_detail_image2_iv);
		imageView3 = (ImageView) findViewById(R.id.shop_detail_image3_iv);
		qqTextView = (TextView) findViewById(R.id.shop_detail_qq_tv);
		phoneTextView1 = (TextView) findViewById(R.id.shop_detail_phone_tv);
		phoneTextView2 = (TextView) findViewById(R.id.shop_detail_phone_tv2);
		layout = (LinearLayout) findViewById(R.id.shop_detail_phone_ll2);

		addressTextView.setOnClickListener(new MapOnClickListener());
		frameLayout.setOnClickListener(new MapOnClickListener());
		phoneTextView1.setOnClickListener(new TextOnClickListener());
		phoneTextView2.setOnClickListener(new TextOnClickListener());
	}

	@Override
	public void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
		Intent intent = new Intent(this, DownloadService.class);
		bindService(intent, mServiceConnection, Service.BIND_AUTO_CREATE);
	}

	@Override
	public void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
		unbindService(mServiceConnection);
	}

	private void setBitmapToView(ImageView imageView, String url) {
		// FIXME
		// internetUtils = new InternetUtils(url);
		// byte[] imageData = internetUtils.getByteArrayResultFromInternet();
		// Bitmap bm = BitmapFactory.decodeByteArray(imageData, 0,
		// imageData.length);
		// imageView.setImageBitmap(bm);
	}

	private void setPhoneTextToView(TextView textView, String data) {
		textView.setText(data);
		textView.setTag(data);
	}

	public class MapOnClickListener implements OnClickListener {

		@Override
		public void onClick(View v) {
			String lat = retailerInfo.getCoordinate().get(0);
			String lng = retailerInfo.getCoordinate().get(1);

			Intent intent = new Intent(ShopDetailActivity.this,
					BasicMapActivity.class);
			intent.putExtra("Latitude", Double.valueOf(lat));
			intent.putExtra("Longitude", Double.valueOf(lng));
			startActivity(intent);
		}

	}

	public class TextOnClickListener implements OnClickListener {

		@Override
		public void onClick(View v) {
			String phone = String.valueOf(v.getTag());
			Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:"
					+ phone));
			startActivity(intent);
		}

	}

}
