package com.dna.radius.businessmode;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.FragmentActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;

import com.dna.radius.R;
import com.dna.radius.infrastructure.BaseActivity;

public class BusinessChooseImageDialogActivity extends  BaseActivity{
	
	private final static int RESULT_LOAD_IMAGE_GALLERY = 1;
	private final static int RESULT_LOAD_IMAGE_CAMERA = 2;
	
	private static final int IMAGE_HEIGHT = 360;
	private static final int IMAGE_WIDTH = 480;
	
	private ImageView imageView;
	
	private Bitmap currentBitmap = null;

	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.business_choose_image_dialog_activity);
		
		setScreenSize();
		

		Button cameraButton = (Button)findViewById(R.id.camera_button);
		Button galleryButton = (Button)findViewById(R.id.gallery_button);
		Button applyImageButton =  (Button)findViewById(R.id.apply_image_button);
		Button cancelButton =  (Button)findViewById(R.id.cancel_image_button);
		
		imageView = (ImageView)findViewById(R.id.business_choose_image_view);

		cameraButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
				if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
					startActivityForResult(takePictureIntent, RESULT_LOAD_IMAGE_CAMERA);
				}

			}
		});

		galleryButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent i = new Intent(
						Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
				startActivityForResult(i, RESULT_LOAD_IMAGE_GALLERY);				
			}
		});
		
		applyImageButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(currentBitmap == null){
					createAlertDialog(getResources().getString(R.string.apply_image_error));
				}else{
					Intent result = new Intent();
					
					ByteArrayOutputStream stream = new ByteArrayOutputStream();
					currentBitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
					byte[] byteArray = stream.toByteArray();
					
					result.putExtra("data", byteArray);
					setResult(RESULT_OK, result);
					finish();
				}
			}
		});
		
		cancelButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				Intent result = new Intent();
				setResult(RESULT_CANCELED, result);
				finish();
			}
		});
	}
	
	
	private void setScreenSize() {
		// This will set this dialog-themed activity to take 80% of the screen
		DisplayMetrics metrics = getResources().getDisplayMetrics();
		int screenWidth = (int) (metrics.widthPixels * 0.9);
		int screenHeight = (int) (metrics.heightPixels * 0.8);
		getWindow().setLayout(screenWidth, screenHeight);
	}


	/**
	 * currently used for handling an image which was picked from the gallery by the user, or
	 * an image which was took by the camera.
	 */
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		Bitmap bMap = null;
		
		/**receives an image from the gallery, and change the image of the business*/
		if (requestCode == RESULT_LOAD_IMAGE_GALLERY && resultCode == FragmentActivity.RESULT_OK && null != data) {
			
			Uri selectedImage = data.getData();
			if (selectedImage == null) return; //User pressed 'back'
			
			String[] filePathColumn = { MediaStore.Images.Media.DATA };

			Cursor cursor = getContentResolver().query(selectedImage,
					filePathColumn, null, null, null);
			cursor.moveToFirst();

			int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
			String picturePath = cursor.getString(columnIndex);
			cursor.close();

			bMap = BitmapFactory.decodeFile(picturePath);
			
			//rotation if needed
			ExifInterface exif;
			try {
				exif = new ExifInterface(picturePath);
				int ImageOrientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, 1);
				Matrix matrix = new Matrix();
				
				if(ImageOrientation==ExifInterface.ORIENTATION_ROTATE_90){
					matrix.postRotate(90);
				}
				else if(ImageOrientation==ExifInterface.ORIENTATION_ROTATE_180){
					matrix.postRotate(180);
				}
				else if(ImageOrientation==ExifInterface.ORIENTATION_ROTATE_270){
					matrix.postRotate(270);
				}
				
				bMap = Bitmap.createBitmap(bMap, 0, 0, bMap.getWidth(), bMap.getHeight(), matrix, true);
			} catch (IOException e) {
				e.printStackTrace();
			}
			
		}
		
		else if (requestCode == RESULT_LOAD_IMAGE_CAMERA  && resultCode == FragmentActivity.RESULT_OK) {
			
			Bundle extras = data.getExtras();
			bMap = (Bitmap) extras.get("data");

		}else{
			Log.e("BusinessChooseImageFragment", "returned from unknown activity");
			return;
		}

		if (bMap != null) {
			bMap = processImage(bMap);
			imageView.setImageBitmap(bMap);
			currentBitmap = bMap;
			
		}else{
			Log.e("BusinessChooseImageFragment", "returned bitmap is null");
		}
	}
	
	
	/**
	 * downsamples the image to size IMAGE_HEIGHTxIMAGE_WIDTH,
	 * also, performs jpeg comression.
	 * @return
	 */
	public static Bitmap processImage(Bitmap bitmap){
		
		//downsampling
		double proportion = (double)bitmap.getHeight() / (double)IMAGE_HEIGHT;
		int newImageHeight = IMAGE_HEIGHT;
		int newImageWidth= (int)((double)bitmap.getWidth() / proportion);
		Bitmap resizedBitmap = Bitmap.createScaledBitmap(bitmap, newImageWidth, newImageHeight, true);
		
		//crops from the center
		Bitmap croppedBitmap = ThumbnailUtils.extractThumbnail(resizedBitmap, IMAGE_WIDTH, IMAGE_HEIGHT);
		
		Log.d("BusinessChooseImage","returned a new image, width,height=" +croppedBitmap.getWidth() + "," +  croppedBitmap.getHeight() );
		Log.d("BusinessChooseImage","original size: width,height=" +bitmap.getWidth() + "," +  bitmap.getHeight() );
		
		return croppedBitmap;
	}
}
