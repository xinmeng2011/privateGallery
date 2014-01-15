package com.mm.privategallery.controler;

import com.mm.privategallery.R;
import com.mm.privategallery.model.GalleryEngine;
import com.mm.utility.SDHelper;
import com.mm.utility.ToastUtility;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.app.Activity;
import android.content.Intent;
import android.view.View;

public class HelloActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_hello);
		initUI();
	}

	@Override
	protected void onStart() {
		super.onStart();

		mHandler.postDelayed(new Runnable() {

			@Override
			public void run() {
				checkSDStatus();
			}
		}, 500);
		// wait for ui showed
	}

	private boolean checkSDStatus() {
		boolean status = SDHelper.hasStorage(true);
		// for test status = !status;
		if (!status) {
			ToastUtility.showToast(R.string.no_sd_tip, HelloActivity.this);
		}
		return status;
	}

	private final Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {

			super.handleMessage(msg);
		}
	};

	private void initUI() {
		findViewById(R.id.gallery_bt).setOnClickListener(mClickListener);
	}

	private View.OnClickListener mClickListener = new View.OnClickListener() {

		@Override
		public void onClick(View v) {
			if (!checkSDStatus()) {
				return;
			}
			Intent intent = new Intent();
			switch (v.getId()) {
			case R.id.gallery_bt:
				intent.setClass(HelloActivity.this, ChooseGalleryActivity.class);
				startActivity(intent);
				break;
			default:
				break;
			}

		}
	};

	private void Test() {
		String pathString = "/storage/emulated/0/DCIM/Camera/20131108_232326.jpg";
		GalleryEngine.getSingle().encryptImage(pathString);
	}

}
