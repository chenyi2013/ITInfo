package com.kevin.util;

import java.io.Closeable;
import java.io.IOException;

import android.graphics.Bitmap;
import android.support.v4.util.LruCache;
import android.util.Log;

public class CacheUtils<K, V> {
	private static final String TAG = "CacheUtils";

	/**
	 * 根据需求，缓存可能有必要设计成单例
	 */
	private LruCache<K, V> mCache;

	/**
	 * 缓存的大小
	 */
	private int mCacheSize;

	/**
	 * 指定缓存的大小，以字节为单位
	 * 
	 * @param size
	 */
	public CacheUtils(int size) {
		mCacheSize = size;
	}

	/**
	 * 得到一个指定泛型的LRU缓存。LruCache是以线程安全模型实现的，所以没有必要再加锁。
	 * 
	 * @return
	 */
	public LruCache<K, V> getLruCache() {
		if (mCache == null) {
			mCache = new LruCache<K, V>(mCacheSize) {
				@Override
				protected int sizeOf(K key, V value) {
					int entitySize = 1;
					if (value instanceof CharSequence) {
						entitySize = value.toString().getBytes().length;
					} else if (value instanceof Bitmap) {
						entitySize = ((Bitmap) value).getRowBytes()
								* ((Bitmap) value).getHeight();
					} else {
						Log.d(TAG, "不会计算" + value.getClass()
								+ "的大小，请重写sizeOf方法");
					}
					return entitySize;
				}

				@Override
				protected void entryRemoved(boolean evicted, K key, V oldValue,
						V newValue) {
					super.entryRemoved(evicted, key, oldValue, newValue);
					if (oldValue instanceof Closeable) {
						try {
							((Closeable) oldValue).close();
							oldValue = null;
						} catch (IOException e) {
							e.printStackTrace();
						}
					} else if (oldValue instanceof Bitmap) {
						((Bitmap) oldValue).recycle();
						oldValue = null;
					} else {
						Log.d(TAG, "不会清理" + oldValue.getClass()
								+ "这种资源，请重写entryRemoved方法");
					}
				}

			};
		}
		return mCache;
	}

	/**
	 * 得到建议的缓存大小
	 * 
	 * @param portion
	 *            缓存大小是设备可用内存的 portion 分之一
	 * @return
	 */
	public static int getSuggestCacheSize(int portion) {
		long MAX_SIZE = Runtime.getRuntime().maxMemory();
		return (int) (MAX_SIZE / portion);
	}

	/**
	 * 缓存为设备最大内存的8分之一
	 * 
	 * @return
	 */
	public static int getSuggestCacheSize() {
		return getSuggestCacheSize(8);
	}

	/**
	 * 判断缓存性能是否良好的阀值
	 */
	private final float GOOD_THRESHOLD = 0.8f;

	/**
	 * 判断缓存性能是否优秀
	 * 
	 * 如果命中率不高，可以考虑增大缓存大小
	 * 
	 * @return
	 */
	public boolean isGoodCache() {
		return mCache != null ? (mCache.hitCount() / mCache.putCount()) > GOOD_THRESHOLD
				: false;
	}
}