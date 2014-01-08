package com.mm.privategallery.view;

import com.mm.privategallery.R;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class BottomBar extends LinearLayout {
	private Context mContext;
	private TextView mLeftBtTextView;
	private TextView mRightBtTextView;
	public BottomBar(Context context, AttributeSet attrs) {
		super(context, attrs);
	    mContext = context;
		initUI();
	}
	
	private void initUI(){
		LayoutInflater.from(getContext()).inflate(R.layout.bottom_edit_bar,
				this, true);
		mLeftBtTextView=(TextView)findViewById(R.id.left_btn);
		mRightBtTextView = (TextView)findViewById(R.id.right_btn);
	}
	
	public void setLeftBtn(String text, int resId){
		mLeftBtTextView.setText(text);
		mLeftBtTextView.setCompoundDrawablesWithIntrinsicBounds(0, resId, 0, 0);
	}
	
	public void setClickListener(OnClickListener listener){
		mLeftBtTextView.setOnClickListener(listener);
		mRightBtTextView.setOnClickListener(listener);
	}
}
