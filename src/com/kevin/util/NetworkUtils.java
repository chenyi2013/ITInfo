/*
 * Copyright (C) 2014 Chen Kevin 
 * All rights,including trade secret rights, reserved
 */
package com.kevin.util;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

public class NetworkUtils {
	private static final String TAG = "NetworkUtils";
	private static final int DOWNLOAD_SUCCESS = 1;
	private static final int DOWNLOAD_FAILURE = 2;
	private static final boolean isDebug =false;
	
	public interface ObtainDataCallback
	{
		public void getByteArrayData(byte data[]);
	}
	
	/**
	 * 
	 * @param url
	 * @param callback
	 */
	public static void getDownloadData(final String url, final ObtainDataCallback callback)
	{
		final Handler handler = new Handler(){
			@Override
			public void handleMessage(Message msg) {
				if(isDebug)
				{
				    Log.i(TAG,"receive message");
				}
				
				if(msg.what == DOWNLOAD_SUCCESS )
				{
					callback.getByteArrayData((byte[])msg.obj);
				}
			}
		};
		
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				
				byte []data = HttpUtils.getNetworkData(url);
				
				Message msg = Message.obtain(handler);
				
				if(data != null)
				{
					msg.what = DOWNLOAD_SUCCESS;
					msg.obj = data;
					
					if(isDebug)
					{
						Log.i(TAG,"download network data success");
					}
					
				}else
				{
					msg.what = DOWNLOAD_FAILURE;
					
					if(isDebug)
					{
						Log.i(TAG,"download network data failure");
					}
				}
				
				msg.sendToTarget();
				if(isDebug)
				{
					Log.i(TAG,"send message");
				}
				
			}
		}).start();
	}

}
