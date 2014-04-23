package com.kevin.map;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.location.LocationManagerProxy;
import com.amap.api.location.LocationProviderProxy;
import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.LocationSource;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.MyLocationStyle;
import com.amapv2.apis.poisearch.PoiKeywordSearchActivity;
import com.amapv2.apis.route.RouteActivity;
import com.kevin.itinfo.R;


/**
 * Intent intent=new Intent(MainActivity.this, BasicMapActivity.class);
 * intent.putExtra("Latitude", 23.140811); intent.putExtra("Longitude",
 * 113.343349); startActivity(intent); AMapV2地图中介绍如何显示一个基本地图
 */
public class BasicMapActivity extends Activity implements LocationSource,
		AMapLocationListener {

	private OnLocationChangedListener mListener;
	private LocationManagerProxy mAMapLocationManager;

	private MapView mapView;
	private AMap aMap;
	private LatLng latLng;
	private Button poiSearch, roadSearch, localSearch;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.map_activity);
		mapView = (MapView) findViewById(R.id.map);
		mapView.onCreate(savedInstanceState);// 此方法必须重写

		poiSearch = (Button) findViewById(R.id.poiSearch);
		roadSearch = (Button) findViewById(R.id.roadSearch);
		localSearch = (Button) findViewById(R.id.localSearch);
		init();

	}

	/**
	 * 初始化AMap对象
	 */
	private void init() {
		if (aMap == null) {
			aMap = mapView.getMap();
			Intent intent = getIntent();
			float Latitude = (float) intent
					.getDoubleExtra("Latitude", 39.90403);
			float Longitude = (float) intent.getDoubleExtra("Longitude",
					116.407525);

			// 获取到sharePreference 对象， 参数一为xml文件名，参数为文件的可操作模式
			SharedPreferences sp = this.getSharedPreferences(
					Constants.SHAREDATA, MODE_APPEND);
			// 获取到编辑对象
			SharedPreferences.Editor edit = sp.edit();
			// 添加新的值，可见是键值对的形式添加
			edit.putFloat("Latitude", Latitude);
			edit.putFloat("Longitude", Longitude);
			// 提交.
			edit.commit();

			latLng = new LatLng(Latitude, Longitude);

			// 设置onClick事件
			poiSearch.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					Intent intent = new Intent(BasicMapActivity.this,
							PoiKeywordSearchActivity.class);
					startActivity(intent);
				}
			});
			roadSearch.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					Intent intent = new Intent(BasicMapActivity.this,
							RouteActivity.class);
					startActivity(intent);
				}
			});
			localSearch.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					MyLocationStyle myLocationStyle = new MyLocationStyle();
					myLocationStyle.myLocationIcon(BitmapDescriptorFactory
							.fromResource(R.drawable.location_marker));// 设置小蓝点的图标
					myLocationStyle.strokeWidth(2);// 设置圆形的边框粗细
					aMap.setMyLocationStyle(myLocationStyle);
					// 设置定位监听
					aMap.getUiSettings().setMyLocationButtonEnabled(true);// 设置默认定位按钮是否显示
					aMap.setMyLocationEnabled(true);//
					// 设置为true表示显示定位层并可触发定位，false表示隐藏定位层并不可触发定位，默认是false
				}
			});

		}
	}

	/**
	 * 方法必须重写
	 */
	@Override
	protected void onResume() {
		super.onResume();
		mapView.onResume();
		aMap.setLocationSource(this);
		aMap.addMarker(new MarkerOptions()
				.position(latLng)
				.title("北京")
				.snippet("天安们")
				.icon(BitmapDescriptorFactory
						.fromResource(R.drawable.new_location_at)));

		aMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
		aMap.moveCamera(CameraUpdateFactory.zoomTo(14));

	}

	/**
	 * 方法必须重写
	 */
	@Override
	protected void onPause() {
		super.onPause();
		mapView.onPause();
	}

	/**
	 * 方法必须重写
	 */
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		mapView.onSaveInstanceState(outState);
	}

	/**
	 * 方法必须重写
	 */
	@Override
	protected void onDestroy() {
		super.onDestroy();
		mapView.onDestroy();
	}

	@Override
	public void onLocationChanged(Location location) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onProviderEnabled(String provider) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onProviderDisabled(String provider) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onLocationChanged(AMapLocation aLocation) {
		// TODO Auto-generated method stub
		if (mListener != null) {
			mListener.onLocationChanged(aLocation);// 显示系统小蓝点
		}
	}

	@Override
	public void activate(OnLocationChangedListener listener) {
		// TODO Auto-generated method stub
		mListener = listener;
		if (mAMapLocationManager == null) {
			mAMapLocationManager = LocationManagerProxy.getInstance(this);
			/*
			 * mAMapLocManager.setGpsEnable(false);
			 * 1.0.2版本新增方法，设置true表示混合定位中包含gps定位，false表示纯网络定位，默认是true Location
			 * API定位采用GPS和网络混合定位方式
			 * ，第一个参数是定位provider，第二个参数时间最短是5000毫秒，第三个参数距离间隔单位是米，第四个参数是定位监听者
			 */
			mAMapLocationManager.requestLocationUpdates(
					LocationProviderProxy.AMapNetwork, 5000, 10, this);
		}
	}

	@Override
	public void deactivate() {
		// TODO Auto-generated method stub
		mListener = null;
		if (mAMapLocationManager != null) {
			mAMapLocationManager.removeUpdates(this);
			mAMapLocationManager.destory();
		}
		mAMapLocationManager = null;
	}
}
