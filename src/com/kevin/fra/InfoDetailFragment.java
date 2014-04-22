package com.kevin.fra;

import java.util.ArrayList;

import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
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
import android.widget.ImageView;
import android.widget.TextView;

import com.kevin.bean.News;
import com.kevin.itinfo.NewsContent;
import com.kevin.itinfo.R;
import com.kevin.services.DownloadService;
import com.kevin.services.DownloadService.LocalServiceBinder;
import com.kevin.util.ImageCache;
import com.kevin.util.ImageCache.ImageCacheParams;
import com.kevin.util.ImageFetcher;
import com.kevin.view.PullToRefreshListView;

public class InfoDetailFragment extends Fragment {

	private static final String IMAGE_CACHE_DIR = "thumbs";

	public final static String NEWS_ID = "news_id";

	public final static String NEWS_TITLE = "news_title";

	public final static String NEWS_COVER = "news_cover";

	private int mImageThumbSize;

	private ImageFetcher mImageFetcher;

	/** ������ʾ�����б��ListView */
	private PullToRefreshListView mInfoList;

	/** �����б� */
	private ArrayList<News> mNewsList;

	/** �����б������� */
	private InfoAdapter mInfoAdapter;

	/** �����������ݵ�Services */
	private DownloadService mDownloadService;

	/** ������DownloadServiceͨ���õ�Handler */
	private Handler handler = new Handler() {

		public void handleMessage(Message msg) {
			if (msg.what == getArguments().getInt(InformationFragment.NEWS_ID)) {

				Log.i("kevin", "handleMessage");
				mNewsList = (ArrayList<News>) msg.obj;
				if (mNewsList != null) {
					mInfoAdapter = new InfoAdapter();
					mInfoList.setAdapter(mInfoAdapter);
					mInfoAdapter.notifyDataSetChanged();
				}

			}

		};
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
				int what = getArguments().getInt(InformationFragment.NEWS_ID);
				mDownloadService.loadNews(handler, what);
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
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		mImageFetcher.setExitTasksEarly(false);
		if (mInfoAdapter != null) {
			mInfoAdapter.notifyDataSetChanged();
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
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		return inflater.inflate(R.layout.info_detail_fra, container, false);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
		initView();

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

	/** ��ʼ����ͼ */
	private void initView() {

		mInfoList = (PullToRefreshListView) getView().findViewById(
				R.id.info_list_view);
		mInfoList.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> adapterView, View view,
					int position, long id) {

				News news = mNewsList.get(position - 1);

				Intent intent = new Intent(getActivity(), NewsContent.class);
				intent.putExtra(NEWS_ID, news.getId());
				intent.putExtra(NEWS_TITLE, news.getTitle());
				intent.putExtra(NEWS_COVER, news.getCover());

				startActivity(intent);

			}
		});
	}

	private class InfoAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return mNewsList.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return mNewsList.get(position);
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {

			ViewHolder holder = null;

			if (convertView == null) {
				convertView = ((LayoutInflater) getActivity().getSystemService(
						Context.LAYOUT_INFLATER_SERVICE)).inflate(
						R.layout.news_list_item, parent, false);

				holder = new ViewHolder();
				holder.setNewsTitle((TextView) convertView
						.findViewById(R.id.news_title));
				holder.setNewsContent((TextView) convertView
						.findViewById(R.id.news_content));
				holder.setNewsDate((TextView) convertView
						.findViewById(R.id.news_date));
				holder.setPosition(position);
				holder.setNewsImg((ImageView) convertView
						.findViewById(R.id.news_img));
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}

			holder.getNewsTitle().setText(mNewsList.get(position).getTitle());
			holder.getNewsContent().setText(
					mNewsList.get(position).getDescription());
			holder.getNewsDate().setText(mNewsList.get(position).getDate());
			mImageFetcher.loadImage(mNewsList.get(position).getCover(),
					holder.getNewsImg());
			return convertView;
		}

	}

	/** �ô����ֹͼƬ��λ */
	public class ViewHolder {
		/** ����ͼƬ */
		private ImageView newsImg;

		/** ���ű��� */
		private TextView newsTitle;

		/** �������� */
		private TextView newsContent;

		/** ���ŷ������� */
		private TextView newsDate;

		/** ��ListView����ʾ��λ�� */
		private int position;

		public ImageView getNewsImg() {
			return newsImg;
		}

		public void setNewsImg(ImageView newsImg) {
			this.newsImg = newsImg;
		}

		public TextView getNewsTitle() {
			return newsTitle;
		}

		public void setNewsTitle(TextView newsTitle) {
			this.newsTitle = newsTitle;
		}

		public TextView getNewsContent() {
			return newsContent;
		}

		public void setNewsContent(TextView newsContent) {
			this.newsContent = newsContent;
		}

		public TextView getNewsDate() {
			return newsDate;
		}

		public void setNewsDate(TextView newsDate) {
			this.newsDate = newsDate;
		}

		public int getPosition() {
			return position;
		}

		public void setPosition(int position) {
			this.position = position;
		}

	}

}
