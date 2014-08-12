package com.example.mapsample;

import android.app.Fragment;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

public class DealPresentorFragment extends Fragment{
	Bitmap Image;
	public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.deal_presantor_fragment,container, false);
		ImageView imageView = (ImageView) view.findViewById(R.id.business_image_view);
		imageView.setImageBitmap(Image);
	   return view;
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		//TODO load images and data here
		Image = BitmapFactory.decodeResource(getResources(), R.drawable.burger);
	}
}
