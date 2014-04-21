package com.kevin.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.kevin.bean.Dealer;
import com.kevin.bean.Dealers;
import com.kevin.bean.Distribution;
import com.kevin.bean.Distributions;
import com.kevin.bean.GoodsInfo;
import com.kevin.bean.News;

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

}
