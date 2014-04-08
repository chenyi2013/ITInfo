package com.kevin.services;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.v4.util.LruCache;
import android.widget.Toast;

import com.kevin.bean.News;
import com.kevin.config.Urls;
import com.kevin.util.CacheUtils;
import com.kevin.util.DiskLruCache;
import com.kevin.util.JsonUtils;
import com.kevin.util.NetworkUtils;

/**
 * DownloadService类专门用于为客户端提供数据，这些数据的来源有3个。 1.从网络上加载 2.从缓存中读取 3.从本地存储器上加载
 * 当客户端向DownloadService服务请求数据时，DownloadService服务会首先查看客户端请求的数据
 * 在缓存是否有，如果有就加载，如果没有则在本地存储器上查找，如果本地存储器上有则从本地存储器上加载。如果没
 * 有则从网络上加载。当从网络上加载数据成功后则将此数据发送到客户端，同时将数据存储缓存在内存和本地存储器中。
 * 
 * 为了使用户体验更好，我们在首次进入程序后会启动本服务类预加载首页的数据。
 * 
 * @author BM
 * 
 */
@SuppressLint("NewApi")
public class DownloadService extends Service {

	/** 用于测试的tag */
	private final static String TAG = DownloadService.class.getName();

	/** 所要接收的Action的前缀 */
	public final static String INTENT_PREFIX = "com.kevin.services";

	/** 用于预加载新闻的Action */
	public final static String LOAD_NEWS = INTENT_PREFIX + ".load_news";

	/** 用于判断是否打印日志信息 */
	public static boolean isDebug = false;

	/** 内存缓存工具类 */
	private CacheUtils<String, String> cacheUtils;

	/** 内存缓存 */
	private LruCache<String, String> cache;

	/** 磁盘缓存 */
	private DiskLruCache mDiskLruCache;

	/** 为了防止多个线程同时访问同一个文件而设置的锁 */
	private final Object mDiskCacheLock = new Object();

	/** 是否启用磁盘缓存 */
	private boolean mDiskCacheStarting = true;

	/** 磁盘缓存大小 */
	private static final int DISK_CACHE_SIZE = 1024 * 1024 * 10;// 10MB

	/** 缓存文件存放的目录 */
	private static final String DISK_CACHE_SUBDIR = "thumbnails";

	private static final String ENTRY_KEY = "entry_key";

	private LocalServiceBinder mBinder = new LocalServiceBinder();

	public DownloadService() {

	}

	@Override
	public void onCreate() {
		super.onCreate();

		cacheUtils = new CacheUtils<String, String>(
				CacheUtils.getSuggestCacheSize());
		cache = cacheUtils.getLruCache();
		File cacheDir = getDiskCacheDir(DISK_CACHE_SUBDIR);
		new InitDiskCacheTask().execute(cacheDir);

	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {

		if (intent.getAction().equals(LOAD_NEWS)) {

		}
		return super.onStartCommand(intent, flags, startId);
	}

	@Override
	public IBinder onBind(Intent intent) {
		// TODO: Return the communication channel to the service.
		return mBinder;
	}

	public class LocalServiceBinder extends Binder {

		/**
		 * 客户端调用此方法获得DownloadService的实例
		 * */
		public DownloadService getDownloadService() {

			return DownloadService.this;
		}

	}

	public void loadNews(final Handler handler, final int what) {

		String json = getStringFromMemCache(ENTRY_KEY + what);

		if (json == null) {

			json = getStringFromDiskCache(ENTRY_KEY + what);
		}

		if (json != null) {
			ArrayList<News> newsList = JsonUtils.getNewsList(json);
			Message msg = handler.obtainMessage();
			msg.obj = newsList;
			msg.what = what;
			handler.sendMessage(msg);

		} else {

			NetworkUtils.getDownloadData(Urls.NEWS_URL + what,
					new NetworkUtils.ObtainDataCallback() {

						@Override
						public void getByteArrayData(byte[] data) {
							String json = null;
							try {
								json = new String(data, "UTF-8");

							} catch (UnsupportedEncodingException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							ArrayList<News> newsList = null;
							if (json != null) {

								try {
									newsList = JsonUtils.getNewsList(json);
								} catch (Exception e) {
									Toast.makeText(getApplicationContext(),
											"网络不好，请稍候重试！", Toast.LENGTH_LONG)
											.show();
									return;
								}

								Message msg = handler.obtainMessage();
								msg.obj = newsList;
								msg.what = what;
								handler.sendMessage(msg);
								addStringToCache(ENTRY_KEY + what, json);

							}

						}
					});
		}

	}

	public File getDiskCacheDir(String uniqueName) {
		// Check if media is mounted or storage is built-in, if so, try and use
		// external cache dir
		// otherwise use internal cache dir
		final String cachePath = getCacheDir().getPath();

		return new File(cachePath + File.separator + uniqueName);
	}

	class InitDiskCacheTask extends AsyncTask<File, Void, Void> {
		@Override
		protected Void doInBackground(File... params) {
			synchronized (mDiskCacheLock) {
				File cacheDir = params[0];
				try {
					mDiskLruCache = DiskLruCache.open(cacheDir, 100, 1,
							DISK_CACHE_SIZE);
					mDiskCacheStarting = false; // Finished initialization
					mDiskCacheLock.notifyAll(); // Wake any waiting threads
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
			return null;
		}
	}

	public void addStringToCache(String key, String value) {
		// Add to memory cache as before
		if (getStringFromMemCache(key) == null) {

			cache.put(key, value);
		}
		// Also add to disk cache
		synchronized (mDiskCacheLock) {
			try {
				DiskLruCache.Editor editor = mDiskLruCache.edit(key);
				editor.set(0, value);
				editor.commit();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
	}

	public String getStringFromMemCache(String key) {
		return cache.get(key);
	}

	public String getStringFromDiskCache(String key) {
		synchronized (mDiskCacheLock) {
			// Wait while disk cache is started from background thread
			while (mDiskCacheStarting) {
				try {
					mDiskCacheLock.wait();
				} catch (InterruptedException e) {
				}
			}

			try {
				if (mDiskLruCache != null && mDiskLruCache.get(key) != null) {

					return mDiskLruCache.get(key).getString(0);
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return null;
	}

}
