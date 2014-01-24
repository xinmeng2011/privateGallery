package com.mm.privategallery.controler;

import java.security.PublicKey;
import java.util.ArrayList;
import java.util.List;

import com.mm.privategallery.R;
import com.mm.privategallery.controler.GalleryFolderAdapter.ViewHolder;
import com.mm.privategallery.dao.ImageInfo;
import com.mm.privategallery.model.BitmapCache;
import com.mm.privategallery.model.GalleryFolderDataItem;
import com.mm.privategallery.view.DecodeImageView;

import android.R.integer;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class PictureGridAdapter extends BaseAdapter {

	private LayoutInflater mInflater;
	private List<ImageInfo> mImagesDataList;
	private Handler mHandler;
	public PictureGridAdapter(Context context, Handler handler){
		mInflater = LayoutInflater.from(context);
		mHandler = handler;
	}
	
	public List<ImageInfo> getData(){
		return mImagesDataList;
	}
	
	public void setData(List<ImageInfo> data){
		if(mImagesDataList == data){
			return;
		}
		mImagesDataList = data;
		notifyDataSetChanged();
	}
	
	@Override
	public int getCount() {
		if(mImagesDataList == null){
			return 0;
		}else{
			return mImagesDataList.size();
		}
	}

	@Override
	public Object getItem(int arg0) {
		if(mImagesDataList == null){
			return null;
		}else{
			return mImagesDataList.get(arg0);
		}
	}

	@Override
	public long getItemId(int arg0) {
		// TODO Auto-generated method stub
		return arg0;
	}

	private View newView(final int position, final ViewGroup parent, final int viewType){
		View view = mInflater.inflate(R.layout.picture_item, null);
		ViewHolder holder = new ViewHolder();
		holder.mPicImageView = (DecodeImageView)view.findViewById(R.id.pic_src_image);
		holder.mMarkImageView = (ImageView)view.findViewById(R.id.mark_image);
		view.setTag(holder);
		return view;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		int viewType = getItemViewType(position);
		View v;
		if (convertView == null) {
			v = newView(position, parent, viewType);
		} else {
			v = convertView;
		}
		bindView(v, position, viewType);
		return v;
	}

	private void bindView(final View convertView,final  int position, final int viewType){
		ViewHolder holder = (ViewHolder)convertView.getTag();
	    if(holder == null){
	    	return;
	    }
	    
	    ImageInfo info = mImagesDataList.get(position);
	    holder.mPicImageView.setPicPath(info.pathString, mHandler);
	    if(info.isSelected){
	    	holder.mMarkImageView.setVisibility(View.VISIBLE);
	    }else{
	    	holder.mMarkImageView.setVisibility(View.GONE);
	    }
	}
	
	private class ViewHolder{
		DecodeImageView mPicImageView;
		ImageView mMarkImageView;
	}
	
	public int getSelectedItemCount(){
		if(mImagesDataList == null){
			return 0;
		}
		int count=0;
		for (int i = 0; i < mImagesDataList.size(); i++) {
			ImageInfo item = mImagesDataList.get(i);
			if(item !=null && item.isSelected){
				count ++;
			}
		}
		return count;
	}
	
	public void refreshSelected(){
		if(mImagesDataList == null){
			return ;
		}
		for (int i = 0; i < mImagesDataList.size(); i++) {
			ImageInfo item = mImagesDataList.get(i);
			if(item !=null && item.isSelected){
				item.isSelected = false;
			}
		}
		notifyDataSetChanged();
		return ;
	}
	
	public void chooseAllOrNot(){
		if(mImagesDataList == null){
			return ;
		}
		boolean bchoose = getSelectedItemCount()!=mImagesDataList.size();
		for (int i = 0; i < mImagesDataList.size(); i++) {
			ImageInfo item = mImagesDataList.get(i);
			if(item !=null){
				item.isSelected = bchoose;
			}
		}
		notifyDataSetChanged();
		return ;
	}
	
	public List<ImageInfo> getSelectedItem(){
		List<ImageInfo> items= new ArrayList<ImageInfo>();
		if(mImagesDataList == null){
			return items;
		}
		for (int i = 0; i < mImagesDataList.size(); i++) {
			ImageInfo item = mImagesDataList.get(i);
			if(item !=null && item.isSelected){
			  items.add(item);
			}
		}
		return items;
	}
	
	public void clearSelectedItems(){
		if(mImagesDataList == null){
			return;
		}
		List<ImageInfo> selecteds= getSelectedItem();
		
		while (selecteds.size()!=0) {
			ImageInfo targetDataItem=  selecteds.get(0);
			for (int i = 0; i < mImagesDataList.size(); i++) {
				ImageInfo item = mImagesDataList.get(i);
				if(item == targetDataItem){
					mImagesDataList.remove(i);
					break;
				}
			}
			selecteds.remove(0);
		}
		notifyDataSetChanged();
		return ;
	}
}
