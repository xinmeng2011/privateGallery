package com.mm.privategallery.view;

import com.mm.privategallery.R;
import com.mm.utility.DensityUtil;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

public class ChooseGalleryTopBar extends LinearLayout {

	private OnClickListener mClickListener;
	private TextView mPrivateBt;
	private TextView mGalleryBt;
	private Context mContext;
	public ChooseGalleryTopBar(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		initUI();
	}

	public ChooseGalleryTopBar(Context context, AttributeSet attrs) {
		super(context, attrs);
	    mContext = context;
		initUI();
		mPrivateBt = (TextView)findViewById(R.id.private_bt);
		mPrivateBt.setOnTouchListener(mOnTouch);
		mGalleryBt = (TextView)findViewById(R.id.system_bt);
		mGalleryBt.setOnTouchListener(mOnTouch);
		
		findViewById(R.id.back_bt).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(mClickListener != null){
					mClickListener.onClick(v);
				}
				
			}
		});
	}
	private OnTouchListener mOnTouch= new OnTouchListener() {
		
		@Override
		public boolean onTouch(View v, MotionEvent event) {
			switch (v.getId()) {
			case R.id.private_bt:
				updateTopUI(true);
				mClickListener.onClick(v);
				break;
			case R.id.system_bt:
				updateTopUI(false);
				mClickListener.onClick(v);
				break;
			default:
				break;
			}
			return false;
		}
	};

	public void updateTopUI(boolean inPrivate){
		if(inPrivate){
			mPrivateBt.setBackgroundResource(R.drawable.tab_right_select);
			mPrivateBt.setTextColor(getResources().getColor(R.color.white));
			mPrivateBt.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_lock_nor, 0, 0, 0);
			mPrivateBt.setPadding(DensityUtil.dip2px(mContext,20f), 0, DensityUtil.dip2px(mContext,20f), 0);
			mGalleryBt.setBackgroundResource(R.drawable.tab_left_nor);
			mGalleryBt.setTextColor(getResources().getColor(R.color.red_pink));
			mGalleryBt.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_picture_chose, 0, 0, 0);
			mGalleryBt.setPadding(DensityUtil.dip2px(mContext,20f), 0, DensityUtil.dip2px(mContext,20f), 0);
		}else{
			mPrivateBt.setBackgroundResource(R.drawable.tab_right_nor);
			mPrivateBt.setTextColor(getResources().getColor(R.color.red_pink));
			mPrivateBt.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_lock_chose, 0, 0, 0);
			mPrivateBt.setPadding(DensityUtil.dip2px(mContext,20f), 0, DensityUtil.dip2px(mContext,20f), 0);
			mGalleryBt.setBackgroundResource(R.drawable.tab_left_select);
			mGalleryBt.setTextColor(getResources().getColor(R.color.white));
			mGalleryBt.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_picture_nor, 0, 0, 0);
			mGalleryBt.setPadding(DensityUtil.dip2px(mContext,20f), 0, DensityUtil.dip2px(mContext,20f), 0);
		}
	}
	private void initUI(){
		LayoutInflater.from(getContext()).inflate(R.layout.topbar_choose_gallery,
				this, true);
	}
	
	@Override
	public void setOnClickListener(OnClickListener l){
		mClickListener = l;
	}
	
	
	
	
}
