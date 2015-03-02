package com.example.recipesforlife.views;

import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;

import com.example.recipesforlife.controllers.imageBean;

import android.content.ContentResolver;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.widget.ImageView;

public class ImageLoader extends AsyncTask <Void, Void, Bitmap>{


	private Context context;
	private InputStream in;
	private imageBean imgbean;
	private final WeakReference imageViewReference;



	ImageLoader(Context context,  imageBean imgBean, ImageView imageView)
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
	protected Bitmap doInBackground(Void... arg0) {

		Bitmap bitmap = null;

		ByteArrayInputStream imageStream = new ByteArrayInputStream(imgbean.getImage());
		try {
			bitmap = decodeSampledBitmapFromResource(context, imageStream, 200,200);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}


		return bitmap;
	}

	@Override
	protected void onPostExecute( Bitmap result )  {

		ImageView imageView = (ImageView) imageViewReference.get();
		imageView.setImageBitmap(result);


	}

	public static Bitmap decodeSampledBitmapFromResource(Context context, ByteArrayInputStream imageStream,
			int reqWidth, int reqHeight) 
					throws FileNotFoundException {
		
		// First decode with inJustDecodeBounds=true to check dimensions
		final BitmapFactory.Options options = new BitmapFactory.Options();
	    options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
		// Decode bitmap with inSampleSize set
		options.inJustDecodeBounds = false;
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
}