package com.mm.utility;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.channels.FileChannel;

import com.mm.privategallery.model.PrivateGalleryApp;

import android.content.Context;
import android.content.res.AssetManager;
import android.nfc.Tag;
import android.os.Environment;
import android.os.StatFs;
import android.util.Log;


public final class FileUtil {
	public static boolean isFileExist(final String path) {
		File file = new File(path);
		return file.exists();
	}
	public static int getFileSize(String path) {
		if (null == path) {
			return 0;
		}
		try {
			return (int) new File(path).length();
		} catch (Throwable t) {
			return 0;
		}
	}
	public static String getDataDirPath(){	
		StringBuffer sb = new StringBuffer();
		sb.append(getInternalDir());
		sb.append("/");
		return sb.toString();
	}
	
	public static File getInternalDir() {
		
		StringBuffer sb = new StringBuffer();
		sb.append(getInternal());
		sb.append("/files");
		return new File(sb.toString());
	}
	
	public static String getInternal() {
		String packageName = PrivateGalleryApp.globalContext
				.getPackageName();
		StringBuffer sb = new StringBuffer();
		sb.append("/data/data/");
		sb.append(packageName);
	
		return sb.toString();
	}
}
