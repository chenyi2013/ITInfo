package com.amapv2.apis.poisearch;

import java.util.ArrayList;
import java.util.List;

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import com.amap.api.maps.AMap;
import com.amap.api.maps.AMap.InfoWindowAdapter;
import com.amap.api.maps.AMap.OnMarkerClickListener;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.SupportMapFragment;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.services.core.AMapException;
import com.amap.api.services.core.PoiItem;
import com.amap.api.services.core.SuggestionCity;
import com.amap.api.services.help.Inputtips;
import com.amap.api.services.help.Inputtips.InputtipsListener;
import com.amap.api.services.help.Tip;
import com.amap.api.services.overlay.PoiOverlay;
import com.amap.api.services.poisearch.PoiItemDetail;
import com.amap.api.services.poisearch.PoiResult;
import com.amap.api.services.poisearch.PoiSearch;
import com.amap.api.services.poisearch.PoiSearch.OnPoiSearchListener;
import com.amapv2.apis.util.AMapUtil;
import com.amapv2.apis.util.ToastUtil;
import com.kevin.itinfo.R;
import com.kevin.map.Constants;

/**
 * AMapV2��ͼ�м򵥽���poisearch����
 */
public class PoiKeywordSearchActivity extends FragmentActivity implements
		OnMarkerClickListener, InfoWindowAdapter, TextWatcher,
		OnPoiSearchListener, OnClickListener {
	private AMap aMap;
	private AutoCompleteTextView searchText;// ���������ؼ���
	private String keyWord = "";// poi�����ؼ���
	private ProgressDialog progDialog = null;// ����ʱ������
	private EditText editCity;// ����
	private PoiResult poiResult; // poi���صĽ��
	private int currentPage = 0;// ��ǰҳ�棬��0��ʼ����
	private PoiSearch.Query query;// Poi��ѯ������
	private PoiSearch poiSearch;// POI����
	private float Latitude;
	private float Longitude;
	private LatLng latLng;
	private ImageButton poi_search_check;
	private LinearLayout poikey_search_lin;
	private int count;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.poikeywordsearch_activity);
		init();
		poi_search_check = (ImageButton) findViewById(R.id.poi_search_check);
		poikey_search_lin = (LinearLayout) findViewById(R.id.poikey_search_lin);
		setUpMap();
		getData();
	}

	/**
	 * ��ʼ��AMap����
	 */
	private void init() {
		if (aMap == null) {
			aMap = ((SupportMapFragment) getSupportFragmentManager()
					.findFragmentById(R.id.map)).getMap();

		}

	}

	/**
	 * ��ȡsharePreferences�ķ���
	 */
	private void getData() {
		SharedPreferences sp = this.getSharedPreferences(Constants.SHAREDATA,
				MODE_APPEND);
		Latitude = sp.getFloat("Latitude", (float) Constants.BEIJING.latitude);
		Longitude = sp.getFloat("Longitude",
				(float) Constants.BEIJING.longitude);
		latLng = new LatLng(Latitude, Longitude);
		System.out.println("ά�ȣ�" + Latitude + "���ȣ�" + Longitude);
	}

	/**
	 * ����ҳ�����
	 */
	private void setUpMap() {
		Button searButton = (Button) findViewById(R.id.searchButton);
		searButton.setOnClickListener(this);
		Button nextButton = (Button) findViewById(R.id.nextButton);
		nextButton.setOnClickListener(this);
		searchText = (AutoCompleteTextView) findViewById(R.id.keyWord);
		searchText.addTextChangedListener(this);// ����ı����������¼�
		editCity = (EditText) findViewById(R.id.city);
		aMap.setOnMarkerClickListener(this);// ��ӵ��marker�����¼�
		aMap.setInfoWindowAdapter(this);// �����ʾinfowindow�����¼�
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		aMap.addMarker(new MarkerOptions()
				.position(latLng)
				.title("����")
				.snippet("�찲��")
				.icon(BitmapDescriptorFactory
						.fromResource(R.drawable.new_location_at)));

		aMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
		aMap.moveCamera(CameraUpdateFactory.zoomTo(14));
		poi_search_check.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (count % 2 == 0) {

					poi_search_check
							.setImageResource(R.drawable.map_route_down);
					poikey_search_lin.setVisibility(View.GONE);
				} else {

					poi_search_check.setImageResource(R.drawable.map_route_up);
					poikey_search_lin.setVisibility(View.VISIBLE);

				}
				count++;
			}
		});

	}

	/**
	 * ���������ť
	 */
	public void searchButton() {
		keyWord = AMapUtil.checkEditText(searchText);
		if ("".equals(keyWord)) {
			ToastUtil.show(PoiKeywordSearchActivity.this, "�����������ؼ���");
			return;
		} else {
			doSearchQuery();
		}
	}

	/**
	 * �����һҳ��ť
	 */
	public void nextButton() {
		if (query != null && poiSearch != null && poiResult != null) {
			if (poiResult.getPageCount() - 1 > currentPage) {
				currentPage++;
				query.setPageNum(currentPage);// ���ò��һҳ
				poiSearch.searchPOIAsyn();
			} else {
				ToastUtil.show(PoiKeywordSearchActivity.this,
						R.string.no_result);
			}
		}
	}

	/**
	 * ��ʾ���ȿ�
	 */
	private void showProgressDialog() {
		if (progDialog == null)
			progDialog = new ProgressDialog(this);
		progDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		progDialog.setIndeterminate(false);
		progDialog.setCancelable(false);
		progDialog.setMessage("��������:\n" + keyWord);
		progDialog.show();
	}

	/**
	 * ���ؽ��ȿ�
	 */
	private void dissmissProgressDialog() {
		if (progDialog != null) {
			progDialog.dismiss();
		}
	}

	/**
	 * ��ʼ����poi����
	 */
	protected void doSearchQuery() {
		showProgressDialog();// ��ʾ���ȿ�
		currentPage = 0;
		query = new PoiSearch.Query(keyWord, "", editCity.getText().toString());// ��һ��������ʾ�����ַ������ڶ���������ʾpoi�������ͣ�������������ʾpoi�������򣨿��ַ�������ȫ����
		query.setPageSize(10);// ����ÿҳ��෵�ض�����poiitem
		query.setPageNum(currentPage);// ���ò��һҳ

		poiSearch = new PoiSearch(this, query);
		poiSearch.setOnPoiSearchListener(this);
		poiSearch.searchPOIAsyn();
	}

	@Override
	public boolean onMarkerClick(Marker marker) {
		marker.showInfoWindow();
		return false;
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
	 * poiû�����������ݣ�����һЩ�Ƽ����е���Ϣ
	 */
	private void showSuggestCity(List<SuggestionCity> cities) {
		String infomation = "�Ƽ�����\n";
		for (int i = 0; i < cities.size(); i++) {
			infomation += "��������:" + cities.get(i).getCityName() + "��������:"
					+ cities.get(i).getCityCode() + "���б���:"
					+ cities.get(i).getAdCode() + "\n";
		}
		ToastUtil.show(PoiKeywordSearchActivity.this, infomation);

	}

	@Override
	public void afterTextChanged(Editable s) {

	}

	@Override
	public void beforeTextChanged(CharSequence s, int start, int count,
			int after) {

	}

	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count) {
		String newText = s.toString().trim();
		Inputtips inputTips = new Inputtips(PoiKeywordSearchActivity.this,
				new InputtipsListener() {

					@Override
					public void onGetInputtips(List<Tip> tipList, int rCode) {
						if (rCode == 0) {// ��ȷ����
							List<String> listString = new ArrayList<String>();
							for (int i = 0; i < tipList.size(); i++) {
								listString.add(tipList.get(i).getName());
							}
							ArrayAdapter<String> aAdapter = new ArrayAdapter<String>(
									getApplicationContext(),
									R.layout.route_inputs, listString);
							searchText.setAdapter(aAdapter);
							aAdapter.notifyDataSetChanged();
						}
					}
				});
		try {
			inputTips.requestInputtips(newText, editCity.getText().toString());// ��һ��������ʾ��ʾ�ؼ��֣��ڶ�������Ĭ�ϴ���ȫ����Ҳ����Ϊ��������

		} catch (AMapException e) {
			e.printStackTrace();
		}
	}

	/**
	 * POI�����ѯ�ص�
	 */
	@Override
	public void onPoiItemDetailSearched(PoiItemDetail arg0, int rCode) {

	}

	/**
	 * POI��ѯ�ص�
	 */
	@Override
	public void onPoiSearched(PoiResult result, int rCode) {
		dissmissProgressDialog();// ���ضԻ���
		if (rCode == 0) {
			if (result != null && result.getQuery() != null) {// ����poi�Ľ��
				if (result.getQuery().equals(query)) {// �Ƿ���ͬһ��
					poiResult = result;
					// ȡ����������poiitems�ж���ҳ
					List<PoiItem> poiItems = poiResult.getPois();// ȡ�õ�һҳ��poiitem���ݣ�ҳ��������0��ʼ
					List<SuggestionCity> suggestionCities = poiResult
							.getSearchSuggestionCitys();// ����������poiitem����ʱ���᷵�غ��������ؼ��ֵĳ�����Ϣ

					if (poiItems != null && poiItems.size() > 0) {
						aMap.clear();// ����֮ǰ��ͼ��
						PoiOverlay poiOverlay = new PoiOverlay(aMap, poiItems);
						poiOverlay.removeFromMap();
						poiOverlay.addToMap();
						poiOverlay.zoomToSpan();
					} else if (suggestionCities != null
							&& suggestionCities.size() > 0) {
						showSuggestCity(suggestionCities);
					} else {
						ToastUtil.show(PoiKeywordSearchActivity.this,
								R.string.no_result);
					}
				}
			} else {
				ToastUtil.show(PoiKeywordSearchActivity.this,
						R.string.no_result);
			}
		} else {
			ToastUtil.show(PoiKeywordSearchActivity.this,
					R.string.error_network);
		}

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		/**
		 * ���������ť
		 */
		case R.id.searchButton:
			searchButton();
			break;
		/**
		 * �����һҳ��ť
		 */
		case R.id.nextButton:
			nextButton();
			break;
		default:
			break;
		}

	}
}
