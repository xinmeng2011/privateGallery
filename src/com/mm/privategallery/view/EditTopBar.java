package com.mm.privategallery.view;

import com.mm.privategallery.R;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class EditTopBar extends LinearLayout {
	private Context mContext;
	private TextView mTitleTextView;
	private ImageView mChooseImageView;
	public EditTopBar(Context context, AttributeSet attrs) {
		super(context, attrs);
	    mContext = context;
		initUI();
	}
	
	private void initUI(){
		LayoutInflater.from(getContext()).inflate(R.layout.topbar_edit,
				this, true);
		mTitleTextView=(TextView)findViewById(R.id.edit_title);
		mChooseImageView = (ImageView)findViewById(R.id.choose_bt);
	}
	
	public void setTitle(String text){
		mTitleTextView.setText(text);
	}
	
	public void setChooseAll(boolean bChooseAll){
		if(bChooseAll){
			mChooseImageView.setImageResource(R.drawable.topbat_edit_choose_bg);
		}else{
			mChooseImageView.setImageResource(R.drawable.topbar_edit_nochoose_bg);
		}
	}
	
	public void setChooseClickListener(OnClickListener l){
		mChooseImageView.setOnClickListener(l);
	}
}
