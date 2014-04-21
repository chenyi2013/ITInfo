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
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.kevin.bean.Distribution;
import com.kevin.itinfo.R;
import com.kevin.services.DownloadService;
import com.kevin.services.DownloadService.LocalServiceBinder;
import com.kevin.util.ImageCache;
import com.kevin.util.ImageFetcher;

public class ManufacturerFragment extends Fragment {

	private static final String IMAGE_CACHE_DIR = "thumbs";

	private int mImageThumbSize;

	public final static int SUCCESS = 1;

	private ImageFetcher mImageFetcher;
	private List<Distribution> list;
	private GridView gv;
	private MyAdapter adapter = new MyAdapter();

	/** 用于下载数据的Services */
	private DownloadService mDownloadService;

	/** 用于与DownloadService通信用的Handler */
	private Handler handler = new Handler() {

		public void handleMessage(Message msg) {
			if (msg != null && msg.what == SUCCESS) {
				list = (List<Distribution>) msg.obj;
				adapter.addData(list);
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
				mDownloadService.loadDistributions(handler, SUCCESS);
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
		View v = inflater.inflate(R.layout.manufacture_fragment, container,
				false);
		gv = (GridView) v.findViewById(R.id.payout_place_grid);
		gv.setAdapter(adapter);
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
		getActivity().unbindService(mServiceConnection);
	}

	class MyAdapter extends BaseAdapter {
		private List<Distribution> dist = new ArrayList<Distribution>();

		public MyAdapter() {
			super();
			// TODO Auto-generated constructor stub
		}

		public MyAdapter(List<Distribution> dist) {
			addData(dist);
		}

		public void addData(List<Distribution> dist) {
			if (dist != null) {
				this.dist.addAll(dist);
			}
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return dist.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return dist.get(position);
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public View getView(int position, View converView, ViewGroup parent) {
			View v = null;
			if (converView == null) {
				v = LayoutInflater.from(getActivity()).inflate(
						R.layout.payout_grid_item, null);
			} else {
				v = converView;
			}
			ImageView iv = (ImageView) v.findViewById(R.id.payout_grid_image);
			TextView tv = (TextView) v.findViewById(R.id.payout_grid_title);
			tv.setText(dist.get(position).getTitle());
			mImageFetcher.loadImage(dist.get(position).getCover(), iv);
			v.setPadding(5, 10, 5, 10);
			return v;
		}

	}

}
