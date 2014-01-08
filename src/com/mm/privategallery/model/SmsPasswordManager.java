package com.mm.privategallery.model;



import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import com.mm.utility.FileUtil;
import com.mm.utility.MD5Util;

import android.util.Log;


/**
 * <pre>
 * Copyright (C) 1998-2013 TENCENT Inc.All Rights Reserved.
 *
 * Description£º
 * 
 * History£º
 * 
 * User				Date			Info		Reason
 * simonliao		2013-6-18		Create		
 * </pre>
 */
public class SmsPasswordManager {
	private final static String TAG = "SmsPasswordManager";
	
	private static SmsPasswordManager sInstance = new SmsPasswordManager();
	
	private final static String PWD_PREFIX = "com.tencent.pb.";
	
	public final static String PWD_FILE_NAME = "key.dat";
	public static final String BK_FILE_NAME = "pbsjkk";
	
	private String mPwdPath = "";
	
	private String mPassword ;

	public static SmsPasswordManager getInstance() {
		return sInstance;
	}

	private SmsPasswordManager() {
		mPwdPath = FileUtil.getDataDirPath() + PWD_FILE_NAME;

		String fileDir = mPwdPath.substring(0, mPwdPath.lastIndexOf("/"));
		File fileDirFile = new File(fileDir);
		if (!fileDirFile.exists()) {
				fileDirFile.mkdirs();
		}
		
		mPassword = readFromFile();
	}
	
	private void saveToFile(String password) {
		if (password == null) {
			password = "";
		}

		FileOutputStream fos = null;
		ObjectOutputStream dos = null;
		try {
			fos = new FileOutputStream(mPwdPath, false);
			dos = new ObjectOutputStream(fos);
			byte[] buffer = password.getBytes();
			dos.write(buffer);
		} catch (Throwable e) {
		} finally {
			if (null != dos) {
				try {
					dos.close();
				} catch (IOException e) {
				}
			}

			if (null != fos) {
				try {
					fos.close();
				} catch (IOException e) {
				}
			}
		}

		Log.d("simon", "saved key.dat");
	}

	private String readFromFile() {
		String password = "";
		FileInputStream fis = null;
		ObjectInputStream dis = null;

		if (!FileUtil.isFileExist(mPwdPath)) {
			Log.d("simon", "key.dat file not exist");
			return password;
		}

		try {
			int fileSize = FileUtil.getFileSize(mPwdPath);
			fis = new FileInputStream(mPwdPath);
			dis = new ObjectInputStream(fis);
			byte[] buffer = new byte[fileSize];
			int count = dis.read(buffer);
			password = new String(buffer, 0, count);
		} catch (Throwable e) {
			Log.w("simon", "key.dat read exception");
			password = "";
		} finally {
			if (null != dis) {
				try {
					dis.close();
				} catch (IOException e) {
					Log.w("simon", "key.dat close exception1");
				}
			}

			if (null != fis) {
				try {
					fis.close();
				} catch (IOException e) {
					Log.w("simon", "key.dat close exception2");
				}
			}
		}

		return password;
	}

	private void setPassword(String password){
		if(password == null){
			password = "";
		}
		mPassword = password;
		saveToFile(password);
	}
	
	private String getPassword(){
		if(mPassword == null){
			return "";
		}
		
		return mPassword;
	}
	
	public void updatePassword(String password){
		String ret = md5encode(password);
		Log.d(TAG, "set new password:" + ret);
		setPassword(ret);
	}
	
	public boolean isPasswordCorrect(String password){
		String ret = md5encode(password);
		if(getPassword().equals(ret)){
			return true;
		}
		
		return false;
	}
	
	private String md5encode(String password){
		if (password == null || password.length() == 0) {
			return "";
		}
		
		password = PWD_PREFIX + password;
		String ret = MD5Util.MD5Encode(password.getBytes());
		return ret;
	}
	
	public boolean hasPassword(){
		if(getPassword().length() > 0){
			return true;
		}
		
		return false;
	}
	
	public void clearPassword(){
		setPassword("");
	}
	
}

