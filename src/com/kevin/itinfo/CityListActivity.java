package com.kevin.itinfo;

import java.util.ArrayList;

import android.app.Activity;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.SectionIndexer;
import android.widget.TextView;

import com.kevin.bean.City;
import com.kevin.bean.CityClassification;
import com.kevin.bean.Citys;
import com.kevin.services.DownloadService;
import com.kevin.services.DownloadService.LocalServiceBinder;
import com.kevin.view.IndexableListView;

public class CityListActivity extends Activity {

	public final static int RESULT_CODE = 101;
	private final static int SUCCESS = 1;
	private ArrayList<City> mCityList;
	private Citys mCitys;
	private String[] indexers;
	private IndexableListView mCityListView;

	/** 用于下载数据的Services */
	private DownloadService mDownloadService;
	/** 用于与DownloadService通信用的Handler */
	private Handler handler = new Handler() {

		public void handleMessage(Message msg) {

			if (msg != null && msg.what == SUCCESS) {

				mCitys = (Citys) msg.obj;
				initCityList(mCitys);
				CityAdapter adapter = new CityAdapter();
				mCityListView.setAdapter(adapter);
				mCityListView.setVisibility(View.VISIBLE);
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
				mDownloadService.loadCitys(handler, SUCCESS);

			}

		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.city_layout);
		mCityListView = (IndexableListView) findViewById(R.id.city_list);
		mCityListView.setVisibility(View.GONE);
		mCityListView.setFastScrollEnabled(true);

		mCityListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> adapterView, View view,
					int position, long id) {
				SharedPreferences sharedPreferences = getSharedPreferences(
						getResources().getString(R.string.city),
						Context.MODE_PRIVATE);
				SharedPreferences.Editor editor = sharedPreferences.edit();
				String cityId = getResources().getString(R.string.city_id);
				String cityName = getResources().getString(R.string.city_name);
				editor.putString(cityId, mCityList.get(position).getId());
				editor.putString(cityName, mCityList.get(position).getName());
				editor.commit();

				Intent intent = new Intent();
				intent.putExtra(cityId, mCityList.get(position).getId());
				intent.putExtra(cityName, mCityList.get(position).getName());
				setResult(RESULT_CODE, intent);
				finish();
			}
		});

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

	class CityAdapter extends BaseAdapter implements SectionIndexer {

		private LayoutInflater mInflater;

		public CityAdapter() {
			mInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return mCityList.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return mCityList.get(position);
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			if (convertView == null) {
				convertView = mInflater.inflate(R.layout.city_list_item,
						parent, false);
			}

			City city = mCityList.get(position);

			TextView indexer = (TextView) convertView
					.findViewById(R.id.city_letter);
			TextView cityName = (TextView) convertView
					.findViewById(R.id.city_name);

			if (city.isTitle()) {
				indexer.setVisibility(View.VISIBLE);
				indexer.setText(city.getIndexer());
			} else {
				indexer.setVisibility(View.GONE);
			}
			cityName.setText(city.getName());

			return convertView;
		}

		@Override
		public int getPositionForSection(int section) {
			for (int i = 0; i < mCityList.size(); i++) {
				if (indexers[section].equals(mCityList.get(i).getIndexer())) {
					return i;
				}
			}
			return 0;
		}

		@Override
		public int getSectionForPosition(int position) {
			// TODO Auto-generated method stub
			for (int i = 0; i < indexers.length; i++) {

				if (mCityList.get(i).getIndexer() != null
						&& indexers[i].equals(mCityList.get(i).getIndexer())) {
					return i;
				}
			}
			return 0;
		}

		@Override
		public Object[] getSections() {
			// TODO Auto-generated method stub
			return indexers;
		}

	}

	private void initCityList(Citys citys) {
		mCityList = new ArrayList<City>();
		ArrayList<City> hotCity = citys.getHot_city();
		for (int i = 0; i < hotCity.size(); i++) {
			City city = hotCity.get(i);
			if (i == 0) {
				city.setTitle(true);
				city.setIndexer("热门城市");
			}
			mCityList.add(city);
		}

		ArrayList<CityClassification> classifications = citys.getAll_city();
		indexers = new String[classifications.size() + 1];
		indexers[0] = "热";
		for (int i = 0; i < classifications.size(); i++) {
			CityClassification cityClassification = classifications.get(i);
			ArrayList<City> citylist = cityClassification.getList();
			for (int j = 0; j < citylist.size(); j++) {
				City city = citylist.get(j);
				if (j == 0) {
					city.setTitle(true);
					city.setIndexer(cityClassification.getFirstchar());
					indexers[i + 1] = cityClassification.getFirstchar();
				}
				mCityList.add(city);

			}
		}
	}

}
