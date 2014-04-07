package com.kevin.util;

import java.util.ArrayList;
import java.util.HashMap;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
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
}
