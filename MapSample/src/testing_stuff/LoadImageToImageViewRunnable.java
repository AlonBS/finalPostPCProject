package testing_stuff;

import java.lang.ref.WeakReference;

import com.dna.radius.R;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.widget.ImageView;

public class LoadImageToImageViewRunnable  implements Runnable {
	private final WeakReference<ImageView> imageViewRef;
	private long businessID;
	private Context context;
	
	public LoadImageToImageViewRunnable(ImageView imageView,long businessID,Context context) {
		// Use a WeakReference to ensure the ImageView can be garbage collected
		this.imageViewRef = new WeakReference<ImageView>(imageView);
		this.businessID = businessID;
		this.context = context;
	}

	@Override
	public void run() {
		//TODO ALON this value should be loaded from parse, using the businessID.
		boolean imageExists = true; 
		
		//if the business doesn't have an image - returns.
        if(!imageExists){
        	return;
        }
        //TODO ALON the image should be loaded from parse, using the businessID.
        Bitmap bMap = BitmapFactory.decodeResource(context.getResources(),R.drawable.burger); 
        
        if (imageViewRef != null && bMap != null) {
            final ImageView imageView = imageViewRef.get();
            if (imageView != null) {
                imageView.setImageBitmap(bMap);
            }
        }
        
	}
	

	
}
