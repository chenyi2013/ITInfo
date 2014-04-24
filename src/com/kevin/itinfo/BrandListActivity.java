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
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.SectionIndexer;
import android.widget.TextView;

import com.kevin.bean.Brand;
import com.kevin.bean.Normal;
import com.kevin.bean.Partition;
import com.kevin.services.DownloadService;
import com.kevin.services.DownloadService.LocalServiceBinder;
import com.kevin.view.IndexableListView;

public class BrandListActivity extends BaseActivity {

	private IndexableListView mBrandListView;
	private BrandListAdapter mAdapter;
	public static final int SUCCESS = 1;

	/** 用于下载数据的Services */
	private DownloadService mDownloadService;
	/** 用于与DownloadService通信用的Handler */
	private Handler handler = new Handler() {

		public void handleMessage(Message msg) {

			if (msg != null) {

				Normal normal = (Normal) msg.obj;
				mAdapter = new BrandListAdapter(getPartitions(normal.getPartition()));
				mBrandListView.setAdapter(mAdapter);
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
		setContentView(R.layout.brand_list_activity);
		mBrandListView = (IndexableListView) findViewById(R.id.brand_list_view);
		mBrandListView.setFastScrollEnabled(true);

	}

	private class BrandListAdapter extends BaseAdapter implements
			SectionIndexer {
		private LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
		private ArrayList<Partition> partitions = new ArrayList<Partition>();

		public BrandListAdapter(ArrayList<Partition> partitions) {
			this.partitions = partitions;
		}

		@Override
		public int getCount() {

			return partitions.size();

		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return partitions.get(position);
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub

			Partition partition = partitions.get(position);

			if (convertView == null) {

				convertView = inflater.inflate(R.layout.brand_list_item,
						parent, false);
			}
			LinearLayout layout = (LinearLayout) convertView;
			layout.removeAllViews();
			if (partition != null
					&& Integer.parseInt(partition.getCounter()) > 0) {
				ArrayList<Brand> brands = partition.getBrands();
				if (brands != null && brands.size() > 0) {
					View view = null;

					for (int i = 0; i < brands.size(); i++) {

						Brand brand = brands.get(i);
						if (i % 2 == 0) {
							view = inflater
									.inflate(R.layout.brand_list_sub_item,
											layout, false);

							TextView indexView1 = (TextView) view
									.findViewById(R.id.brand_index_1);
							indexView1.setText(partition.getIndex());

							TextView brandName1 = (TextView) view
									.findViewById(R.id.brand_name_1);
							brandName1.setText(brand.getTitle());

							layout.addView(view);
						} else {
							TextView indexView2 = (TextView) view
									.findViewById(R.id.brand_index_2);
							indexView2.setText(partition.getIndex());

							TextView brandName2 = (TextView) view
									.findViewById(R.id.brand_name_2);
							brandName2.setText(brand.getTitle());

							view = null;
						}
					}
				}
			}

			return convertView;
		}

		@Override
		public int getPositionForSection(int section) {
			// TODO Auto-generated method stub
			return section;
		}

		@Override
		public int getSectionForPosition(int position) {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public Object[] getSections() {
			String sSections = getSection(partitions);
			String[] sections = new String[sSections.length()];
			for (int i = 0; i < sSections.length(); i++)
				sections[i] = String.valueOf(sSections.charAt(i));
			return sections;
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
