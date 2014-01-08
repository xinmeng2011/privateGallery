package com.mm.privategallery.dao;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.mm.utility.SDHelper;

import android.text.StaticLayout;

public class PrivateDao {

	public List<ImageInfo> getAllPrivateImage(){
		List<String> allFilesList = new ArrayList<String>();
		getAllFiles(SDHelper.getDataSpacePath(), allFilesList);
		List<ImageInfo> resultsImageInfos = new ArrayList<ImageInfo>();
		for (int i = 0; i < allFilesList.size(); i++) {
			ImageInfo one = new ImageInfo();
			one.isPrivate = true;
			one.pathString = allFilesList.get(i);
			one.infoString = "";
			int pos= one.pathString.lastIndexOf("/");
			one.infoString = one.pathString.substring(pos+1, one.pathString.length());
			resultsImageInfos.add(one);
		}
		return resultsImageInfos;
	}
	
	private void getAllFiles(String path, List<String> pathList){  
        
		File root = new File(path);
		
        File files[] = root.listFiles();  
        if(files != null)  
        for(File f:files){  
            if(f.isDirectory()){  
                getAllFiles(f.getPath(),pathList);  
            }  
            else{  
                pathList.add(f.getPath());  
            }  
        }  
    }  
}
