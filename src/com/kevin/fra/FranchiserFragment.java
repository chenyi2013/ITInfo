package com.kevin.fra;

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
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.kevin.bean.Dealer;
import com.kevin.itinfo.DealerListActivity;
import com.kevin.itinfo.R;
import com.kevin.services.DownloadService;
import com.kevin.services.DownloadService.LocalServiceBinder;
import com.kevin.util.ImageCache;
import com.kevin.util.ImageFetcher;

public class FranchiserFragment extends Fragment {

	private static final String IMAGE_CACHE_DIR = "thumbs";

	public final static String TYPE = "type";
	public final static String INFO = "info";

	private int mImageThumbSize;

	public final static int SUCCESS = 1;

	private ImageFetcher mImageFetcher;
	private boolean lock = true;
	private GridView centerGridView;
	private GridViewAdapter adapter;

	/** 用于下载数据的Services */
	private DownloadService mDownloadService;

	/** 用于与DownloadService通信用的Handler */
	private Handler handler = new Handler() {

		public void handleMessage(Message msg) {

			if (msg != null && lock && msg.what == SUCCESS) {
				lock = false;
				adapter = new GridViewAdapter((List<Dealer>) msg.obj);
				centerGridView.setAdapter(adapter);
				adapter.notifyDataSetChanged();
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
				mDownloadService.loadDealers(handler, SUCCESS);
			}

		}
	};

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		mImageThumbSize = getResources().getDimensionPixelSize(
				R.dimen.image_thumbnail_size);
		ImageCache.ImageCacheParams cacheParams = new ImageCache.ImageCacheParams(
				getActivity(), IMAGE_CACHE_DIR);
		cacheParams.setMemCacheSizePercent(0.25f);
		mImageFetcher = new ImageFetcher(getActivity(), mImageThumbSize);
		mImageFetcher.setLoadingImage(R.drawable.empty_photo);
		mImageFetcher.addImageCache(getActivity().getSupportFragmentManager(),
				cacheParams);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View v = inflater.inflate(R.layout.saler_main_activity, container,
				false);
		centerGridView = (GridView) v.findViewById(R.id.saler_grid);

		centerGridView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> adapterView, View view,
					int position, long id) {
				ViewHolder viewHolder = (ViewHolder) view.getTag();
				Dealer dealer = viewHolder.dealer;

				Intent intent = new Intent(getActivity(),
						DealerListActivity.class);
				Bundle bundle = new Bundle();
				bundle.putSerializable(TYPE, dealer);
				intent.putExtra(INFO, bundle);

				startActivity(intent);

			}
		});
		return v;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
	}

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		mImageFetcher.setExitTasksEarly(false);
		if (adapter != null) {
			adapter.notifyDataSetChanged();
		}

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

	@Override
	public void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
		Intent intent = new Intent(getActivity(), DownloadService.class);
		getActivity().bindService(intent, mServiceConnection,
				Service.BIND_AUTO_CREATE);
	}

	@Override
	public void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
		lock = true;
		getActivity().unbindService(mServiceConnection);
	}

	private class GridViewAdapter extends BaseAdapter {
		List<Dealer> list = new ArrayList<Dealer>();
		LayoutInflater inflater = null;
		ViewHolder viewHolder;

		public GridViewAdapter(List<Dealer> dealers) {
			this.list.addAll(dealers);
			if (getActivity() != null) {
				inflater = LayoutInflater.from(getActivity());
			}

		}

		@Override
		public int getCount() {
			return this.list.size();
		}

		@Override
		public Object getItem(int position) {
			return this.list.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if (convertView == null) {
				convertView = inflater.inflate(R.layout.saler_main_grid_item,
						parent, false);
				viewHolder = new ViewHolder();
				viewHolder.imageView = (ImageView) convertView
						.findViewById(R.id.saler_grid_image);
				viewHolder.textView = (TextView) convertView
						.findViewById(R.id.saler_grid_title);
				convertView.setTag(viewHolder);
			}
			viewHolder = (ViewHolder) convertView.getTag();
			viewHolder.position = position;
			Dealer dealer = list.get(position);
			viewHolder.dealer = dealer;
			viewHolder.textView.setText(dealer.getName());
			String imageUrl = dealer.getImage();
			mImageFetcher.loadImage(imageUrl, viewHolder.imageView);
			return convertView;
		}
	}

	class ViewHolder {
		ImageView imageView;
		TextView textView;
		int position;
		Dealer dealer;
	}

}
