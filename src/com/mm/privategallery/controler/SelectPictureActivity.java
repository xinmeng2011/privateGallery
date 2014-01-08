package com.mm.privategallery.controler;


import java.io.File;
import java.util.List;

import com.mm.privategallery.R;
import com.mm.privategallery.dao.ImageInfo;
import com.mm.privategallery.model.GalleryEngine;
import com.mm.privategallery.model.GalleryFolderDataItem;
import com.mm.privategallery.view.BottomBar;
import com.mm.privategallery.view.EditTopBar;
import com.mm.utility.SDHelper;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.GridView;
import android.widget.RelativeLayout;
import android.widget.Toast;

public class SelectPictureActivity extends Activity {

	public static final String GALLERY_PATH = "GALLERY_PATH";

	public String mFolderPathString = "";
	private PictureGridAdapter mAdapter;
	private GridView mPicturesGridView;
	private boolean mEdit=false;
	private EditTopBar mEditTopBar;
	private BottomBar mBottomBar;
	private RelativeLayout mTopbar;
	private boolean mPrivate=false;
	private ProgressDialog mProgressDialog;
	
	private final int DELETE_OK=1;
	private final int TANSFER_OK =2;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		Intent it= getIntent();
		if(it!=null){
			mFolderPathString = it.getStringExtra(GALLERY_PATH);
		}
		mPrivate = SDHelper.isPrivateImage(mFolderPathString);
		Toast.makeText(this, mFolderPathString, 500).show();
	}
	@Override
	protected void onStart() {
		super.onStart();
		initUI();
		initData();
	}
	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
	}
	
	private void initUI(){
		setContentView(R.layout.activity_select_pic);
		mTopbar = (RelativeLayout)findViewById(R.id.top_wrapper);
		mEditTopBar = (EditTopBar)findViewById(R.id.topbar_edit);
		mBottomBar = (BottomBar)findViewById(R.id.bottombar_edit);
		mBottomBar.setClickListener(mBottomClickListener);
		if(mPrivate){
			mBottomBar.setLeftBtn(getResources().getString(R.string.remove_private), 
					R.drawable.tab_private_bt_bg);
		}
		findViewById(R.id.back_bt).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
			    finish();	
			}
		});
		mEditTopBar.setChooseClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				mAdapter.chooseAllOrNot();
				updateEditTopbar();
			}
		});
		mPicturesGridView = (GridView)findViewById(R.id.picture_map);
	    mPicturesGridView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				ImageInfo info= (ImageInfo)mAdapter.getItem(arg2);
				if(info == null){
					return;
				}
				if(!mEdit){
					String pathString= info.pathString;
					Intent it= new Intent();
					it.putExtra(SinglePictureViewActivity.SINGLE_IMAGE_PATH, pathString);
					it.setClass(SelectPictureActivity.this, SinglePictureViewActivity.class);
				    startActivity(it);
				}else{
					info.isSelected = !info.isSelected;
					mAdapter.notifyDataSetChanged();
					updateEditTopbar();
				}
			}
		});
	    
	    mPicturesGridView.setOnItemLongClickListener( new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				mEdit = !mEdit;
				setEditStatus(mEdit);
				if(!mEdit){
					return true;
				}
				ImageInfo info= (ImageInfo)mAdapter.getItem(arg2);
				if(info == null){
					return true;
				}
				info.isSelected = true;
				mAdapter.notifyDataSetChanged();
				updateEditTopbar();
				return true;
			}
		});
	}
	
	private void refreshSelectedItem(List<ImageInfo> info){
		if(info == null){
			return;
		}
		for (int i = 0; i < info.size(); i++) {
			ImageInfo info2 = info.get(i);
			if(info2 !=null){
				info2.isSelected = false;
			}
		}
	}
	
	private void initData(){
		mAdapter = new PictureGridAdapter(this);
		mPicturesGridView.setAdapter(mAdapter);
		
		GalleryFolderDataItem itemFolder = null;
		if(!mPrivate){
			itemFolder = GalleryEngine.getSingle().getFolderItemFromPath(mFolderPathString);
		}else{
			itemFolder = GalleryEngine.getSingle().getPrivateFolderItemFromPath(mFolderPathString);
		}
		List<ImageInfo> images= itemFolder.mImageList;
		refreshSelectedItem(images);
		mAdapter.setData(images);
	}
	
	private void setEditStatus(boolean bEdit){
		if(bEdit){
			mTopbar.setVisibility(View.INVISIBLE);
			mEditTopBar.setVisibility(View.VISIBLE);
			mBottomBar.setVisibility(View.VISIBLE);
			setBottomBarPrivate(mPrivate);
		}else{
			mAdapter.refreshSelected();
			mTopbar.setVisibility(View.VISIBLE);
			mEditTopBar.setVisibility(View.GONE);
			mBottomBar.setVisibility(View.GONE);
		}
	}
	
	private void updateEditTopbar(){
		int count=mAdapter.getSelectedItemCount();
		mEditTopBar.setTitle("“——°‘Ò" + count + "œÓ");
		mEditTopBar.setChooseAll(count != mAdapter.getCount());
	}
	
	private void setBottomBarPrivate(boolean bPrivate){
		if(bPrivate){
			mBottomBar.setLeftBtn(getResources().getString(R.string.remove_private), R.drawable.tab_private_bt_bg);
		}else{
			mBottomBar.setLeftBtn(getResources().getString(R.string.add_private), R.drawable.tab_private_bt_bg);
		}
	}
	
	private OnClickListener mBottomClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View arg0) {
			if(arg0.getId() == R.id.right_btn){
				deletePictures();
			}else if(arg0.getId() == R.id.left_btn){
				tansferPictures();
			}
			
		}
	};
	
	private void tansferPictures(){
		if(!mPrivate){
			mProgressDialog = ProgressDialog.show(this, "º”√‹Õº∆¨", "«Î…‘∫Û..", true, true); 
		}else{
			mProgressDialog = ProgressDialog.show(this, "Ω‚√‹Õº∆¨", "«Î…‘∫Û..", true, true); 
		}
		
		new Thread(
				new Runnable() {	
					@Override
					public void run() {
						List<ImageInfo> items= mAdapter.getSelectedItem();
						for (int i = 0; i < items.size(); i++) {
							ImageInfo item= items.get(i);
							if(item != null){
								if(!mPrivate){
									GalleryEngine.getSingle().encryptImageFileAndDelOld(item.pathString);
								}else
								{
									GalleryEngine.getSingle().decryptImageAndDel(item.pathString);
								}
							}
						}
						if(!mPrivate){
							SDHelper.sendbroadcastScanSD(mFolderPathString);
						}
						mHandler.sendEmptyMessage(TANSFER_OK);
					}
				}).start();
	}
	
	private void deletePictures(){
		mProgressDialog = ProgressDialog.show(this, "…æ≥˝Õº∆¨", "«Î…‘∫Û..", true, true); 
		new Thread(
		new Runnable() {	
			@Override
			public void run() {
				List<ImageInfo> items= mAdapter.getSelectedItem();
				for (int i = 0; i < items.size(); i++) {
					ImageInfo item= items.get(i);
					if(item != null){
						SDHelper.deleteFile(new File(item.pathString));
					}
				}
				SDHelper.sendbroadcastScanSD(mFolderPathString);
				mHandler.sendEmptyMessage(DELETE_OK);
			}
		}).start();
	}
	private void closeDlg(){
		if(mProgressDialog != null){
			mProgressDialog.dismiss();
		}
	}
	public Handler mHandler = new Handler(){
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case DELETE_OK:
				mAdapter.clearSelectedItems();
				closeDlg();
				setEditStatus(false);
				break;
			case TANSFER_OK:
				mAdapter.clearSelectedItems();
				closeDlg();
				setEditStatus(false);
			default:
				break;
			}
			}
	};
}
