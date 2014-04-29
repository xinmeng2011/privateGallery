package com.mm.privategallery.model;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import android.widget.Toast;

import com.mm.privategallery.dao.GalleryDao;
import com.mm.privategallery.dao.ImageInfo;
import com.mm.privategallery.dao.PrivateDao;
import com.mm.utility.BitmapUtility;
import com.mm.utility.DESHelper;
import com.mm.utility.SDHelper;

public class GalleryEngine {

	static private GalleryEngine self;
	private List<GalleryFolderDataItem> mPublicFolderDataItems;
	private Map<String, GalleryFolderDataItem> mPublicFolderHashMap = new LinkedHashMap<String, GalleryFolderDataItem>();
	
	private List<GalleryFolderDataItem> mPrivateFolderDataItems;
	private Map<String, GalleryFolderDataItem> mPrivateFolderHashMap = new LinkedHashMap<String, GalleryFolderDataItem>();
	
	public static GalleryEngine getSingle() {
		synchronized (GalleryEngine.class) {
			if (self == null) {
				self = new GalleryEngine();
			}
		}
		return self;
	}

	public List<GalleryFolderDataItem> getPrivateFolderList(){
		if(mPrivateFolderDataItems != null){
			return mPrivateFolderDataItems;
		}
		
		PrivateDao dataCenter = new PrivateDao();
		List<ImageInfo> infos = dataCenter.getAllPrivateImage();
		
		for (int i = 0; i < infos.size(); i++) {
			ImageInfo singleInfo = infos.get(i);
			String folderPathString= singleInfo.pathString.replace(singleInfo.infoString, "");
			if(mPrivateFolderHashMap.containsKey(folderPathString)){
				mPrivateFolderHashMap.get(folderPathString).mImageList.add(singleInfo);
			}else{
				GalleryFolderDataItem folderDataItem = new GalleryFolderDataItem();
				folderDataItem.mIsPrivate = true;
				folderDataItem.mFolderPath=folderPathString;
				folderDataItem.mFolderName=returnLastFolderName
						(singleInfo.pathString, singleInfo.infoString);
				folderDataItem.mImageList.add(singleInfo);
				mPrivateFolderHashMap.put(folderPathString, folderDataItem);
			}
		}
		mPrivateFolderDataItems = new ArrayList<GalleryFolderDataItem>();
		Iterator it = mPrivateFolderHashMap.keySet().iterator();
        while (it.hasNext()) {
            String key = it.next().toString();
            mPrivateFolderDataItems.add(mPrivateFolderHashMap.get(key));
        }
		return mPrivateFolderDataItems;
		
	}
	
	public List<GalleryFolderDataItem> getGalleryFolderList(){
		if(mPublicFolderDataItems != null){
			return mPublicFolderDataItems;
		}
		GalleryDao dataCenter = new GalleryDao(PrivateGalleryApp.globalContext);
		List<ImageInfo> infos =  dataCenter.getAllPictureInfo();
		
		for (int i = 0; i < infos.size(); i++) {
			ImageInfo singleInfo = infos.get(i);
			String folderPathString= singleInfo.pathString.replace(singleInfo.infoString, "");
			if(mPublicFolderHashMap.containsKey(folderPathString)){
				mPublicFolderHashMap.get(folderPathString).mImageList.add(singleInfo);
			}else{
				GalleryFolderDataItem folderDataItem = new GalleryFolderDataItem();
				folderDataItem.mFolderPath=folderPathString;
				folderDataItem.mFolderName=returnLastFolderName
						(singleInfo.pathString, singleInfo.infoString);
				folderDataItem.mImageList.add(singleInfo);
				mPublicFolderHashMap.put(folderPathString, folderDataItem);
			}
		}
		mPublicFolderDataItems = new ArrayList<GalleryFolderDataItem>();
		Iterator it = mPublicFolderHashMap.keySet().iterator();
		while (it.hasNext()) {
            String key = it.next().toString();
            mPublicFolderDataItems.add(mPublicFolderHashMap.get(key));
        }
		return mPublicFolderDataItems;
	}
	
	private String returnLastFolderName(String allPath, String name){
		String shortFolderNameString="";
		String pathString = allPath.replace(name, "");
		pathString = pathString.substring(0, pathString.length()-1);
		int pos = pathString.lastIndexOf("/");
		if(pos != -1){
			shortFolderNameString=pathString.substring(pos+1);
		}
		return shortFolderNameString;
	}
	
	public GalleryFolderDataItem getFolderItemFromPath(String path){
		if(mPublicFolderHashMap.containsKey(path)){
			return mPublicFolderHashMap.get(path);
		}else{
			return null;
		}
	}
	
	public void removeFoldFromFolderMap(String path){
		if(mPrivateFolderHashMap.containsKey(path)){
			mPrivateFolderHashMap.remove(path);
		}
	}
	
	public GalleryFolderDataItem getPrivateFolderItemFromPath(String path){
		if(mPrivateFolderHashMap.containsKey(path)){
			return mPrivateFolderHashMap.get(path);
		}else{
			return null;
		}
	}
	
	public boolean encryptImage(String path){
		// 读文件
		FileInputStream is;
		byte[] arraySrc = null;
		try {
			is = new FileInputStream(path);	
			arraySrc = new byte[is.available()];		
			is.read(arraySrc);
		    is.close();
		    byte[] arrayDes = DESHelper.encryptDESFile(arraySrc, BitmapUtility.passwordString);
		    String newFileNameString=String.valueOf(System.currentTimeMillis());
		    SDHelper.writeSDFile(newFileNameString, arrayDes);
		}
        catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	public boolean decryptImage(String path){
		// 读文件
		FileInputStream is;
		byte[] arraySrc = null;
		try {
			is = new FileInputStream(path);	
			arraySrc = new byte[is.available()];		
			is.read(arraySrc);
		    is.close();
		    byte[] arrayDes = DESHelper.decryptDESFile(arraySrc, BitmapUtility.passwordString);
		    String newFileNameString=String.valueOf(System.currentTimeMillis())+".jpg";
		    SDHelper.writeRecoverFile(newFileNameString, arrayDes);
		}
        catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	public void decryptImageAndDel(String path){
		if(decryptImage(path)){
			//clearPrivateData();
 			clearPublicData();
		}else{
			
		}
	}
	
	public boolean encryptImage2Folder(String path, String folder){
		// 读文件
		FileInputStream is;
		byte[] arraySrc = null;
		try {
			is = new FileInputStream(path);	
			arraySrc = new byte[is.available()];		
			is.read(arraySrc);
		    is.close();
		    byte[] arrayDes = DESHelper.encryptDESFile(arraySrc, BitmapUtility.passwordString);
		    String newFileNameString=String.valueOf(System.currentTimeMillis());
		    folder += newFileNameString;
		    SDHelper.writeSDFile(folder, arrayDes);
		}
        catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	public boolean encryptImageFileAndDelOld(String path){
		if(encryptImage(path)){
			SDHelper.deleteSinglePicture(PrivateGalleryApp.globalContext,path);
			clearPrivateData();
			return true;
		}else{
			return false;
		}
	}
	
	public boolean encryptImageFolderAndDelOld(String orgPath, String newFolder, List<String> images){
		SDHelper.createNewPrivateFolder(newFolder);
		String newPathString=newFolder;
		if(newPathString.charAt(newPathString.length()-1) != '/'){
			newPathString += '/';
			}
		for (int i = 0; i < images.size(); i++) {
			String targetString= images.get(i);
			encryptImage2Folder(targetString,newPathString);
		}
		clearPrivateData();
		return true;
	}
	
	private void clearPrivateData(){
		mPrivateFolderDataItems = null;
		mPrivateFolderHashMap.clear();
	}
	
	private void clearPublicData(){
		mPublicFolderDataItems = null;
		mPublicFolderHashMap.clear();
	}
}
