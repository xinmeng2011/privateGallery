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
		
		// ָ��Ҫ��ѯ��uri��Դ  
        Uri uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;  
        
        // ��ȡContentResolver  
        ContentResolver contentResolver = mContext.getContentResolver();  
        // ��ѯ���ֶ�  
        String[] projection = { MediaStore.Images.Media._ID,  
                MediaStore.Images.Media.DISPLAY_NAME,  
                MediaStore.Images.Media.DATA, MediaStore.Images.Media.SIZE };  
        // ����  
        String selection = MediaStore.Images.Media.MIME_TYPE + "=?";  
        // ����ֵ(�@�e�Ĳ�������ͼƬ�ĸ�ʽ�����Ǳ�׼�����в�Ҫ�Ķ�)  
        String[] selectionArgs = { "image/jpeg" };  
        // ����  
        String sortOrder = MediaStore.Images.Media.DATE_MODIFIED + " desc";  
        // ��ѯsd���ϵ�ͼƬ  
        Cursor cursor = contentResolver.query(uri, projection, null,  
                null, sortOrder);  
        if (cursor != null) {  
            cursor.moveToFirst();  
            ImageInfo singleImageInfo2 = new ImageInfo();
            //imageMap.put("imageID", cursor.getString(cursor  
             //       .getColumnIndex(MediaStore.Images.Media._ID)));  
            // ���ͼƬ��ʾ������  
            singleImageInfo2.infoString = cursor.getString(cursor  
                    .getColumnIndex(MediaStore.Images.Media.DISPLAY_NAME));
            do {   
            	ImageInfo singleImageInfo = new ImageInfo();
                //imageMap.put("imageID", cursor.getString(cursor  
                 //       .getColumnIndex(MediaStore.Images.Media._ID)));  
                // ���ͼƬ��ʾ������  
                singleImageInfo.infoString = cursor.getString(cursor  
                        .getColumnIndex(MediaStore.Images.Media.DISPLAY_NAME));
                //imageMap.put("imageName", cursor.getString(cursor  
                //        .getColumnIndex(MediaStore.Images.Media.DISPLAY_NAME)));  
                // ���ͼƬ����Ϣ  
//                imageMap.put(  
//                        "imageInfo",  
//                        ""  
//                                + cursor.getLong(cursor  
//                                        .getColumnIndex(MediaStore.Images.Media.SIZE) / 1024)  
//                                + "kb");  
//                // ���ͼƬ���ڵ�·��(����ʹ��·������URI)  
//                imageMap.put("data", cursor.getString(cursor  
//                        .getColumnIndex(MediaStore.Images.Media.DATA)));
                singleImageInfo.pathString = cursor.getString(cursor  
                    .getColumnIndex(MediaStore.Images.Media.DATA));
                if(SDHelper.isPrivateImage(singleImageInfo.pathString)){
                	continue;
                }
                infos.add(singleImageInfo);  
            }  while (cursor.moveToNext());
            // �ر�cursor  
            cursor.close();  
        }  
        return infos;  
	}
}
