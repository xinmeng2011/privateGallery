package com.mm.privategallery.controler;

import java.util.ArrayList;
import java.util.List;

import com.mm.privategallery.R;
import com.mm.privategallery.model.BitmapCache;
import com.mm.privategallery.model.GalleryFolderDataItem;
import com.mm.utility.BitmapUtility;

import android.R.integer;
import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

public class GalleryFolderAdapter extends BaseAdapter {

	private LayoutInflater myInflater;
	private Context mContext;
	private List<GalleryFolderDataItem> mFolderItems;
	private boolean mBEditMode=false;
	public GalleryFolderAdapter(Context context){
		super();
		mContext = context;
		myInflater = LayoutInflater.from(context);
	}
	
	public void setEditStatus(boolean status){
		mBEditMode = status;
		notifyDataSetChanged();
	}
	
	public void setData(List<GalleryFolderDataItem> items){
		if(mFolderItems == items){
			return;
		}
		mFolderItems = items;
		notifyDataSetChanged();
	}
	@Override
	public int getCount() {
		if(mFolderItems == null){
			return 0;
		}else{
			return mFolderItems.size();
		}
	}

	@Override
	public Object getItem(int arg0) {
		if(mFolderItems == null || mFolderItems.size()<arg0){
			return null;
		}else{
			return mFolderItems.get(arg0);
		}
	}

	@Override
	public long getItemId(int arg0) {
		// TODO Auto-generated method stub
		return arg0;
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
	
	private View newView(final int position, final ViewGroup parent, final int viewType){
		View view = myInflater.inflate(R.layout.gallery_folder_item, null);
		ViewHolder holder = new ViewHolder();
		holder.mCover1 =(ImageView) view.findViewById(R.id.folder_cover1);
		holder.mCover2 =(ImageView) view.findViewById(R.id.folder_cover2);
		holder.mDescribe = (TextView)view.findViewById(R.id.folder_describe);
		holder.mCount = (TextView)view.findViewById(R.id.count_tv);
		holder.mMark = (ImageView)view.findViewById(R.id.mark_image);
		holder.mCover2FrameLayout = (FrameLayout)view.findViewById(R.id.layout2);
		view.setTag(holder);
		return view;
	}
	
	private void bindView(final View convertView,final  int position, final int viewType){
		
		ViewHolder holder = (ViewHolder)convertView.getTag();
		
		if(holder == null){
			return;
		}
		
		GalleryFolderDataItem item = mFolderItems.get(position);
		
		if(item == null){
			return;
		}
		
		holder.mDescribe.setText(item.mFolderName);	
		holder.mCount.setText(String.valueOf(item.mImageList.size()));
		List<Bitmap> imagesBitmaps= BitmapCache.getSingle().getFolderBitmap2Cache(item.mFolderPath, item.mIsPrivate);
		if(imagesBitmaps == null){
			return;
		}
		if(imagesBitmaps.size()>0){
			holder.mCover1.setImageBitmap(imagesBitmaps.get(0));
		}
		if(imagesBitmaps.size()>1){
			holder.mCover2.setVisibility(View.VISIBLE);
			holder.mCover2FrameLayout.setVisibility(View.VISIBLE);
			holder.mCover2.setImageBitmap(imagesBitmaps.get(1));
		}else{
			holder.mCover2.setVisibility(View.INVISIBLE);
			holder.mCover2FrameLayout.setVisibility(View.INVISIBLE);
			
		}
		if(item.isSelected){
			holder.mMark.setVisibility(View.VISIBLE);
		}else{
			holder.mMark.setVisibility(View.INVISIBLE);
		}
	}
	
	public int getSelectedItemCount(){
		if(mFolderItems == null){
			return 0;
		}
		int count=0;
		for (int i = 0; i < mFolderItems.size(); i++) {
			GalleryFolderDataItem item = mFolderItems.get(i);
			if(item !=null && item.isSelected){
				count ++;
			}
		}
		return count;
	}
	
	public void chooseAllOrNot(){
		if(mFolderItems == null){
			return ;
		}
		boolean choose=getSelectedItemCount() != mFolderItems.size();
		for (int i = 0; i < mFolderItems.size(); i++) {
			GalleryFolderDataItem item = mFolderItems.get(i);
			if(item !=null ){
			  item.isSelected =choose;
			}
		}
		notifyDataSetChanged();
		return ;
	}
	
	public List<GalleryFolderDataItem> getSelectedItem(){
		List<GalleryFolderDataItem> items= new ArrayList<GalleryFolderDataItem>();
		if(mFolderItems == null){
			return items;
		}
		for (int i = 0; i < mFolderItems.size(); i++) {
			GalleryFolderDataItem item = mFolderItems.get(i);
			if(item !=null && item.isSelected){
			  items.add(item);
			}
		}
		return items;
	}

	public void clearSelectedItems(){
		if(mFolderItems == null){
			return;
		}
		List<GalleryFolderDataItem> selecteds= getSelectedItem();
		
		while (selecteds.size()!=0) {
			GalleryFolderDataItem targetDataItem=  selecteds.get(0);
			for (int i = 0; i < mFolderItems.size(); i++) {
				GalleryFolderDataItem item = mFolderItems.get(i);
				if(item == targetDataItem){
					mFolderItems.remove(i);
					break;
				}
			}
			selecteds.remove(0);
		}
		notifyDataSetChanged();
		return ;
	}
	
	
	class ViewHolder{
		ImageView mCover1;
		ImageView mCover2;
		FrameLayout mCover2FrameLayout;
		ImageView mMark;
		TextView mCount;
		TextView mDescribe;
	}
}
