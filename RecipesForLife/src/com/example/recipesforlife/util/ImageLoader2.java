package com.example.recipesforlife.util;

import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;
import java.util.Collections;
import java.util.Map;
import java.util.UUID;
import java.util.WeakHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.util.Log;
import android.widget.ImageView;


/**
 * Image Loader for list view images 
 * Modified verision of https://github.com/thest1/LazyList/blob/master/src/com/fedorvlasov/lazylist/ImageLoader.java
 * @author Kari
 *
 */
public class ImageLoader2 {

	MemoryCache memoryCache=new MemoryCache();

	private Map<ImageView, String> imageViews=Collections.synchronizedMap(new WeakHashMap<ImageView, String>());
	ExecutorService executorService;
	Handler handler=new Handler();//handler to display images in UI thread
	Utility utils;
	String uuid = "";
	Context context;

	public ImageLoader2(Context context){
		executorService=Executors.newFixedThreadPool(5);
		utils = new Utility();
		this.context = context;
	}


	/**
	 * Sets the image to be displayed if in cache otherwise it will queue photo
	 * @param imageView
	 * @param arr
	 * @param recipeid
	 */
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
		} 
	}

	/**
	 * Submits photo to executor service
	 * @param bytearr
	 * @param imageView
	 */
	private void queuePhoto(byte[] bytearr, ImageView imageView)
	{
		PhotoToLoad p=new PhotoToLoad( bytearr, imageView, uuid);
		executorService.submit(new PhotosLoader(p));
	}

	/**
	 * Gets bitmap by using method decode sampled bitmap
	 * @param bytearr
	 * @return Bitmap
	 */
	private Bitmap getBitmap(byte[] bytearr) 
	{

		ByteArrayInputStream imageStream = new ByteArrayInputStream(bytearr);
		Bitmap b = null;
		try {
			b = decodeSampledBitmap(context, imageStream , 200, 150 );
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if(b!=null)
			return b;
		return null;


	}

	/**
	 * Gets bitmap with correct sample size for width and height
	 * @param context
	 * @param imageStream
	 * @param reqWidth
	 * @param reqHeight
	 * @return
	 * @throws FileNotFoundException
	 */
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

	/**
	 * Calculates sample size based on width and height
	 * @param options
	 * @param reqWidth
	 * @param reqHeight
	 * @return
	 */
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

	/**
	 *  Class handles photo loading
	 * @author Kari
	 *
	 */
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
				//gets image
				Bitmap bmp=getBitmap(photoToLoad.bytearr);
				//places image in cache
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

	/**
	 * Checks if imageview has been used
	 * @param photoToLoad
	 * @return
	 */
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
		}
	}

	/**
	 * Clears cache
	 */
	public void clearCache() {
		memoryCache.clear();
	}



}