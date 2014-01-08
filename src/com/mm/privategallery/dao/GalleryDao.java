package com.mm.privategallery.dao;

import java.util.ArrayList;
import java.util.List;

import com.mm.utility.SDHelper;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;

public class GalleryDao {

	private Context mContext;
	
	public GalleryDao(Context context){
		mContext = context;
	}
	
	public List<ImageInfo> getAllPictureInfo(){
		ArrayList<ImageInfo> infos = new ArrayList<ImageInfo>();
		
		// 指定要查询的uri资源  
        Uri uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;  
        
        // 获取ContentResolver  
        ContentResolver contentResolver = mContext.getContentResolver();  
        // 查询的字段  
        String[] projection = { MediaStore.Images.Media._ID,  
                MediaStore.Images.Media.DISPLAY_NAME,  
                MediaStore.Images.Media.DATA, MediaStore.Images.Media.SIZE };  
        // 条件  
        String selection = MediaStore.Images.Media.MIME_TYPE + "=?";  
        // 条件值(這裡的参数不是图片的格式，而是标准，所有不要改动)  
        String[] selectionArgs = { "image/jpeg" };  
        // 排序  
        String sortOrder = MediaStore.Images.Media.DATE_MODIFIED + " desc";  
        // 查询sd卡上的图片  
        Cursor cursor = contentResolver.query(uri, projection, null,  
                null, sortOrder);  
        if (cursor != null) {  
            cursor.moveToFirst();  
            ImageInfo singleImageInfo2 = new ImageInfo();
            //imageMap.put("imageID", cursor.getString(cursor  
             //       .getColumnIndex(MediaStore.Images.Media._ID)));  
            // 获得图片显示的名称  
            singleImageInfo2.infoString = cursor.getString(cursor  
                    .getColumnIndex(MediaStore.Images.Media.DISPLAY_NAME));
            do {   
            	ImageInfo singleImageInfo = new ImageInfo();
                //imageMap.put("imageID", cursor.getString(cursor  
                 //       .getColumnIndex(MediaStore.Images.Media._ID)));  
                // 获得图片显示的名称  
                singleImageInfo.infoString = cursor.getString(cursor  
                        .getColumnIndex(MediaStore.Images.Media.DISPLAY_NAME));
                //imageMap.put("imageName", cursor.getString(cursor  
                //        .getColumnIndex(MediaStore.Images.Media.DISPLAY_NAME)));  
                // 获得图片的信息  
//                imageMap.put(  
//                        "imageInfo",  
//                        ""  
//                                + cursor.getLong(cursor  
//                                        .getColumnIndex(MediaStore.Images.Media.SIZE) / 1024)  
//                                + "kb");  
//                // 获得图片所在的路径(可以使用路径构建URI)  
//                imageMap.put("data", cursor.getString(cursor  
//                        .getColumnIndex(MediaStore.Images.Media.DATA)));
                singleImageInfo.pathString = cursor.getString(cursor  
                    .getColumnIndex(MediaStore.Images.Media.DATA));
                if(SDHelper.isPrivateImage(singleImageInfo.pathString)){
                	continue;
                }
                infos.add(singleImageInfo);  
            }  while (cursor.moveToNext());
            // 关闭cursor  
            cursor.close();  
        }  
        return infos;  
	}
}
