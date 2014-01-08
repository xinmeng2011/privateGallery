package com.mm.privategallery.view;


import com.mm.privategallery.R;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;



public class LockViewGroup extends LinearLayout {
	
	private Context mContext;
	
	private LayoutInflater mInflater;
	
	private TextView mTvTips ;
	
	private LockPatternView mLockView;
	
	public LockViewGroup(Context context) {
		super(context);
		initView(context);
	}

	public LockViewGroup(Context context, AttributeSet attrs) {
		super(context, attrs);
		initView(context);
	}

	private void initView(Context context){
		mContext = context;
		mInflater = LayoutInflater.from(mContext);
		
		View v = mInflater.inflate(R.layout.layout_lock_viewgroup, this);
		mTvTips = (TextView)v.findViewById(R.id.tv_tips);
		mLockView = (LockPatternView)v.findViewById(R.id.lock_view);
	}
	
	public TextView getTipsView(){
		return mTvTips;
	}
	
	public LockPatternView getLockView(){
		return mLockView;
	}
}

