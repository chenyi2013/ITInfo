package com.kevin.itinfo;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.kevin.bean.Dealer;
import com.kevin.bean.GoodsInfo;
import com.kevin.fra.FranchiserFragment;

public class DealerListActivity extends BaseActivity {

	private ListView mProductTypeList;
	private DealerListAdapter mAdapter;
	private TextView mTitleTextView;
	private List<GoodsInfo> mGoodsInfos;
	public final static String ID = "id";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.saler_product_type_activity);
		mTitleTextView = (TextView) findViewById(R.id.product_type_title);
		mProductTypeList = (ListView) findViewById(R.id.product_type_List);
		mProductTypeList.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> adapterView, View view,
					int position, long id) {
				Bundle bundle = new Bundle();
				bundle.putString(ID, mGoodsInfos.get(position).getId());
				openActivity(CopyOfBrandListActivity.class, bundle);

			}
		});

		Dealer dealer = (Dealer) getIntent().getBundleExtra(
				FranchiserFragment.INFO).getSerializable(
				FranchiserFragment.TYPE);
		if (dealer != null) {
			mTitleTextView.setText(dealer.getName());
			mGoodsInfos = dealer.getGoodInfos();
			mAdapter = new DealerListAdapter(mGoodsInfos);
			mProductTypeList.setAdapter(mAdapter);
			mAdapter.notifyDataSetChanged();
		}
	}

	class DealerListAdapter extends BaseAdapter {
		List<GoodsInfo> list = new ArrayList<GoodsInfo>();
		LayoutInflater inflater = LayoutInflater.from(DealerListActivity.this);
		ViewHolder viewHolder;

		public DealerListAdapter(List<GoodsInfo> goodsInfos) {
			this.list.addAll(goodsInfos);
		}

		@Override
		public int getCount() {
			return list.size();
		}

		@Override
		public Object getItem(int position) {
			return list.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if (convertView == null) {
				convertView = inflater.inflate(
						R.layout.saler_product_type_list_item, parent, false);
				viewHolder = new ViewHolder();
				viewHolder.textView = (TextView) convertView
						.findViewById(R.id.product_type_item_title);
				viewHolder.imageView = (ImageView) convertView
						.findViewById(R.id.image);
				convertView.setTag(viewHolder);
			}
			GoodsInfo goodsInfo = list.get(position);
			viewHolder = (ViewHolder) convertView.getTag();
			viewHolder.position = position;
			viewHolder.id = goodsInfo.getId();
			viewHolder.textView.setText(goodsInfo.getTitle());
			return convertView;
		}
	}

	class ViewHolder {
		TextView textView;
		ImageView imageView;
		int position;
		String id;
	}

}
