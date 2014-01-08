package com.mm.privategallery.model;

import java.util.ArrayList;
import java.util.List;

import com.mm.privategallery.dao.ImageInfo;

public class GalleryFolderDataItem {

	public String mFolderName;
	public String mFolderPath;
	public List<ImageInfo> mImageList = new ArrayList<ImageInfo>();
	public boolean mIsPrivate = false;
	public boolean isSelected=false;
}
