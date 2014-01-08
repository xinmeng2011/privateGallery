package com.mm.privategallery.model;

import com.mm.utility.SDHelper;

import android.app.Application;
import android.content.Context;

public class PrivateGalleryApp extends Application{

	public static Context globalContext;
	@Override
	public void onCreate() {
		super.onCreate();
		globalContext = getApplicationContext();
		SDHelper.checkSDPrivateFolder();

	}
}
