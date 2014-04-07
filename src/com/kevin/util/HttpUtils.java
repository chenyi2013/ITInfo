/*
 * Copyright (C) 2014 Chen Kevin 
 * All rights,including trade secret rights, reserved
 */
package com.kevin.util;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import android.util.Log;

public class HttpUtils {
	
	private static final String TAG = "HttpUtils";
	private static final boolean isDebug = false;
	/**
	 * get the network data from a specified url and return an byte array of the
	 * network data
	 * 
	 */
	public static byte[] getNetworkData(String url) {

		byte[] data = null;
		HttpClient client = new DefaultHttpClient();
		HttpGet get = new HttpGet(url);

		try {
			HttpResponse response = client.execute(get);
			if (response.getStatusLine().getStatusCode() == 200) {

				if(isDebug)
				{
					Log.i(TAG,"Access Network success");
				}
				data = EntityUtils.toByteArray(response.getEntity());
			}
		} catch (ClientProtocolException e) {

			e.printStackTrace();
		} catch (IOException e) {

			e.printStackTrace();
		}
		return data;

	}
	/**
	 * get the network data from a specified url and return an string of the
	 * network data
	 * 
	 */
	public static String getNetworkData(String url, String charset) {
		String data = null;
		try {
			data = new String(getNetworkData(url), charset);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return data;
	}
}
