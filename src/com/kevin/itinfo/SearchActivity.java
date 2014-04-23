package com.kevin.itinfo;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnKeyListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.kevin.bean.Search;
import com.kevin.services.DownloadService;
import com.kevin.services.DownloadService.LocalServiceBinder;

public class SearchActivity extends BaseActivity implements OnItemClickListener {

	private TextView mSearchCounter;
	private TextView mBtnSearch;
	private EditText mDt;
	private List<Search> mSearchList;
	private ProgressBar mLoading;
	private MyAdapter mAdapter;
	private ListView mLv;
	private String mKey;
	private static int SUCCESS;

	public final static String BRAND_ID = "brand_id";
	public final static String KEY = "key";

	/** 用于下载数据的Services */
	private DownloadService mDownloadService;
	/** 用于与DownloadService通信用的Handler */
	private Handler handler = new Handler() {

		public void handleMessage(Message msg) {
			mLoading.setVisibility(View.INVISIBLE);
			mSearchCounter.setVisibility(View.VISIBLE);
			if (msg.obj != null) {
				mSearchList = (List<Search>) msg.obj;
				mAdapter.addNews(mSearchList);
				mAdapter.notifyDataSetChanged();
			} else {
				if (mSearchList != null) {
					mSearchList.clear();
					mAdapter.notifyDataSetChanged();
				}

			}

			mSearchCounter.setText("搜索 " + mDt.getText().toString() + " 共"
					+ msg.arg1 + "个结果");
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
			}

		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.search_product_type_activity);
		mSearchCounter = (TextView) findViewById(R.id.search_produtc_type_counter);
		mDt = (EditText) findViewById(R.id.search_edit);
		mLoading = (ProgressBar) findViewById(R.id.loading_progress);
		mLv = (ListView) findViewById(R.id.search_product_type_List);
		mBtnSearch = (TextView) findViewById(R.id.search);
		mLv.setOnItemClickListener(this);
		mAdapter = new MyAdapter();
		mLv.setAdapter(mAdapter);
		mBtnSearch.setOnClickListener(new TextView.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (mDt.getText().toString().trim().length() == 0) {
					Toast.makeText(SearchActivity.this, "关键字不能为空",
							Toast.LENGTH_LONG).show();
				} else {
					mLoading.setVisibility(View.VISIBLE);
					mKey = mDt.getText().toString();
					if (mDownloadService != null) {
						mDownloadService.loadSearchResult(handler, SUCCESS,
								mKey);
					}
				}

			}
		});

		mDt.setOnKeyListener(new OnKeyListener() {

			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				// TODO Auto-generated method stub
				if (keyCode == KeyEvent.KEYCODE_ENTER) {
					if (mDt.getText().toString().trim().length() == 0) {
						Toast.makeText(SearchActivity.this, "关键字不能为空",
								Toast.LENGTH_LONG).show();
					} else {
						mLoading.setVisibility(View.VISIBLE);
						mKey = mDt.getText().toString();
						if (mDownloadService != null) {
							mDownloadService.loadSearchResult(handler, SUCCESS,
									mKey);
						}
					}
				}
				return false;
			}
		});

	}

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();

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

	class MyAdapter extends BaseAdapter {
		private List<Search> dist = new ArrayList<Search>();

		public MyAdapter() {
			super();
			// TODO Auto-generated constructor stub
		}

		public MyAdapter(List<Search> list) {
			addNews(list);
		}

		public void addNews(List<Search> list) {
			if (list != null) {
				this.dist = list;
			}
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return dist.size();
		}

		@Override
		public Object getItem(int arg0) {
			// TODO Auto-generated method stub
			return dist.get(arg0);
		}

		@Override
		public long getItemId(int arg0) {
			// TODO Auto-generated method stub
			return arg0;
		}

		@Override
		public View getView(int arg0, View arg1, ViewGroup arg2) {
			View v = null;
			if (arg1 == null) {
				v = LayoutInflater.from(SearchActivity.this).inflate(
						R.layout.search_product_type_list_item, null);
			} else {
				v = arg1;
			}
			TextView title = (TextView) v
					.findViewById(R.id.search_product_type_item_title);
			TextView counter = (TextView) v
					.findViewById(R.id.search_product_type_item_counter);
			title.setText(dist.get(arg0).getName());
			counter.setText(dist.get(arg0).getCounter() + "款符合条件");
			return v;
		}

	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {

		Intent intent = new Intent(this, SearchShopListActivity.class);
		intent.putExtra(BRAND_ID, mSearchList.get(arg2).getId() + "");
		intent.putExtra(KEY, mKey);

		startActivity(intent);

	}

}
