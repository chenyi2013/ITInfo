package com.kevin.fra;

import java.util.ArrayList;

import android.annotation.SuppressLint;
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
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.kevin.bean.News;
import com.kevin.itinfo.R;
import com.kevin.services.DownloadService;
import com.kevin.services.DownloadService.LocalServiceBinder;
import com.kevin.view.PullToRefreshListView;

public class InfoDetailFragment extends Fragment {

	/** 用于显示新闻列表的ListView */
	private PullToRefreshListView mInfoList;

	/** 新闻列表 */
	private ArrayList<News> mNewsList;

	/** 新闻列表适配器 */
	private InfoAdapter mInfoAdapter;

	/** 用于下载数据的Services */
	private DownloadService mDownloadService;

	/** 用于与DownloadService通信用的Handler */
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

	/** 初始化视图 */
	private void initView() {

		mInfoList = (PullToRefreshListView) getView().findViewById(
				R.id.info_list_view);
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
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}

			holder.getNewsTitle().setText(mNewsList.get(position).getTitle());
			holder.getNewsContent().setText(
					mNewsList.get(position).getDescription());
			holder.getNewsDate().setText(mNewsList.get(position).getDate());

			return convertView;
		}

	}

	/** 用此类防止图片错位 */
	public class ViewHolder {
		/** 新闻图片 */
		private ImageView newsImg;

		/** 新闻标题 */
		private TextView newsTitle;

		/** 新闻内容 */
		private TextView newsContent;

		/** 新闻发布日期 */
		private TextView newsDate;

		/** 在ListView中显示的位置 */
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
