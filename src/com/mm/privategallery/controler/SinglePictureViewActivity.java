package com.mm.privategallery.controler;

import com.mm.privategallery.R;
import com.mm.utility.BitmapUtility;

import android.R.integer;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;

public class SinglePictureViewActivity extends Activity  implements OnTouchListener,  
OnGestureListener{

	private ImageView mImageView;
	public static final String SINGLE_IMAGE_PATH="SINGLE_IMAGE_PATH";
	public static final String SINGLE_ITEM_ID="SINGLE_ITEM_ID";
	private int mPicId;
	private Bitmap mBigBitmap;
	private GestureDetector mGestureDetector;
    private static final int FLING_MIN_DISTANCE = 50;  
    private static final int FLING_MIN_VELOCITY = 0;  
    private String[] mPicPath=null;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		Intent it = getIntent();
		if(it == null){
			return;
		}
		//mSingleImagePath = it.getStringExtra(SINGLE_IMAGE_PATH);
		mPicPath = it.getStringArrayExtra(SINGLE_IMAGE_PATH);
		mPicId = it.getIntExtra(SINGLE_ITEM_ID, 0);
	}

	@Override
	protected void onStart() {
		super.onStart();
		initUI();
		setupBitmap(mPicPath[mPicId]);
	}

	@Override
	protected void onStop() {
		super.onStop();
		destoryBitmap();
	}

	private void initUI(){
		setContentView(R.layout.activity_single_pic);
		mImageView = (ImageView)findViewById(R.id.single_image);
        mGestureDetector = new GestureDetector(this);  
        RelativeLayout ll=(RelativeLayout)findViewById(R.id.rl);  
        ll.setOnTouchListener(this);  
        ll.setLongClickable(true);  
	}
	
	
	private void setupBitmap(String path){
		mBigBitmap = BitmapUtility.getOriginBitmap(path);
        //将图片显示到ImageView中  
        mImageView.setImageBitmap(mBigBitmap); 
	}
	
	private void destoryBitmap(){
		if(mBigBitmap != null){
			mBigBitmap.recycle();
		}
	}
	
    @Override  
    public boolean onTouch(View v, MotionEvent event) {    
         return mGestureDetector.onTouchEvent(event);   
    }  
    
    @Override  
    public boolean onDown(MotionEvent e) {  
        // TODO Auto-generated method stub  
        return false;  
    }  
    @Override  
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,  
            float velocityY) {  
        // TODO Auto-generated method stub  
         if (e1.getX()-e2.getX() > FLING_MIN_DISTANCE   
                    && Math.abs(velocityX) > FLING_MIN_VELOCITY) { 
        	 mPicId++;
        	 mPicId = mPicId%mPicPath.length;
        	 setupBitmap(mPicPath[mPicId]);
 
            } else if (e2.getX()-e1.getX() > FLING_MIN_DISTANCE   
                    && Math.abs(velocityX) > FLING_MIN_VELOCITY) {   
                // Fling right   
           	 mPicId--;
           	 if(mPicId<0){
           		 mPicId = mPicPath.length-1;
           	 }
           	 setupBitmap(mPicPath[mPicId]);
            }   
            return false;   
    }  
    @Override  
    public void onLongPress(MotionEvent e) {  
        // TODO Auto-generated method stub  
    }  
    
    
    @Override  
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,  
            float distanceY) {  
        // TODO Auto-generated method stub  
        return false;  
    }  
    
    
    @Override  
    public void onShowPress(MotionEvent e) {  
        // TODO Auto-generated method stub  
    }  
    
    @Override  
    public boolean onSingleTapUp(MotionEvent e) {  
        // TODO Auto-generated method stub  
        return false;  
    }  
}
