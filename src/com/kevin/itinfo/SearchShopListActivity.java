package com.kevin.itinfo;

import java.util.ArrayList;
import java.util.List;

import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.kevin.bean.QQInfo;
import com.kevin.bean.Retailer;
import com.kevin.services.DownloadService;
import com.kevin.services.DownloadService.LocalServiceBinder;

public class SearchShopListActivity extends BaseActivity {

	private String mBandID;
	private ListView mLv;
	private List<Retailer> mRetailers = new ArrayList<Retailer>();
	private RetailerListAdapter mAdapter;
	private Button mAllButton;
	private String mKey;

	public final static String RETAILER = "retailer";
	public final static String BUNDLE = "bundle";

	public static final int SUCCESS = 1;

	/** 用于下载数据的Services */
	private DownloadService mDownloadService;
	/** 用于与DownloadService通信用的Handler */
	private Handler handler = new Handler() {

		public void handleMessage(Message msg) {

			if (msg != null) {
				mRetailers = (List<Retailer>) msg.obj;
				mAdapter = new RetailerListAdapter();
				mLv.setAdapter(mAdapter);
				mAdapter.notifyDataSetChanged();
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
				if (mBandID != null) {
					mDownloadService.loadRetailers(handler, SUCCESS, mKey,
							mBandID);
				}
			}

		}
	};

	@Override
	protected void onCreate(Bundle arg0) {
		// TODO Auto-generated method stub
		super.onCreate(arg0);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.saler_shop_list_activity);
		mLv = (ListView) findViewById(R.id.saler_shop_list_List);
		mLv.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				ViewHolder viewHolder = (ViewHolder) view.getTag();
				Retailer retailer = viewHolder.retailer;
				Intent intent = new Intent(SearchShopListActivity.this,
						ShopDetailActivity.class);
				Bundle bundle = new Bundle();
				bundle.putSerializable(RETAILER, retailer);
				intent.putExtra(BUNDLE, bundle);
				startActivity(intent);

			}
		});
		mAllButton = (Button) findViewById(R.id.saler_shop_city_button);
		mAllButton.setOnClickListener(new Button.OnClickListener() {

			@Override
			public void onClick(View v) {
				// FIXME 显示全国列表

			}
		});

		mBandID = getIntent().getStringExtra(SearchActivity.BRAND_ID);
		mKey = getIntent().getStringExtra(SearchActivity.KEY);
	}

	class RetailerListAdapter extends BaseAdapter {
		LayoutInflater inflater = LayoutInflater
				.from(SearchShopListActivity.this);
		ViewHolder viewHolder;

		@Override
		public int getCount() {
			return mRetailers.size();
		}

		@Override
		public Object getItem(int position) {
			return mRetailers.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if (convertView == null) {
				convertView = inflater.inflate(
						R.layout.saler_shop_list_activity_item, parent, false);
				viewHolder = new ViewHolder();
				viewHolder.titleTextView = (TextView) convertView
						.findViewById(R.id.saler_shop_item_title);
				viewHolder.qqTextView = (TextView) convertView
						.findViewById(R.id.saler_shop_item_qq);
				convertView.setTag(viewHolder);

			}
			viewHolder = (ViewHolder) convertView.getTag();
			Retailer retailer = mRetailers.get(position);
			viewHolder.titleTextView.setText(retailer.getName());
			viewHolder.retailer = retailer;

			StringBuffer stringBuffer = new StringBuffer();
			for (QQInfo qq : retailer.getQq()) {
				stringBuffer.append(qq.getQq() + ",");
			}
			viewHolder.qqTextView.setText(stringBuffer.substring(0,
					stringBuffer.length() - 1));
			return convertView;
		}
	}

	class ViewHolder {
		TextView titleTextView;
		TextView qqTextView;
		Retailer retailer;
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

}
