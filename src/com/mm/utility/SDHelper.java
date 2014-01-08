package com.mm.utility;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import com.mm.privategallery.R.string;
import com.mm.privategallery.model.PrivateGalleryApp;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;

public class SDHelper {
	
	 static String mPrivatePath = "/cartman_mm/";
	 static String mPublicPath = "/kyle_mm/";
	 
	public static boolean isPrivateImage(String path){
		return path.contains(mPrivatePath);
	}
	 
    public static boolean hasStorage(boolean requireWriteAccess) {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            if (requireWriteAccess) {
                boolean writable = checkFsWritable();
                return writable;
            } else {
                return true;
            }
        } else if (!requireWriteAccess && Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
            return true;
        }
        return false;
    }
    
    private static boolean checkFsWritable() {
        // Create a temporary file to see whether a volume is really writeable.
        // It's important not to put it in the root directory which may have a
        // limit on the number of files.
        String directoryName = Environment.getExternalStorageDirectory().toString() + "/DCIM";
        File directory = new File(directoryName);
        if (!directory.isDirectory()) {
            if (!directory.mkdirs()) {
                return false;
            }
        }
        File f = new File(directoryName, ".probe");
        try {
            // Remove stale file if any
            if (f.exists()) {
                f.delete();
            }
            if (!f.createNewFile()) {
                return false;
            }
            f.delete();
            return true;
        } catch (IOException ex) {
            return false;
        }
    }

    public static void allScan(Context context) {  
        context.sendBroadcast(new Intent(  
                Intent.ACTION_MEDIA_MOUNTED,  
                Uri.parse("file://" + Environment.getExternalStorageDirectory())));  
    }  

	public static boolean writeSDFile(File file, byte[] buffer, boolean append)
			throws IOException {		
		
		if(file == null || buffer == null)
			return false;
		FileOutputStream fout;
		// 文件不存在，要先创建文件，然后写入数据
		if (!file.exists()) {
			file.getParentFile().mkdirs();
			file.createNewFile();
		}
		if (!file.canWrite()) {
			return false;
		}
		fout = new FileOutputStream(file, append);
		try {
			fout.write(buffer);
			fout.flush();
		} finally{
			fout.close();
		}
		return true;
	}
	
	public static String getDataSpacePath(){
		return getSDPath() + mPrivatePath;
	}
    
	public static String getDataPublicPath(){
		return getSDPath() + mPublicPath;
	}
	
	public static void writeSDFile(String filename, byte[] data){
		String fileFillPathString = getSDPath() + mPrivatePath +filename;
		File file= new File(fileFillPathString);
		try {
			writeSDFile(file, data, false);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void writeRecoverFile(String filename, byte[] data){
		String fileFillPathString = getSDPath() + mPublicPath +filename;
		File file= new File(fileFillPathString);
		try {
			writeSDFile(file, data, false);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void writeSDFileToFolder(String filename, String folder, byte[] data){
		String fileFillPathString = folder +filename;
		File file= new File(fileFillPathString);
		try {
			writeSDFile(file, data, false);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static String createNewPrivateFolder(String folderName){
		String folderPathString=getSDPath() + mPrivatePath +folderName;
		if(folderPathString.charAt(folderPathString.length()-1) != '/'){
		   folderPathString += '/';
		}
		File path = new File(folderPathString);
		if(!path.exists()){
			path.mkdir();
		}
		return folderPathString;
	}
	
	private static String getSDPath(){
		return Environment.getExternalStorageDirectory().getPath();
	}
	
	public static void checkSDPrivateFolder(){
		String folderPathString = getSDPath() + mPrivatePath;
		File path = new File(folderPathString);
		if(!path.exists()){
			path.mkdir();
		}
		
		String folderPathString2 = getDataPublicPath();
		path= new File(folderPathString2);
		if(!path.exists()){
			path.mkdir();
		}
	}
	
	private static void createFolderUnderPrivate(String folderName){
		String folderPathString = getSDPath() + mPrivatePath;
		checkSDPrivateFolder();
		String newFolder= folderPathString + folderName + "/";
		File path = new File(newFolder);
		if(!path.exists()){
			path.mkdir();
		}
	}
	
	public static void sendbroadcastScanSD(String path){
		Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);     
		 Uri uri = Uri.fromFile(new File(path));     
		 intent.setData(uri);     
		 PrivateGalleryApp.globalContext.sendBroadcast(intent);    
	}
	
	 public static void deleteFile(File file) {
	        if (file.exists()) { // 判断文件是否存在
	            if (file.isFile()) { // 判断是否是文件
	                file.delete(); // delete()方法 你应该知道 是删除的意思;
	            } else if (file.isDirectory()) { // 否则如果它是一个目录
	                File files[] = file.listFiles(); // 声明目录下所有的文件 files[];
	                for (int i = 0; i < files.length; i++) { // 遍历目录下所有的文件
	                    deleteFile(files[i]); // 把每个文件 用这个方法进行迭代
	                }
	            }
	            file.delete();
	        }
	    }
}
