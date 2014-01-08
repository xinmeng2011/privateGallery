package com.mm.privategallery.controler;

import com.mm.privategallery.R;
import com.mm.utility.BitmapUtility;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.widget.ImageView;

public class SinglePictureViewActivity extends Activity {

	private ImageView mImageView;
	public static final String SINGLE_IMAGE_PATH="SINGLE_IMAGE_PATH"; 
	private String mSingleImagePath;
	private Bitmap mBigBitmap;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		Intent it = getIntent();
		if(it == null){
			return;
		}
		mSingleImagePath = it.getStringExtra(SINGLE_IMAGE_PATH);
	}

	@Override
	protected void onStart() {
		super.onStart();
		initUI();
		initBitmap();
	}

	@Override
	protected void onStop() {
		super.onStop();
		destoryBitmap();
	}

	private void initUI(){
		setContentView(R.layout.activity_single_pic);
		mImageView = (ImageView)findViewById(R.id.single_image);
	}
	
	private void initBitmap(){
		mBigBitmap = BitmapUtility.getOriginBitmap(mSingleImagePath);
        //将图片显示到ImageView中  
        mImageView.setImageBitmap(mBigBitmap); 
	}
	
	private void destoryBitmap(){
		if(mBigBitmap != null){
			mBigBitmap.recycle();
		}
	}
}
