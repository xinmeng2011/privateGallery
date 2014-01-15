package com.mm.utility;

import android.content.Context;
import android.widget.Toast;

public class ToastUtility {

	static public void showToast(String str, Context ctx){
		Toast.makeText(ctx, str, Toast.LENGTH_SHORT).show();
	}
	
	static public void showToast(int strId, Context ctx){
		Toast.makeText(ctx, strId, Toast.LENGTH_SHORT).show();
	}
	
	static public void showLongToast(String str, Context ctx){
		Toast.makeText(ctx, str, Toast.LENGTH_LONG).show();
	}
	
	static public void showLongToast(int strId, Context ctx){
		Toast.makeText(ctx, strId, Toast.LENGTH_LONG).show();
	}
}
