package com.mm.utility;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;

import android.R.integer;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.media.ExifInterface;



public class BitmapUtility {
	
	private final static int PIXEL_SIZE_IMAGE = 200;
	
	private final static int PIXEL_JOINT_IMAGE_MARGIN = 10;
	static public String passwordString= "cartman1";
	
	static public Bitmap getDecodeBitmapWithArg(String bitmapPath){
	     BitmapFactory.Options options = new BitmapFactory.Options();
	     options.inJustDecodeBounds = true;
	        // 获取这个图片的宽和高
	     Bitmap bitmap = BitmapFactory.decodeFile(bitmapPath, options); //此时返回bm为空
	     options.inJustDecodeBounds = false;
	         //计算缩放比
	     int compareSide= options.outHeight>options.outWidth?options.outHeight:options.outWidth;
	     int be = (int)(compareSide / (float)PIXEL_SIZE_IMAGE);
	     if (be <= 0)
            be = 1;
	     int newWidth =options.outWidth;
	     int newHeight = options.outHeight;

	     if(be>1){
		     if(options.outHeight>options.outWidth){
		    	 newWidth = options.outWidth/be;
		     }else{
		    	 newHeight = options.outHeight/be;
		     }
	     }
	     options.inSampleSize = be;
	     bitmap=BitmapFactory.decodeFile(bitmapPath,options);
	     int degrees = getDegrees(bitmapPath);
	     Bitmap bmp= rotate(bitmap, degrees, newWidth, newHeight);
	     //bitmap.recycle();
	     //重新读入图片，注意这次要把options.inJustDecodeBounds 设为 false哦
	     return bmp;
	}
	
	static public Bitmap getDecodePrivateBitmap(String bitmapPath){
		
		FileInputStream is;
		byte[] arraySrc = null;
		byte[] arrayDes = null;
		try {
			is = new FileInputStream(bitmapPath);	
			arraySrc = new byte[is.available()];		
			is.read(arraySrc);
		    is.close();
		    arrayDes = DESHelper.decryptDESFile(arraySrc, passwordString);
		}
        catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
		
	     BitmapFactory.Options options = new BitmapFactory.Options();
	     options.inJustDecodeBounds = true;
	        // 获取这个图片的宽和高
	     int size= arrayDes.length;
	     Bitmap bitmap = BitmapFactory.decodeByteArray(arrayDes,0,arrayDes.length,options); //此时返回bm为空
	     options.inJustDecodeBounds = false;
	         //计算缩放比
	     int compareSide= options.outHeight>options.outWidth?options.outHeight:options.outWidth;
	     int be = (int)(compareSide / (float)PIXEL_SIZE_IMAGE);
	     if (be <= 0)
           be = 1;
	     int newWidth =options.outWidth;
	     int newHeight = options.outHeight;

	     if(be>1){
		     if(options.outHeight>options.outWidth){
		    	 newWidth = options.outWidth/be;
		     }else{
		    	 newHeight = options.outHeight/be;
		     }
	     }
	     options.inSampleSize = be;
	     bitmap=BitmapFactory.decodeByteArray(arrayDes,0,arrayDes.length,options);
	     int degrees = getDegrees(bitmapPath);
	     Bitmap bmp= rotate(bitmap, degrees, newWidth, newHeight);
	     //bitmap.recycle();
	     //重新读入图片，注意这次要把options.inJustDecodeBounds 设为 false哦
	     return bmp;
	}
	
	static private int getDegrees(String path){
		ExifInterface exifInterface;
		try {
			exifInterface = new ExifInterface(path);
			int tag = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, -1);
			int degree = 0;
			if (tag == ExifInterface.ORIENTATION_ROTATE_90) {
				degree = 90;
			} else if (tag == ExifInterface.ORIENTATION_ROTATE_180) {
				degree = 180;
			} else if (tag == ExifInterface.ORIENTATION_ROTATE_270) {
				degree = 270;
			}
			return degree;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return 0;
		}
	}
	
	static public Bitmap getJointBitmap4Rooms(List<String> imagePaths, boolean isPrivate){
		    int imagesize = PIXEL_SIZE_IMAGE*2 + PIXEL_JOINT_IMAGE_MARGIN;
			Bitmap result = Bitmap.createBitmap(imagesize, imagesize, Config.ARGB_8888);
			Canvas canvas = new Canvas(result);
			
			for(int i=0; i<4; i++){
				if(imagePaths.size()> i){
					String pathString = imagePaths.get(i);
					Bitmap bitmap = null;
					if(!isPrivate){
						bitmap=getDecodeBitmapWithArg(pathString);
					}else{
						bitmap=getDecodePrivateBitmap(pathString);
					}
					if(bitmap == null){
						continue;
					}
					int left = i%2 * (PIXEL_SIZE_IMAGE + PIXEL_JOINT_IMAGE_MARGIN);
					int top = i/2*(PIXEL_SIZE_IMAGE + PIXEL_JOINT_IMAGE_MARGIN);
					Rect desRect = new Rect(left, top, left+PIXEL_SIZE_IMAGE, top+PIXEL_SIZE_IMAGE);
					Rect srcRect =new Rect(0,0,bitmap.getWidth(), bitmap.getHeight());
					int srcLeft=0,srcTop=0;
					int srcRight = bitmap.getWidth();
					int srcBottom = bitmap.getHeight();
					int srcWidth= bitmap.getWidth();
					int srcHeight = bitmap.getHeight();
					
					if(bitmap.getWidth()>PIXEL_SIZE_IMAGE){
					   int off = (bitmap.getWidth() - PIXEL_SIZE_IMAGE)/2;
					   srcLeft = off;
					   srcRight = off + PIXEL_SIZE_IMAGE;
					   srcWidth = PIXEL_SIZE_IMAGE;
					}
					if(bitmap.getHeight()>PIXEL_SIZE_IMAGE){
						int off = (bitmap.getHeight() - PIXEL_SIZE_IMAGE)/2;
						srcTop = off;
						srcBottom = off + PIXEL_SIZE_IMAGE;
						srcHeight = PIXEL_SIZE_IMAGE;
					}
					srcRect.set(srcLeft, srcTop, srcRight, srcBottom);
					int leftOff = (PIXEL_SIZE_IMAGE - srcWidth)/2;
					int topOff = (PIXEL_SIZE_IMAGE - srcHeight)/2;
					desRect.set(left + leftOff, top+ topOff,left+leftOff+srcWidth, top +srcHeight+topOff);
					canvas.drawBitmap(bitmap, srcRect, desRect, null);
					bitmap.recycle();
				}
			}
			return result;
	}

	public static Bitmap rotate(Bitmap b, int degrees , int widthNew , int heightNew) {
        if (degrees != 0 && b != null) {
            Matrix m = new Matrix();
            m.setRotate(degrees,
                    (float) widthNew, (float) heightNew);
            try {
                Bitmap b2 = Bitmap.createBitmap(
                        b, 0, 0, b.getWidth(), b.getHeight(), m, true);
                if (b != b2) {
                    b.recycle();  //Android开发网再次提示Bitmap操作完应该显示的释放
                    b = b2;
                }
            } catch (OutOfMemoryError ex) {
                // Android123建议大家如何出现了内存不足异常，最好return 原始的bitmap对象。.
            }
        }
        return b;
    }
	
	public static Bitmap getOriginBitmap(String pathString){
		// first make sure the sample 
		int sample=1;
		BitmapFactory.Options op = new BitmapFactory.Options();
		op.inJustDecodeBounds = true;
		
		BitmapFactory.decodeFile(pathString, op);
		
		if(op.outHeight* op.outWidth > 500*500){
			sample = (op.outHeight* op.outWidth)/(500*500);
		}
		op.inJustDecodeBounds = false;
		op.inSampleSize = sample;
		
		int degree = getDegrees(pathString);
		if(degree == 0){
			return BitmapFactory.decodeFile(pathString,op);
		}
	    Bitmap srcBitmap = BitmapFactory.decodeFile(pathString,op);  
	    if(srcBitmap == null){
	    	return null;
	    }
        Matrix m = new Matrix();
        m.setRotate(degree,
                (float) srcBitmap.getWidth(), (float)srcBitmap.getHeight());
        try {
            Bitmap b2 = Bitmap.createBitmap(
                    srcBitmap, 0, 0, srcBitmap.getWidth(), srcBitmap.getHeight(), m, true);
            if(b2 != srcBitmap){
            	srcBitmap.recycle();
            }
            return b2;
        } catch (OutOfMemoryError ex) {
            // Android123建议大家如何出现了内存不足异常，最好return 原始的bitmap对象。.
        }
        return null;
	}
}
