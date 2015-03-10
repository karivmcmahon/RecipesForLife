package com.example.recipesforlife.views;

import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;
import java.util.Collections;
import java.util.Map;
import java.util.UUID;
import java.util.WeakHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.example.recipesforlife.models.util;
import com.example.recipesforlife.models.utility;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.util.Log;
import android.widget.ImageView;


//Modified https://github.com/thest1/LazyList/blob/master/src/com/fedorvlasov/lazylist/ImageLoader.java
public class ImageLoader2 {

	MemoryCache memoryCache=new MemoryCache();
	FileCache fileCache;
	private Map<ImageView, String> imageViews=Collections.synchronizedMap(new WeakHashMap<ImageView, String>());
	ExecutorService executorService;
	Handler handler=new Handler();//handler to display images in UI thread
	utility utils;
	String uuid = "";
	Context context;

	public ImageLoader2(Context context){
		fileCache=new FileCache(context);
		executorService=Executors.newFixedThreadPool(5);
		utils = new utility();
		this.context = context;
	}

	//final int stub_id=R.drawable.stub;
	public void DisplayImage(ImageView imageView, byte[] arr, String recipeid)
	{
		uuid = recipeid;
		imageViews.put(imageView, recipeid);
		Bitmap bitmap=memoryCache.get(recipeid);
		if(bitmap!=null)
			imageView.setImageBitmap(bitmap);
	   else
        {
            queuePhoto(arr, imageView);
            //imageView.setImageResource(stub_id);
        } 
	}

	private void queuePhoto(byte[] bytearr, ImageView imageView)
	{
		PhotoToLoad p=new PhotoToLoad( bytearr, imageView, uuid);
		executorService.submit(new PhotosLoader(p));
	}

	private Bitmap getBitmap(byte[] bytearr) 
	{

		ByteArrayInputStream imageStream = new ByteArrayInputStream(bytearr);
		//from SD cache
		Bitmap b = null;
		try {
			b = decodeSampledBitmap(context, imageStream , 200, 150 );
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Log.v("Bitmap", "Bitmap " + b);
		if(b!=null)
			return b;
		return null;


	}

	public static Bitmap decodeSampledBitmap(Context context, ByteArrayInputStream imageStream,
			int reqWidth, int reqHeight) 
					throws FileNotFoundException {

		// First decode with inJustDecodeBounds=true to check dimensions
		final BitmapFactory.Options options = new BitmapFactory.Options();
		options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
		// Decode bitmap with inSampleSize se#
		options.inJustDecodeBounds = false;
		options.inPurgeable = true;
		options.inInputShareable = true;
		return BitmapFactory.decodeStream(imageStream, null, options);
	}

	public static int calculateInSampleSize(
			BitmapFactory.Options options, int reqWidth, int reqHeight) {
		// Raw height and width of image
		final int height = options.outHeight;
		final int width = options.outWidth;
		int inSampleSize = 1;

		if (height > reqHeight || width > reqWidth) {

			final int halfHeight = height / 2;
			final int halfWidth = width / 2;

			// Calculate the largest inSampleSize value that is a power of 2 and keeps both
			// height and width larger than the requested height and width.
			while ((halfHeight / inSampleSize) > reqHeight
					&& (halfWidth / inSampleSize) > reqWidth) {
				inSampleSize *= 2;
			}
		}

		return inSampleSize;
	}

	//Task for the queue
	private class PhotoToLoad
	{
		public byte[] bytearr;
		public ImageView imageView;
		public String recipeid;
		public PhotoToLoad(byte[] b, ImageView i, String id){
			bytearr=b; 
			imageView=i;
			recipeid=id;
		}
	}

	class PhotosLoader implements Runnable {
		PhotoToLoad photoToLoad;
		PhotosLoader(PhotoToLoad photoToLoad){
			this.photoToLoad=photoToLoad;
		}

		@Override
		public void run() {
			try{
				if(imageViewReused(photoToLoad))
					return;
				Bitmap bmp=getBitmap(photoToLoad.bytearr);
				memoryCache.put(photoToLoad.recipeid, bmp);
				if(imageViewReused(photoToLoad))
					return;
				BitmapDisplayer bd=new BitmapDisplayer(bmp, photoToLoad);
				handler.post(bd);
			}catch(Throwable th){
				th.printStackTrace();
			}
		}
	}

	boolean imageViewReused(PhotoToLoad photoToLoad){
		String tag=imageViews.get(photoToLoad.imageView);
		if(tag==null || !tag.equals(photoToLoad.recipeid))
			return true;
		return false;
	}

	//Used to display bitmap in the UI thread
	class BitmapDisplayer implements Runnable
	{
		Bitmap bitmap;
		PhotoToLoad photoToLoad;
		public BitmapDisplayer(Bitmap b, PhotoToLoad p){bitmap=b;photoToLoad=p;}
		public void run()
		{
			if(imageViewReused(photoToLoad))
				return;
			if(bitmap!=null)
				photoToLoad.imageView.setImageBitmap(bitmap);
		//	else
			//	photoToLoad.imageView.setImageResource(stub_id);
		}
	}

	public void clearCache() {
		memoryCache.clear();
		fileCache.clear();
	}
	
	public String generateUUID( ) {
		//   final String uuid = UUID.randomUUID().toString().replaceAll("-", "");
		final String uuid = UUID.randomUUID().toString();
		return uuid;
	}

}