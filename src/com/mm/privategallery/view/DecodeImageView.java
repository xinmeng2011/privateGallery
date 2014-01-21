package com.mm.privategallery.view;

import com.mm.privategallery.model.BitmapCache;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.AttributeSet;
import android.widget.ImageView;

public class DecodeImageView extends ImageView implements BitmapCache.IDecodeInvoke{

	private String mPicPath;
	public DecodeImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}
	
	public void setPicPath(String path){
		mPicPath = path;
		Bitmap bitmap= BitmapCache.getSingle().getSingleBitmapCache(mPicPath);
		if(bitmap != null){
			this.setImageBitmap(bitmap);
		}
	}

	@Override
	public void NotifyDecodeReady(String path) {
		if(path.equals(mPicPath)){
			Bitmap bitmap = BitmapCache.getSingle().getSingleBitmapCache(mPicPath);
			this.setImageBitmap(bitmap);
		}
	}
}
