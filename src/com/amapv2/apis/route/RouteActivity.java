package com.amapv2.apis.route;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.Spinner;

import com.amap.api.maps.AMap;
import com.amap.api.maps.AMap.InfoWindowAdapter;
import com.amap.api.maps.AMap.OnInfoWindowClickListener;
import com.amap.api.maps.AMap.OnMapClickListener;
import com.amap.api.maps.AMap.OnMarkerClickListener;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.core.PoiItem;
import com.amap.api.services.overlay.BusRouteOverlay;
import com.amap.api.services.overlay.DrivingRouteOverlay;
import com.amap.api.services.overlay.WalkRouteOverlay;
import com.amap.api.services.poisearch.PoiItemDetail;
import com.amap.api.services.poisearch.PoiResult;
import com.amap.api.services.poisearch.PoiSearch;
import com.amap.api.services.poisearch.PoiSearch.OnPoiSearchListener;
import com.amap.api.services.route.BusPath;
import com.amap.api.services.route.BusRouteResult;
import com.amap.api.services.route.DrivePath;
import com.amap.api.services.route.DriveRouteResult;
import com.amap.api.services.route.RouteSearch;
import com.amap.api.services.route.RouteSearch.BusRouteQuery;
import com.amap.api.services.route.RouteSearch.DriveRouteQuery;
import com.amap.api.services.route.RouteSearch.OnRouteSearchListener;
import com.amap.api.services.route.RouteSearch.WalkRouteQuery;
import com.amap.api.services.route.WalkPath;
import com.amap.api.services.route.WalkRouteResult;
import com.amapv2.apis.route.RouteSearchPoiDialog.OnListItemClick;
import com.amapv2.apis.util.AMapUtil;
import com.amapv2.apis.util.ToastUtil;
import com.kevin.itinfo.R;
import com.kevin.map.Constants;

/**
 * AMapV2地图中简单介绍route搜索
 */
public class RouteActivity extends Activity implements OnMarkerClickListener,
		OnMapClickListener, OnInfoWindowClickListener, InfoWindowAdapter,
		OnPoiSearchListener, OnRouteSearchListener, OnClickListener {
	private AMap aMap;
	private MapView mapView;
	private Button drivingButton;
	private Button busButton;
	private Button walkButton;

	private ImageButton startImageButton;
	private ImageButton endImageButton;
	private ImageButton routeSearchImagebtn;

	private EditText startTextView;
	private EditText endTextView;
	private ProgressDialog progDialog = null;// 搜索时进度条
	private int busMode = RouteSearch.BusDefault;// 公交默认模式
	private int drivingMode = RouteSearch.DrivingDefault;// 驾车默认模式
	private int walkMode = RouteSearch.WalkDefault;// 步行默认模式
	private BusRouteResult busRouteResult;// 公交模式查询结果
	private DriveRouteResult driveRouteResult;// 驾车模式查询结果
	private WalkRouteResult walkRouteResult;// 步行模式查询结果
	private int routeType = 1;// 1代表公交模式，2代表驾车模式，3代表步行模式
	private String strStart;
	private String strEnd;
	private LatLonPoint startPoint = null;
	private LatLonPoint endPoint = null;
	private PoiSearch.Query startSearchQuery;
	private PoiSearch.Query endSearchQuery;

	private boolean isClickStart = false;
	private boolean isClickTarget = false;
	private Marker startMk, targetMk, myMk;
	private RouteSearch routeSearch;
	public ArrayAdapter<String> aAdapter;

	private float Latitude;
	private float Longitude;
	private LatLng latLng;

	private Spinner selectCity;// 选择城市列表
	private String[] itemCitys = { "北京-010", "广州-020", "上海-021" };
	private String cityCode = "";

	private RelativeLayout relayout;
	private ImageButton imageButton;
	private int count = 0;

	@Override
	protected void onCreate(Bundle bundle) {
		super.onCreate(bundle);
		setContentView(R.layout.route_activity);
		mapView = (MapView) findViewById(R.id.map);
		mapView.onCreate(bundle);// 此方法必须重写
		init();
		getSpinner();
		getData();
	}

	/**
	 * 初始化AMap对象
	 */
	private void init() {
		if (aMap == null) {
			aMap = mapView.getMap();
			registerListener();
		}
		relayout = (RelativeLayout) findViewById(R.id.RelativeLayout_roadsearch_top);
		imageButton = (ImageButton) findViewById(R.id.route_down);

		// ///
		routeSearch = new RouteSearch(this);
		routeSearch.setRouteSearchListener(this);
		startTextView = (EditText) findViewById(R.id.autotextview_roadsearch_start);
		endTextView = (EditText) findViewById(R.id.autotextview_roadsearch_goals);
		busButton = (Button) findViewById(R.id.imagebtn_roadsearch_tab_transit);
		busButton.setOnClickListener(this);
		drivingButton = (Button) findViewById(R.id.imagebtn_roadsearch_tab_driving);
		drivingButton.setOnClickListener(this);
		walkButton = (Button) findViewById(R.id.imagebtn_roadsearch_tab_walk);
		walkButton.setOnClickListener(this);
		startImageButton = (ImageButton) findViewById(R.id.imagebtn_roadsearch_startoption);
		startImageButton.setOnClickListener(this);
		endImageButton = (ImageButton) findViewById(R.id.imagebtn_roadsearch_endoption);
		endImageButton.setOnClickListener(this);
		routeSearchImagebtn = (ImageButton) findViewById(R.id.imagebtn_roadsearch_search);
		routeSearchImagebtn.setOnClickListener(this);
	}

	/**
	 * 显示路径
	 */
	private void getSpinner() {
		selectCity = (Spinner) findViewById(R.id.cityCode);
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, getCitys());
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		selectCity.setAdapter(adapter);
		selectCity.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				String cityString = itemCitys[position];
				cityCode = cityString.substring(cityString.indexOf("-") + 1);
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
				// TODO Auto-generated method stub
				cityCode = "010";
			}
		});// 添加spinner选择框监听事件

	}

	/**
	 * 获得城市名
	 * 
	 * @return
	 */
	private List<String> getCitys() {
		List<String> cityNames = new ArrayList<String>();
		for (int i = 0; i < itemCitys.length; i++) {
			String cityString = itemCitys[i];
			cityCode = cityString.substring(0, cityString.indexOf("-"));
			cityNames.add(cityCode);
		}
		return cityNames;
	}

	/**
	 * 获取sharePreferences的方法
	 */
	private void getData() {
		SharedPreferences sp = this.getSharedPreferences(Constants.SHAREDATA,
				MODE_APPEND);
		Latitude = sp.getFloat("Latitude", (float) Constants.BEIJING.latitude);
		Longitude = sp.getFloat("Longitude",
				(float) Constants.BEIJING.longitude);
		latLng = new LatLng(Latitude, Longitude);
		System.out.println("维度：" + Latitude + "经度：" + Longitude);
	}

	/**
	 * 方法必须重写
	 */
	@Override
	protected void onResume() {
		super.onResume();
		mapView.onResume();

		myMk = aMap.addMarker(new MarkerOptions()
				.position(latLng)
				.title("北京")
				.snippet("天安门")
				.anchor(0.5f, 1)
				.icon(BitmapDescriptorFactory
						.fromResource(R.drawable.new_location_at)));

		aMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
		aMap.moveCamera(CameraUpdateFactory.zoomTo(14));
		imageButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (count % 2 == 0) {

					imageButton.setImageResource(R.drawable.map_route_down);
					relayout.setVisibility(View.GONE);
				} else {

					imageButton.setImageResource(R.drawable.map_route_up);
					relayout.setVisibility(View.VISIBLE);

				}
				count++;
			}
		});
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

	/**
	 * 选择公交模式
	 */
	private void busRoute() {
		routeType = 1;// 标识为公交模式
		busMode = RouteSearch.BusDefault;
		drivingButton.setBackgroundResource(R.drawable.mode_driving_off);
		busButton.setBackgroundResource(R.drawable.mode_transit_on);
		walkButton.setBackgroundResource(R.drawable.mode_walk_off);

	}

	/**
	 * 选择驾车模式
	 */
	private void drivingRoute() {
		routeType = 2;// 标识为驾车模式
		drivingMode = RouteSearch.DrivingSaveMoney;
		drivingButton.setBackgroundResource(R.drawable.mode_driving_on);
		busButton.setBackgroundResource(R.drawable.mode_transit_off);
		walkButton.setBackgroundResource(R.drawable.mode_walk_off);
	}

	/**
	 * 选择步行模式
	 */
	private void walkRoute() {
		routeType = 3;// 标识为步行模式
		walkMode = RouteSearch.WalkMultipath;
		drivingButton.setBackgroundResource(R.drawable.mode_driving_off);
		busButton.setBackgroundResource(R.drawable.mode_transit_off);
		walkButton.setBackgroundResource(R.drawable.mode_walk_on);
	}

	/**
	 * 在地图上选取起点
	 */
	private void startImagePoint() {
		ToastUtil.show(RouteActivity.this, "在地图上点击您的起点");
		isClickStart = true;
		isClickTarget = false;
		registerListener();
	}

	/**
	 * 在地图上选取终点
	 */
	private void endImagePoint() {
		ToastUtil.show(RouteActivity.this, "在地图上点击您的终点");
		isClickTarget = true;
		isClickStart = false;
		registerListener();
	}

	/**
	 * 点击搜索按钮开始Route搜索
	 */
	public void searchRoute() {
		strStart = startTextView.getText().toString().trim();
		strEnd = endTextView.getText().toString().trim();
		if (strStart == null || strStart.length() == 0) {
			ToastUtil.show(RouteActivity.this, "请选择起点");
			return;
		}
		if (strEnd == null || strEnd.length() == 0) {
			ToastUtil.show(RouteActivity.this, "请选择终点");
			return;
		}
		startSearchResult();// 开始搜终点
	}

	@Override
	public void onInfoWindowClick(Marker marker) {
		isClickStart = false;
		isClickTarget = false;
		if (startMk.equals(marker)) {
			startTextView.setText("地图上的点");
			startPoint = AMapUtil.convertToLatLonPoint(startMk.getPosition());
			startMk.hideInfoWindow();
			startMk.remove();
		} else if (targetMk.equals(marker)) {
			endTextView.setText("地图上的点");
			endPoint = AMapUtil.convertToLatLonPoint(targetMk.getPosition());
			targetMk.hideInfoWindow();
			targetMk.remove();
		}
	}

	@Override
	public boolean onMarkerClick(Marker marker) {
		if (marker.isInfoWindowShown()) {
			marker.hideInfoWindow();
		} else if (myMk.equals(marker)) {
			endTextView.setText("地图上的点");
			endPoint = AMapUtil.convertToLatLonPoint(myMk.getPosition());
			myMk.hideInfoWindow();
		} else {
			marker.showInfoWindow();
		}
		return false;
	}

	@Override
	public void onMapClick(LatLng latng) {
		if (isClickStart) {
			startMk = aMap.addMarker(new MarkerOptions()
					.anchor(0.5f, 1)
					.icon(BitmapDescriptorFactory
							.fromResource(R.drawable.point)).position(latng)
					.title("点击选择为起点"));
			startMk.showInfoWindow();
		} else if (isClickTarget) {
			targetMk = aMap.addMarker(new MarkerOptions()
					.anchor(0.5f, 1)
					.icon(BitmapDescriptorFactory
							.fromResource(R.drawable.point)).position(latng)
					.title("点击选择为目的地"));
			targetMk.showInfoWindow();
		}
	}

	@Override
	public View getInfoContents(Marker marker) {
		return null;
	}

	@Override
	public View getInfoWindow(Marker marker) {
		return null;
	}

	/**
	 * 注册监听
	 */
	private void registerListener() {
		aMap.setOnMapClickListener(RouteActivity.this);
		aMap.setOnMarkerClickListener(RouteActivity.this);
		aMap.setOnInfoWindowClickListener(RouteActivity.this);
		aMap.setInfoWindowAdapter(RouteActivity.this);
	}

	/**
	 * 显示进度框
	 */
	private void showProgressDialog() {
		if (progDialog == null)
			progDialog = new ProgressDialog(this);
		progDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		progDialog.setIndeterminate(false);
		progDialog.setCancelable(true);
		progDialog.setMessage("正在搜索");
		progDialog.show();
	}

	/**
	 * 隐藏进度框
	 */
	private void dissmissProgressDialog() {
		if (progDialog != null) {
			progDialog.dismiss();
		}
	}

	/**
	 * 查询路径规划起点
	 */
	public void startSearchResult() {
		strStart = startTextView.getText().toString().trim();
		if (startPoint != null && strStart.equals("地图上的点")) {
			endSearchResult();
		} else {
			showProgressDialog();
			startSearchQuery = new PoiSearch.Query(strStart, "", cityCode); // 第一个参数表示查询关键字，第二参数表示poi搜索类型，第三个参数表示城市区号或者城市名
			startSearchQuery.setPageNum(0);// 设置查询第几页，第一页从0开始
			startSearchQuery.setPageSize(20);// 设置每页返回多少条数据
			PoiSearch poiSearch = new PoiSearch(RouteActivity.this,
					startSearchQuery);
			poiSearch.setOnPoiSearchListener(this);
			poiSearch.searchPOIAsyn();// 异步poi查询
		}
	}

	/**
	 * 查询路径规划终点
	 */
	public void endSearchResult() {
		strEnd = endTextView.getText().toString().trim();
		if (endPoint != null && strEnd.equals("地图上的点")) {
			searchRouteResult(startPoint, endPoint);
		} else {
			showProgressDialog();
			endSearchQuery = new PoiSearch.Query(strEnd, "", cityCode); // 第一个参数表示查询关键字，第二参数表示poi搜索类型，第三个参数表示城市区号或者城市名
			endSearchQuery.setPageNum(0);// 设置查询第几页，第一页从0开始
			endSearchQuery.setPageSize(20);// 设置每页返回多少条数据
			PoiSearch poiSearch = new PoiSearch(RouteActivity.this,
					endSearchQuery);
			poiSearch.setOnPoiSearchListener(this);
			poiSearch.searchPOIAsyn(); // 异步poi查询
		}
	}

	/**
	 * 开始搜索路径规划方案
	 */
	public void searchRouteResult(LatLonPoint startPoint, LatLonPoint endPoint) {
		showProgressDialog();
		final RouteSearch.FromAndTo fromAndTo = new RouteSearch.FromAndTo(
				startPoint, endPoint);
		if (routeType == 1) {// 公交路径规划
			BusRouteQuery query = new BusRouteQuery(fromAndTo, busMode,
					cityCode, 0);// 第一个参数表示路径规划的起点和终点，第二个参数表示公交查询模式，第三个参数表示公交查询城市区号，第四个参数表示是否计算夜班车，0表示不计算
			routeSearch.calculateBusRouteAsyn(query);// 异步路径规划公交模式查询
		} else if (routeType == 2) {// 驾车路径规划
			DriveRouteQuery query = new DriveRouteQuery(fromAndTo, drivingMode,
					null, null, "");// 第一个参数表示路径规划的起点和终点，第二个参数表示驾车模式，第三个参数表示途经点，第四个参数表示避让区域，第五个参数表示避让道路
			routeSearch.calculateDriveRouteAsyn(query);// 异步路径规划驾车模式查询
		} else if (routeType == 3) {// 步行路径规划
			WalkRouteQuery query = new WalkRouteQuery(fromAndTo, walkMode);
			routeSearch.calculateWalkRouteAsyn(query);// 异步路径规划步行模式查询
		}
	}

	@Override
	public void onPoiItemDetailSearched(PoiItemDetail arg0, int arg1) {

	}

	/**
	 * POI搜索结果回调
	 */
	@Override
	public void onPoiSearched(PoiResult result, int rCode) {
		dissmissProgressDialog();
		if (rCode == 0) {// 返回成功
			if (result != null && result.getQuery() != null
					&& result.getPois() != null && result.getPois().size() > 0) {// 搜索poi的结果
				if (result.getQuery().equals(startSearchQuery)) {
					List<PoiItem> poiItems = result.getPois();// 取得poiitem数据
					RouteSearchPoiDialog dialog = new RouteSearchPoiDialog(
							RouteActivity.this, poiItems);
					dialog.setTitle("您要找的起点是:");
					dialog.show();
					dialog.setOnListClickListener(new OnListItemClick() {
						@Override
						public void onListItemClick(
								RouteSearchPoiDialog dialog,
								PoiItem startpoiItem) {
							startPoint = startpoiItem.getLatLonPoint();
							strStart = startpoiItem.getTitle();
							startTextView.setText(strStart);
							endSearchResult();// 开始搜终点
						}

					});
				} else if (result.getQuery().equals(endSearchQuery)) {
					List<PoiItem> poiItems = result.getPois();// 取得poiitem数据
					RouteSearchPoiDialog dialog = new RouteSearchPoiDialog(
							RouteActivity.this, poiItems);
					dialog.setTitle("您要找的终点是:");
					dialog.show();
					dialog.setOnListClickListener(new OnListItemClick() {
						@Override
						public void onListItemClick(
								RouteSearchPoiDialog dialog, PoiItem endpoiItem) {
							endPoint = endpoiItem.getLatLonPoint();
							strEnd = endpoiItem.getTitle();
							endTextView.setText(strEnd);
							searchRouteResult(startPoint, endPoint);// 进行路径规划搜索
						}

					});
				}
			} else {
				ToastUtil.show(RouteActivity.this, R.string.no_result);
			}
		} else {
			ToastUtil.show(RouteActivity.this, R.string.error_network);
		}
	}

	/**
	 * 公交路线查询回调
	 */
	@Override
	public void onBusRouteSearched(BusRouteResult result, int rCode) {
		dissmissProgressDialog();
		if (rCode == 0) {
			if (result != null && result.getPaths() != null
					&& result.getPaths().size() > 0) {
				busRouteResult = result;
				BusPath busPath = busRouteResult.getPaths().get(0);
				aMap.clear();// 清理地图上的所有覆盖物
				BusRouteOverlay routeOverlay = new BusRouteOverlay(this, aMap,
						busPath, busRouteResult.getStartPos(),
						busRouteResult.getTargetPos());
				routeOverlay.removeFromMap();
				routeOverlay.addToMap();
				routeOverlay.zoomToSpan();
			} else {
				ToastUtil.show(RouteActivity.this, R.string.no_result);
			}
		} else {
			ToastUtil.show(RouteActivity.this, R.string.error_network);
		}
	}

	/**
	 * 驾车结果回调
	 */
	@Override
	public void onDriveRouteSearched(DriveRouteResult result, int rCode) {
		dissmissProgressDialog();
		if (rCode == 0) {
			if (result != null && result.getPaths() != null
					&& result.getPaths().size() > 0) {
				driveRouteResult = result;
				DrivePath drivePath = driveRouteResult.getPaths().get(0);
				aMap.clear();// 清理地图上的所有覆盖物
				DrivingRouteOverlay drivingRouteOverlay = new DrivingRouteOverlay(
						this, aMap, drivePath, driveRouteResult.getStartPos(),
						driveRouteResult.getTargetPos());
				drivingRouteOverlay.removeFromMap();
				drivingRouteOverlay.addToMap();
				drivingRouteOverlay.zoomToSpan();
			} else {
				ToastUtil.show(RouteActivity.this, R.string.no_result);
			}
		} else {
			ToastUtil.show(RouteActivity.this, R.string.error_network);
		}
	}

	/**
	 * 步行路线结果回调
	 */
	@Override
	public void onWalkRouteSearched(WalkRouteResult result, int rCode) {
		dissmissProgressDialog();
		if (rCode == 0) {
			if (result != null && result.getPaths() != null
					&& result.getPaths().size() > 0) {
				walkRouteResult = result;
				WalkPath walkPath = walkRouteResult.getPaths().get(0);
				aMap.clear();// 清理地图上的所有覆盖物
				WalkRouteOverlay walkRouteOverlay = new WalkRouteOverlay(this,
						aMap, walkPath, walkRouteResult.getStartPos(),
						walkRouteResult.getTargetPos());
				walkRouteOverlay.removeFromMap();
				walkRouteOverlay.addToMap();
				walkRouteOverlay.zoomToSpan();
			} else {
				ToastUtil.show(RouteActivity.this, R.string.no_result);
			}
		} else {
			ToastUtil.show(RouteActivity.this, R.string.error_network);
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.imagebtn_roadsearch_startoption:
			startImagePoint();
			break;
		case R.id.imagebtn_roadsearch_endoption:
			endImagePoint();
			break;
		case R.id.imagebtn_roadsearch_tab_transit:
			busRoute();
			break;
		case R.id.imagebtn_roadsearch_tab_driving:
			drivingRoute();
			break;
		case R.id.imagebtn_roadsearch_tab_walk:
			walkRoute();
			break;
		case R.id.imagebtn_roadsearch_search:
			searchRoute();
			break;
		default:
			break;
		}
	}
}
