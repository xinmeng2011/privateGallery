package com.mm.privategallery.view;

import com.mm.privategallery.model.BitmapCache;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.ImageView;

public class DecodeImageView extends ImageView implements BitmapCache.IDecodeInvoke{

	private String mPicPath;
	private Handler mUiHandler;
	public DecodeImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	
	public void setPicPath(String path, Handler uiHandler){
		Log.i("xinmeng_bitmap", "setPicPath" + path);
		mPicPath = path;
		mUiHandler = uiHandler;
		Bitmap bitmap= BitmapCache.getSingle().getSingleBitmapCache(this,mPicPath);
		if(bitmap != null){
			Log.i("xinmeng_bitmap", "setPicPath OK" + path);
			this.setImageBitmap(bitmap);
		}
	}

	@Override
	public boolean NotifyDecodeReady(String path) {
		Log.i("xinmeng_bitmap", path + "<==>" + mPicPath);
		if(path.equals(mPicPath)){
			Log.i("xinmeng_bitmap", "ImageView match" + mPicPath);
			final Bitmap bitmap = BitmapCache.getSingle().getSingleBitmapCache(this,mPicPath);
			
			mUiHandler.post(new Runnable() {				
				@Override
				public void run() {
					Log.i("xinmeng_bitmap", "ImageView got it" + mPicPath);
					DecodeImageView.this.setImageBitmap(bitmap);
				}
			});
			return true;
		}
		return false;
	}
}
