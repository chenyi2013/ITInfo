package com.kevin.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.kevin.bean.Citys;
import com.kevin.bean.Dealer;
import com.kevin.bean.Dealers;
import com.kevin.bean.Distribution;
import com.kevin.bean.Distributions;
import com.kevin.bean.GoodsInfo;
import com.kevin.bean.News;
import com.kevin.bean.Normal;
import com.kevin.bean.PhoneInfo;
import com.kevin.bean.QQInfo;
import com.kevin.bean.Retailer;
import com.kevin.bean.RetailerInfo;
import com.kevin.bean.Retailers;
import com.kevin.bean.Search;

public class JsonUtils {

	/**
	 * 将获得的json字符串解析成所需要的新闻列表
	 * 
	 * @param json
	 * @return
	 * @throws Exception
	 */

	public static ArrayList<News> getNewsList(String json)
			throws NullPointerException {

		Gson gson = new Gson();
		HashMap<String, ArrayList<News>> map = new HashMap<String, ArrayList<News>>();
		TypeToken<HashMap<String, ArrayList<News>>> token = new TypeToken<HashMap<String, ArrayList<News>>>() {
		};
		map = gson.fromJson(json, token.getType());
		if (map == null) {
			throw new NullPointerException("map is null");
		}
		return map.get("list");
	}

	/**
	 * 解析经销商列表
	 */

	public static Dealers getDealersFromJson(String jsonStr) {
		Dealers dealers = new Dealers();
		List<Dealer> dealerList = new ArrayList<Dealer>();
		try {
			JSONObject jsonObject = new JSONObject(jsonStr);
			JSONArray jsonArray = jsonObject.getJSONArray("list");
			for (int i = 0; i < jsonArray.length(); i++) {
				JSONObject jsObj = jsonArray.getJSONObject(i);
				Dealer dealer = new Dealer();
				dealer.setId(jsObj.getString("id"));
				dealer.setImage(jsObj.getString("image"));
				dealer.setName(jsObj.getString("name"));
				List<GoodsInfo> goodList = new ArrayList<GoodsInfo>();
				JSONArray jsArr = jsObj.getJSONArray("list");
				for (int j = 0; j < jsArr.length(); j++) {
					GoodsInfo goodsInfo = new GoodsInfo();
					JSONObject good = jsArr.getJSONObject(j);
					goodsInfo.setId(good.getString("id"));
					goodsInfo.setTitle(good.getString("title"));
					goodList.add(goodsInfo);
				}
				dealer.setGoodInfos(goodList);
				dealerList.add(dealer);
			}
			dealers.setDealers(dealerList);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return dealers;
	}

	/**
	 * 解析厂商列表
	 */

	public static Distributions getDistributionsFromJson(String jsonStr) {
		Distributions distributions = new Distributions();
		try {
			JSONObject jsonObject = new JSONObject(jsonStr);
			JSONArray jsonArray = jsonObject.getJSONArray("list");
			List<Distribution> distributionList = new ArrayList<Distribution>();
			for (int i = 0; i < jsonArray.length(); i++) {
				JSONObject jsObj = jsonArray.getJSONObject(i);
				Distribution distribution = new Distribution();
				distribution.setTitle(jsObj.getString("title"));
				distribution.setCover(jsObj.getString("cover"));
				distribution.setFocus(jsObj.getString("focus"));
				distribution.setContent(jsObj.getString("content"));
				distribution.setAddress(jsObj.getString("address"));
				JSONArray jsonArray2 = jsObj.getJSONArray("coordinate");
				List<String> coordinate = new ArrayList<String>();
				for (int j = 0; j < jsonArray2.length(); j++) {
					coordinate.add(jsonArray2.getString(j));
				}
				distribution.setCoordinate(coordinate);
				distributionList.add(distribution);
			}
			distributions.setDeDistributions(distributionList);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return distributions;
	}

	/**
	 * 解析搜索
	 */
	public static List<Search> getSearch(String json) {
		List<Search> list = new ArrayList<Search>();
		JSONObject jsonObject;
		try {
			jsonObject = new JSONObject(json);
			if (jsonObject.getInt("total") == 0) {
				return null;
			} else {
				JSONArray arr = jsonObject.getJSONArray("list");
				for (int i = 0; i < arr.length(); i++) {
					Search s = new Search();
					JSONObject ob = arr.getJSONObject(i);
					s.setId(ob.getInt("id"));
					s.setName(ob.getString("name"));
					s.setCounter(ob.getString("counter"));
					list.add(s);
				}
				return list;
			}

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return null;
	}

	/**
	 * 解析搜索结果个数
	 */
	public static int getSearchCount(String json) {
		int count = 0;
		JSONObject jsonObject;
		try {
			jsonObject = new JSONObject(json);
			count = jsonObject.getInt("total");
			return count;
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return 0;
	}

	/**
	 * 解析相应商品的商家列表
	 */

	public static Retailers getRetailersFromJson(String jsonStr) {
		Retailers retailers = new Retailers();
		List<Retailer> retailersList = new ArrayList<Retailer>();
		try {
			JSONObject jsonObject = new JSONObject(jsonStr);
			JSONArray jsonArray = jsonObject.getJSONArray("list");
			for (int i = 0; i < jsonArray.length(); i++) {
				Retailer retailer = new Retailer();
				JSONObject jsObj = jsonArray.getJSONObject(i);
				JSONArray qqArr = jsObj.getJSONArray("qq");
				List<QQInfo> qqList = new ArrayList<QQInfo>();
				for (int j = 0; j < qqArr.length(); j++) {
					QQInfo qqInfo = new QQInfo();
					qqInfo.setQq(qqArr.getString(j));
					qqList.add(qqInfo);
				}
				retailer.setQq(qqList);
				retailer.setAgency_id(jsObj.getString("agency_id"));
				retailer.setName(jsObj.getString("name"));
				JSONArray phoneArr = jsObj.getJSONArray("phone");
				List<PhoneInfo> phoneList = new ArrayList<PhoneInfo>();
				for (int m = 0; m < phoneArr.length(); m++) {
					PhoneInfo phoneInfo = new PhoneInfo();
					phoneInfo.setPhoneNum(phoneArr.getString(m));
					phoneList.add(phoneInfo);
				}
				retailer.setPhoneNum(phoneList);
				retailersList.add(retailer);
			}
			retailers.setRetailersList(retailersList);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return retailers;
	}

	/**
	 * 解析商家详细信息
	 */

	public static RetailerInfo getRetailerInfoFromJson(String jsonStr) {
		RetailerInfo retailerInfo = new RetailerInfo();
		try {
			JSONObject jsonObject = new JSONObject(jsonStr);
			retailerInfo.setId(jsonObject.getString("id"));
			retailerInfo.setTitle(jsonObject.getString("title"));
			retailerInfo.setArea(jsonObject.getString("area"));
			retailerInfo.setAddress(jsonObject.getString("address"));
			retailerInfo.setDescription(jsonObject.getString("description"));
			JSONArray jsonArray1 = jsonObject.getJSONArray("coordinate");
			List<String> coordinateList = new ArrayList<String>();
			for (int i = 0; i < jsonArray1.length(); i++) {
				coordinateList.add(jsonArray1.getString(i));
			}
			retailerInfo.setCoordinate(coordinateList);
			JSONArray jsonArray2 = jsonObject.getJSONArray("tel");
			List<String> telList = new ArrayList<String>();
			for (int j = 0; j < jsonArray2.length(); j++) {
				telList.add(jsonArray2.getString(j));
			}
			retailerInfo.setTel(telList);
			JSONArray jsonArray3 = jsonObject.getJSONArray("qq");
			List<String> qqList = new ArrayList<String>();
			for (int m = 0; m < jsonArray3.length(); m++) {
				qqList.add(jsonArray3.getString(m));
			}
			retailerInfo.setQq(qqList);
			JSONArray jsonArray4 = jsonObject.getJSONArray("img");
			List<String> imgList = new ArrayList<String>();
			for (int n = 0; n < jsonArray4.length(); n++) {
				imgList.add(jsonArray4.getString(n));
			}
			retailerInfo.setImg(imgList);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return retailerInfo;
	}

	public static Normal getNormal(String json) {
		Gson gson = new Gson();
		TypeToken<HashMap<String, Normal>> typeToken = new TypeToken<HashMap<String, Normal>>() {
		};
		HashMap<String, Normal> map = gson.fromJson(json, typeToken.getType());
		return map.get("normal");
	}

	public static Citys getCitys(String json) {
		Gson gson = new Gson();
		return gson.fromJson(json, Citys.class);
	}

}
