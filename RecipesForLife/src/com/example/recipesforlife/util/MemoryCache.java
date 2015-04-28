package com.example.recipesforlife.util;

import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

import android.graphics.Bitmap;
import android.util.Log;

/**
 * This class is the memory cache as part of the image loader from list view
 * Based on this code https://github.com/thest1/LazyList/blob/master/src/com/fedorvlasov/lazylist/MemoryCache.java
 * 
 *
 */
public class MemoryCache {

	private static final String TAG = "MemoryCache";
	private Map<String, Bitmap> cache=Collections.synchronizedMap(
			new LinkedHashMap<String, Bitmap>(10,1.5f,true));//Last argument true for LRU ordering
	private long size=0;//current allocated size
	private long limit=1000000;//max memory in bytes

	public MemoryCache(){
		//use 25% of available heap size
		setLimit(Runtime.getRuntime().maxMemory()/4);
	}

	/**
	 * Sets limit
	 * 
	 * @param new_limit
	 */
	public void setLimit(long new_limit){
		limit=new_limit;

	}

	/**
	 * Get bitmap based on id
	 * 
	 * @param id			Image id
	 * @return Bitmap		Image
	 */
	public Bitmap get(String id){
		try{
			if(!cache.containsKey(id))
				return null;
			return cache.get(id);
		}catch(NullPointerException ex){
			ex.printStackTrace();
			return null;
		}
	}

	/**
	 * Puts bitmap in cache
	 * 
	 * @param id		Image id
	 * @param bitmap	Image
	 */
	public void put(String id, Bitmap bitmap){
		try{
			if(cache.containsKey(id))
				size-=getSizeInBytes(cache.get(id));
			cache.put(id, bitmap);
			size+=getSizeInBytes(bitmap);
			checkSize();
		}catch(Throwable th){
			th.printStackTrace();
		}
	}

	/**
	 * Checks the cache size
	 */
	private void checkSize() {
		Log.i(TAG, "cache size="+size+" length="+cache.size());
		if(size>limit){
			Iterator<Entry<String, Bitmap>> iter=cache.entrySet().iterator();//least recently accessed item will be the first one iterated  
			while(iter.hasNext()){
				Entry<String, Bitmap> entry=iter.next();
				size-=getSizeInBytes(entry.getValue());
				iter.remove();
				if(size<=limit)
					break;
			}
			Log.i(TAG, "Clean cache. New size "+cache.size());
		}
	}

	/**
	 * Clears the cache
	 */
	public void clear() {
		try{
			
			cache.clear();
			size=0;
		}catch(NullPointerException ex){
			ex.printStackTrace();
		}
	}

	
	/**
	 * Gets the bitmap size in bytes
	 * 
	 * @param bitmap	Image
	 * @return long		Bitmap size
	 */
	public long getSizeInBytes(Bitmap bitmap) {
		if(bitmap==null)
			return 0;
		return bitmap.getRowBytes() * bitmap.getHeight();
	}

}
