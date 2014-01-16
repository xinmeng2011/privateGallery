package com.mm.privategallery.controler;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.mm.privategallery.R;
import com.mm.privategallery.model.GalleryEngine;
import com.mm.privategallery.model.GalleryFolderDataItem;
import com.mm.privategallery.model.SmsPasswordManager;
import com.mm.privategallery.view.BottomBar;
import com.mm.privategallery.view.ChooseGalleryTopBar;
import com.mm.privategallery.view.EditTopBar;
import com.mm.utility.SDHelper;

import android.R.bool;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

public class ChooseGalleryActivity extends Activity {

	private GridView mGalleryGridView;
	private GalleryFolderAdapter mAdapter;
	private ChooseGalleryTopBar mTopbar;
	private TextView mCount;
	private EditTopBar mEditTopBar;
	private BottomBar mBottomBar;
	private ProgressDialog mProgressDialog;
	private TextView mTypeTextView;
	private boolean isPrivateMode = false;
	private boolean isEdit=false;
	
	private final int DELETE_OK=1;
	private final int TERSFER_OK=2;
	private final int READ_GALLERY_DONE = 3;
	private final static int REQUEST_CODE_SET_PWD = 0x07;
	private final static int REQUEST_CODE_VERIFY_PWD = 0x08;
	private boolean mIsPrivatePassed = false;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_gallery);
		SDHelper.allScan(this);
	    initUI();
	}
	
	private void closeDlg(){
		if(mProgressDialog != null){
			mProgressDialog.dismiss();
		}
	}
	
	private void initUI(){
		mGalleryGridView = (GridView)findViewById(R.id.gallery_map);
		mTopbar = (ChooseGalleryTopBar)findViewById(R.id.topbar_normal);
		mTopbar.setOnClickListener(mClickListener);
		mEditTopBar = (EditTopBar)findViewById(R.id.topbar_edit);
		mBottomBar = (BottomBar)findViewById(R.id.bottombar_edit);
		mBottomBar.setClickListener(mClickListener);
		mEditTopBar.setChooseClickListener(mClickListener);
		mCount = (TextView)findViewById(R.id.tv_count);
		mTypeTextView = (TextView)findViewById(R.id.tv);
		mGalleryGridView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				String galleryPathString ="";
				GalleryFolderDataItem item = (GalleryFolderDataItem)mAdapter.getItem(arg2);
				if(item == null){
					return;
				}
				if(!isEdit){
					galleryPathString = item.mFolderPath;
					Intent it = new Intent();
					it.setClass(ChooseGalleryActivity.this, SelectPictureActivity.class);
					it.putExtra(SelectPictureActivity.GALLERY_PATH, galleryPathString);
					startActivity(it);
				}else{
					item.isSelected = !item.isSelected;
					mAdapter.notifyDataSetChanged();
					updateEditTopbar();
				}
			}
			
		});
		
		
		mGalleryGridView.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				GalleryFolderDataItem item = (GalleryFolderDataItem)mAdapter.getItem(arg2);
				if(item == null){
					return false;
				}
				if(isEdit){
					goBack();
				}else{
					setEditStatus(true);
					item.isSelected = true;
					mAdapter.notifyDataSetChanged();
					updateEditTopbar();
				}
				return true;
			}
		});
	}
	
	public void refreshSelectedStatus(List<GalleryFolderDataItem> folders){
		for (int i = 0; i < folders.size(); i++) {
			GalleryFolderDataItem item= folders.get(i);
			if(item != null){
				item.isSelected = false;
			}
		}
	}

	private void initData(){
		if(!isPrivateMode){
			mProgressDialog = ProgressDialog.show(this, getResources().getString(R.string.plz_wait), getResources().getString(R.string.reading));
			readSystemGalleryAsync();
			mAdapter = new GalleryFolderAdapter(this);
			mGalleryGridView.setAdapter(mAdapter);
		}else{
			switchToPrisvateView();
		}
	}
	
	private void switchToGalleryView(){
		if(!isPrivateMode){
			return;
		}
		List<GalleryFolderDataItem> folders = GalleryEngine.getSingle().getGalleryFolderList();
		mAdapter.setData(folders);
		isPrivateMode = false;
		mCount.setText(folders.size() + "个");
		mTypeTextView.setText(R.string.photo_title);
	}
	 
	private void switchToPrisvateView(){
		if(isPrivateMode){
			return;
		}if(mIsPrivatePassed){
				List<GalleryFolderDataItem> folders = GalleryEngine.getSingle().getPrivateFolderList();
				mAdapter.setData(folders);
				isPrivateMode = true;
				mCount.setText(folders.size() + "个");
				mTypeTextView.setText(R.string.private_photo);
		}else{
			if (SmsPasswordManager.getInstance().hasPassword()) {
				gotoVerifyPassword();
			}else{
				openPswFirst();
			}
		}
	}
	
	@Override
	protected void onStart() {
		super.onStart();
		initData();
	}

	@Override
	protected void onStop() {
		super.onStop();
	}

	private View.OnClickListener mClickListener = new View.OnClickListener() {
		
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.back_bt:
				goBack();
				break;
			case R.id.choose_bt:
				mAdapter.chooseAllOrNot();
				updateEditTopbar();
				break;
			case R.id.left_btn:
				encryptSelectedItems();
				break;
			case R.id.right_btn:
				deleteSelectedItems();
				break;
			case R.id.private_bt:
				switchToPrisvateView();
				break;
			case R.id.system_bt:
				switchToGalleryView();
				break;
			default:
				break;
			}
		}
	};
	
	private void deleteSelectedItems(){
		mProgressDialog = ProgressDialog.show(this, "删除图片", "请稍后..", true, true); 
		new Thread(
		new Runnable() {	
			@Override
			public void run() {
				List<GalleryFolderDataItem> items= mAdapter.getSelectedItem();
				for (int i = 0; i < items.size(); i++) {
					GalleryFolderDataItem item= items.get(i);
					if(item != null){
						String pathString= item.mFolderPath;
						SDHelper.deleteFile(new File(pathString));
						SDHelper.sendbroadcastScanSD(pathString);
					}
				}
				mHandler.sendEmptyMessage(DELETE_OK);
			}
		}).start();
	}
	
	private void encryptSelectedItems(){
		if(isPrivateMode){
			Toast.makeText(this, "更多功能敬请期待", 500).show();
			return;
		}
		if(isPrivateMode){
			mProgressDialog = ProgressDialog.show(this, "解密图片", "请稍后..", true, true); 	
		}else{
			mProgressDialog = ProgressDialog.show(this, "加密图片", "请稍后..", true, true); 	
		}
		new Thread(
		new Runnable() {	
			@Override
			public void run() {
				List<GalleryFolderDataItem> items= mAdapter.getSelectedItem();
				String pathString = "";
				String nameString = "";
				for (int i = 0; i < items.size(); i++) {
					GalleryFolderDataItem item= items.get(i);
					if(item != null){
						List<String> imageNamesList= new ArrayList<String>();
						pathString= item.mFolderPath;
						nameString= item.mFolderName;
						for (int j = 0; j < item.mImageList.size(); j++) {
							imageNamesList.add(item.mImageList.get(j).pathString);	
						}
						GalleryEngine.getSingle().encryptImageFolderAndDelOld
						(pathString, nameString, imageNamesList);
					}
				}
				mHandler.sendEmptyMessage(TERSFER_OK);
			}
		}).start();
	}
	
	private void setEditStatus(boolean bEdit){
		if(bEdit){
			isEdit = true;
			mTopbar.setVisibility(View.INVISIBLE);
			mEditTopBar.setVisibility(View.VISIBLE);
			mBottomBar.setVisibility(View.VISIBLE);
			setBottomBarPrivate(isPrivateMode);
		}else{
			isEdit = false;
			mTopbar.setVisibility(View.VISIBLE);
			mEditTopBar.setVisibility(View.GONE);
			mBottomBar.setVisibility(View.GONE);
		}
	}
	
	private void updateEditTopbar(){
		int count=mAdapter.getSelectedItemCount();
		mEditTopBar.setTitle("已选择" + count + "项");
		mEditTopBar.setChooseAll(count != mAdapter.getCount());
	}
	
	private void setBottomBarPrivate(boolean bPrivate){
		if(bPrivate){
			mBottomBar.setLeftBtn(getResources().getString(R.string.remove_private), R.drawable.tab_private_bt_bg);
		}else{
			mBottomBar.setLeftBtn(getResources().getString(R.string.add_private), R.drawable.tab_private_bt_bg);
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
			case TERSFER_OK:
				mAdapter.clearSelectedItems();
				closeDlg();
				setEditStatus(false);
				break;
			case READ_GALLERY_DONE:
				bindData();
				closeDlg();
				break;
			default:
				break;
			}
		}
	};
	
	private void openPswFirst(){
		Intent intent = new Intent();
		intent.setClass(this, SmsPasswordActivity.class);
		intent.putExtra(SmsPasswordActivity.INTENT_PARAMS_MODE,
				SmsPasswordActivity.MODE_SET_PWD);
		this.startActivityForResult(intent, REQUEST_CODE_SET_PWD);
	}
	
	public void gotoVerifyPassword(){
		Intent intent = new Intent();
		intent.setClass(this, SmsPasswordActivity.class);
		intent.putExtra(SmsPasswordActivity.INTENT_PARAMS_MODE, SmsPasswordActivity.MODE_VERIFY_PWD);
		this.startActivityForResult(intent, REQUEST_CODE_VERIFY_PWD);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

			switch (requestCode) {
			case REQUEST_CODE_SET_PWD:
				if (resultCode != RESULT_OK) {
					return;
				}else{// 密码设置成功
					mIsPrivatePassed = true;
					mTopbar.updateTopUI(true);
					switchToPrisvateView();
					Toast.makeText(this,"设置成功", Toast.LENGTH_LONG).show();
				}
				break;
			case REQUEST_CODE_VERIFY_PWD:
				if (resultCode != RESULT_OK) {
					//没有验证成功 不切换
					return;
				}
				else{
					mIsPrivatePassed = true;
					mTopbar.updateTopUI(true);
					switchToPrisvateView();
				}
				Toast.makeText(this, getString(R.string.lock_modify_success), Toast.LENGTH_LONG).show();
				break;
			default:
					break;
			}
	}
	
	private void readSystemGalleryAsync(){
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				GalleryEngine.getSingle().getGalleryFolderList();
				mHandler.sendEmptyMessage(READ_GALLERY_DONE);
			}
		}).start();
	}
	
	private void bindData(){
		List<GalleryFolderDataItem> folders = GalleryEngine.getSingle().getGalleryFolderList();
		refreshSelectedStatus(folders);
		mAdapter.setData(folders);
		mCount.setText(folders.size() + "个");
	}
	
	private void goBack(){
		if(!isEdit){
			finish();
		}else{
			mAdapter.clearEditStatus();
			setEditStatus(false);
		}
	}
	
	public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && isEdit) {
        	goBack();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
