package com.kevin.itinfo;

import java.util.ArrayList;

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
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.SectionIndexer;
import android.widget.TextView;
import android.widget.Toast;

import com.kevin.bean.Brand;
import com.kevin.bean.Normal;
import com.kevin.bean.Partition;
import com.kevin.services.DownloadService;
import com.kevin.services.DownloadService.LocalServiceBinder;
import com.kevin.view.IndexableListView;

public class CopyOfBrandListActivity extends BaseActivity {

	// private IndexableListView mBrandListView;
	// private BrandListAdapter mAdapter;
	private LayoutInflater mInflater;
	public static final int SUCCESS = 1;
	private LinearLayout mBrLayout;
	public final static String BRAND_ID = "brand_id";
	public final static String CITY_ID = "city_id";

	/** 用于下载数据的Services */
	private DownloadService mDownloadService;
	/** 用于与DownloadService通信用的Handler */
	private Handler handler = new Handler() {

		public void handleMessage(Message msg) {

			if (msg != null) {

				Normal normal = (Normal) msg.obj;
				ArrayList<Partition> partitions = getPartitions(normal
						.getPartition());
				inflaterChildView(partitions);
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
				Intent intent = getIntent();
				String key = intent.getExtras()
						.getString(DealerListActivity.ID);
				mDownloadService.loadNormalBrand(handler, SUCCESS, key);
			}

		}
	};

	@Override
	protected void onCreate(Bundle arg0) {
		// TODO Auto-generated method stub
		super.onCreate(arg0);
		setContentView(R.layout.aa_brand_list_activity);
		mInflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
		mBrLayout = (LinearLayout) findViewById(R.id.brand_list_view);

	}

	public void inflaterChildView(ArrayList<Partition> partitions) {
		for (int position = 0; position < partitions.size(); position++) {
			Partition partition = partitions.get(position);

			LinearLayout layout = (LinearLayout) mInflater.inflate(
					R.layout.brand_list_item, mBrLayout, false);
			;
			if (partition != null
					&& Integer.parseInt(partition.getCounter()) > 0) {
				ArrayList<Brand> brands = partition.getBrands();
				if (brands != null && brands.size() > 0) {
					View view = null;

					for (int i = 0; i < brands.size(); i++) {

						final Brand brand = brands.get(i);
						if (i % 2 == 0) {
							view = mInflater
									.inflate(R.layout.brand_list_sub_item,
											layout, false);
							View brandNameLayout1 = view
									.findViewById(R.id.brand_name1_layout);
							TextView indexView1 = (TextView) view
									.findViewById(R.id.brand_index_1);
							indexView1.setText(partition.getIndex());

							TextView brandName1 = (TextView) view
									.findViewById(R.id.brand_name_1);
							brandName1.setText(brand.getTitle());
							brandNameLayout1
									.setOnClickListener(new OnClickListener() {

										@Override
										public void onClick(View v) {
											// TODO Auto-generated method stub
											Intent intent = new Intent(
													CopyOfBrandListActivity.this,
													DealerInfoListActivity.class);
											intent.putExtra(BRAND_ID,
													brand.getId());
											// intent.putExtra(CITY_ID, "");
											startActivity(intent);
										}
									});

							layout.addView(view);
						} else {
							View brandNameLayout2 = view
									.findViewById(R.id.brand_name2_layout);
							TextView indexView2 = (TextView) view
									.findViewById(R.id.brand_index_2);
							indexView2.setText(partition.getIndex());
							TextView brandName2 = (TextView) view
									.findViewById(R.id.brand_name_2);
							brandName2.setText(brand.getTitle());
							brandNameLayout2
									.setOnClickListener(new OnClickListener() {

										@Override
										public void onClick(View v) {

											Intent intent = new Intent(
													CopyOfBrandListActivity.this,
													DealerInfoListActivity.class);
											intent.putExtra(BRAND_ID,
													brand.getId());
											// intent.putExtra(CITY_ID, "");
											startActivity(intent);
										}
									});

							view = null;
						}
					}
				}
			}
			mBrLayout.addView(layout);
		}

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

	private ArrayList<Partition> getPartitions(ArrayList<Partition> mPartitions) {
		ArrayList<Partition> partitions = new ArrayList<Partition>();
		for (int i = 0; i < mPartitions.size(); i++) {
			Partition partition = mPartitions.get(i);
			if (!partition.getCounter().equals("0")) {
				partitions.add(partition);
			}
		}
		return partitions;
	}

	private String getSection(ArrayList<Partition> partitions) {

		StringBuffer buffer = new StringBuffer();
		for (int i = 0; i < partitions.size(); i++) {
			Partition partition = partitions.get(i);
			if (!partition.getCounter().equals("0")) {
				buffer.append(partition.getIndex());
			}
		}
		return buffer.toString();
	}

}
