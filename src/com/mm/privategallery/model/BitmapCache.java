package com.mm.privategallery.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.mm.utility.BitmapUtility;

import android.graphics.Bitmap;

public class BitmapCache {
	
	private static BitmapCache self;
	
	private static Map<String, Bitmap> mBitmapCacheMap= new HashMap<String, Bitmap>();
	public static BitmapCache getSingle() {
		synchronized (BitmapCache.class) {
			if (self == null) {
				self = new BitmapCache();
			}
		}
		return self;
	}

	public Bitmap getFolderCoverBitmapCache(String path, boolean isPrivate){
		if(mBitmapCacheMap.containsKey(path)){
			return mBitmapCacheMap.get(path);
		}else{
			GalleryFolderDataItem item ;
			if(!isPrivate){
				item= GalleryEngine.getSingle().getFolderItemFromPath(path);
			}else{
				item= GalleryEngine.getSingle().getPrivateFolderItemFromPath(path);
			}
			if(item == null){
				return null;
			}
			List<String> pathArrayList = new ArrayList<String>();
			for (int i = 0; i < item.mImageList.size(); i++) {
				pathArrayList.add(item.mImageList.get(i).pathString);
			}
			Bitmap bitmap = BitmapUtility.getJointBitmap4Rooms(pathArrayList,isPrivate);
			mBitmapCacheMap.put(path, bitmap);
			return bitmap;
		}
	}
	
	public List<Bitmap> getFolderBitmap2Cache(String path , boolean isPrivate){
		GalleryFolderDataItem item ;
		if(!isPrivate){
			item= GalleryEngine.getSingle().getFolderItemFromPath(path);
		}else{
			item= GalleryEngine.getSingle().getPrivateFolderItemFromPath(path);
		}
		if(item == null){
			return null;
		}
		
		List<Bitmap> resultsBitmaps= new ArrayList<Bitmap>();
		for(int i = 0; i< item.mImageList.size(); i++ ){
			String pathItemString= item.mImageList.get(i).pathString;
			Bitmap bitmap = null;
			if(mBitmapCacheMap.containsKey(pathItemString)){
				bitmap = mBitmapCacheMap.get(pathItemString);
			}else{
				if(!isPrivate){
					bitmap =  BitmapUtility.getDecodeBitmapWithArg(pathItemString);
				}else{
					bitmap = BitmapUtility.getDecodePrivateBitmap(pathItemString);
				}
				mBitmapCacheMap.put(pathItemString, bitmap);
			}
			resultsBitmaps.add(bitmap);
			if(i==2){
				break;
			}
		}
		return resultsBitmaps;
	}
	
	public Bitmap getSingleBitmapCache(String path){
		if(mBitmapCacheMap.containsKey(path)){
			return mBitmapCacheMap.get(path);
		}else{
			Bitmap bitmap =  BitmapUtility.getDecodeBitmapWithArg(path);
			mBitmapCacheMap.put(path, bitmap);
			return bitmap;
		}
	}
	
}
