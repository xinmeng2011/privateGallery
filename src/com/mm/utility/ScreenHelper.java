package com.mm.utility;

import android.content.Context;

public class ScreenHelper {

	public static float density=0;
	public static float getDesity(Context context){
		if (density == 0) {
			// 根据GOOGLE IO文档中提到的，对density作缓存能起来省电的作用
			density = context.getResources().getDisplayMetrics().density;
		}
		return density;
	}
	
//	public static int getScreenWidth(Context context){
//		int width  = 0;
//		width = context.getResources().getDefaultDisplay().getWidth();
//		return width;
//	}
//	
//	public static int getScreenHeight(){
//		int height = 0;
//		return height;
//	}
}
