package com.example.recipesforlife.util;

import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;
import java.lang.ref.WeakReference;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.widget.ImageView;

import com.example.recipesforlife.controllers.ImageBean;

/**
 * Handles the loading of single images
 * @author Kari
 *
 */
public class ImageLoader extends AsyncTask <Void, Void, Bitmap>{
	private Context context;
	private ImageBean imgbean;
	private final WeakReference imageViewReference;



	public ImageLoader(Context context,  ImageBean imgBean, ImageView imageView)
	{
		imgbean = imgBean;
		ImageLoader.this.context = context;
		imageViewReference = new WeakReference(imageView);
	}

	@Override
	protected void onPreExecute()
	{


	}

	@Override
	//Loads image in background - away from ui thread
	protected Bitmap doInBackground(Void... arg0) {

		//Get bitmap of image in the background
		Bitmap bitmap = null;
		ByteArrayInputStream imageStream = new ByteArrayInputStream(imgbean.getImage());
		try {
			bitmap = decodeSampledBitmapFromResource(context, imageStream, 200,200);
		} catch (FileNotFoundException e) {
					e.printStackTrace();
		}
		return bitmap;
	}

	@Override
	//Once load is finished set the image to the imageview
	protected void onPostExecute( Bitmap result )  {

		ImageView imageView = (ImageView) imageViewReference.get();
		imageView.setImageBitmap(result);
	}

	/**
	 * Sets bitmap options 
	 * 
	 * @param context			Activity context
	 * @param imageStream		byte array input stream
	 * @param reqWidth			Required width for image
	 * @param reqHeight			Required height for image
	 * @return Changed bitmap	Altered image
	 * @throws FileNotFoundException
	 */
	@SuppressWarnings("deprecation")
	private static Bitmap decodeSampledBitmapFromResource(Context context, ByteArrayInputStream imageStream,
			int reqWidth, int reqHeight) 
					throws FileNotFoundException {

		
		// First decode with inJustDecodeBounds=true to check dimensions
		final BitmapFactory.Options options = new BitmapFactory.Options();
		options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
		options.inPurgeable = true;
		options.inInputShareable = true;
	
		// Decode bitmap with inSampleSize set
		options.inJustDecodeBounds = false;
		return BitmapFactory.decodeStream(imageStream, null, options);
	}

	/**
	 * Calculates the best sample size for the required width height and width
	 * 
	 * @param options		Bitmap options for image
	 * @param reqWidth		Required width for image
	 * @param reqHeight		Required height for image
	 * @return int 			Sample size
	 */
	private static int calculateInSampleSize(
			BitmapFactory.Options options, int reqWidth, int reqHeight) 
	{
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
}