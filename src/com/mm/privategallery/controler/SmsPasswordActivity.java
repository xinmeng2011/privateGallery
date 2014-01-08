package com.mm.privategallery.controler;


import java.util.List;

import com.mm.privategallery.R;
import com.mm.privategallery.model.SmsPasswordManager;
import com.mm.privategallery.view.LockPatternUtils;
import com.mm.privategallery.view.LockPatternView.Cell;
import com.mm.privategallery.view.LockPatternView.DisplayMode;
import com.mm.privategallery.view.LockViewGroup;
import com.mm.privategallery.view.ScrollLockView;
import com.mm.privategallery.view.LockPatternView;
import com.mm.privategallery.view.ScrollLockView.ViewMode;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;


public class SmsPasswordActivity extends Activity implements LockPatternView.OnPatternListener {
	
	private final static String TAG = "SmsPasswordActivity";
	
	public final static String INTENT_PARAMS_MODE = "intent_params_mode";
	
	public final static int MODE_SET_PWD = 0;
	
	public final static int MODE_VERIFY_PWD = 1;
	
	public final static int MODE_UPDATE_PWD = 2;
	
	private final static int REQUEST_UPDATE_KEYWORD = 3;
	
	private final static int MIN_INPUT_LENGTH = 4;
	
	private int mMode = MODE_SET_PWD;
	
	private ScrollLockView mDoubleView ;
	
	private LockViewGroup mLockViewVerify;
	
	private LockViewGroup mLockViewInput;
	
	private LockViewGroup mLockViewConfirm;
	
	private Button mBtnOk;
	
	private TextView mBtnModifyPwd;
	
	private DisplayStatus mDspStatus;
	
	private String mNewPwdTemp ;
	
	private String mScreenPwd ;	
	private View mBottomContainer;
	
	private Handler mHandler = new Handler();
	private Runnable mRunnable = null;
	
	protected enum DisplayStatus {
		SetNewInput, //第一次进入设置密码
		SetNewConfirm, //第一次确认密码
		Verify, //验证
		UpdateVerify, //修改密码
		UpdateInput, 
		UpdateConfirm// 确认修改的密码
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_lock);
		
		initView();
		initData();
		
		updateTopBarStatus();
	}
	
	private void initView(){

		mDoubleView = (ScrollLockView)findViewById(R.id.lock_container);
		mLockViewVerify = (LockViewGroup)mDoubleView.getVerifyView();
		mLockViewInput = (LockViewGroup)mDoubleView.getInuptView();
		mLockViewConfirm = (LockViewGroup)mDoubleView.getConfirmView();
		mLockViewVerify.getLockView().setOnPatternListener(this);
		mLockViewInput.getLockView().setOnPatternListener(this);
		mLockViewConfirm.getLockView().setOnPatternListener(this);
		
		mBtnOk = (Button)findViewById(R.id.btn_ok);
		mBtnOk.setOnClickListener(mOnOkClicked);
		
		mBtnModifyPwd = (TextView)findViewById(R.id.btn_modify_pwd);
		mBtnModifyPwd.setOnClickListener(mOnModifyPwdClicked);
		
		mBottomContainer = findViewById(R.id.bottom_container);
	}
	
	private void updateTopBarStatus() {
		String title = "";
		switch(mMode){
		case MODE_SET_PWD:
			title = getString(R.string.lock_set_new_title);
			break;
		case MODE_VERIFY_PWD:
			title = getString(R.string.lock_verify_title);
			break;
		case MODE_UPDATE_PWD:
			title = getString(R.string.lock_update_title);
			break;
		default:
			break;
		}
	}
	
	private void initData(){
		Intent intent = getIntent();
		Bundle bundle = intent.getExtras();
		if(bundle == null){
			return ;
		}
		
		mMode = bundle.getInt(INTENT_PARAMS_MODE,MODE_SET_PWD);
		switch(mMode){
		case MODE_SET_PWD:
			mLockViewInput.getTipsView().setText(R.string.lock_set_new_input_info);
			mLockViewConfirm.getTipsView().setText(R.string.lock_set_new_confirm_info);
			mDoubleView.setToView(ViewMode.NewInput);
			mBtnOk.setText(R.string.lock_set_new_input_btn);
			mDspStatus = DisplayStatus.SetNewInput;
			mBtnOk.setEnabled(false);
			break;
		case MODE_VERIFY_PWD:
			mDspStatus = DisplayStatus.Verify;
			mLockViewVerify.getTipsView().setText(R.string.lock_verify_info);
			mBottomContainer.setVisibility(View.GONE);
			mBtnModifyPwd.setVisibility(View.VISIBLE);
			break;
		case MODE_UPDATE_PWD:
			mDspStatus = DisplayStatus.UpdateVerify;
			mLockViewVerify.getTipsView().setText(R.string.lock_update_verify_info);
			mLockViewInput.getTipsView().setText(R.string.lock_update_input_info);
			mLockViewConfirm.getTipsView().setText(R.string.lock_update_confirm_info);
			mBtnOk.setText("");
			mBottomContainer.setVisibility(View.GONE);
			break;
		default:
			break;
		}
	}
	
	private void initTips() {
		switch(mMode){
		case MODE_SET_PWD:
			mLockViewInput.getTipsView().setText(R.string.lock_set_new_input_info);
			mLockViewConfirm.getTipsView().setText(R.string.lock_set_new_confirm_info);
			break;
		case MODE_VERIFY_PWD:
			mLockViewVerify.getTipsView().setText(R.string.lock_verify_info);
			break;
		case MODE_UPDATE_PWD:
			mLockViewVerify.getTipsView().setText(R.string.lock_update_verify_info);
			mLockViewInput.getTipsView().setText(R.string.lock_update_input_info);
			mLockViewConfirm.getTipsView().setText(R.string.lock_update_confirm_info);
			break;
		default:
			break;
		}	
	}

	private View.OnClickListener mOnOkClicked = new View.OnClickListener() {
		
		@Override
		public void onClick(View v) {
			switch (mDspStatus) {
			case SetNewInput:
				mNewPwdTemp = mScreenPwd;
				mDspStatus = DisplayStatus.SetNewConfirm;
				mDoubleView.scrollToView(ViewMode.Confirm);
				mBtnOk.setText(R.string.lock_set_new_confirm_btn);
				mBtnOk.setEnabled(false);
				break;
			case SetNewConfirm:
				SmsPasswordManager.getInstance().updatePassword(mNewPwdTemp);
				SmsPasswordActivity.this.setResult(RESULT_OK);
				SmsPasswordActivity.this.finish();
				break;
			case UpdateInput:
				mNewPwdTemp = mScreenPwd;
				mDspStatus = DisplayStatus.UpdateConfirm;
				mDoubleView.scrollToView(ViewMode.Confirm);
				mBtnOk.setText(R.string.lock_set_new_confirm_btn);
				mBtnOk.setEnabled(false);
				break;
			case UpdateConfirm:
				SmsPasswordManager.getInstance().updatePassword(mNewPwdTemp);
				SmsPasswordActivity.this.setResult(RESULT_OK);
				SmsPasswordActivity.this.finish();
				break;
			default:
				break;
			}
		}
	};
	
	
	private View.OnClickListener mOnModifyPwdClicked = new View.OnClickListener() {

		@Override
		public void onClick(View v) {
			if(mDspStatus != DisplayStatus.Verify){
				return ;
			}
			
			//修改密码
			Intent intent = new Intent();
			intent.setClass(SmsPasswordActivity.this, SmsPasswordActivity.class);
			intent.putExtra(SmsPasswordActivity.INTENT_PARAMS_MODE, SmsPasswordActivity.MODE_UPDATE_PWD);
			SmsPasswordActivity.this.startActivityForResult(intent, REQUEST_UPDATE_KEYWORD);
		}
	};
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
			if(requestCode == REQUEST_UPDATE_KEYWORD){// 更新密码成功 关闭页面
				if(resultCode == RESULT_OK){
					SmsPasswordActivity.this.setResult(RESULT_OK);
					SmsPasswordActivity.this.finish();
				}
			}
	}
	
	@Override
	public void onPatternStart(LockPatternView v) {
		if (null != mRunnable) {
			mHandler.removeCallbacks(mRunnable);
		}
		initTips();
	}

	@Override
	public void onPatternCleared(LockPatternView v) {
		v.setDisplayMode(DisplayMode.Correct);
	}

	@Override
	public void onPatternCellAdded(LockPatternView v, List<Cell> pattern) {
	}

	@Override
	public void onPatternDetected(LockPatternView v, List<Cell> pattern) {
		String pwd = LockPatternUtils.patternToString(pattern);
		String tips = null;
		if(pwd == null || pwd.length() < MIN_INPUT_LENGTH){
			v.setDisplayMode(DisplayMode.Wrong);
			mBtnOk.setEnabled(false);
			changeTips(getString(R.string.lock_min_connect_length, MIN_INPUT_LENGTH));
			clearErrorPattern(v);
			return ;
		}
		
		Log.d(TAG,"screen is " + pwd + ", temp is " + mNewPwdTemp);
		mScreenPwd = pwd;
		switch (mDspStatus) {
		case SetNewInput:
		case UpdateInput:
			mBtnOk.setEnabled(true);
			break;
		case SetNewConfirm:
		case UpdateConfirm:
			if(!mScreenPwd.equals(mNewPwdTemp)){
				v.setDisplayMode(DisplayMode.Wrong);
				mBtnOk.setEnabled(false);
				changeTips(R.string.unlock_error);
				clearErrorPattern(v);
			} else {
				v.setDisplayMode(DisplayMode.Correct);
				mBtnOk.setEnabled(true);
			}
			break;
		case Verify:
			if(SmsPasswordManager.getInstance().isPasswordCorrect(mScreenPwd) == false){
				v.setDisplayMode(DisplayMode.Wrong);
				changeTips(R.string.unlock_error);
				clearErrorPattern(v);
			} else {
				this.setResult(RESULT_OK);
				this.finish();
			}
			break;
		case UpdateVerify:
			if(SmsPasswordManager.getInstance().isPasswordCorrect(mScreenPwd) == false){
				v.setDisplayMode(DisplayMode.Wrong);
				changeTips(R.string.unlock_error);
				clearErrorPattern(v);
			} else {
				mDspStatus = DisplayStatus.UpdateInput;
				v.clearPattern();
				mDoubleView.scrollToView(ViewMode.NewInput);
				mBtnOk.setText(R.string.lock_set_new_input_btn);
				mBottomContainer.setVisibility(View.VISIBLE);
				mBtnOk.setEnabled(false);
			}
			break;
		default:
			break;
		}
	}
	
	private void clearErrorPattern(final LockPatternView patternView) {
		if (null == patternView) {
			return;
		}
		
		if (null != mRunnable) {
			mHandler.removeCallbacks(mRunnable);
			mRunnable = null;
		}
		mRunnable = new Runnable() {
			
			@Override
			public void run() {
				patternView.clearPattern();
			}
		};
		mHandler.postDelayed(mRunnable, 2000);
	}
	
	private void changeTips(int resourceID) {
		String tips = getString(resourceID, MIN_INPUT_LENGTH);
		changeTips(tips);
	}
	
	private void changeTips(String tips) {
		if (null == tips || tips.length() < 1) {
			return;
		}
		mLockViewInput.getTipsView().setText(tips);
		mLockViewConfirm.getTipsView().setText(tips);
		mLockViewVerify.getTipsView().setText(tips);
	}
}

