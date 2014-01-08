package com.mm.privategallery.view;



import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Scroller;

public class ScrollLockView extends ViewGroup {
	private final static String TAG = "DoubleView";
	
	//初始屏幕序号
	private static final int INIT_SCREEN = 0;
	
	private Scroller mScroller;

	private int mCurScreen = INIT_SCREEN;
	
	protected Context mContext;
	
	private float mScrollTimeFactor = 2f;//滑动手指离开屏幕后，视图滚动的时间因子，默认值为2. 此值越大，做滚动动画效果的时间越长。
	
	private LockViewGroup mLockVerify;
	
	private LockViewGroup mLockInput;
	
	private LockViewGroup mLockConfirm;
	
	public enum ViewMode {
		Verify,
		NewInput,
		Confirm
	}
	
	public ScrollLockView(Context context) {
        super(context);
        init(context);
    }

    public ScrollLockView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public ScrollLockView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }
    
    private void init(Context context) {
		mContext = context;
		mScroller = new Scroller(context);
		
		mLockVerify = new LockViewGroup(context);
		ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(
				ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.FILL_PARENT);
		mLockVerify.setLayoutParams(params);
		this.addView(mLockVerify);
		
		mLockInput = new LockViewGroup(context);
		params = new ViewGroup.LayoutParams(
				ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.FILL_PARENT);
		mLockInput.setLayoutParams(params);
		this.addView(mLockInput);
		
		mLockConfirm = new LockViewGroup(context);
		params = new ViewGroup.LayoutParams(
				ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.FILL_PARENT);
		mLockConfirm.setLayoutParams(params);
		this.addView(mLockConfirm);
	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		// TODO 自动生成的方法存根
		int childLeft = 0;
		final int childCount = getChildCount();
		try {
			for (int i = 0; i < childCount; i++) {
				final View childView = getChildAt(i);
				if(childView==null) {
					continue;
				}
				if (childView.getVisibility() != View.GONE) {
					final int childWidth = childView.getMeasuredWidth();
					childView.forceLayout();
					childView.layout(childLeft, 0, childLeft + childWidth,
							childView.getMeasuredHeight());
					childLeft += childWidth;
				}
			}
		} catch (NullPointerException e) {
			Log.e(TAG, " onLayout " + e.getMessage());
		}
	}
	
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		
		// The children are given the same width and height as the scrollLayout
		measureChildren(widthMeasureSpec, heightMeasureSpec);
		
		final int width = MeasureSpec.getSize(widthMeasureSpec);
		final int height = MeasureSpec.getSize(heightMeasureSpec);

		int maxHeight = height;
		int count = this.getChildCount();
		for (int i = 0; i < count; i++) {
			int childHeight = getChildAt(i).getMeasuredHeight();
			if(childHeight > maxHeight){
				maxHeight = childHeight;
			}
		}
		
		maxHeight += (getPaddingTop() + getPaddingBottom());
		if(MeasureSpec.getMode(heightMeasureSpec) != MeasureSpec.EXACTLY){
			this.setMeasuredDimension(width, maxHeight);
		}

		// Log.d(TAG, "moving to screen "+mCurScreen);
		//snapToScreen(mCurScreen);
		scrollTo(mCurScreen * width, 0);
	}
	
	private void snapToScreen(int whichScreen) {
		whichScreen = Math.max(0, Math.min(whichScreen, getChildCount() - 1));

		// get the valid layout page
		whichScreen = Math.max(0, Math.min(whichScreen, getChildCount() - 1));
		if (getScrollX() != (whichScreen * getWidth())) {
			final int delta = whichScreen * getWidth() - getScrollX();
			mScroller.startScroll(getScrollX(), 0, delta, 0,
					(int) (Math.abs(delta) * mScrollTimeFactor));
			invalidate(); // Redraw the layout
			changeScreen(whichScreen);
		} else {
			changeScreen(whichScreen);
		}
	}
	
	private void setToScreen(int whichScreen) {
		whichScreen = Math.max(0, Math.min(whichScreen, getChildCount() - 1));
		// 在滑动过程中切换tab
		if (!mScroller.isFinished()) {
			mScroller.abortAnimation();
		}
		scrollTo(whichScreen * getWidth(), 0);
		changeScreen(whichScreen);
	}
	
	@Override
	public void computeScroll() {
		if (mScroller.computeScrollOffset()) {
			scrollTo(mScroller.getCurrX(), mScroller.getCurrY());
			postInvalidate();
		}
	}
	
	/**
	 * 改变当前显示的页面, 会回调通知外部监听器
	 *
	 * @author 
	 * @param whichScreen
	 *            目标页面
	 */
	private void changeScreen(int whichScreen) {
		if (mCurScreen == whichScreen) {
			return;
		}

		mCurScreen = whichScreen;
	}
	
	public LockViewGroup getVerifyView(){
		return mLockVerify;
	}
	
	public LockViewGroup getInuptView(){
		return mLockInput;
	}
	
	public LockViewGroup getConfirmView(){
		return mLockConfirm;
	}
	
	private int getScreenFromMode(ViewMode mode){
		int whichScreen = 0;
		switch(mode){
			case Verify:
				whichScreen = 0;
				break;
			case NewInput:
				whichScreen = 1;
				break;
			case Confirm:
				whichScreen = 2;
				break;
			default:
				whichScreen = mCurScreen;
				break;
		}
		
		return whichScreen;
	}
	
	public void scrollToView(ViewMode mode) {
		int whichScreen = getScreenFromMode(mode);
		snapToScreen(whichScreen);
	}
	
	public void setToView(ViewMode mode){
		int whichScreen = getScreenFromMode(mode);
		setToScreen(whichScreen);
	}
}

