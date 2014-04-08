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
 * DownloadService��ר������Ϊ�ͻ����ṩ���ݣ���Щ���ݵ���Դ��3���� 1.�������ϼ��� 2.�ӻ����ж�ȡ 3.�ӱ��ش洢���ϼ���
 * ���ͻ�����DownloadService������������ʱ��DownloadService��������Ȳ鿴�ͻ������������
 * �ڻ����Ƿ��У�����оͼ��أ����û�����ڱ��ش洢���ϲ��ң�������ش洢��������ӱ��ش洢���ϼ��ء����û
 * ����������ϼ��ء����������ϼ������ݳɹ����򽫴����ݷ��͵��ͻ��ˣ�ͬʱ�����ݴ洢�������ڴ�ͱ��ش洢���С�
 * 
 * Ϊ��ʹ�û�������ã��������״ν��������������������Ԥ������ҳ�����ݡ�
 * 
 * @author BM
 * 
 */
@SuppressLint("NewApi")
public class DownloadService extends Service {

	/** ���ڲ��Ե�tag */
	private final static String TAG = DownloadService.class.getName();

	/** ��Ҫ���յ�Action��ǰ׺ */
	public final static String INTENT_PREFIX = "com.kevin.services";

	/** ����Ԥ�������ŵ�Action */
	public final static String LOAD_NEWS = INTENT_PREFIX + ".load_news";

	/** �����ж��Ƿ��ӡ��־��Ϣ */
	public static boolean isDebug = false;

	/** �ڴ滺�湤���� */
	private CacheUtils<String, String> cacheUtils;

	/** �ڴ滺�� */
	private LruCache<String, String> cache;

	/** ���̻��� */
	private DiskLruCache mDiskLruCache;

	/** Ϊ�˷�ֹ����߳�ͬʱ����ͬһ���ļ������õ��� */
	private final Object mDiskCacheLock = new Object();

	/** �Ƿ����ô��̻��� */
	private boolean mDiskCacheStarting = true;

	/** ���̻����С */
	private static final int DISK_CACHE_SIZE = 1024 * 1024 * 10;// 10MB

	/** �����ļ���ŵ�Ŀ¼ */
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
		 * �ͻ��˵��ô˷������DownloadService��ʵ��
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
											"���粻�ã����Ժ����ԣ�", Toast.LENGTH_LONG)
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
